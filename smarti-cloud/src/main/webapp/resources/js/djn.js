var files=[];
var droppedFiles = [];
var dataTable = [];
var total = 0;
var i = 0;
var $element = $( ".wb-sessto" );
var settings = $element.data( "wb-sessto" );
if(window.location.pathname.indexOf("/smarti-cloud")>=0) {
	contentPath = "/smarti-cloud";
}
var path = "";
var type = "image";
//var win = (!window.frameElement && window.dialogArguments) || opener || parent || top;
var selDiv = "", topInsert="", viewInsert=null;
//var form_upload = document.getElementById("form-upload");
/*var tinyMCE;
var tinymce = tinyMCE = win.tinymce;*/
var input = "";
document.addEventListener("DOMContentLoaded", init, false);
var i18n = window.wb.i18n;
var confirmDate =0;
var count = 0;
$(window).on('load',function() {
	var menu = document.getElementById("wb-lng");
	if(menu) {
		menu.classList.remove("wb-inv");
/*		setTimeout(function() {
			menu.classList.add("wb-inv");
		},1200000);*/
	}
	var buttons = document.querySelectorAll("button[disabled]");
	for(var i=0; i<buttons.length;i++) {
		buttons[i].disabled = false;
	}
	//buttons.forEach(function(b) {b.disabled=false});
		

	
});
var p=$("#pageNumber").val();;
var _throttleTimer = null;
var _throttleDelay = 100;
var $window = $(window);
var $document = $(document);

$document.ready(function () {
    $window
        .off('scroll', ScrollHandler)
        .on('scroll', ScrollHandler);

});

function ScrollHandler(e) {
    //throttle event:
    clearTimeout(_throttleTimer);
    _throttleTimer = setTimeout(function () {
        //console.log('scroll');

        //do work
        if ($window.scrollTop() + $window.height() > $document.height() - 100) {
        	avalaiblePages = $("#availablePages").val();
        	type = $("#type").val();
        	kw=$("#kw").val();
        	var topage="browsemore";
        	//var pageNumber = $("#pageNumber").val();
        	if($("#topage")) topage = $("#topage").val();
        	path=$("#folderpath").val();

        	if(p < avalaiblePages && $("#topage")) {
                p ++;
                
                //alert("browsemore.html?path="+path+"&type="+type+"&kw="+kw+"&p="+p+"&topage="+topage);
                $("#loading").html("<img src=\"/resources/images/loadingx400.gif\" width=\"48\" height=\"48\" alt=\"\">");
                //alert("near bottom!"+"browsemore.html?path="+path+"&input="+input+"&kw="+kw+"&p="+p);
                $.ajax ({
    			    url: "browsemore.html?path="+path+"&type="+type+"&kw="+kw+"&p="+p+"&topage="+topage,
    			    type: "GET", 
    			    contentType: "text/html",
    			    //processData: false,
    			    success: function(data) {
    			    	$("#contentmore").append(data);
    	                $("#loading").html("");
    			    },
    			    error: function() {
    	                $("#loading").html("");
    				    //alert("出错：");

    			    }
    			    // ... Other options like success and etc
    			}); 	    
                
            }

        }else if($window.scrollTop() ==0) {
        	if(firstModified != 'undefined' && firstModified>0) {
        		var path = $("#pagePath").val();
        		username = $("#username").val();
        		var userrole = $("#userrole").val();
        		$("#online_chat_loading").removeClass("wb-inv");
        	    $.ajax({
        		    url: '/protected/chat.json',
        		    data: {
        			    path: path,
        			    operator : "<",
        			    lastModified: new Date(firstModified).toISOString()
        			    },
        		    type: "GET",
        		    contentType: "application/json",
        		    success: function(data) {
    	        		$("#online_chat_loading").addClass("wb-inv");
    	        		var count=0;
        		    	$.each(data.items,function(i,c){
        			    	//if(c.lastModified<firstModified) {
        					    var html = 	"";
        					    var cDate = new Date(c.lastModified);
        					    count++;
        					    if(c.createdBy==username) {
        						    html = '<div id="'+c.uid+'" class="panel panel-default"><header class="panel-heading">';
        							if(userrole=="Administrator") {
        								html +='<h5 class="panel-title">'+c.title+" ("+c.path+') <span class="small text-left">'+cDate.toLocaleString()+'</span><a href="javascript:removeTag('+"'"+c.uid+"'"+')"><button title="\u70B9\u51FB\u5220\u9664" class="btn btn-warning btn-xs pull-right"><span class="glyphicon glyphicon-trash"></span></button></a><span id="timer'+c.uid+'" class="badge pull-right"></span></h5>';
        							}else {
        								html +='<h5 class="panel-title">'+c.title+" ("+c.createdBy+') <span class="small text-left">'+cDate.toLocaleString()+'</span><a href="javascript:removeTag('+"'"+c.uid+"'"+')"><button title="\u70B9\u51FB\u5220\u9664" class="btn btn-warning btn-xs pull-right"><span class="glyphicon glyphicon-trash"></span></button></a><span id="timer'+c.uid+'" class="badge pull-right"></span></h5>';
        							}
        							html +='</header><div class="panel-body"><section class="media"><img class=\"media-object pull-right\" src=\"'+c.icon+"\"><div class=\"media-body\">"+c.content+'</div></section></div></div><div class="clearfix"></div>';

        					    }else {
        							if(userrole=="Administrator") {
        							    html = '<div id="'+c.uid+'" class="panel panel-success"><header class="panel-heading">';
        								html +='<h5 class="panel-title">'+c.title+" ("+c.path+') <span class="small">'+cDate.toLocaleString()+'</span>';
        								html +='<a href="javascript:removeTag('+"'"+c.uid+"'"+')"><button title="\u70B9\u51FB\u5220\u9664" class="btn btn-warning btn-xs pull-right"><span class="glyphicon glyphicon-trash"></span></button></a>';
        								
        							}else {
        							    html = '<div id="'+c.uid+'" class="panel panel-success"><header class="panel-heading">';
        								html +='<h5 class="panel-title">'+c.title+" ("+c.createdBy+') <span class="small">'+cDate.toLocaleString()+'</span>';
        								
        							}        							
        							html +='<span id="timer'+c.uid+'" class="badge pull-right"></span></h5>';
        							html +='</header><div class="panel-body"><section class="media"><img class=\"media-object pull-left\" src=\"'+c.icon+"\"><div class=\"media-body\">"+c.content+'</div></section></div></div><div class="clearfix"></div>';
        							
        						}
        						$("#online_chat").before(html);
        						if(firstModified > c.lastModified || firstModified ==0) {
        							firstModified = c.lastModified;
        						}
        						if(c.timer>0) {
        							var timer = c.timer*1000;
        							var id = c.uid;
        							deleteChat(id,timer);
/*        							setTimeout((function(id){
        								deleteTag(id);
        							})(id),timer);*/
        							
        						} 	
        			    	//}
        			    	/*else {
        			    		alert("last="+new Date(c.lastModified).toISOString()+"="+new Date(lastModified).toISOString());		    		
        			    	}*/

        				});
        		    	if(count==0) firstModified = -1;;

        			},
        			error: function() {
        			    $("#online_chat_loading").addClass("wb-inv");
         		    }

        		});	         		
        	}
        		
        }

    }, _throttleDelay);
}

