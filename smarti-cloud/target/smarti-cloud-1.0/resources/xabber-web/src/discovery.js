define("xabber-discovery", function () {
  return function (xabber) {
    var env = xabber.env,
        constants = env.constants,
        $ = env.$,
        _ = env._,
        moment = env.moment,
        Strophe = env.Strophe,
        $iq = env.$iq;

    xabber.ClientFeature = Backbone.Model.extend({
        idAttribute: 'namespace'
    });

    xabber.FeatureView = xabber.BasicView.extend({
        className: 'client-feature',
        template: function () {
            this.$el.append('<div class="feature-name one-line"/>')
                    .append('<div class="supports"/>');
        },

        _initialize: function () {
            this.render();
            this.$el.appendTo(this.model.collection.account.settings_right.$('.capabilities'));
            this.model.on("change", this.render, this);
            this.model.on("destroy", this.remove, this);
        },

        render: function () {
            var name = this.model.get('verbose_name'),
                supports = this.model.get('supports');
            this.$('.feature-name').text(name);
            this.$('.supports').text(supports ? 'Available' : 'Unavailable')
        }
    });

    xabber.ClientFeatures = Backbone.Collection.extend({
        model: xabber.ClientFeature,

        initialize: function (models, options) {
            this.account = options.account;
            this.connection = this.account.connection;
            this.connection.disco.addIdentity(
                'client',
                'web',
                xabber.get('client_name'),
                'en'
            );
            this.addFeature(Strophe.NS.VCARD, 'XEP-0054: vCard-temp');
            this.addFeature(Strophe.NS.BLOCKING, 'XEP-0191: Blocking Command');
            this.addFeature(Strophe.NS.PING, 'XEP-0199: XMPP Ping');
            this.addFeature(Strophe.NS.CARBONS, 'XEP-0280: Message carbons');
            this.addFeature(Strophe.NS.MAM, 'XEP-0313: Message archive management');
            this.addFeature(Strophe.NS.HTTP_UPLOAD, 'XEP-0363: HTTP File Upload');
        },

        addFeature: function (namespace, verbose_name) {
            var feature = this.create({
                namespace: namespace,
                verbose_name: verbose_name
            });
            this.connection.disco.addFeature(namespace);
            var view = new xabber.FeatureView({model: feature});
        }
    });

    xabber.ServerFeature = Backbone.Model.extend({
        idAttribute: 'var'
    });

    xabber.ServerFeatures = Backbone.Collection.extend({
        model: xabber.ServerFeature,

        initialize: function (models, options) {
            this.account = options.account;
            this.connection = this.account.connection;
            this.on("add", this.onFeatureAdded, this);
        },

        request: function () {
            this.connection.disco.info(this.account.domain, null, this.onInfo.bind(this));
            this.connection.disco.items(this.account.domain, null, this.onItems.bind(this));
        },

        onItems: function (stanza) {
            $(stanza).find('query item').each(function (idx, item) {
                this.connection.disco.info(
                    $(item).attr('jid'),
                    null,
                    this.onInfo.bind(this));
            }.bind(this));
        },

        onInfo: function (stanza) {
            var $stanza = $(stanza),
                from = $stanza.attr('from'),
                self = this;
            $stanza.find('feature').each(function () {
                var namespace = $(this).attr('var');
                self.create({
                    'var': namespace,
                    from: from
                });
                if (namespace === Strophe.NS.AUTH_TOKENS) {
                    self.account.settings_right.getAllXTokens();
                    if (self.account.get('x_token'))
                        if ((self.account.get('x_token').token) && (moment(moment.now()).startOf('seconds').format('x') < self.account.get('x_token').expire*1000))
                            return;
                    self.getXToken();
                }
            });
        },

        getXToken: function () {
            var iq = $iq({
                from: this.account.get('jid'),
                type: 'set',
                to: Strophe.getDomainFromJid(this.account.get('jid'))
            }).c('issue', { xmlns: Strophe.NS.AUTH_TOKENS})
                .c('client').t(xabber.get('client_name')).up()
                .c('os').t(navigator.platform);
            this.account.sendIQ(iq,
                function (callback_iq) {
                var token = $(callback_iq).find('token').text(),
                    expires_at = $(callback_iq).find('expire').text(),
                    token_uid = $(callback_iq).find('token-uid').text();
                    this.account.save('auth_type', 'x-token');
                    this.account.save('x_token', {token: token, expire: expires_at, token_uid: token_uid });
                    this.account.save('password', null);
                    this.connection.x_token = {token: token, expire: expires_at, token_uid: token_uid };
                    this.connection.pass = token;
                    this.connection._sasl_data["server-signature"] = null;
                    /*this.account.deactivate();
                    this.account.activate();*/
                    this.account.session.set('deactivate', 'set_off');
                    this.connection.disconnect();
                    setTimeout(function () {
                        this.account.connect();
                    }.bind(this), 1000);
                    this.account.settings_right.getAllXTokens();
                }.bind(this));
        },

        onFeatureAdded: function (feature) {
            var _var = feature.get('var'),
                client_feature = this.account.client_features.get(_var);
            client_feature && client_feature.set('supports', true);

            var prefs = feature.get('preferences') || {};
            if (_var === Strophe.NS.MAM && prefs.default !== 'always') {
                this.account.sendIQ(
                    $iq({type: 'get'}).c('prefs', {xmlns: Strophe.NS.MAM}),
                    _.bind(this.receiveMAMPreferences, this, feature)
                );
            }
        },

        receiveMAMPreferences: function (feature, iq) {
            var $prefs = $(iq).find('prefs[xmlns="'+Strophe.NS.MAM+'"]');
            var default_pref = $prefs.attr('default');
            if (default_pref !== 'always') {
                var stanza = $iq({'type': 'set'})
                    .c('prefs', {xmlns: Strophe.NS.MAM, 'default': 'always'});
                $prefs.children().each(function (idx, child) {
                    stanza.cnode(child).up();
                });
                this.account.sendIQ(stanza, function (iq) {
                    feature.set('preferences', {'default': 'always'});
                });
            } else {
                feature.set('preferences', {'default': 'always'});
            }
        }
    });

    xabber.Account.addInitPlugin(function () {
        this.client_features = new xabber.ClientFeatures(null, {account: this});
        this.server_features = new xabber.ServerFeatures(null, {account: this});
    });

    xabber.Account.addConnPlugin(function () {
        this.last_stanza_timestamp = moment.now();

        this.connection.deleteHandler(this._last_stanza_handler);
        this._last_stanza_handler = this.connection.addHandler(function () {
            this.last_stanza_timestamp = moment.now();
            return true;
        }.bind(this));

        this.connection.deleteHandler(this._pong_handler);
        this._pong_handler = this.connection.ping.addPingHandler(function (ping) {
            this.last_stanza_timestamp = moment.now();
            this.connection.ping.pong(ping);
            return true;
        }.bind(this));

        this.connection.deleteTimedHandler(this._ping_handler);
        this._ping_handler = this.connection.addTimedHandler(10000, function () {
            var downtime = moment.now() - this.last_stanza_timestamp;
            if (downtime / 1000 > (xabber.settings.ping_interval || 180)) {
                this.last_stanza_timestamp = moment.now();
                this.connection.ping.ping(this.get('jid'));
            }
            return true;
        }.bind(this));

        this.server_features.request();
    }, true, true);

    xabber.Account.addConnPlugin(function () {
        var disco = this.connection.disco;
        this.connection.addHandler(disco._onDiscoInfo.bind(disco),
                Strophe.NS.DISCO_INFO, 'iq', 'get', null, null);
        this.connection.addHandler(disco._onDiscoItems.bind(disco),
                Strophe.NS.DISCO_ITEMS, 'iq', 'get', null, null);
    }, false, true);

    return xabber;
  };
});
