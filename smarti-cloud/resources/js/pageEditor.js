var contentPath="";
var carousel="";
var el = document.querySelector(".carousel");
var i18n = window.wb.i18n;

if(el) {
	carousel = el.innerHTML;
}
if(window.location.pathname.indexOf("/smarti-cloud")>=0) {
	contentPath = "/smarti-cloud";
}
tinymce.init({
	  selector: 'h2.caneditable',
	  inline: true,
	  toolbar: 'undo redo',
	  menubar: false
	});
tinymce.init({
	  selector: 'div.description',
	  inline: true,
	  language: 'zh_CN.GB2312',
	  plugins: ['save paste advlist autolink lists link imagetools autosave'],
	  toolbar: 'save undo redo paste',
	  autosave_ask_before_unload: false,
	  autosave_interval: "20s",
	  menubar: false,
	  save_onsavecallback: function() {
            // USE THIS IN YOUR AJAX CALL

	    var id = tinyMCE.activeEditor.id;
	    var uid = id.replace("description","");
	    var div_id = document.getElementById(id);
	    var name = div_id.getAttribute("property") || "description";
	    //var uid = div_id.getAttribute("uid");
	    if(uid ) {
	        var content = tinyMCE.activeEditor.getContent();
/*	        var fd = new FormData();
	        fd.append('name', name);
	        fd.append('uid', uid);
	        fd.append('value', content);*/
	        $.ajax({
	    	    url: 'updateNodeProperty.html',
	    	    data: {
	    	    	uid: uid,
	    	    	name: name,
	    	    	value: content
	    	    	},
	    	    type: "POST",
	    	    success: function(msg) {
	    	    	if(msg.indexOf("error")>=0)
	    		        $("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("save")+i18n("fail")+"!</h3>"+msg+"</section>");
		           else
	    	    		$("#header_message").html("<section class='alert alert-info'><h3>"+i18n("save")+i18n("success")+"!</h3></section>");
	    	    	
	    	    },
	    	    error: function() {
	    	        $("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("save")+i18n("fail")+"!</h3></section>");
	    	    }		                
	        });
	    }else {
	    	$("#header_message").html("uid is missing,name="+name);    
	    }
	
	}	  
	});

tinymce.init({
	  selector: 'div.image',
	  inline: true,
	  language: 'zh_CN.GB2312',
	  plugins: ['save paste imagetools autosave'],
	  toolbar: 'save undo redo paste | advlist autolink lists link',
	  autosave_ask_before_unload: false,
	  menubar: false,
	  save_onsavecallback: function() {
          // USE THIS IN YOUR AJAX CALL
	    var id = tinyMCE.activeEditor.id;
	    var uid = id.replace("description","");
	    var div_id = document.getElementById(id);
	    var name = div_id.getAttribute("property") || "description";
	    //var uid = div_id.getAttribute("uid");
	    if(uid ) {
	        var content = tinyMCE.activeEditor.getContent();
	        var fd = new FormData();
	        fd.append('name', name);
	        fd.append('uid', uid);
	        fd.append('value', content);
	        $.ajax({
	    	    url: 'updateImage.html',
	    	    data: fd,
	    	    type: "POST",
	    	    processData: false,
	    	    contentType: false,
	    	    success: function(msg) {
	    	    	if(msg.indexOf("error")>=0)
	    		        $("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("save")+i18n("success")+"!</h3>"+msg+"</section>");
		           else
	    	    		$("#header_message").html("<section class='alert alert-info'><h3>"+i18n("save")+i18n("success")+"!</h3></section>");
	    	    	
	    	    },
	    	    error: function() {
	    	        $("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("save")+i18n("success")+"!</h3></section>");
	    	    }		                
	        });
	    }else {
	    	$("#header_message").html("uid is missing,name="+name);    
	    }
	
	}	  
	});
	tinymce.init({
	  selector: 'div.caneditable',
	  language: 'zh_CN.GB2312',
	  content_css: contentPath+'/resources/dist/css/wet-boew.min.css'+','+ contentPath+'/resources/css/tiny_mce_editor.css',
	  inline: true,
	  plugins: [
	    'save advlist autolink lists link image imagetools template charmap print preview anchor',
	    'noneditable searchreplace visualblocks code fullscreen',
	    'insertdatetime media table carousel contextmenu paste'
	  ],
	  convert_urls: true,
	  relative_urls: true,
	  remove_script_host: true,
	  noneditable_noneditable_class:"noneditable",
	  noneditable_editable_class:"editable",
	  noneditable_regexp:/\[\[[^\]]+\]\]/g,
	  contextmenu: "link image media inserttable template | cell row column deletetable",
	  templates:contentPath+"/templates/assets/json/components.json",//"resources/templates/wet.json",
	  table_class_list: [    {title: 'None', value: ''},
	                         {title: 'Wet-table', value: 'wb-tables table'},
	                         {title: 'Wet-table,strip', value: 'wb-tables table table-striped table-hover'},
	                         {title: 'Wet-table,strip,bordered', value: 'wb-tables table table-striped table-hover table-bordered'}
	                       ],
      table_default_attributes: {
          class: 'wb-tables table'
      },
      image_class_list:  [{title: 'Image Responsive', value: 'img-responsive'},
					      {title: 'Image Thumbnail', value: 'thumbnail'},
					      {title: 'Image Thumbnail Responsive', value: 'img-responsive thumbnail'},
					      {title: 'Pull left', value: 'pull-left img-responsive'},
					      {title: 'Pull right', value: 'pull-right img-responsive'},
					      {title: 'None', value: ''}],
      image_default_attributes: {
          class: 'img-responsive'
      },
      link_class_list: [  {title: 'None', value: ''},
					      {title: 'Button', value: 'btn btn-default'},
					      {title: 'Button fullwidth', value: 'btn btn-default btn-block'},
					      {title: 'Button primary', value: 'btn btn-primary'},
					      {title: 'Button success', value: 'btn btn-success'},
					      {title: 'Button warning', value: 'btn btn-warning'},
					      {title: 'Button info', value: 'btn btn-info'},
					      {title: 'Button danger', value: 'btn btn-danger'},
					      {title: 'Lightbox', value: 'wb-lbx'},
					      {title: 'Footnote', value: 'fn-lnk'}],
      file_browser_callback : function(field_name, url, type, win) {
    	  fileBrowserCallBack(field_name, url, type, win);
    	  //win.document.getElementById(field_name).value = 'my browser value';
      },
/*      init_instance_callback: function(editor) {
    	  editor.on('focus', function(e) {
    		  $("#left-iframe").removeClass("wb-inv");	  
    	  });
    	  editor.on('blur', function(e) {
    		  $("#left-iframe").addClass("wb-inv");	  
    	  });
    	     	   	     	  
      },*/
      file_browser_callback_types: 'file image media',
	  toolbar: 'save insert undo redo | outdent indent bullist numlist | link image media paste | template',// styleselect | superscript | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist
	    save_enablewhendirty: true,
	    save_onsavecallback: function() {
	                        // USE THIS IN YOUR AJAX CALL
	                var id = tinyMCE.activeEditor.id;
	                var div_id = document.getElementById(id);
	                var name = div_id.getAttribute("property") || "content";
	                if(id) {
	                	var data_replace = tinyMCE.activeEditor.dom.select(".carousel");
	                	if(data_replace.length>0) {
	                		data_replace[0].innerHTML = carousel;
	                	};
	                	tinyMCE.DOM.removeClass("div","wb-data-ajax-replace-inited");
	                	tinyMCE.DOM.removeClass("div","wb-init");	 
	                	tinyMCE.DOM.removeClass("div","wb-tabs-inited");
	                	tinyMCE.DOM.removeClass("a","wb-lbx-inited");	                	
	                	tinyMCE.DOM.removeClass("a","wb-init");	                	

		                var content = tinyMCE.activeEditor.getContent();
		                content = content.replace("-edit","");
        	    		$("#header_message").html("<section class='alert alert-info'><h3>"+i18n("save")+"...</h3></section>");

		                $.ajax({
		            	    url: 'updateProperty.html',
		            	    data: {
		            		    uid: id,
		            		    name: name,
		            		    value: content
		            		    },
		            	    type: "POST", 
		            	    success: function(msg) {
		            	    	if(msg.indexOf("error")>=0)
		            		        $("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("save")+i18n("fail")+"!</h3>"+msg+"</section>");
		    		           else
		            	    		$("#header_message").html("<section class='alert alert-info'><h3>"+i18n("save")+i18n("success")+"!</h3></section>");
		            	    	
		            	    },
		            	    error: function() {
		            	        $("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("save")+i18n("fail")+"!</h3></section>");
		            	    }		                
		                });
		            }else {
		            	$("#error").html(i18n("fial")+name);    
			        }

	        }
	});

tinymce.init({ selector:'.h2',
	  language: 'zh_CN.GB2312',
	  plugins: "table",
	  inline: true  });
  
function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
    ev.dataTransfer.setData("text", ev.target.id);
}



/*function updateThis() {
	var id = this.getAttribute("id");
	var uid = this.getAttribute("uid");
	var name = this.getAttribute("name");
	var type = this.getAttribute("type");
	var value = (type=='checkbox'?$(this).is(":checked"):this.value);
	if(uid) {
    $.ajax({
	    url: contentPath+'/updateProperty.html',
	    data: {
		    uid: uid,
		    name: name,
		    value: value
		    },
	    type: "POST", 
	    success: function(msg) {
		    if(type=="checkbox") {
		    	document.getElementById(id).parentNode.classList.add("alert-success");
		    	if(value=="true" || value==true)
		    		document.getElementById("link"+uid).classList.add("wb-inv");
		    	else
		    		document.getElementById("link"+uid).classList.remove("wb-inv");
			}else  {

	    		document.getElementById(id).classList.add("alert-success");
			}
	    },
	    error: function() {
	    	document.getElementById(id).classList.add("alert-warning");
	    }
	    // ... Other options like success and etc
	});	 
	} else {
		$("#header_message").html("uid missing");
    	
    }
}*/

function createPage() {
	var formData = $("#createPage").serializeArray();
	document.getElementById("submit_upload").disabled=true;
    $.ajax({
	    url: 'createPage.html',
	    data: formData,
	    type: "POST", 
	    success: function(msg) {
	    	if(msg.indexOf("/content")==0) {
	    		window.location.href="editor.html?path="+msg;
	     	}else
	     		$("#error").html("<section class=\"alert alert-warning\">"+msg+"</section>");
	    },
	    error: function() {
	    	$("#error").html(i18n("fail"));
	    }
	    // ... Other options like success and etc
	});	 

}

function createFolder() {
	var formData = $("#createFolder").serializeArray();
    $.ajax({
	    url: 'createFolder.html',
	    data: formData,
	    type: "POST", 
	    success: function(msg) {
	    	if(msg.indexOf("error:")>0) {
	    		$("#header_message").html("<section class=\"alert alert-warning\"><h3>"+i18n("fail")+"</h3><p>"+msg+"</p></section>");
	    	}else
	    		window.location.reload();
	    },
	    error: function() {
	    	$("#header_message").html("<section class=\"alert alert-warning\"><h3>"+i18n("fail")+"</h3><p></p></section>");
	    }
	    // ... Other options like success and etc
	});	 

}

function deleteAsset(path,uid) {

    $.ajax({
	    url: 'deleteasset.html',
	    data: {
	    	path: path
	    },
	    type: "POST", 
	    success: function(msg) {
    			
	    	if(msg.indexOf("error:")>0) {
	    		$("#header_message").html("<section class=\"alert alert-warning\"><h3>"+i18n("fail")+"</h3><p>"+msg+"</p></section>");
	    	}else {
	    		$("#"+uid).remove();
	    	}
	    	

	    },
	    error: function() {
	    	$("#header_message").html("<section class=\"alert alert-warning\"><h3>"+i18n("fail")+"</h3><p></p></section>");
	    }
	    // ... Other options like success and etc
	});	 	
}

function deletePage(path,parent) {

    $.ajax({
	    url: 'delete.html',
	    data: {
	    	path: path
	    },
	    type: "POST", 
	    success: function(msg) {
    			
	    	if(msg.indexOf("error:")>0) {
	    		$("#header_message").html("<section class=\"alert alert-warning\"><h3>"+i18n("fail")+"</h3><p>"+msg+"</p></section>");
	    	}else {
	    		window.location.href="editor.html?path="+parent;
	    	}
	    	

	    },
	    error: function() {
	    	$("#header_message").html("<section class=\"alert alert-warning\"><h3>"+i18n("fail")+"</h3><p></p></section>");
	    }
	    // ... Other options like success and etc
	});	 	
}

/*function updateProperty(obj) {
	var id = obj.getAttribute("id");
	var uid = obj.getAttribute("uid");
	var name = obj.getAttribute("name");
	var type = obj.getAttribute("type");
	var value = (type=='checkbox'?$(obj).is(":checked"):obj.value);

	if(uid) {
    $.ajax({
	    url: contentPath+'/updateProperty.html',
	    data: {
		    uid: uid,
		    name: name,
		    value: value
		    },
	    type: "POST", 
	    success: function(msg) {
		    if(type=="checkbox") {
				if(msg.indexOf("error")>=0)
					document.getElementById(id).parentNode.classList.add("alert-danger");
				else
					document.getElementById(id).parentNode.classList.add("alert-success");
			}else  {
				if(msg.indexOf("error")>=0)
					document.getElementById(id).classList.add("alert-danger");
				else
					document.getElementById(id).classList.add("alert-success");
			}
	    },
	    error: function() {
	    	document.getElementById(id).classList.add("alert-warning");
	    }
	    // ... Other options like success and etc
	});	 
	} else {
		alert("uid missing");
    	
    }
}*/
function updateTemplate() {
	var form = document.getElementById('template');
	var uid = document.getElementById('template_uid').value;
	var value = document.getElementById('template_content').value;
	var name= "content";

    $.ajax({
	    url: 'updateProperty.html',
	    data: {
		    uid: uid,
		    name: name,
		    value: value
		    },
	    type: "POST", 
	    success: function(msg) {
	    	if(msg.indexOf("error:")>=0)
		    	$("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("fail")+"</h3><p>"+msg+"</p></section>");
	    	else
	    	$("#header_message").html("<section class='alert alert-success'><h3>"+i18n("success")+"</h3></section>");	    	
	    		
	    },
	    error: function() {
	    	$("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("fail")+"</h3></section>");
	    }

	});	    
}

function updateContent() {
	var uid = document.getElementById('content_uid').value;
	var value = document.getElementById('content_value').value;

    $.ajax({
	    url: 'updateContent.html',
	    data: {
		    uid: uid,
		    content: value
		    },
	    type: "POST", 
	    success: function(msg) {
	    	if(msg.indexOf("error:")>=0)
		    	$("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("fail")+"</h3><p>"+msg+"</p></section>");
	    	else {
	    		
	    		window.location.reload();
	    	}
	    	
	    	//$("#header_message").html("<section class='alert alert-success'><h3>"+i18n("success")+"</h3></section>");	    	
	    		
	    },
	    error: function() {
	    	$("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("fail")+"</h3></section>");
	    }

	});	    
}
function publish(uid) {
	$("#header_message").html("<section class='alert alert-info'><h3>"+i18n("process")+"</h3></section>");	    	

    $.ajax({
	    url: 'publish.html',
	    data: {
		    uid: uid,
		    name: "status",
		    value: "true"
		    },
	    type: "POST", 
	    success: function(msg) {
	    	if(msg.indexOf("error:")>=0)
		    	$("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("publish")+i18n("fail")+"</h3><p>"+msg+"</p></section>");
	    	else
	    	$("#header_message").html("<section class='alert alert-success'><h3>"+i18n("publish")+i18n("success")+"</h3></section>");	    	
	    },
	    error: function() {
	    	$("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("publish")+i18n("fail")+"</h3></section>");
	    }

	});	 
}

function publishFolder(uid) {
	$("#modal_message").html("<section class='alert alert-info'><h3>"+i18n("process")+"</h3></section>");	    	
    $.ajax({
	    url: 'publishfolder.html',
	    data: {
		    uid: uid,
		    name: "status",
		    value: "true"
		    },
	    type: "POST", 
	    success: function(msg) {
	    	if(msg.indexOf("error:")>=0)
		    	$("#modal_message").html("<section class='alert alert-danger'><h3>"+i18n("publish")+i18n("fail")+"</h3><p>"+msg+"</p></section>");
	    	else
	    	$("#modal_message").html("<section class='alert alert-success'><h3>"+i18n("success")+i18n("publish")+msg+i18n("page")+"</h3></section>");	    	
	    },
	    error: function() {
	    	$("#modal_message").html("<section class='alert alert-danger'><h3>"+i18n("publish")+i18n("fail")+"</h3></section>");
	    }

	});	 
}

function unpublishFolder(uid) {
	$("#modal_message").html("<section class='alert alert-info'><h3>"+i18n("process")+"</h3></section>");	    	
    $.ajax({
	    url: 'unpublishfolder.html',
	    data: {
		    uid: uid,
		    name: "status",
		    value: "true"
		    },
	    type: "POST", 
	    success: function(msg) {
	    	if(msg.indexOf("error:")>=0)
		    	$("#modal_message").html("<section class='alert alert-danger'><h3>"+i18n("publish")+i18n("fail")+"</h3><p>"+msg+"</p></section>");
	    	else
	    	$("#modal_message").html("<section class='alert alert-success'><h3>"+i18n("success")+i18n("unpublish")+msg+i18n("page")+"</h3></section>");	    	
	    },
	    error: function() {
	    	$("#modal_message").html("<section class='alert alert-danger'><h3>"+i18n("unpublish")+i18n("fail")+"</h3></section>");
	    }

	});	 
}
document.addEventListener("dragenter", function(event) {
    if (event.target.className && event.target.className.indexOf("component")>=0 ) {
        event.target.style.border = "3px dotted red";
       
    }
});

// By default, data/elements cannot be dropped in other elements. To allow a drop, we must prevent the default handling of the element
document.addEventListener("dragover", function(event) {
    event.preventDefault();
});

document.addEventListener("DOMContentLoaded", init, false);

function init() {

	var form_control = document.querySelectorAll('.form-editable');

	for(var i=0; i<form_control.length;i++) {
		form_control[i].addEventListener('change',updateThis);
		}

}

// When the draggable p element leaves the droptarget, reset the DIVS's border style
document.addEventListener("dragleave", function(event) {
	if (event.target.className && event.target.className.indexOf("component")>=0 ) {
        event.target.style.border = "";
	}
});
/*
document.addEventListener("click",function(event){
	if(event.target.className && event.target.className.indexOf("lbx-modal")>=0) {
		var href = event.target.getAttribute("href");
		if(href) {
			var modal = document.getElementById(href.replace("#",""));
			if(modal)
				if(modal.className.indexOf("mfp-hide")>=0) 
					modal.classList.remove("mfp-hide");
				else
					modal.classList.add("mfp-hide");
			};
		}

});
*/
window.addEventListener("dragover",function(e){
	  e = e || event;
	  e.preventDefault();
	},false);
	window.addEventListener("drop",function(e){
	  e = e || event;
	  e.preventDefault();
	},false);

function fileBrowserCallBack(field_name, url, type, win) {

	 // alert("Field_Name: " + field_name + "nURL: " + url + "nType: " + type + "nWin: " + win); // debug/testing

    /* If you work with sessions in PHP and your client doesn't accept cookies you might need to carry
       the session name and session ID in the request string (can look like this: "?PHPSESSID=88p0n70s9dsknra96qhuk6etm5").
       These lines of code extract the necessary parameters and add them back to the filebrowser URL again. */

    /* Here goes the URL to your server-side script which manages all file browser things. */
    var cmsURL = contentPath+"/site/"+type+".html";//window.location.pathname;     // your URL could look like "/scripts/my_file_browser.php"
    var searchString = window.location.search; // possible parameters
    if (searchString.length < 1) {
        // add "?" to the URL to include parameters (in other words: create a search string because there wasn't one before)
        searchString = "?";"/site/"+type+".html"
    }else {
    	searchString ="?";//remove searchString +="&"
        }
;
    // newer writing style of the TinyMCE developers for tinyMCE.openWindow

    tinyMCE.activeEditor.windowManager.open({
        file : cmsURL + searchString.trim() + "type=" + type+"&input="+field_name+"&cachebuster=123", // PHP session ID is now included if there is one at all
        title : "Select Assets",
        width : 420,  // Your dimensions may differ - toy around with them!
        height : 560,
        resizable : "yes",
        inline : "yes",  // This parameter only has an effect if you use the inlinepopups plugin!
        close_previous : "yes"
    }, {
        window : win,
        input : field_name
    });
    return false;
	}

function showImages(obj) {

	$("#left-iframe").html('<iframe src="'+contentPath+'/site/browse.html" width="100%" height="600"  frameBorder="0"></iframe>');
}
function showVideos(obj) {

	$("#left-iframe").html('<iframe src="'+contentPath+'/site/media.html" width="100%" height="600"  frameBorder="0"></iframe>');
}
function showTemplates(obj) {

	$("#left-iframe").html('<iframe src="'+contentPath+'/site/templates.html" width="100%" height="600"  frameBorder="0"></iframe>');
}

function showPages(obj) {

	$("#left-iframe").html('<iframe src="'+contentPath+'/pages.html" width="100%" height="600"  frameBorder="0"></iframe>');
}

function openPdf() {
	var uids = document.getElementsByName("puid");
	var url = "";

	for(var i = 0; i<uids.length; i++) {
		if(uids[i].checked) {
			if(url =="")
				url+="?uid="+uids[i].value;
			else
				url+="&uid="+uids[i].value;
		}
			
	}
	
	if(url =="") {
    	$("#header_message").html("<section class='alert alert-warning'><h3>"+i18n("select_andthen_open")+"</h3></section>");
    	return;
	}	
	url ="viewpdf"+url;
	window.location.href=url;
}

function deleteFiles() {
	if(confirm(i18n("are_you_sure_delete")+"?")) {
		var uids = document.getElementsByName("puid");
		var url = "";
		for(var i = 0; i<uids.length; i++) {
			if(uids[i].checked) {
				if(url =="")
					url+="?uid="+uids[i].value;
				else
					url+="&uid="+uids[i].value;
			} 
			
				
		}
		if(url =="") {
			alert("æ²¡æœ‰é€‰æ‹©æ–‡ä»¶ï¼�");
			return ;
		}
	    $.ajax({
		    url: 'deleteassets.html'+url,
		    type: "POST", 
		    success: function(msg) {
		    	if(!msg.indexOf("error:")>=0)
		    		window.location.reload();
		    	else
		    		alert(msg);
		    },
		    error: function() {
		    	alert(i18n("fail"));
		    }
		    // ... Other options like success and etc
		});	

	}

}


function rotate(uid) {
	$("#rotate_running"+uid).removeClass("wb-inv");
	var angle = $("#rotate"+uid).val();
    $.ajax({
	    url: 'rotateImage.html',
	    data: {
		    uid: uid,
		    angle: angle
		    },
	    type: "POST", 
	    success: function(msg) {
	    	if(msg.indexOf("error:")>=0)
		    	$("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("fail")+"</h3><p>"+msg+"</p></section>");
	    	else {
	    		$("#img"+uid).attr("src","viewimage?uid="+uid+"&t="+(new Date().getTime()));
		    	$("#header_message").html("<section class='alert alert-success'><h3>"+i18n("success")+"</h3></section>");	  
	    	}
	    	$("#rotate_running"+uid).addClass("wb-inv");	
	    },
	    error: function() {
	    	$("#rotate_running"+uid).addClass("wb-inv");
	    	$("#header_message").html("<section class='alert alert-danger'><h3>"+i18n("fail")+"</h3></section>");
	    }

	});	    
}

function editImage(uid) {
	var img = $("#editImage");
	img.attr("uid",uid);
	img.html("<img alt='' src=\"file?uid="+uid+"\" class=\"img-responsive\">");
	
}