function deleteChat(uid,timer) {
	  var elem = document.getElementById("timer"+uid);
	  var count = timer;
	  var id = uid;
	  var idVar = setInterval(countDown, 1000);
	  function countDown() {
	    if (count == 0) {
	      clearInterval(idVar);
	      deleteTag(id);
	    } else {
	      count-=1000;
	      elem.innerHTML = ""+count/1000;
	    }
	  }
}
function AssetTable(folder,asset) {
	this._asset = asset;
	this.lastModified = asset.lastModified;
	this.folder = folder,
	this.location = asset.location;
	this.description = assets.description;
	
	this.asset = function() {
		return "<a href=\""+asset.icon+"\"><img alt=\"\" class=\"img-responsive img-rounded pull-left\" src=\""+asset.iconSmall+"\">"+asset.title+"</a>";
	};
};

function pushDataTabe(folder) {
	$.each(data.assets,function(i,a){
		dataTable.push(new AssetTable(folder.titile,a));
	});			
	$.each(data.subfolders,function(i,f){
		pushDataTable(f);
	});	
}

function getDataTable() {
    $.ajax({
	    url: '/site/getfolder.json',
	    data: {
		    path: $("#folderpath").val(),
		    type: $("#type").val()
		    },
	    type: "GET",
	    contentType: "application/json",
	    success: function(data) {
	    	pushDataTable(data);
		},
		error: function() {

		    }

	});		
	
}

/*function pw(s) {
	var v=$("#j_password").val();
	if(v=="") {
		count=0;
		$("#count").val("0");
	}

	count = $("#count").val();
	if(count==0) {
		$("#j_password").val(s);		
	}else {
		$("#j_password").val(v+s);
	}
	count++;
	$("#count").val(count);
	if(count==4) {
		$("#submit").click();
		}
}*/
var j_password = document.querySelector('#j_password');
if(j_password)
	j_password.addEventListener("focus",function(e) {
	j_password.value="";
	$("#details_password").removeAttr("open");
});

var security = document.getElementsByClassName('security');

for(var i=0; i<security.length;i++) {
	security[i].addEventListener("click",function(e) {
		var v=$("#j_password").val();
		if(v=="") {
			count=0;
			$("#count").val("0");
		}

		count = $("#count").val();
		if(count==0) {
			$("#j_password").val(this.id);		
		}else {
			$("#j_password").val(v+this.id);
		}
		count++;
		$("#count").val(count);
		if(count==4) {
			$("#submit").click();
			}		
	});
}

var password = document.getElementsByClassName('password');

