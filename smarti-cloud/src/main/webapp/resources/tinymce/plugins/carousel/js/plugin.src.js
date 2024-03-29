(function() {
    // Load plugin specific language pack
    tinymce.PluginManager.requireLangPack('carousel');

    tinymce.create('tinymce.plugins.CarouselPlugin', {
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
            ed.addCommand('mceCarousel', function() {
            	var url_uid = "./browse.html";
            	var carousel = tinyMCE.activeEditor.dom.select('.carousel');
            	if(carousel && carousel !='') {
            		var path = tinyMCE.activeEditor.dom.getAttrib(carousel,"id","");
            		if(path=="") 
            			url_uid = "./browse.html";
            		else 
            			url_uid = "./browse.html?path="+path;
            	}
            	
                ed.windowManager.open({
                	title : "\u6587\u4ef6\u6d4f\u89c8",
                    file : url_uid,
                    width : 420 + ed.getLang('carousel.delta_width', 0),
                    height : 560 + ed.getLang('carousel.delta_height', 0),
                    inline : 1                
                }, {
                    plugin_url : url, // Plugin absolute URL
                    type: "carousel" // Custom argument
                });
            });

            // Register example button
            ed.addButton('carousel', {
                title : 'Carousel',
                cmd : 'mceCarousel',
                image : url + '/img/next.gif'
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

        /**
         * Returns information about the plugin as a name/value array.
         * The current keys are longname, author, authorurl, infourl and version.
         *
         * @return {Object} Name/value array containing information about the plugin.
         */
        getInfo : function() {
            return {
                longname : 'Carousel plugin',
                author : 'Some author',
                authorurl : 'http://tinymce.moxiecode.com',
                infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/example',
                version : "1.0"
            };
        }
    });

    // Register plugin
    tinymce.PluginManager.add('carousel', tinymce.plugins.CarouselPlugin);
})();
