var i18n = window.wb.i18n;
var path = "";
var firstModified=0;
var lastModified= 0;
var unreadCount = 0;
var contentPath="";
var comment="";
var files;
var username=$("#username").val();
if(window.location.pathname.indexOf("/smarti-cloud")>=0) {
	contentPath = "/smarti-cloud";
}
if($("#pagePath")) {
	path = $("#pagePath").val();
}
tinymce.init({
	  selector: 'div.online_editor',
	  language: 'zh_CN.GB2312',
	  content_css: contentPath+'/resources/wet-boew/css/wet-boew.min.css'+','+ contentPath+'/resources/css/tiny_mce_editor.css',
	  inline: true,
	  plugins: ['link image paste  media emoticons'],
	  toolbar: 'undo redo styleselect link image media emoticons',
	  menubar: false,
      image_class_list:  [{title: 'Image Responsive', value: 'img-responsive'},
					      {title: 'Image Thumbnail', value: 'thumbnail'},
					      {title: 'Image Circle', value: 'img-circle'},
					      {title: 'Image Rounded', value: 'img-rounded'},
					      {title: 'Image Thumbnail Responsive', value: 'img-responsive thumbnail'},
					      {title: 'Pull left', value: 'pull-left img-responsive'},
					      {title: 'Pull right', value: 'pull-right img-responsive'},
					      {title: 'None', value: ''}],
      image_default_attributes: {
          class: 'img-responsive'
      },	  
      file_browser_callback : function(field_name, url, type, win) {
    	  fileBrowserCallBack(field_name, url, type, win);
    	  //win.document.getElementById(field_name).value = 'my browser value';
      }
	});


//  
//function allowDrop(ev) {
//    ev.preventDefault();
//}
//
//function drag(ev) {
//    ev.dataTransfer.setData("text", ev.target.id);
//}


function updateNode() {
	var id = this.getAttribute("id");
	var uid = this.getAttribute("uid");
	var path = this.getAttribute("path");
	var name = this.getAttribute("name");
	var type = this.getAttribute("type");
	var value = (type=='checkbox'?$(this).is(":checked"):this.value);
	if(uid) {
    $.ajax({
	    url: contentPath+'/site/updateProperty.html',
	    data: {
		    uid: uid,
		    path: path,
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
			}else  if(msg.indexOf("error:")>=0){
		    	document.getElementById(id).classList.add("alert-warning");
			}else {
				document.getElementById(id).classList.add("alert-success");
				}
	    },
	    error: function() {
	    	document.getElementById(id).classList.add("alert-warning");
	    }
	    // ... Other options like success and etc
	});	 
	} else {
		alert("uid is missing");
    	
    }
}

//document.addEventListener("dragenter", function(event) {
//    if (event.target.className && event.target.className.indexOf("component")>=0 ) {
//        event.target.style.border = "3px dotted red";
//       
//    }
//});
//
//// By default, data/elements cannot be dropped in other elements. To allow a drop, we must prevent the default handling of the element
//document.addEventListener("dragover", function(event) {
//    event.preventDefault();
//});
//
//document.addEventListener("DOMContentLoaded", init, false);
//
//function init() {
//
//	var form_control = document.querySelectorAll('.form-editable');
//
//	for(var i=0; i<form_control.length;i++) {
//		form_control[i].addEventListener('change',updateProperty);
//		}
//}
//
//
//// When the draggable p element leaves the droptarget, reset the DIVS's border style
//document.addEventListener("dragleave", function(event) {
//	if (event.target.className && event.target.className.indexOf("component")>=0 ) {
//        event.target.style.border = "";
//	}
//});
//
//window.addEventListener("dragover",function(e){
//	  e = e || event;
//	  e.preventDefault();
//	},false);
//	window.addEventListener("drop",function(e){
//	  e = e || event;
//	  e.preventDefault();
//	},false);

