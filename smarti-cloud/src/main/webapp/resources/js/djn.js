var files;
$(window).on('load',function() {
	var menu = document.getElementById("wb-lng");
	if(menu) {
		menu.classList.remove("wb-inv");
/*		setTimeout(function() {
			menu.classList.add("wb-inv");
		},1200000);*/
	}
	var buttons = document.querySelectorAll("button[disabled]");
	buttons.forEach(function(b) {b.disabled=false});
		

	
});
var contentPath = "";
if(window.location.pathname.indexOf("/smarti-cloud")>=0) {
	contentPath = "/smarti-cloud";
}
var path = "";
var type = "image";
//var win = (!window.frameElement && window.dialogArguments) || opener || parent || top;
var selDiv = "", topInsert="";
//var form_upload = document.getElementById("form-upload");
/*var tinyMCE;
var tinymce = tinyMCE = win.tinymce;*/
var input = "";
document.addEventListener("DOMContentLoaded", init, false);
var i18n = window.wb.i18n;


function init() {
	var fileUpload = document.querySelector('#fileUpload');
	if(fileUpload != null) {
		fileUpload.addEventListener('change', handleFileSelect, false);	
	}

	var form_control = document.querySelectorAll('.form-editable');
	for(var i=0; i<form_control.length;i++) {
		form_control[i].addEventListener('change',updateThis);
		}
	selDiv = document.querySelector("#selectedFiles");
	topInsert = document.querySelector("#top_insert");
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
	    url: 'updateProperty.html',
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
	    url: 'updateNodeProperty.html',
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
	    url: 'updateProperty.html',
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


function openFiles() {
	selDiv = document.querySelector("#selectedFiles");
	fileUpload = document.querySelector("#fileUpload");
	fileUpload.click();
}

function resetSelDiv() {
	selDiv = document.querySelector("#selectedFiles");;
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
	document.getElementById("submit_upload").disabled=false;
	
}

function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
    ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
    ev.preventDefault();
    var id = ev.target.id;
    var data="";
    if(id)
    if(id=="uploadBox" || id=="uploadImg" || id.indexOf("folder")>=0) {
    	data = ev.dataTransfer.getData("text");
    	path = $("#path").val();
    	if(ev.target.getAttribute("path")) {
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
    	for(var i = 0; i<items.length; i++) {
    		if(items[i].kind == 'string' && items[i].type=="application/x-moz-file-promise-url") {
	    		items[i].getAsString(function (s) {
	    	    	if(s.indexOf("http")>=0 && imported[s]==null) {
	    			    imported[s] = true;   
	    	    		if(s.indexOf("viewimage?path=")>0){
	    	    			  var uid = s.split("viewimage?path=")[1];
	    	    			  uid = uid.split("&")[0];
	  	    	    		  selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("proccess")+"</h2><p><img alt='' src='"+contentPath+"/resources/images/ui-anim_basic_16x16.gif'/> "+s+"</p></section>";
	    	    			  $.ajax({
		    					    url: 'importAssetMove.html?path='+path+"&uid="+uid,
		    					    type: "GET", 
		    					    contentType: false,
		    					    processData: false,
		    					    success: function(data) {
		    					    	if(data.title.indexOf("error:")>=0)
		    						        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2><p>"+data.title+"</p></section>"; // 
		    					    	else {
		    					    		//$("#"+uid).remove();
		    							    selDiv.innerHTML = "<section class=\"alert alert-success\"><h2 class=\"4\">"+i18n("success")+"</h2><p>"+data.title+"</p></section>";
		    							    var html = $("#div_uid").html();
		    							    html = html.split("{uid}").join(data.uid);
		    							    html = html.replace("imgreplace","img");
		    							    html = html.replace("{title}",data.title);
		    							    html = html.replace("{icon}",data.icon);
		    							    
		    							    html = html.replace("{link}",data.link);
		    						    	//$("#top_insert").prepend("<div class=\"col-md-4\"> <a href=\"javascript:returnFileUrl('viewimage?uid="+data.uid+"')\"><img class=\"img-responsive\" src=\"viewimage?uid="+data.uid +"&w=4\"/></a></div>");
		    							    //$("#top_insert").prepend(html);
		    							    $("#top_insert").after(html);

		    					    	}
		    					    },
		    					    error: function() {
		    					        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2></section>"; // 
		    					    }
		    					    // ... Other options like success and etc
		    					}); 
		    			      return;
	    	    			
	    	    		}else {
		    	    		selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("upload")+"</h2><p><img alt='' src='"+contentPath+"/resources/images/ui-anim_basic_16x16.gif'/> "+s+"</p></section>";
		    			      $.ajax({
		    					    url: 'importAsset.html?path='+path+"&url="+s,
		    					    type: "GET", 
		    					    contentType: false,
		    					    processData: false,
		    					    success: function(data) {
		    					    	if(data.title.indexOf("error:")>=0)
		    						        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2><p>"+data.title+"</p></section>"; // 
		    					    	else {
		    							    selDiv.innerHTML = "<section class=\"alert alert-success\"><h2 class=\"4\">"+i18n("success")+"</h2><p>"+data.title+"</p></section>";
		    							    var html = $("#div_uid").html();
		    							    html = html.split("{uid}").join(data.uid);
		    							    html = html.replace("imgreplace","img");
		    							    html = html.replace("{title}",data.title);
		    							    html = html.replace("{icon}",data.icon);
		    							    
		    							    html = html.replace("{link}",data.link);
		    						    	//$("#top_insert").prepend("<div class=\"col-md-4\"> <a href=\"javascript:returnFileUrl('viewimage?uid="+data.uid+"')\"><img class=\"img-responsive\" src=\"viewimage?uid="+data.uid +"&w=4\"/></a></div>");
		    							    //$("#top_insert").prepend(html);
		    							    $("#top_insert").after(html);

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
	    		if(items[i].type=="text/html") {
	    		items[i].getAsString(function (s) {
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
			    	    			  selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("upload")+"</h2><p><img alt='' src='"+contentPath+"/resources/images/ui-anim_basic_16x16.gif'/> "+url+"</p></section>";
			    			      $.ajax({
				    					    url: 'importAssetMove.html?path='+path+"&uid="+uid,
				    					    type: "GET", 
				    					    contentType: false,
				    					    processData: false,
				    					    success: function(data) {
				    					    	if(data.title.indexOf("error:")>=0)
				    						        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2><p>"+data.title+"</p></section>"; // 
				    					    	else {
				    					    		//$("#"+uid).remove();
				    							    selDiv.innerHTML = "<section class=\"alert alert-success\"><h2 class=\"4\">"+i18n("success")+"</h2><p>"+data.title+"</p></section>";
				    							    var html = $("#div_uid").html();
				    							    html = html.split("{uid}").join(data.uid);
				    							    html = html.replace("imgreplace","img");
				    							    html = html.replace("{title}",data.title);
				    							    html = html.replace("{icon}",data.icon);
				    							    
				    							    html = html.replace("{link}",data.link);
				    						    	//$("#top_insert").prepend("<div class=\"col-md-4\"> <a href=\"javascript:returnFileUrl('viewimage?uid="+data.uid+"')\"><img class=\"img-responsive\" src=\"viewimage?uid="+data.uid +"&w=4\"/></a></div>");
				    							    //$("#top_insert").prepend(html);
				    							    $("#top_insert").after(html);

				    					    	}
				    					    },
				    					    error: function() {
				    					        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+"</h2></section>"; // 
				    					    }
				    					    // ... Other options like success and etc
				    					}); 
				    			      return;			    	    			  
			    	    		}else	{
				    	    		selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("upload")+"</h2><p><img alt='' src='"+contentPath+"/resources/images/ui-anim_basic_16x16.gif'/> "+url+"</p></section>";
				    			      $.ajax({
				    					    url: 'importAsset.html?path='+path+"&url="+url,
				    					    type: "GET", 
				    					    contentType: false,
				    					    processData: false,
				    					    success: function(data) {
				    					    	if(data.title.indexOf("error:")>=0)
				    						        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+" </h2><p>"+data.title+"</p></section>"; // 
				    					    	else {
				    							    selDiv.innerHTML = "<section class=\"alert alert-success\"><h2 class=\"4\">"+i18n("success")+"</h2><p>"+data.title+"</p></section>";
				    							    var html = $("#div_uid").html();
				    							    html = html.split("{uid}").join(data.uid);
				    							    html = html.replace("imgreplace","img");
				    							    html = html.replace("{title}",data.title);
				    							    html = html.replace("{icon}",data.icon);
				    							    
				    							    html = html.replace("{link}",data.link);
				    						    	//$("#top_insert").prepend("<div class=\"col-md-4\"> <a href=\"javascript:returnFileUrl('viewimage?uid="+data.uid+"')\"><img class=\"img-responsive\" src=\"viewimage?uid="+data.uid +"&w=4\"/></a></div>");
				    							    //$("#top_insert").prepend(html);
				    							    $("#top_insert").after(html);

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
    	files = [];
    	var folderCreated = false;
	    droppedFiles = ev.dataTransfer.files;
		var items = ev.dataTransfer.items;
		topInsert.innerHTML="";
		for(var i = 0; i< droppedFiles.length; i++) {
		    if(items !=null) {
			    var entry = items[i].webkitGetAsEntry();
			    if(entry.isDirectory) {
			        selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("create_folder")+droppedFiles[i].name+"...</h2></section>"; // 
			        $("#foldername").val(droppedFiles[i].name);
			        $("#titlefolder").val(droppedFiles[i].name);
			        createFolder();
			        folderCreated = true;
			        continue;
			    }
			    	
		    }

		    files[i] = 	droppedFiles[i];		
			//uploadFile(droppedFiles[i]);

		};
	    //document.getElementById("submit_upload").disabled=false;
		ev.target.classList.remove("well");
		ev.target.style.border = "1px solid #aaaaaa";
    }
    if(files.length>0) {
    	i = 0;
    	uploadFiles();
    }else if(folderCreated==false){
    	if(data.indexOf("http")==0) {
    		selDiv.innerHTML=  "<section class=\"alert alert-info\"><h2 class=\"h3\">"+i18n("upload")+"</h2><p><img alt='' src='"+contentPath+"/resources/images/ui-anim_basic_16x16.gif'/> "+data+"</p></section>";
	      $.ajax({
			    url: 'importAsset.html?path='+path+"&url="+data,
			    type: "GET", 
			    contentType: false,
			    processData: false,
			    success: function(data) {
			    	if(data.title.indexOf("error:")>=0)
				        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+" </h2><p>"+data.title+"</p></section>"; // 
			    	else {
					    selDiv.innerHTML = "<section class=\"alert alert-success\"><h2 class=\"4\">"+i18n("success")+"</h2><p>"+data.title+"</p></section>";
					    var html = $("#div_uid").html();
					    html = html.split("{uid}").join(data.uid);
					    html = html.replace("imgreplace","img");
					    html = html.replace("{title}",data.title);
					    html = html.replace("{icon}",data.icon);
					    
					    html = html.replace("{link}",data.link);
				    	//$("#top_insert").prepend("<div class=\"col-md-4\"> <a href=\"javascript:returnFileUrl('viewimage?uid="+data.uid+"')\"><img class=\"img-responsive\" src=\"viewimage?uid="+data.uid +"&w=4\"/></a></div>");
					    //$("#top_insert").prepend(html);
					    $("#top_insert").after(html);

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
    
}

function uploadFolder() {
	path = $("#path").val();
	var folderPath = $("#folderPath").val();
	var override = $("#override").is(":checked")?"true":"false";
	$.ajax ({
	    url: 'importFolder.html?path='+path+"&url="+folderPath+"&override="+override,
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
	var running = "<img src=\"resources/images/ui-anim_basic_16x16.gif\">"+ url + "<br/>";
	selDiv.innerHTML = running;	
	$("#submit_upload_url").attr("disabled",true);
	$.ajax ({
	    url: 'importAsset.html?path='+path+"&url="+url+"&override="+override,
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
	var running = "<img src=\"resources/images/ui-anim_basic_16x16.gif\">"+ file.name + "<br/>";
	selDiv.innerHTML = running;
	var override = $("#override").is(":checked")?"true":"false";
	var formData = new FormData();
	formData.append("path",path);
    formData.append("file", file,file.name);
    formData.append("filename",file.name);
    formData.append("lastModified",file.lastModified);
    formData.append("total",files.length);  
    formData.append("override",override);    
    var fileSize = 0;
    if("size" in file)
      fileSize = file.size;
    else
	  fileSize = file.fileSize;
  
    	
    if(fileSize > 200000000) {
        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("document_limit")+"200MB</h2></section>"; // 
    	
	    return;
    	
    }
    var start = new Date();
    var end = new Date();
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
    	                selDiv.innerHTML="<section id=\""+file.name+"\"><h5>"+running+"</h5><progress class=\"full-width\" value=\""+evt.loaded +"\" max=\""+evt.total+"\"><span class=\"wb-inv\">"+percentComplete+"%</span></progress></section>";
    		    		selDiv.innerHTML += "<p>"+(speed/1000).toFixed(2)+" MB/s</p>";
    	                
    	            }
    	       }, false);

    	       // Download progress
    	       xhr.addEventListener("progress", function(evt){
    	           if (evt.lengthComputable) {
	   	                percentComplete = (evt.loaded / evt.total)*100;
    	                selDiv.innerHTML="<section id=\""+file.name+"\"><h5>"+file.name+"</h5><progress class=\"full-width\" value=\""+evt.loaded +"\" max=\""+evt.total+"\"><span class=\"wb-inv\">"+percentComplete+"%</span></progress></section>";
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
		    	if(data.title.indexOf("error:")>=0) {
			        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h5\">"+i18n("fail")+"</h2><p>"+data.title+"</p></section>"; // 
		    	}else {
				    var speed = 0;
				    speed = fileSize*8/(end.getTime() - start.getTime());
	                selDiv.innerHTML ="<section id=\""+file.name+"\"><h5>"+file.name+"("+(speed/1000).toFixed(2)+" MB/s)</h5><progress class=\"full-width\" value=\""+fileSize +"\" max=\""+fileSize+"\"><span class=\"wb-inv\">"+100+"%</span></progress></section>";
		    		selDiv.innerHTML += "<section class=\"alert alert-success\"><h3 class=\"5\">"+(i+1)+"/"+total+i18n("document_uploaded")+i18n("success")+"</h3></section>";

		    		var html = $("#div_uid").html();
				    html = html.split("{uid}").join(data.uid);
				    html = html.replace("imgreplace","img");
				    html = html.replace("{title}",data.title);
				    html = html.replace("{link}",data.link);		
				    html = html.replace("{icon}",data.icon);					    
				    html = html.replace("-edit","");
			    	//$("#top_insert").prepend("<div class=\"col-md-4\"> <a href=\"javascript:returnFileUrl('viewimage?uid="+data.uid+"')\"><img class=\"img-responsive\" src=\"viewimage?uid="+data.uid +"&w=4\"/></a></div>");
				    $("#top_insert").after(html);

		    	}
		    },
		    error: function(jqXHR, exception) {
		        selDiv.innerHTML=  "<section class=\"alert alert-warning\"><h2 class=\"h3\">"+i18n("fail")+":"+file.name+",sttus:"+jqXHR+",exception:"+exception+"</h2></section>"; // 
		    }
		    // ... Other options like success and etc
		});	
}
var total = 0;
var i = 0;
function uploadFiles() {
	selDiv.innerHTML = "";
	percentComplete = 0;
	total = files.length;
	i = 0;
	document.getElementById("submit_upload").disabled=true;
	uploadFile(files[i]);
	setTimeout(checkProgress,1000);

}

function checkProgress() {

	if(percentComplete ==1000 && i <total-1) {
		i++;
		uploadFile(files[i]);
		setTimeout(checkProgress,1000);
	}else if(i<total-1){
		setTimeout(checkProgress,1000);
	}else if(i==total -1 || total==0){
		var override = $("#override").is(":checked")?"true":"false";
		if(override=="true") {
	    	if(percentComplete ==1000 && i==total -  1) {
	    		setTimeout(function() {window.location.reload();},2000);
	    	}else {
	    		setTimeout(checkProgress,1000);
	    	}			
		}

	} 

	

}


function deleteNode(path) {

	if(confirm(i18n("are_you_sure_delete")+path+"?")) {
	    $.ajax({
		    url: 'delete.html',
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
}

function deleteNode(path,uid) {

	if(confirm(i18n("are_you_sure_delete")+path+"?")) {
	    $.ajax({
		    url: 'delete.html',
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
}

function deleteUser(path) {

	if(confirm(i18n("are_you_sure_delete")+path+"?")) {
	    $.ajax({
		    url: 'delete.html',
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

	if(confirm(i18n("are_you_sure_delete")+id+"?")) {
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
}
/*function returnFileUrl(fileUrl) {
	var editor = tinymce.EditorManager.activeEditor;

	if(input)	
		win.document.getElementById(input).value = fileUrl;
	else if(editor)
		editor.insertContent('<div class="noneditable" data-ajax-replace="'+fileUrl+'"><img class="img-responsive" src="'+fileUrl+'" title="内容会被替代"/></div>');
	
	close();
}
function close() {
    var editor = tinymce.EditorManager.activeEditor;

    // To avoid domain relaxing issue in Opera
    function close() {
      editor.windowManager.close(window);
      tinymce = tinyMCE = editor =  null; // Cleanup
    }

    if (tinymce.isOpera) {
      win.setTimeout(close, 0);
    } else {
      close();
    }
  };*/
function setDataView(id,view) {
	//f($("#"+id).attr("data-inview") == "") 
	//$("#"+id).attr("data-inview",view);
	var left_bar = document.getElementById("left-bar");
	if(left_bar.classList.contains("wb-inv"))
		left_bar.classList.remove("wb-inv");
	//$(document).scrollTop( $("#wb-cont").offset().top ); 
	//$( "#left-bar" ).trigger( "open.wb-overlay" )
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

function toggle(source) {
	  checkboxes = document.getElementsByName('puid');
	  for(var i=0, n=checkboxes.length;i<n;i++) {
		  if(checkboxes[i].value !='{uid}')
			  checkboxes[i].checked = source.checked;
	  }

}

document.addEventListener("dragenter", function(event) {
	var id = event.target.id;
	if(id)
	if(id=="uploadBox" || id=="uploadImg" || (id.indexOf("folder")>=0)) {
		event.target.classList.add("well");
        event.target.style.border = "3px dotted blue";
        
	}

});

//document.addEventListener("click",function(event) {
//	if(tinymce) {
//		var editor = tinymce.EditorManager.activeEditor;
//		if( event.target.classList.contains("img-responsive")) {
//			if(input)	
//				win.document.getElementById(input).value = event.target.id;
//			else if(editor)
//				editor.insertContent('<div class="noneditable" data-ajax-replace="'+event.target.id+'"><img class="img-responsive" src="'+event.target.id+'" title="内容会被替代"/></div>');
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
	if(id=="uploadBox" || id=="uploadImg"  || (id.indexOf("folder")>=0)) {
        event.target.style.border = "1px solid #aaaaaa";
        event.target.classList.remove("well");
        }

});

window.addEventListener("drop",function(e){
	  e = e || event;
	  e.preventDefault();
	},false);
	
