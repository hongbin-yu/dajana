(function() {
    // Load plugin specific language pack
    tinymce.PluginManager.requireLangPack('youchat');

    tinymce.create('tinymce.plugins.YouchatPlugin', {
        /**
         * Initializes the plugin, this will be executed after the plugin has been created.
         * This call is done before the editor instance has finished it's initialization so use the onInit event
         * of the editor instance to intercept that event.
         *
         * @param {tinymce.Editor} ed Editor instance that the plugin is initialized in.
         * @param {string} url Absolute URL to where the plugin is located.
         */
        init : function(ed, url) {
            // Register the command so that it can be invoked by using tinyMCE.activeEditor.execCommand('mceExample');
            ed.addCommand('mceYouchat', function() {
            	var id = document.getElementById("pageId").getAttribute("value");
            	openOverlay(id,'youchat-bar');
            });

            // Register example button
            ed.addButton('youchat', {
                title : 'Youchat',
                cmd : 'mceYouchat',
                image : url + '/img/mail.jpg'
            });

            // Add a node change handler, selects the button in the UI when a image is selected
            /*
            ed.onChange.add(function(ed, cm, n) {
                cm.setActive('carsouel', n.nodeName == 'IMG');
            });*/
           
        },

        /**
         * Creates control instances based in the incomming name. This method is normally not
         * needed since the addButton method of the tinymce.Editor class is a more easy way of adding buttons
         * but you sometimes need to create more complex controls like listboxes, split buttons etc then this
         * method can be used to create those.
         *
         * @param {String} n Name of the control to create.
         * @param {tinymce.ControlManager} cm Control manager to use inorder to create new control.
         * @return {tinymce.ui.Control} New control instance or null if no control was created.
         */
        createControl : function(n, cm) {
            return null;
        },


    });

    // Register plugin
    tinymce.PluginManager.add('youchat', tinymce.plugins.YouchatPlugin);
})();