function fileBrowserCallBack(field_name, url, type, win) {

	
	 //alert("Field_Name: " + field_name + "nURL: " + url + "nType: " + type + "nWin: " + win); // debug/testing

    /* If you work with sessions in PHP and your client doesn't accept cookies you might need to carry
       the session name and session ID in the request string (can look like this: "?PHPSESSID=88p0n70s9dsknra96qhuk6etm5").
       These lines of code extract the necessary parameters and add them back to the filebrowser URL again. */

    /* Here goes the URL to your server-side script which manages all file browser things. */
    var cmsURL = contentPath+"/site/"+type+".html";//window.location.pathname;     // your URL could look like "/scripts/my_file_browser.php"
    var searchString = window.location.search; // possible parameters
    if (searchString.length < 1) {
        // add "?" to the URL to include parameters (in other words: create a search string because there wasn't one before)
        searchString = "?";
    }else {
    	searchString ="?";//remove searchString +="&"
        }

    // newer writing style of the TinyMCE developers for tinyMCE.openWindow

    tinyMCE.activeEditor.windowManager.open({
        file : cmsURL + searchString.trim() + "type=" + type+"&input="+field_name+"&cachebuster=123", // PHP session ID is now included if there is one at all
        title : "\u6587\u4ef6\u6d4f\u89c8",
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

function sendChat(url) {
	$("#chat_message").html("<section class='alert alert-info'><h5>"+i18n("sending")+"</h5><section>");
	$("#online_chat_running").removeClass("wb-inv");
	$("#online_chat_send").attr("disabled",true);
	var editor = tinymce.get("online_chat_editor");

	if(editor) {
		editor.dom.removeClass("div","wb-data-ajax-replace-inited");
		editor.dom.removeClass("div","wb-init");
		editor.dom.removeClass("div","wb-tabs-inited");
		editor.dom.removeClass("a","wb-lbx-inited");
		editor.dom.removeClass("a","wb-init");
		editor.dom.removeClass("img","img-responsive");
		editor.dom.addClass("img","img-responsive");
	    $.ajax({
		    url: contentPath+'/protected/addyouchat.html',
		    type: "POST",
		    data: {
		    	path : url,
		    	content: editor.getContent()
		    },
		    success: function(msg) {
		    	if(msg.indexOf("error:")>=0) {
		    		$("#chat_message").html("<section class='alert alert-danger'><h5>"+i18n("fail")+"</h5><p>"+msg+"</p><section>");
		    	}else {
			    	$("#chat_message").html("<section class='alert alert-success'><h5>"+i18n("success")+"</h5><section>");
		    		
		    	}
	
		    	path = msg;
		    	syncChat();
		    	checkUnread();
		    	tinyMCE.activeEditor.setContent("<p></p>");
		    	$("#online_chat_running").addClass("wb-inv");
		    },
		    error: function() {
		    	window.location.href=contentPath+"/login?redirect="+window.location.href
		    	//$("#online_chat").html('<section class="alert alert-warning"><h5>\u4F60\u6CA1\u6709\u767B\u5165\uFF01</53></section>');
		    }
		});	
	    
	    
	} else {
		$("#chat_message").html("<section class='alert alert-danger'><h5>ç¼–è¾‘å™¨æ²¡æ‰¾åˆ°</h5><section>");
	}
	
	$("#selectedFiles").html("");

}

function sendComment(url,status) {

	var editor = tinymce.get("online_comment_editor");
	var new_comment = editor.getContent();
	if(new_comment=='') {
		$("#comment_message").html("<section class='alert alert-info'><h5>"+i18n("no_comment")+".</h5><section>");
		return;
	}
	if(comment==new_comment) {
		$("#comment_message").html("<section class='alert alert-info'><h5>"+i18n("comment_sent")+".</h5><section>");
		return;
	}
	comment = new_comment;
	$("#comment_message").html("<section class='alert alert-info'><h5>"+i18n("sending")+"</h5><section>");
	$("#online_comment_running"+status).removeClass("wb-inv");
	$("#online_comment_send").attr("disabled",true);
	var editor = tinymce.get("online_comment_editor");
	if(editor) {
    $.ajax({
	    url: contentPath+'/protected/addcomment.html',
	    type: "POST",
	    data: {
	    	path : url,
	    	status: status,
	    	content: comment
	    },
	    success: function(msg) {

	    	
	    	if(msg.indexOf("error:")>=0) {
	    		$("#comment_message").html("<section class='alert alert-danger'><h5>"+i18n("fail")+"</h5><p>"+msg+"</p><section>");
	    	}else {
		    	$("#comment_message").html("<section class='alert alert-success'><h5>"+i18n("success")+"</h5><section>");
	    		
	    	}

	    	path = msg;
	    	//syncChat();
	    	tinyMCE.activeEditor.setContent("<p></p>");
	    	$("#online_comment_running"+status).addClass("wb-inv");
	    },
	    error: function() {
	    	$("#online_comment_running"+status).addClass("wb-inv");
	    	window.location.href=contentPath+"/login?redirect="+window.location.href
	    	//$("#online_chat").html('<section class="alert alert-warning"><h5>\u4F60\u6CA1\u6709\u767B\u5165\uFF01</53></section>');
	    }
	});	
	} else {
		$("#comment_message").html("<section class='alert alert-danger'><h5>"+i18n("fail")+", no editor</h5><section>");
	}
	setTimeout(function() {
		$("#online_comment_send").attr("disabled",false);	    	
	},30000); 
}

function djchat(path) {
	
	$( ".wb-overlay" ).trigger( "wb-init.wb-overlay" );
	$( ".wb-overlay" ).trigger( "open.wb-overlay" );
}

function syncChat() {
	$("#online_chat_running").removeClass("wb-inv");
	$("#online_chat_send").attr("disabled",true);	
	path = $("#pagePath").val();
	username = $("#username").val();
	var userrole = $("#userrole").val();
    //alert(new Date(lastModified).toISOString());
    $.ajax({
	    url: contentPath+'/protected/chat.json',
	    data: {
		    path: path,
		    lastModified: new Date(lastModified).toISOString()
		    },
	    type: "GET",
	    contentType: "application/json",
	    timeout: 50000,
	    success: function(data) {
	    	setTimeout(function() {
	    		$("#online_chat_send").attr("disabled",false);	    	
	    	},1000); 
	    	$.each(data.items,function(i,c){
		    	if(c.lastModified>lastModified || firstModified ==0) {
				    var html = 	"";
				    var cDate = new Date(c.lastModified);
				    if(c.createdBy==username) {
					    html = '<div id="'+c.uid+'" class="panel panel-default"><header class="panel-heading">';
						if(userrole=="Administrator") {
							html +='<h5 class="panel-title">'+c.title+" ("+c.path+') <span class="small text-left">'+cDate.toLocaleString()+'</span><a href="javascript:removeTag('+"'"+c.uid+"'"+')"><button title="\u70B9\u51FB\u5220\u9664" class="btn btn-warning btn-xs pull-right"><span class="glyphicon glyphicon-trash"></span></button></a></h5>';
						}else {
							html +='<h5 class="panel-title">'+c.title+" ("+c.createdBy+') <span class="small text-left">'+cDate.toLocaleString()+'</span><a href="javascript:removeTag('+"'"+c.uid+"'"+')"><button title="\u70B9\u51FB\u5220\u9664" class="btn btn-warning btn-xs pull-right"><span class="glyphicon glyphicon-trash"></span></button></a></h5>';
						}
						html +='</header><div class="panel-body"><section class="media"><img class=\"media-object pull-right\" src=\"'+c.icon+"\"><div class=\"media-body\">"+c.content+'</div></section></div></div></div><div class="clearfix"></div>';

				    }else {
						if(userrole=="Administrator") {
						    html = '<div id="'+c.uid+'" class="panel panel-success"><header class="panel-heading">';
							html +='<h5 class="panel-title">'+c.title+" ("+c.path+') <span class="small">'+cDate.toLocaleString()+'</span>';
							html +='<a href="javascript:removeTag('+"'"+c.uid+"'"+')"><button title="\u70B9\u51FB\u5220\u9664" class="btn btn-warning btn-xs pull-right"><span class="glyphicon glyphicon-trash"></span></button></a>';
							
						}else {
						    html = '<div id="'+c.uid+'" class="panel panel-success"><header class="panel-heading">';
							html +='<h5 class="panel-title">'+c.title+" ("+c.createdBy+') <span class="small">'+cDate.toLocaleString()+'</span>';
							
						}
						html +='</h5>';
						html +='</header><div class="panel-body"><section class="media"><img class=\"media-object pull-left\" src=\"'+c.icon+"\"><div class=\"media-body\">"+c.content+'</div></section></div></div><div class="clearfix"></div>';
						
					}
					$("#online_chat").append(html);
					if(firstModified ==0) {
						firstModified = c.lastModified;
						lastModified = c.lastModified;
					}
					if(firstModified > c.lastModified) {
						firstModified = c.lastModified;
					}
					if(lastModified < c.lastModified) {
						lastModified = c.lastModified;	
					}

						
		    	}
/*		    	else {
		    		alert("last="+new Date(c.lastModified).toISOString()+"="+new Date(lastModified).toISOString());		    		
		    	}*/

			});
		    if(data.pageCount>0) {
		    	setTimeout(syncChat,10000);
		        //checkUnread();		    	
		    }else {
		    	setTimeout(syncChat,30000);
		    }
		    if($("#chat").attr("open")) {
		    	$("#online_notice").html("");	
		    }else if(data.pageCount>0) {
		    	unreadCount += data.pageCount; 
		    	$("#online_notice").html(""+unreadCount);
		    }
		    $("#online_chat_running").addClass("wb-inv");
		},
		error: function() {
		    $("#online_chat_running").addClass("wb-inv");
	    	$("#comment_message").html('<section class="alert alert-warning"><h5>Timeout</h5></section>');
	    	setTimeout(syncChat,30000);
	    }

	});	 

}

function checkUnread() {

	var count=0;
	if( path !=null && path.indexOf('/chat')==0)
    $.ajax({
	    url: '/protected/unreadchat.json',
	    data: {
		    path: path,
		    lastModified: new Date().getTime()
		    },
	    type: "GET",
	    contentType: "application/json",
	    timeout: 50000,
	    success: function(data) {
	    	$.each(data.items,function(i,f){
	    		if(f.childCount>0) {
	    			$("#unread-"+f.uid).html(""+f.childCount);
	    		}else {
	    			$("#unread-"+f.uid).html("");
	    		}
	    		count++;
	    	});
	    	if(count > 3)
	    		setTimeout(checkUnread,3000);
	    	else if(count > 0)
	    		setTimeout(checkUnread,5000);
	    	else
	    		setTimeout(checkUnread,20000);

		},
		error: function() {
	    	setTimeout(checkUnread,60000);
		    $("#online_chat_running").addClass("wb-inv");
	    	$("#comment_message").html('<section class="alert alert-warning"><h5>Timeout</h5></section>');
	    }

	});	 	
}

function fswebcam(view) {
    //$("#online_chat_running").removeClass("wb-inv");
	//$("#online_chat_send").attr("disabled",true);
	//$("#fswebcam").attr("disabled",true);
	//var left_float = document.getElementById("video-iframe");
	$("#video-iframe").html("<img class=\"img-responsive\" src=\""+view+"/?action=snapshot\" alt=\"\">");
/*	if(left_float.getAttribute("style")=="height: 300px; border: 0px none; width: 400px; margin-bottom: 0px; margin-left: 0px;") {
    $.ajax({
	    url: '/protected/video.html?action=close',
	    type: "GET",
	    contentType: "text/html",
	    timeout: 30000,
	    success: function(data) {
	        $.ajax({
	    	    url: '/protected/webcam.json',
	    	    type: "GET",
	    	    contentType: "application/json",
	    	    timeout: 30000,
	    	    success: function(data) {
	    	    	
	    	    	var html="";
	        		html +='<a class="wb-lbx-edit" href="'+data.link+'" target="_BLANK"><img id="img'+data.uid+'" src="'+data.icon+'" class="img-responsive" draggable="true"></img></a>';
	    	    	document.getElementById("online_chat_editor").focus();
	    	    	tinyMCE.activeEditor.selection.select(tinyMCE.activeEditor.getBody(), true);
	    	    	tinyMCE.activeEditor.selection.collapse(false);
	    	    	tinyMCE.activeEditor.selection.setContent(html);
	    	    	tinyMCE.activeEditor.setDirty(true);
	    	        $("#online_chat_running").addClass("wb-inv");
	    	    	$("#online_chat_send").attr("disabled",false);
	    	    	$("#fswebcam").attr("disabled",false);
	    	        $.ajax({
	    	    	    url: '/protected/video.html?action=open&width=300',
	    	    	    type: "GET",
	    	    	    contentType: "text/html",
	    	    	    timeout: 30000,
	    	    	    success: function(data) {
	    	    		},
	    	    		error: function() {
	    	    	    }

	    	    	});		    	    	
	    	    	
	    		},
	    		error: function() {
	    		    $("#online_chat_running").addClass("wb-inv");
	    			$("#online_chat_send").attr("disabled",false);
	    			$("#fswebcam").attr("disabled",false);
	    	    	$("#comment_message").html('<section class="alert alert-warning"><h5>Timeout</h5></section>');
	    	    }

	    	});		    	
		},
		error: function() {
			$("#fswebcam").attr("disabled",false);
	    }

	});			
	}else {
        $.ajax({
    	    url: '/protected/webcam.json',
    	    type: "GET",
    	    contentType: "application/json",
    	    timeout: 30000,
    	    success: function(data) {
    	    	
    	    	var html="";
        		html +='<a class="wb-lbx-edit" href="'+data.link+'" target="_BLANK"><img id="img'+data.uid+'" src="'+data.icon+'" class="img-responsive" draggable="true"></img></a>';
    	    	document.getElementById("online_chat_editor").focus();
    	    	tinyMCE.activeEditor.selection.select(tinyMCE.activeEditor.getBody(), true);
    	    	tinyMCE.activeEditor.selection.collapse(false);
    	    	tinyMCE.activeEditor.selection.setContent(html);
    	    	tinyMCE.activeEditor.setDirty(true);
    	        $("#online_chat_running").addClass("wb-inv");
    	    	$("#online_chat_send").attr("disabled",false);
    	    	$("#fswebcam").attr("disabled",false);
    	        $.ajax({
    	    	    url: '/protected/video.html?action=open&width=300',
    	    	    type: "GET",
    	    	    contentType: "text/html",
    	    	    timeout: 30000,
    	    	    success: function(data) {
    	    		},
    	    		error: function() {
    	    	    }

    	    	});		    	    	
    	    	
    		},
    		error: function() {
    		    $("#online_chat_running").addClass("wb-inv");
    			$("#online_chat_send").attr("disabled",false);
    			$("#fswebcam").attr("disabled",false);
    	    	$("#comment_message").html('<section class="alert alert-warning"><h5>Timeout</h5></section>');
    	    }

    	});				
	}*/

}

function webvideo(view,width) {
	$("#video-iframe").html("");
	    $.ajax({
		    url: '/protected/video.html?action=open&width='+width,
		    type: "GET",
		    contentType: "text/html",
		    timeout: 30000,
		    success: function(data) {
			},
			error: function() {
		    }

		});		

		$("#video-iframe").html("<img class=\"img-responsive\" src=\""+view+"/?action=stream\" alt=\"\">");

}

function stopvideo() {
	
	$("#video-iframe").html("");	
    $.ajax({
	    url: '/protected/video.html?action=close',
	    type: "GET",
	    contentType: "text/html",
	    timeout: 30000,
	    success: function(data) {
	    	$("#video-iframe").html("");
		},
		error: function() {
	    }

	});		
}

function addUser(group,path) {
    $.ajax({
	    url: '/protected/adduser.html',
	    data: {
		    path: path,
		    group: group
		    },
	    type: "GET",
	    contentType: "application/json",
	    timeout: 5000,
	    success: function(data) {

	    	if(data.title.indexOf("error:")>=0 || data.title.indexOf("warning:")>=0) {
	    		alert(data.title);
	    	}else {
			    var html = 	'<div id="'+data.uid+'" class="col-md-3">';
			    	html += '<a href="javascript:removeUser(\''+data.uid+'\',\''+data.path+'\')" title="退群"><img class="img-responsive" src="file/icon.jpg?path=/'+data.userName+'/assets/icon/x120.jpg" alt="删除"></a>';
			    	html += '<p>'+data.title+' ('+data.userName+')';
			    	html += '</div>';
				$("#inGroup").append(html);	    		
	    	}	


	    
	    }
    });
    }

function removeUser(uid,path) {

    $.ajax({
	    url: '/protected/removeuser.html',
	    data: {
		    path: path
		    },
	    type: "GET",
	    contentType: "application/json",
	    timeout: 5000,
	    success: function(data) {

	    	if(data.title.indexOf("error:")>=0 || data.title.indexOf("warning:")>=0) {
	    		alert(data.title);
	    	}else {
	    		$("#"+uid).remove();
	    	}	


	    
	    }
    });
    }
function openOverlay(id,view) {
	$("#"+id).focus();
	$("#"+view).trigger("open.wb-overlay");
}


if($("#pagePath")) {
	if( path !=null && path.indexOf('/chat')==0) {
		syncChat();
		checkUnread();
	}


}



function errorException(jqXHR, exception) {
    busy = 0;
    if (jqXHR.status === 0) {
    	$("#header_message").html('<section class="alert alert-warning"><h3>'+i18n("fail")+'</h3></section>');
    } else if (jqXHR.status == 404) {
    	//$("#pageinfo").html('Requested page not found. [404]');
        alert('Requested page not found. [404]');
    } else if (jqXHR.status == 500) {
    	//$("#pageinfo").html('Internal Server Error [500].');
        alert('Internal Server Error [500].');
    } else if (exception === 'parsererror') {
    	//$("#pageinfo").html('Requested JSON parse failed.');
        alert('Requested JSON parse failed.');
    } else if (exception === 'timeout') {
    	//$("#pageinfo").html('Time out error.');
        alert('Time out error.');
    } else if (exception === 'abort') {
    	//$("#pageinfo").html('Ajax request aborted.');
        alert('Ajax request aborted.');
    } else {
    	//$("#pageinfo").html('Uncaught Error.\n' + jqXHR.responseText);
        alert('Uncaught Error.\n' + jqXHR.responseText);
    }

}