for(var i=0; i<password.length;i++) {
	password[i].addEventListener("click",function(e) {
		var v=$("#password").val();
		if(v=="") {
			count=0;
			$("#count").val("0");
		}
		count = $("#count").val();
		if(count==0) {
			$("#password").val(this.id);		
		}else {
			$("#password").val(v+this.id);
		}	

		count++;
		$("#count").val(count);
		if(count==4) {
			$("#details_pass").removeAttr("open");
			$("#details_confirm").attr("open","open");
			}	
		
	});
}
var conform = document.getElementsByClassName('confirm');
for(var i=0; i<conform.length;i++) {
	conform[i].addEventListener("click",function(e) {
		var v=$("#passwordconfirm").val();

		count = $("#count").val();
		if(count==4) {
			$("#passwordconfirm").val(this.id);		
		}else {
			$("#passwordconfirm").val(v+this.id);
		}	

		count++;
		$("#count").val(count);
		if(count==8) {
			$("#submit").click();
			}
	});
}
function init() {
	var fileUpload = document.querySelector('#fileUpload');
	if(fileUpload != null) {
		fileUpload.addEventListener('change', handleFileSelect, false);	
	}
	var iconUpload = document.querySelector('#iconUpload');
	if(iconUpload != null) {
		iconUpload.addEventListener('change', handleIconSelect, false);	
	}
	var form_control = document.querySelectorAll('.form-editable');
	for(var i=0; i<form_control.length;i++) {
		form_control[i].addEventListener('change',updateThis);
		}
	selDiv = document.querySelector("#selectedFiles");
	topInsert = document.querySelector("#top_insert");
	viewInsert = document.querySelector("#view_insert");
	path_input= document.getElementById("path");

	var form = document.getElementById("assets");

	if(path_input) {
		path_input.addEventListener("change", function() {
			form.submit();
			
			});	

		path = path_input.getAttribute("value");

	}

}
function updateProperty(obj) {
	var id = obj.getAttribute("id");
	var uid = obj.getAttribute("uid");
	var path = obj.getAttribute("path");
	var name = obj.getAttribute("name");
	var type = obj.getAttribute("type");
	var value = (type=='checkbox'?$(obj).is(":checked"):obj.value);

    $.ajax({
	    url: '/site/updateProperty.html',
	    data: {
		    uid: uid,
		    name: name,
		    path: path,
		    value: value
		    },
	    type: "POST", 
	    success: function(msg) {
		    if(type=="checkbox") {
		    	document.getElementById(id).parentNode.classList.add("alert-success");
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
}

function updateNode(obj) {
	var id = obj.getAttribute("id");
	var uid = obj.getAttribute("uid");
	var path = obj.getAttribute("path");
	var name = obj.getAttribute("name");
	var type = obj.getAttribute("type");
	var value = (type=='checkbox'?$(obj).is(":checked"):obj.value);

    $.ajax({
	    url: '/site/updateNodeProperty.html',
	    data: {
		    uid: uid,
		    name: name,
		    path: path,
		    value: value
		    },
	    type: "POST", 
	    success: function(msg) {
		    if(type=="checkbox") {
		    	document.getElementById(id).parentNode.classList.add("alert-success");
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
}

function updateThis() {
	var id = this.getAttribute("id");
	var uid = this.getAttribute("uid");
	var path = this.getAttribute("path");
	var name = this.getAttribute("name");
	var type = this.getAttribute("type");
	var value = (type=='checkbox'?$(this).is(":checked"):this.value);

    $.ajax({
	    url: '/site/updateProperty.html',
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
/*		    	if(value=="true" || value==true)
		    		document.getElementById("link"+uid).classList.add("wb-inv");
		    	else
		    		document.getElementById("link"+uid).classList.remove("wb-inv");*/
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
}

function openIcon() {
	selDiv = document.querySelector("#selectedIcon");
	iconUpload = document.querySelector("#iconUpload");
	iconUpload.click();
}

function openFiles() {
	selDiv = document.querySelector("#selectedFiles");
	fileUpload = document.querySelector("#fileUpload");
	fileUpload.click();
}

function resetSelDiv() {
	selDiv = document.querySelector("#selectedFiles");
	selDiv.innerHTML = "";
	total=0;
	document.getElementById("submit_upload").disabled=true;
}

function handleFileSelect(e) {
	
	if(!e.target.files) return;
	selDiv = document.querySelector("#selectedFiles");
	selDiv.innerHTML = "";

	files = e.target.files;

	for(var i=0; i<files.length; i++) {
		var f = files[i];
		
		selDiv.innerHTML += f.name + "<br/>";

	}
/*	var submit_upload = document.getElementById("submit_upload");
	if(submit_upload)
		submit_upload.disabled=false;
	else */
	if(files.length>0 && $("#path").val() !="/forget") {
		uploadFiles();
	}
		
		
	
}

function handleIconSelect(e) {
	
	if(!e.target.files) return;
	files = e.target.files;
	for(var i=0; i<files.length; i++) {
		var f = files[i];
		uploadIcon(f);
		return;
	}
	
}

function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
    ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
	clear();
	//i = 0;
    ev.preventDefault();
    var id = ev.target.id;
	ev.target.classList.remove("well");
	ev.target.style.border = "1px solid #aaaaaa";
    var data="";
    if(id !=null && id=="uploadIcon" ) {
	    droppedFiles = ev.dataTransfer.files;
		for(var i = 0; i< droppedFiles.length; i++) {	
			uploadIcon(droppedFiles[i]);
			return;
		}
    }

    if(id)
    if(id=="uploadBox" || id=="uploadImg" || id.indexOf("folder")>=0) {

    	data = ev.dataTransfer.getData("text");
    	path = $("#path").val();
    	if(id != 'uploadImg' && ev.target.getAttribute("path")) {
    		path = ev.target.getAttribute("path");
    		id = id.replace("folder","selectFiles");
    		selDiv = document.querySelector("#"+id);    			

    	}else {
    		selDiv = document.querySelector("#selectedFiles");
    	}

    	if(path===null) {
        	alert(i18n("path_not_empty"));
    		return;
    	}

    	var items = ev.dataTransfer.items;
    	var imported=[];
    	for(var k = 0; k<items.length; k++) {
    		if(items[k].kind == 'string' && items[k].type=="application/x-moz-file-promise-url") {
	    		items[k].getAsString(function (s) {
	    	    	if(s.indexOf("http")>=0 && imported[s]==null) {
	    			    imported[s] = true;   
	    	    		if(s.indexOf("viewimage?path=")>0){
	    	    			  var uid = s.split("viewimage?path=")[1];
	    	    			  uid = uid.split("&")[0];
	  	    	    		  selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("move")+"</h2><p><img alt='' src='"+contentPath+"/resources/images/ui-anim_basic_16x16.gif'/> "+s+"</p></section>";
	    	    			  $.ajax({
		    					    //url: '/site/importAssetMove.html?path='+path+"&uid="+uid,
		    					    url: '/site/movenode.html?topath='+path+"&frompath="+uid,
		    					    type: "GET", 
		    					    contentType: false,
		    					    processData: false,
		    					    success: function(data) {
		    					    	if(data.indexOf("error:")>=0)
		    						        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><details><summary>"+i18n("fail")+"</summary><p>"+data+"</p></details></section>"; // 
		    					    	else {
		    					    		$("#"+data).remove();
		    							    selDiv.innerHTML = "<section class=\"alert alert-success\"><h2 class=\"h5\">"+i18n("move")+i18n("success")+"</h2><p>"+uid+"</p></section>";
		    							    //output(data);

		    					    	}
		    					    },
		    					    error: function() {
		    					        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h3 class=\"h5\">"+i18n("fail")+"</h5></section>"; // 
		    					    }
		    					    // ... Other options like success and etc
		    					}); 

		    			      return;
	    	    			
	    	    		}else {
		    	    		selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("upload")+"</h2><p><img alt='' src='"+contentPath+"/resources/images/ui-anim_basic_16x16.gif'/> "+s+"</p></section>";
		    			      $.ajax({
		    					    url: '/site/importAsset.html?path='+path+"&url="+s,
		    					    type: "GET", 
		    					    contentType: false,
		    					    processData: false,
		    					    success: function(data) {
		    					    	if(data.title.indexOf("error:")>=0)
		    						        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2><p>"+data.title+"</p></section>"; // 
		    					    	else {
		    							    selDiv.innerHTML = "<section class=\"alert alert-success\"><h2 class=\"4\">"+i18n("success")+"</h2><p>"+data.title+"</p></section>";
		    							    output(data);

		    					    	}
		    					    },
		    					    error: function() {
		    					        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2><p></p></section>"; // 
		    					    }
		    					    // ... Other options like success and etc
		    					}); 

	    	    		}

	    	    		return;
	    	    	}
	    		
	    			
	    		});
	    	}else {
	    		if(items[k].type=="text/html") {
	    		items[k].getAsString(function (s) {
		    		 var img = $(s).find("img");
		    		 var url = null;
		    		 if(img.length >0) {
		    			 url = img.attr("src");
		    		 }else {
		    			 var href = $(s).find("a");
		    			 if(href.length > 0) {
		    				 url = img.attr("href");
		    			 }
		    		 }
			    	    	if(url !=null && url.indexOf("http")>=0 && imported[url]==null) {
			    	    		imported[url] = true;
			    	    		if(url.indexOf("viewimage?path=")>0 || url.indexOf("file/")>0){
			    	    			  var uid = url.split("?path=")[1];
			    	    			  uid = uid.split("&")[0];
			    	    			  selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("move")+"</h2><p><img alt='' src='"+contentPath+"/resources/images/ui-anim_basic_16x16.gif'/> "+url+"</p></section>";
			    			      $.ajax({
				    					    //url: '/site/importAssetMove.html?path='+path+"&uid="+uid,
				    					    url: '/site/movenode.html?topath='+path+"&frompath="+uid,
				    					    type: "GET", 
				    					    contentType: false,
				    					    processData: false,
				    					    success: function(data) {
				    					    	if(data.indexOf("error:")>=0)
				    						        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><details><summary>"+i18n("fail")+"</summary><p>"+data+"</p></details></section>"; // 
				    					    	else {
				    					    		$("#"+data).remove();
				    							    selDiv.innerHTML = "<section class=\"alert alert-success\"><h2 class=\"4\">"+i18n("move")+i18n("success")+"</h2><p>"+uid+"</p></section>";
				    							    //output(data);
				    					    	}
				    					    },
				    					    error: function() {
				    					        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h3 class=\"h5\">"+i18n("fail")+"</h3></section>"; // 
				    					    }
				    					    // ... Other options like success and etc
				    					}); 

				    			      return;			    	    			  
			    	    		}else	{
				    	    		selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("upload")+"</h2><p><img alt='' src='"+contentPath+"/resources/images/ui-anim_basic_16x16.gif'/> "+url+"</p></section>";
				    			      $.ajax({
				    					    url: '/site/importAsset.html?path='+path+"&url="+url,
				    					    type: "GET", 
				    					    contentType: false,
				    					    processData: false,
				    					    success: function(data) {
				    					    	if(data.title.indexOf("error:")>=0)
				    						        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+" </h2><p>"+data.title+"</p></section>"; // 
				    					    	else {
				    							    selDiv.innerHTML = "<section class=\"alert alert-success\"><h2 class=\"4\">"+i18n("success")+"</h2><p>"+data.title+"</p></section>";
				    							    output(data);
				    					    	}
				    					    },
				    					    error: function() {
				    					        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2></section>"; // 
				    					    }
				    					    // ... Other options like success and etc
				    					}); 
				    			    imported[url] = true;   

				    	    		return;
			    	    		}  

			    	    	}
		    		 
		    		});
	    		}
	    	}
    	}
    	//files = [];

    	var folderCreated = false;
	    droppedFiles = ev.dataTransfer.files;
		var items = ev.dataTransfer.items;
		if(topInsert)
			topInsert.innerHTML="";
		for(var n = 0; n< droppedFiles.length; n++) {
		    if(items !=null) {
			    var entry = items[n].webkitGetAsEntry();
			    if(entry.isDirectory) {
			        selDiv.innerHTML=  "<section class=\"alert alert-info\"><h3>"+i18n("create_folder")+droppedFiles[n].name+"...</h3></section>"; // 
			        $("#foldername").val(droppedFiles[n].name);
			        $("#titlefolder").val(droppedFiles[n].name);
			        //createFolder();
			        folderCreated = true;
			        total=0;
			        traverseFileTree(entry,droppedFiles[n].path);
			        //setTimeout(uploadFiles(),2000);
			        continue;
			    }
			    	
		    }

		    files[n] = 	droppedFiles[n];		
			//uploadFile(droppedFiles[i]);

		};
	    //document.getElementById("submit_upload").disabled=false;
		ev.target.classList.remove("well");
		ev.target.style.border = "1px solid #aaaaaa";
    }

    if(files!=null && files.length>0) {
    	i = 0;
    	uploadFiles();
    }else if(folderCreated==false){
    	if(data.indexOf("http")==0) {
    		selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("upload")+"</h2><p><img alt='' src='"+contentPath+"/resources/images/ui-anim_basic_16x16.gif'/> "+data+"</p></section>";
	      $.ajax({
			    url: '/site/importAsset.html?path='+path+"&url="+data,
			    type: "GET", 
			    contentType: false,
			    processData: false,
			    success: function(data) {
			    	if(data.title.indexOf("error:")>=0)
				        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+" </h2><p>"+data.title+"</p></section>"; // 
			    	else {
					    selDiv.innerHTML = "<section class=\"alert alert-success\"><h2 class=\"4\">"+i18n("success")+"</h2><p>"+data.title+"</p></section>";
					output(data);	
			    	}
			    },
			    error: function() {
			        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2></section>"; // 
			    }
			    // ... Other options like success and etc
			}); 
    	} else
	    	  selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("no-match")+" </h2><p>"+data+"</p></section>"; 
    	
    }
    percentComplete =1000;
    checkProgress();
}

function uploadFolder() {
	path = $("#path").val();
	var folderPath = $("#folderPath").val();
	var override = $("#override").is(":checked")?"true":"false";
	$.ajax ({
	    url: '/site/importFolder.html?path='+path+"&url="+folderPath+"&override="+override,
	    type: "GET", 
	    contentType: false,
	    processData: false,
	    success: function(data) {
	    	if(data.indexOf("error:")>=0) {
		        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2><p>"+data+"</p></section>";  
	    	} else {	
		        selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("upload_at_backend")+"</h2>"+data+"</section>"; 
	    	}
	    },
	    error: function() {
	        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2></section>";
	    }				    
	});
}

function uploadUrl() {
	var url = $("#uploadLink").val();
	var override = $("#override").is(":checked")?"true":"false";
	path = $("#path").val();
	var running = "<img src=\"/resources/images/ui-anim_basic_16x16.gif\">"+ url + "<br/>";
	selDiv.innerHTML = running;	
	$("#submit_upload_url").attr("disabled",true);
	$.ajax ({
	    url: '/site/importAsset.html?path='+path+"&url="+url+"&override="+override,
	    type: "GET", 
	    contentType: false,
	    processData: false,
	    timeout: 120000,
	    success: function(data) {
	    	if(data.title.indexOf("error:")>=0) {
	    		$("#submit_upload_url").removeAttr("disabled");
		        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2><p>"+data.title+"</p></section>";  
	    	} else {	
	    		$("#submit_upload_url").removeAttr("disabled");
		        selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("success")+"</h2>"+data.title+"</section>"; 
	    	}
	    },
	    error: function(xmlhttprequest, textstatus, message) {
	    	$("#submit_upload_url").removeAttr("disabled");
	       
	        if(textstatus=="timeout")
	        	selDiv.innerHTML= "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("upload_at_backend")+"</h2>"+"</section>";
	        else 
	        	selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2></section>";
	    }				    
	});
}

var percentComplete = 0;
function uploadFile(file) {
	i++;
	//var running = "<img src=\"/resources/images/ui-anim_basic_16x16.gif\">"+ file.name + " ("+i+"/"+total+")<br/>";
	//selDiv.innerHTML = running;
	var override = $("#override").is(":checked")?"true":"false";
	//var path = $("#path").val();
	//var isoName ="image_" +new Date().toISOString().slice(0,19)+".jpg";
    var id ="uploading_"+ new Date().getTime();

	var formData = new FormData();
	formData.append("path",path);
	//if(file.name == 'image.jpg' || Image.jpg) {
	//    formData.append("file", file,isdoName);
	//    formData.append("filename",isoName);		
	//}else {
	formData.append("file", file,file.name);
	formData.append("filename",file.name);		
	//}
    formData.append("lastModified",file.lastModified);
    formData.append("total",files.length);  
    formData.append("override",override);    
    var fileSize = 0;
    if("size" in file)
      fileSize = file.size;
    else
	  fileSize = file.fileSize;
  
    	
    if(fileSize > 800000000) {
        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("document_limit")+"800MB -"+fileSize+"</h2></section>"; // 
    	
	    return;
    	
    }

	var online_chat_editor = document.getElementById("online_chat_editor");
	if((online_chat_editor || override == 'false' ) && file.name !='Image.jpg' && file.name !='image.jpg') {
		getAsset(formData,file);
	}else 
		sendFormData(formData,file);
	//alert(i);
}

function sendFormData(formData,file,id) {
	var running = "<img src=\"/resources/images/ui-anim_basic_16x16.gif\">"+ file.name + " ("+i+"/"+total+")<br/>";
    if("size" in file)
        fileSize = file.size;
    else
  	  fileSize = file.fileSize;
    var start = new Date();
    var end = new Date();
    //var id = "uploading_"+file.name.replace(/[\s()\.]+/g,"_");//.replace(/\s/g, '');

      $.ajax({
    	    xhr: function() {
    	        var xhr = new window.XMLHttpRequest();

    	        // Upload progress
    	        xhr.upload.addEventListener("progress", function(evt){
    	            if (evt.lengthComputable) {
    				    end = new Date();
    				    var speed = 0;
    				    speed = evt.loaded*8/(end.getTime() - start.getTime());
    	                percentComplete = (evt.loaded / evt.total)*100;
    	                var progress = "<h5>"+running+"</h5><progress class=\"full-width\" value=\""+evt.loaded +"\" max=\""+evt.total+"\"><span class=\"wb-inv\">"+percentComplete+"%</span></progress><p>"+(speed/1000).toFixed(2)+" MB/s</p>";
    	                $("#"+id).html(progress);

    	            }
    	       }, false);

    	       // Download progress
    	       xhr.addEventListener("progress", function(evt){
    	           if (evt.lengthComputable) {
	   	                percentComplete = (evt.loaded / evt.total)*100;
    	                selDiv.innerHTML="<section id=\""+file.name+ " ("+i+"/"+total+")\"><h5>"+file.name+"</h5><progress class=\"full-width\" value=\""+evt.loaded +"\" max=\""+evt.total+"\"><span class=\"wb-inv\">"+percentComplete+"%</span></progress></section>";
    	           }
    	       }, false);

    	       return xhr;
    	    },    	  
		    url: 'uploadAsset.html',
		    data: formData,
		    type: "POST", //ADDED THIS LINE
		    // THIS MUST BE DONE FOR FILE UPLOADING
		    contentType: false,
		    processData: false,
		    enctype: 'multipart/form-data',
		    success: function(data) {
		    	percentComplete = 1000;
		    	if(data.title !=null && data.title.indexOf("error:")>=0) {
		    		var error = "<section class=\"alert alert-warning\"><h3 class=\"h5\">"+i18n("fail")+"</h3><p>"+data.title+"</p></section>";
		    		$("#"+id).html(error);
			        //selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h3 class=\"h5\">"+i18n("fail")+"</h3><p>"+data.title+"</p></section>"; // 
		    		
		    	}else {

		    		$("#"+id).remove();
		
				    var speed = 0;
				    speed = fileSize*8/(end.getTime() - start.getTime());
	                selDiv.innerHTML ="<section id=\""+file.name+ " ("+i+"/"+total+")\"><h5>"+file.name+"("+(speed/1000).toFixed(2)+" MB/s)</h5><progress class=\"full-width\" value=\""+fileSize +"\" max=\""+fileSize+"\"><span class=\"wb-inv\">"+100+"%</span></progress></section>";
		    		selDiv.innerHTML += "<section class=\"alert alert-success\"><h3 class=\"5\">"+(i)+"/"+total+i18n("document_uploaded")+i18n("success")+"</h3></section>";
		    		if(i % 100 ==0) {
		    			$("#top_insert").html("");
		    		}

		    		output(data);
/*			    	if(i==total || total==0) {
		    			setTimeout(function () {
		    				files = [];
		    				droppedFiles = [];
		    				total = 0;
		    				i = 0;
		    			},2000);

		    		}*/
		    	}
		    },
		    error: function(jqXHR, exception) {
		    	var error = "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+":"+file.name+",sttus:"+jqXHR+",exception:"+exception+"</h2></section>";
		    	$("#"+id).html(error);
		        //selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+":"+file.name+",sttus:"+jqXHR+",exception:"+exception+"</h2></section>"; // 
		    }
		    // ... Other options like success and etc
		});		
}

function getAsset(formData,file,id) {
	var running = "<img src=\"/resources/images/ui-anim_basic_16x16.gif\">"+ file.name + " ("+i+"/"+total+")<br/>";

	var filename = file.name;
	var lastModified = file.lastModified==null?0:file.lastModified;
	//var path = $("#path").val();
    var id = "uploading_"+file.name.replace(/[\s()\.]+/g,"_");
    var html = '<div id="'+id+'" class="col-md-4 well">'+running+'</div>';
	$("#top_insert").after(html);	
    $.ajax({
	    url: 'getasset.json?path='+path+'&filename='+filename+'&lastModified='+lastModified,
	    type: "GET",
	    success: function(data) {

	    	if(data.uid) {
	    		if(i % 100 ==0) {
	    			$("#top_insert").html("");
	    		}
	    		$("#"+data.uid).remove();
	    		$("#"+id).remove();
	    		//percentComplete =1000;
	    		selDiv.innerHTML = "<section class=\"alert alert-success\"><h3 class=\"5\">"+(i)+"/"+total+i18n("document_uploaded")+i18n("success")+"</h3></section>";
	    		//i++;
	    		output(data);
	    		if(i <total) {
	    			uploadFile(files[i]);	
	    			//setTimeout(checkProgress,1000);
	    		}
		    	if(i==total || total==0) {
	    			setTimeout(function () {
	    				files = [];
	    				droppedFiles = [];
	    				total = 0;
	    				i = 0;
	    			},2000);

	    		}
	    	}else {
	    		sendFormData(formData,file);
	    	}

	    },
	    error: function(jqXHR, exception) {
	        var error =  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+":"+file.name+",sttus:"+jqXHR+",exception:"+exception+"</h2></section>"; // 
	        $("#"+id).html(error);
	    }
    });
	
}

function uploadIcon(file) {
	$("#uploadIcon").attr("src","/resources/images/ui-anim_basic_16x16.gif");
	
	var formData = new FormData();
	formData.append("path",path);
	
    formData.append("file", file,file.name);
    formData.append("filename",file.name);
    var messageDiv = document.getElementById("messageDiv");
    var fileSize = 0;
    if("size" in file)
      fileSize = file.size;
    else
	  fileSize = file.fileSize;
  
    	
    if(fileSize > 10000000) {
        alert(i18n("document_limit")+"10MB -"+fileSize); // 
    	
	    return;
    	
    }

      $.ajax({
		    url: '/site/uploadIcon.html',
		    data: formData,
		    type: "POST", //ADDED THIS LINE
		    // THIS MUST BE DONE FOR FILE UPLOADING
		    contentType: false,
		    processData: false,
		    enctype: 'multipart/form-data',
		    success: function(data) {

		    	if(data.indexOf("error:")>=0) {

			        messageDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h5\">"+i18n("fail")+"</h2><p>"+data.title+"</p></section>"; // 
		    	}else {

		    		$("#uploadIcon").attr("src",data); 
		    	}
		    },
		    error: function(jqXHR, exception) {
		    	//alert(data);
		        messageDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+":"+file.name+",sttus:"+jqXHR+",exception:"+exception+"</h2></section>"; // 
		    }
		});	
}


function uploadFiles() {
	selDiv.innerHTML = "";
	// Trigger the event on the element
	$element.trigger( "reset.wb-sessto", "" );
	percentComplete = 0;
	total = files.length;
	i = 0;
	
	var submit_upload = document.getElementById("submit_upload");
	if(submit_upload)
		submit_upload.disabled=true;
	if(total > 0)
		uploadFile(files[i]);
	setTimeout(checkProgress,1000);

}

function checkProgress() {

	if(percentComplete ==1000 && i <total) {
		uploadFile(files[i]);
		setTimeout(checkProgress,1000);
	}else if(i<total){
		setTimeout(checkProgress,1000);
	}else if(i==total || total==0){
		var override = $("#override").is(":checked")?"true":"false";
		var path = $("#pagePath").val();
		$element.trigger( "reset.wb-sessto", settings );
		if(override=="true" && path.indexOf("/you")<0) {
	    	if(percentComplete ==1000 && i==total) {
	    		setTimeout(function() {window.location.reload();},2000);
	    	}else {
	    		setTimeout(checkProgress,2000);
	    	}			
		}
		setTimeout(checkProgress,1000);
	}else {
		setTimeout(checkProgress,1000);
	}
	

}

function output(data) {
	var html = "";
	var online_chat_editor = document.getElementById("online_chat_editor");
	if(viewInsert !=null) {
		outputView(data);
		return;
	}
	if(topInsert != null) 
	    html = '<div id="'+data.uid+'" class="col-md-4 well">';
    	if(data.contentType.indexOf('video/')>=0) {
    		html +='<a class="download" href="file/'+data.name+'?path='+data.path+'" target="_BLANK" download><span class="glyphicon glyphicon-download">下载</span></a>'
    			  +'<figure class="wb-mltmd">'
    			  +'<video poster="video2jpg.jpg?path='+data.path+'" title="'+data.title+'" controls="controls" preload="none">'
    			  +'<source type="video/mp4" src="video.mp4?path='+data.path+'"/>'
    			  +'</video>'
    			  +'<figcaption>'
    			  +'<p>'+data.title+'</p>'
    			  +'</figcaption>'
    			  +'</figure>';   		
    	}else if(topInsert != null) {
	    	html +='<div class="checkbox"><input type="checkbox" class="checkbox" name="puid" value="'+data.uid+'">';
		  	if(data.pdf) {
		  		 html +='<a title="打开PDF" href="viewpdf.pdf?uid='+data.uid+'" target="_BLANK"><span class="glyphicon glyphicon-open"></span> 打开PDF</a>';
		  	}
		  	html	+='	<a class="download pull-right" href="file'+data.ext+'?path='+data.path+'" target="_BLANK" download="'+data.title+'"><span class="glyphicon glyphicon-download pull-right">下载</span></a>';

	    	html +='<a href="javascript:openImage(\''+data.link+'\')"><img id="img'+data.uid+'" src="'+data.icon+'" class="img-responsive" draggable="true"></img></a>'
	    		  +'</div>'
				  +'<input class="form-control" id="description'+data.uid+'" name="jcr:description" value="'+(data.description==null?"":+data.description)+'" size="42" uid="'+data.uid+'"  onchange="updateNode(this)"/>';

    	}else {
    		html +='<a href="javascript-edit:openImage(\''+data.link+'\')"><img id="img'+data.uid+'" src="'+data.icon+'" class="img-responsive" draggable="true" alt=""></img></a>';
    	}	  
    	
    	if(topInsert != null) 	{
	    	html +='<details>'
	    		  +'<summary><span class="glyphicon glyphicon-edit"></span>'+data.title+'</summary>'
	    		  +'<div class="form-group">'
	    		  +'<label for="title'+data.uid+'">标题&nbsp;</label><input class="form-control" id="title'+data.uid+'" name="jcr:title" value="'+data.title+'" size="25" uid="'+data.uid+'"  onchange="updateNode(this)"/>'
	    		  +'</div>'
	    		  +'<div class="form-group">'
	    		  +'<label for="url'+data.uid+'">链接&nbsp;</label><input class="form-control" id="url'+data.uid+'" name="url" value="'+(data.url?data.url:'')+'" size="25" uid="'+data.uid+'"  onchange="updateNode(this)"/>'
				  +'</div>'
			      +'<div class="form-group">'
			      +'<select id="rotate'+data.uid+'" name="rotate">'
			      +'<option value="0" selected>0</option>'
				  +'<option value="90">90</option>'
				  +'<option value="-90">-90</option>'
				  +'<option value="180">180</option>'
				  +'</select>'
				  +'<a class="btn btn-default btn-sm" href="javascript:rotate('+data.uid+')">旋转<img class="wb-inv" id="rotate_running'+data.uid+'" src="/resources/images/ui-anim_basic_16x16.gif" alt="旋转"/></a>'		
				  +'</div>'		
				  +'<div class="form-group">'
				  +'<label for="contentType'+data.uid+'">类型&nbsp;</label><input class="form-control" id="contentType'+data.uid+'" name="contentType" value="'+data.contentType+'" size="24" uid="'+data.uid+'" disabled/>'
				  +'</div>'
				  +'<div class="form-group">'
				  +'<label for="size'+data.uid+'">长度&nbsp;</label><input class="form-control" id="size'+data.uid+'" name="size" value="'+data.size+"("+data.width+"x"+data.height+'）" size="24" uid="'+data.uid+'" disabled/>'
				  +'</div>'	
				  +'<div class="form-group">'
				  +'<label for="lastModified'+data.uid+'">文件日期&nbsp;</label>'
				  +getDateString(data.lastModified)
				  +'</div>'			  
				  +'</details>'
	    		  +'</div>';	
	    	$("#top_insert").after(html);		    	
	    }else if(online_chat_editor){
	    	if(i==0) {
		    	document.getElementById("online_chat_editor").focus();
		    	tinyMCE.activeEditor.selection.select(tinyMCE.activeEditor.getBody(), true);
		    	tinyMCE.activeEditor.selection.collapse(false);
	    	}
	    	var selection = tinyMCE.activeEditor.getBody();
	    	if(total > 1) {
	    		html ='<a href="'+data.link+'\"><img id="img'+data.uid+'" src="'+data.icon+'" class="img-responsive" draggable="true" alt=""></img></a>';
	    		if(i==0) {
		    		html ='<a id="link'+data.uid+'" href="'+data.link+'"><img id="img'+data.uid+'" src="'+data.icon+'" class="img-responsive" draggable="true" alt=""></img></a>';
	    			tinyMCE.activeEditor.selection.setContent("<h5><a href=\"javascript-edit:openGallery('"+data.uid+"')\">点击播放"+total+"张连环画</a></h5><div id=\"gallery"+data.uid+"\" class=\"wb-lbx-edit lbx-hide-gal\"><ul class=\"list-inline\"><li></li><ul></div>");
		    		selection = tinymce.activeEditor.dom.select('li')[0];
		    		tinymce.activeEditor.selection.select(selection);
	    		}
	    		tinyMCE.activeEditor.selection.setContent("<li>"+html+"</li>");

	    	}else {
	    		tinyMCE.activeEditor.selection.setContent(html);
	    	}

	    	tinyMCE.activeEditor.setDirty(false);
	    }else {
	    	
	    	if(i==0) {
	    		document.getElementsByClassName("caneditable")[0].focus();
		    	tinyMCE.activeEditor.selection.select(tinyMCE.activeEditor.getBody(), true);
		    	tinyMCE.activeEditor.selection.collapse(false);
	    	}
	    	var selection = tinyMCE.activeEditor.getBody();
	    	if(total > 1) {
	    		html ='<a href="'+data.link+'\"><img id="img'+data.uid+'" src="'+data.icon+'" class="img-responsive" draggable="true" alt=""></img></a>';
	    		if(i==0) {
		    		html ='<a id="link'+data.uid+'" href="'+data.link+'"><img id="img'+data.uid+'" src="'+data.icon+'" class="img-responsive" draggable="true" alt=""></img></a>';
	    			tinyMCE.activeEditor.selection.setContent("<h5><a href=\"javascript-edit:openGallery('"+data.uid+"')\">点击播放"+total+"张连环画</a></h5><div id=\"gallery"+data.uid+"\" class=\"wb-lbx-edit lbx-hide-gal\"><ul class=\"list-inline\"><li></li><ul></div>");
		    		selection = tinymce.activeEditor.dom.select('li')[0];
		    		tinymce.activeEditor.selection.select(selection);
	    		}
	    		tinyMCE.activeEditor.selection.setContent("<li>"+html+"</li>");

	    	}else {
	    		tinyMCE.activeEditor.selection.setContent(html);
	    	}

	    	tinyMCE.activeEditor.setDirty(true);	    	
	    }
	
}

function outputView(data) {
	var html = "";

	    html = '<div id="'+data.uid+'" class="col-md-4 well">';
    	if(data.contentType.indexOf('video/')>=0) {
    		html +='<a class="download" href="file/'+data.name+'?path='+data.path+'" target="_BLANK" download><span class="glyphicon glyphicon-download">下载</span></a>'
    			  +'<figure class="pull-left mrgn-md">'
    			  +'<video poster="video2jpg.jpg?path='+data.path+'" title="'+data.title+'" controls="controls" preload="none" width="150" height="100">'
    			  +'<source type="video/mp4" src="video.mp4?path='+data.path+'"/>'
    			  +'</video>'
    			  +'</figure>';   		
    	}else {
	    	html +='<a href="javascript:openImage(\''+data.link+'\')"><img id="img'+data.uid+'" src="'+data.iconSmall+'" class="img-responsive  pull-left mrgn-rght-md" draggable="true"></img></a>';
    	}
	  	if(data.pdf) {
	  		 html +='<a title="打开PDF" href="viewpdf.pdf?uid='+data.uid+'" target="_BLANK"><span class="glyphicon glyphicon-open"></span> 打开PDF</a>';
	  	}
	  	html	+='	<a class="download pull-right" href="file'+data.ext+'?path='+data.path+'" target="_BLANK" download="'+data.title+'"><span class="glyphicon glyphicon-download pull-right">下载</span></a>';

    	html +='<div><input type="checkbox" name="puid" value="'+data.uid+'"> '+data.title+'</div>';
	  	if(data.description) {
	  		html +='<p>'+data.description+'</p>';
	  	}
    	
	  	html +='<div class="clearfix"></div></div>';
    	$("#view_insert").after(html);		

}

function getDateString(datelong) {
	var d = new Date();
	d.setTime(datelong);
	return d.getFullYear()+"-"+d.getMonth()+"-"+d.getDay();
}
function clear() {
	$("#selectedFiles").html("");
	tinyMCE.activeEditor.setContent("<p></p>");
	files = [];
	droppedFiles = [];
	total = 0;
	i=0;
	
}
function deleteNode(path) {
	if(new Date().getTime() - 30000 <  confirmDate) {
		confirmDate = new Date().getTime();
		removeNode(path);
	}else if(confirm(i18n("are_you_sure_delete")+path+"?")) {
		confirmDate = new Date().getTime();
		removeNode(path);
	}
}

function removeNode(path) {
	$("#delete"+path).removeClass("wb-inv");
    $.ajax({
	    url: '/site/delete.html',
	    data: {
	    	path:path
	    },
	    type: "POST", 
	    success: function(msg) {
	    	if(!msg.indexOf("error:")>=0)
	    		$("#"+path).remove();
	    	else
	    		alert(msg);
	    },
	    error: function() {
	    	alert(i18n("fail"));
	    }
	    // ... Other options like success and etc
	});	 	
}

function deleteNode(path,uid) {
	if(new Date().getTime() - 30000 <  confirmDate) {
		confirmDate = new Date().getTime();
		removeNode(path,uid);		
	}else if(confirm(i18n("are_you_sure_delete")+"?")) {
		confirmDate = new Date().getTime();
		removeNode(path,uid);
	}
}

function removeNode(path,uid) {
    $.ajax({
	    url: '/site/delete.html',
	    data: {
	    	path:path
	    },
	    type: "POST", 
	    success: function(msg) {
	    	if(!msg.indexOf("error:")>=0)
	    		$("#"+uid).remove();
	    	else
	    		alert(msg);
	    },
	    error: function() {
	    	alert(i18n("fail"));
	    }
	    // ... Other options like success and etc
	});	 
}

function deleteUser(path) {

	if(confirm(i18n("are_you_sure_delete")+path+"?")) {
	    $.ajax({
		    url: '/site/delete.html',
		    data: {
		    	path:path
		    },
		    type: "POST", 
		    success: function(msg) {
		    	if(!msg.indexOf("error:")>=0)
		    		window.location.href="usermanager.html";
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
function removeTag(id) {
	if(new Date().getTime() - 30000 <  confirmDate) {
		confirmDate = new Date().getTime();
		deleteTag(id);		
	}else if(confirm(i18n("are_you_sure_delete")+"?")) {
		confirmDate = new Date().getTime();
		deleteTag(id);
	}
}

function deleteTag(id) {
	$.ajax({
	    url: 'delete.html',
	    data: {
	    	uid:id
	    },
	    type: "POST", 
	    success: function(msg) {
	    	if(!msg.indexOf("error:")>=0)
	    		$("#"+id).remove();
	    	else
	    		alert(msg);
	    },
	    error: function() {
	    	alert(i18n("fail"));
	    }
	    // ... Other options like success and etc
	});	
}

function setDataView(id,view) {
	
	$("#"+id).focus();
	$( "#left-bar" ).trigger( "open.wb-overlaylbx" );
	
	var left_float = document.getElementById("left-float");

	if(left_float!=null && left_float !='undefined') {
		if(left_float.getAttribute("style")=="left: 0px; border: 0px none; height: 600px; position: fixed; width: 400px; overflow: hidden; top: 10px; bottom: 30px")
			left_float.setAttribute("style", "left: 0px; border: 0px none; height: 600px; position: fixed; width: 0px; overflow: hidden; top: 10px; bottom: 30px");
		else
			left_float.setAttribute("style", "left: 0px; border: 0px none; height: 600px; position: fixed; width: 400px; overflow: hidden; top: 10px; bottom: 30px");
		
	}
	
	if($("#left-iframe").attr("src")!=view)
		$("#left-iframe").attr("src",view);	
	//$(document).scrollTop( $("#wb-cont").offset().top ); 
	//$( "#left-bar" ).trigger( "open.wb-overlay" );
	//$("#wb-sec").html('<iframe src="/site/browse.html" width="100%" height="600"  frameBorder="0"></iframe>');
	

	//$("#left-iframe").html('<iframe src="/site/browse.html" width="100%" height="600"  frameBorder="0"></iframe>');

}

function ftrClose(url) {
	
	$("#ftrClose").click();
	
	$(document).trigger( "open.wb-lbx", [
	                       			[
	                       				{
	                       					src: url,
	                       					type: "ajax"
	                       				}
	                       			]
	                       		]);
}

function openImage(url) {
	$(document).trigger( "open.wb-lbx", [
	 	                       			[
	 	                       				{
	 	                       					src: url,
	 	                       					type: "image"
	 	                       				}
	 	                       			]
	 	                       		]);
	
}

function openGallery(id) {
	$("#gallery"+id).trigger("wb-init.wb-lbx");
	setTimeout(function(){
		$("#link"+id).click();		
	},500);

}

function toggle(source) {
	  checkboxes = document.getElementsByName('puid');
	  for(var i=0, n=checkboxes.length;i<n;i++) {
		  if(checkboxes[i].value !='{uid}')
			  checkboxes[i].checked = source.checked;
	  }

}

function traverseFileTree(item, path) {
	  path = path || "";
	  if (item.isFile) {
	    // Get file
		  item.file(function(file) {
			  total++;
	      //uploadFile(file);
	      files[files.length] = file;
	      //checkProgress();
	      //alert("File:"+files.length+' path' +path + file.name);
	    });
	  } else if (item.isDirectory) {
	    // Get folder contents
		createFolder(); 

	    var dirReader = item.createReader();
	    dirReader.readEntries(function(entries) {
	      for (var i=0; i<entries.length; i++) {
	        traverseFileTree(entries[i], path + item.name + "/");
	        //sleep(1000);
	      }

	    });
	  }
	}

document.addEventListener("dragenter", function(event) {
	var id = event.target.id;
	if(id)
	if(id=="uploadBox" || id=="uploadImg" ||  id=="uploadIcon" || (id.indexOf("folder")>=0)) {
		//event.target.classList.add("well");
        event.target.style.border = "3px dotted blue";
        
	}

});
function sleep(delay) {
    var start = new Date().getTime();
    while (new Date().getTime() < start + delay);
  }
//document.addEventListener("click",function(event) {
//	if(tinymce) {
//		var editor = tinymce.EditorManager.activeEditor;
//		if( event.target.classList.contains("img-responsive")) {
//			if(input)	
//				win.document.getElementById(input).value = event.target.id;
//			else if(editor)
//				editor.insertContent('<div class="noneditable" data-ajax-replace="'+event.target.id+'"><img class="img-responsive" src="'+event.target.id+'" title="å†…å®¹ä¼šè¢«æ›¿ä»£"/></div>');
//			
//			close();
//	        
//		}
//
//	}
//
//});

// By default, data/elements cannot be dropped in other elements. To allow a drop, we must prevent the default handling of the element
document.addEventListener("dragover", function(event) {
    event.preventDefault();
});

// When the draggable p element leaves the droptarget, reset the DIVS's border style
document.addEventListener("dragleave", function(event) {
	var id = event.target.id;
	if(id)
	if(id=="uploadBox" || id=="uploadImg" || id=="uploadIcon"  || (id.indexOf("folder")>=0)) {
        event.target.style.border = "1px solid #aaaaaa";
        event.target.classList.remove("well");
        }

});

window.addEventListener("drop",function(e){
	  e = e || event;
	  e.preventDefault();
	},false);
	
/*$("body").on('DOMSubtreeModified', "#selectedFiles", function() {
	var submit_youchat = document.getElementById("submit_youchat");
	if(submit_youchat)
		uploadFiles();
});*/