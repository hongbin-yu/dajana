<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<!DOCTYPE html><!--[if lt IE 9]><html class="no-js lt-ie9" lang="zh" dir="ltr"><![endif]--><!--[if gt IE 8]><!-->
<html class="no-js" lang="zh" dir="ltr">
<!--<![endif]-->
<%@include file="header.jsp" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>
<body class="secondary" vocab="http://schema.org/" typeof="WebPage">
<main role="main" property="mainContentOfPage" class="container">
<c:if test="${assets.availablePages>=1 }">
<section class="wb-inview show-none bar-demo" data-inview="top-bar">
     <ul class="pager pagination-sm">
     <li class='previous<c:if test="${assets.pageNumber==0}"> disabled</c:if>' ><a href="<c:url value='/site/browse.html?path=${path }&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber-1}'/>"><span class="glyphicon glyphicon-chevron-left"></span></a></li>
     <li class="text-center">${assets.pageNumber+1}/${assets.availablePages }(${assets.pageCount })</li>    
     <c:if test="${assets.pageNumber+1<assets.availablePages}">
		<li class="next"><a id="nextpageb" href="<c:url value='/site/browse.html?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber+1}'/>"><span class="glyphicon glyphicon-chevron-right"></span></a></li>
     </c:if>    
     </ul>
</section>
</c:if>
<div class="row">
<div class="col-md-4 wb-frmvld">
	<form action="upload.html" method="POST" id="form-upload" enctype="multipart/form-data">
		<input type="hidden" id="path" name="path" value="${folder.path}"/>
		<input type="hidden" id="type" name="type" value="${type}"/>
		<input type="hidden" id="input" name="input" value="${input}"/>	
		<input type="hidden" name="redirect" value="/site/browse.html">	
		<div id="uploadBox" class="form-group" ondrop="drop(event)" ondragover="allowDrop(event)" style="border:1px solid #aaaaaa;">
			<label for="fileUpload" class="required"><a class="btn btn-default" onclick="openFiles()" title="选择或拖入文件"><span id="openFiles" class="field-name glyphicon glyphicon-folder-open"> 选择或拖入文件</span></a></label>
			<input class="form-control wb-inv" type="file" id="fileUpload" name="file" size="60" required="required"  multiple/>
			<div class="form-group" id="selectedFiles">
			</div>
			<input id="submit_upload" type="button" onclick="javascript:uploadFiles()" value="上载" class="btn btn-primary" disabled> <input type="reset" value="清除" onclick="resetSelDiv()" class="btn btn-default">
		</div>
	</form>
</div>
<form action="browse.html" id="assets" name="assets" method="get" accept-charset="UTF-8">
<input type="hidden" name="input" value="${input}"/>
<div class="form-group col-md-4">
<select name="type"  onchange="this.form.submit()">
<option value="" <c:if test="${type=='' }">selected</c:if> ><spring:message code="djn.all"/></option>
<option value="child" <c:if test="${type=='child' }">selected</c:if> ><spring:message code="djn.child"/></option>
<option value="image" <c:if test="${type=='image' }">selected</c:if> ><spring:message code="djn.image"/></option>
<option value="video" <c:if test="${type=='video' }">selected</c:if> ><spring:message code="djn.video"/></option>
<option value="audio" <c:if test="${type=='audio' }">selected</c:if> ><spring:message code="djn.audeo"/></option>
<option value="application" <c:if test="${type=='application' }">selected</c:if> ><spring:message code="djn.file"/></option>
</select> 
<input id="kw" name="kw" value="${kw}" size="15" placeholder="输入关键词"> <input id="submit_search" type="submit" value="搜索" title="搜索" class="btn btn-primary"> 
</div>
</form>
<div class="form-group col-md-4">
<div class="form-group">
<label>
<c:if test="${folder.name!='assets' }">
<a title="${folder.parent }" href='<c:url value="/site/browse.html?path=${folder.parent}&type=${type }&input=${input }"/>'><span class="glyphicon glyphicon-backward"></span></a>
</c:if>
${path }</label>
<%-- <input type="text" class="form-editable" id="title${folder.uid}" name="jcr:title" size="18" value="${folder.title}" uid="${folder.uid}"/><button class="btn btn-primary btn-xs" onclick="javascript:returnCarousel('${folder.path}')" title="加入广告"><span class="glyphicon glyphicon-play"></span></button>
 --%></div>
            <details id="${folder.uid }">
            <summary>${folder.title}</summary>
				<div class="form-group">
				<label for="foldertitle"><spring:message code="djn.title"/>&nbsp;</label><input class="form-control" id="foldertitle" name="jcr:title" value="${folder.title}" size="25" uid="${folder.uid}"  onchange="updateNode(this)"/>
				</div>            
				<div class="form-group">
				<label for="folderorderby"><spring:message code="djn.order"/>&nbsp;</label>
				<select class="form-control" id="folderorderby" name="orderby" value="${folder.orderby}" uid="${folder.uid}"  onchange="updateNode(this)">
					<option value="lastModified desc"><spring:message code="djn.lastModified_desc"/></option>
					<option value="lastModified asc" <c:if test="${folder.orderby=='lastModified asc'}">selected</c:if>><spring:message code="djn.lastModified_asc"/></option>
					<option value="originalDate desc" <c:if test="${folder.orderby=='originalDate desc'}">selected</c:if>><spring:message code="djn.docDate_desc"/></option>
					<option value="originalDate asc" <c:if test="${folder.orderby=='originalDate asc'}">selected</c:if>><spring:message code="djn.docDate_asc"/></option>
				</select>
				</div>
            </details>
<details>
	<summary>
		<label for="path"><span class="glyphicon glyphicon-folder-close"></span>创建子目录</label>
		<input type="hidden" id="folder-path" name="path" value="${folder.path }" >
	</summary>
	<div class="wb-frmvld">
	<form action="javascript:createFolder()" id="createFolder" method="POST">
	<input type="hidden" name="path" value="${folder.path }"/>	
	<div class="form-group">
	<label for="name">路径<strong class="required">(必填)</strong></label>
	<input class="form-control" id="name" required="required" name="name" pattern="[A-Za-z0-9\s]{4,}" data-rule-alphanumeric="true" data-rule-minlength="4" size="40" value="" size="25" placeholder="只能填拼音或数字"/>
	</div>
	<div class="form-group">
	<label for="title">标题<strong class="required">(必填)</strong></label><input class="form-control" id="title" required="required"  name="title" value="" size="25"/>
	</div>
		<input id="submit" type="submit" value="提交" class="btn btn-primary"> <input type="reset" value="重填" class="btn btn-default">
	</form>
	</div>	
</details>
      <ul class="list-group menu list-unstyled">
		<c:forEach items="${folders.items }" var="item" varStatus="loop">
		<li class="list-group-item" ><a title="${item.path }" href="browse.html?path=${item.path }">${item.title }</a><%--   <a title="打开PDF" href="<c:url value="/viewf2p?path=${item.path}"/>" target="_BLANK"><img title="点击打开PDF" src='<c:url value="/resources/images/pdf.gif"></c:url>'>(打开PDF)</a> --%><%-- <img title="加广告" alt="" class="pull-left" src="<c:url value="/resources/images/play.png"></c:url>" onclick="javascript:returnCarousel('${item.path}')"> --%></li>
		</c:forEach>        
      </ul>
     </div>
</div>
<div class="wb-inv" id="div_uid">    
<div id="{uid}" class="col-md-4">
	<div class="checkbox">
		<input type="checkbox" class="checkbox" name="puid" value="{uid}"><a title="打开PDF" href="<c:url value="viewpdf?uid={uid}"/>" target="_BLANK"><img title="点击选中" src='<c:url value="/resources/images/pdf.gif"></c:url>'></a>
		<a class="wb-lbx-edit" href="<c:url value='{link}'></c:url>" target="_BLANK"><imgreplace id="<c:url value='viewimage?uid={uid}&w=4'></c:url>" src="<c:url value='{icon}'></c:url>" class="img-responsive" draggable="true"/></a>
	</div>
	<details>
		<summary><span class="glyphicon glyphicon-edit">{title}</span></summary>
		<a class="wb-lbx" title="删除它" href="<c:url value="/delete.html?uid={uid}&redirect=/assets.html?path=${folder.path }"/>"><span class="glyphicon glyphicon-remove">删除</span></a>
		<div class="form-group">
		<label for="title{uid}">标题&nbsp;</label><input class="form-control" id="title{uid}" name="jcr:title" value="" size="25" uid="{uid}"  onchange="updateNode(this)"/>
		</div>
		<div class="form-group">
		<label for="url{uid}">链接&nbsp;</label><input class="form-control" id="url{uid}" name="url" value="" size="25" uid="{uid}"  onchange="updateNode(this)"/>
		</div>
		<div class="form-group">
		<label for="contentType{uid}">类型&nbsp;</label><input class="form-control" id="contentType{uid}" name="contentType" value="" size="24" uid="{uid}" disabled/>
		</div>		
		<div class="form-group">
		<label for="description{uid}">描述 </label><br/>
		<div class="panel panel-default description" id="description{uid}" property="description"  uid="{uid}" placeholder="description"></div>
		</div>
	</details>
</div>	 
</div>	
<div class="clearfix"></div>
<div class="col-md-4">
<c:if test="${carousel.availablePages>0}">
<div id="carousel" class="wb-tabs carousel-s2 playing">
	<ul role="tablist">
	<c:forEach items="${carousel.items }" var="item" varStatus="loop">
		<c:if test="${loop.count==1}">
			<li class="active">
				<a href="#tab${loop.count}">
					<img class="img-responsive" src="<c:url value='viewimage?uid=${item.uid}&w=12'></c:url>" alt="${item.alt}" />
					<span class="wb-inv">Tab ${loop.count}:${item.title}</span>
				</a>
			</li>
		</c:if>
		<c:if test="${loop.count>1}">
			<li>
				<a href="#tab${loop.count}">
					<img class="img-responsive" src="<c:url value='viewimage?uid=${item.uid}&w=12'></c:url>" alt="${item.alt}" />
					<span class="wb-inv">Tab ${loop.count}:${item.title}</span>
				</a>
			</li>
		</c:if>		
	</c:forEach>
	</ul>
	<div class="tabpanels">
		<c:forEach items="${carousel.items }" var="item" varStatus="loop">
			<c:if test="${loop.count==1}">
				<div role="tabpanel" id="tab${loop.count}" class="fade in">
					<!-- first child - tabpanel -start -->
						<figure>
							<a href="${item.url}" class="learnmore">
								<img class="img-responsive" src="<c:url value='viewimage?uid=${item.uid}&w=12'></c:url>" alt="${item.alt}" />
							</a>
						<figcaption>
							   <a href="${item.url}" class="learnmore">${item.title}</a>
								${item.description}
							</figcaption>
						</figure>
				</div>
			</c:if>	
			<c:if test="${loop.count>1}">
				<div role="tabpanel" id="tab${loop.count}" class="fade out">
					<!-- first child - tabpanel -start -->
						<figure>
					<a href="${item.url}" class="learnmore">
								<img class="img-responsive" src="<c:url value='viewimage?uid=${item.uid}&w=12'></c:url>" alt="${item.alt}" />
					</a>
							<figcaption>
							    <a href="${item.url}" class="learnmore">${item.title}</a>
								${item.description}
							</figcaption>
						</figure>
				</div>
			</c:if>	
		</c:forEach>
	</div>
</div>
<button class="btn btn-primary btn-sm" onclick="javascript:returnCarousel('${folder.path}')" title="加入广告"><span class="glyphicon glyphicon-play">${folder.title } 植入广告</span></button>
</c:if>
</div>
<div class="col-md-4">
<c:if test="${carousel.availablePages>0}">
<div id="gallery" class="wb-lbx lbx-hide-gal">
<ul class="list-inline">
  <c:forEach items="${carousel.items }" var="item" varStatus="loop">
  	<li>
  	  <a href="viewimage?uid=${item.uid }&w=12" title="${item.title }">
        <img src="viewimage?uid=${item.uid }&w=4" alt="图像 ${loop.index }" class="image-actual" />
      </a>
  	</li>
  </c:forEach>
  </ul>
</div> 
<div class="clear"></div>
<button class="btn btn-primary btn-sm" onclick="javascript:returnGallery('${folder.path}')" title="植入画廊"><span class="glyphicon glyphicon-play">${folder.title } 植入画廊</span></button>
</c:if>
</div>
<div class="row">
<div id="top_insert">
</div>
<c:forEach items="${assets.items }" var="item" varStatus="loop">
<div id="${item.uid}" class="col-md-4">
		<c:if test="${item.mp4}">
			<video poster="video2jpg.jpg?path=${item.path }" controls="controls" width="300" height="200" preload="metadata">
			<source type="video/mp4" src="video.mp4?path=${item.path }"/>
			</video>
			<button class="bnt btn-primary btn-sm" title="点击选择此视频" onclick='javascript:returnFileUrl("video.mp4?path=${item.path}","${item.uid}","video2jpg.jpg?path=${item.path }")'><span class="glyphicon glyphicon-film"></span>${item.title} 植入视频</button>
		</c:if>
        <c:if test="${item.doc2pdf}">
        	<a class="download" href="file/${item.name}?path=${item.path}" target="_BLANK" download><span class="glyphicon glyphicon-download">下载</span></a>
		    <a class="${item.cssClass }" href="doc2pdf.pdf?path=${item.path }">
				<img id="img${item.uid}" src="<c:url value='${item.icon }'></c:url>" class="img-responsive" draggable="true"/>
			</a>
		</c:if>			
		<c:if test="${item.audio}">
		<a class="download" href="file/${item.name}?path=${item.path}" download><span class="glyphicon glyphicon-volume-up">下载</span></a>
		<figure class="wb-mltmd">
				<audio title="${item.title }" preload="metadata">
					<source type="${item.contentType }" src="file/${item.name}?path=${item.path}"/>
				</audio>
				<figcaption>
					<button class="bnt btn-primary btn-sm" title="点击选择此音频" onclick='javascript:returnFileUrl("file/${item.name }?path=${item.path}","${item.uid}","file/${item.name }?path=${item.path }")'><span class="glyphicon glyphicon-volume-up"></span>${item.title} 植入音频</button>
				</figcaption>
		</figure>
		</c:if>	
        <c:if test="${item.doc2pdf}">
        	<a class="download" href="file/${item.name}?path=${item.path}" target="_BLANK" download><span class="glyphicon glyphicon-download">下载</span></a>
		    <a class="${item.cssClass }" href="doc2pdf.pdf?path=${item.path }">
				<img id="img${item.uid}" src="<c:url value='${item.icon }'></c:url>" class="img-responsive" draggable="true"/>
			</a>
		</c:if>					
		<c:if test="${!item.mp4 && !item.audio && !item.doc2pdf}">
		<a class="${item.cssClass }-edit" id="href${item.uid }" href="<c:url value='${item.link}'></c:url>">		
		<img src="<c:url value='${item.icon }'></c:url>" class="img-responsive" draggable="true" onclick="javascript:returnFileUrl('${item.link}','${item.uid }')"/>
		${item.description}
		</a>
		</c:if>

<details>
	<summary><span class="glyphicon glyphicon-edit"></span>${item.path}</summary>
	<div class="row">
	<div class="form-group">
	<label for="title${item.uid }">标题&nbsp;</label><input class="form-control" id="title${item.uid }" name="jcr:title" value="${item.title}" size="24" uid="${item.uid}" onchange="javascript:updateNode(this)"/>
	</div>
	<div class="form-group">
	<label for="url${item.uid }">链接&nbsp;</label><input class="form-control" id="url${item.uid }" name="url" value="${item.url}" size="24" uid="${item.uid}" onchange="javascript:updateNode(this)"/>
	</div>
	<div class="form-group">
		<label for="contentType${item.uid }">类型&nbsp;</label><input class="form-control" id="contentType${item.uid }" name="contentType" value="${item.contentType}" size="24" uid="${item.uid}" disabled/>
	</div>	
	<div class="form-group">
		<label for="size${item.uid }">长度&nbsp;</label><input class="form-control" id="size${item.uid}" name="size" value="${item.size}" size="24" uid="${item.uid}" disabled/>
	</div>	
	<div class="form-group wb-inv">
	<label for="description${item.uid }">描述 </label><br/>
	<div class="panel panel-default" id="description${item.uid }" property="description"  uid="${item.uid }">${item.description}</div>
<%-- 	<textarea class="form-control, form-editable" id="description${item.uid }" name="description" cols="22"  uid="${item.uid}">${item.description}</textarea> --%>
	</div>
	</div>
</details>

</div>
<c:if test="${(loop.index + 2) % 3 ==1  }"><div class="clearfix"></div></c:if>
</c:forEach>
</div>
<c:if test="${assets.availablePages>1 }">
<section id="top-bar" class="container wb-overlay modal-content overlay-def wb-bar-t">
     <ul class="pager pagination-sm">
     <li class='previous<c:if test="${assets.pageNumber==0}"> disabled</c:if>' ><a href="<c:url value='/site/browse.html?path=${path }&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber-1}'/>"><span class="glyphicon glyphicon-chevron-left"></span></a></li>
     <li class="text-center">${assets.pageNumber+1}/${assets.availablePages }(${assets.pageCount })</li>    
     <c:if test="${assets.pageNumber+1<assets.availablePages}">
		<li class="next"><a id="nextpageb" href="<c:url value='/site/browse.html?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber+1}'/>"><span class="glyphicon glyphicon-chevron-right"></span></a></li>
     </c:if>    
     </ul>
</section>
</c:if>
</main>
<!--[if gte IE 9 | !IE ]><!-->
<script src="<c:url value='/resources/wet-boew/js/jquery/2.1.4/jquery.js'/>"></script>
<script src="<c:url value='/resources/wet-boew/js/wet-boew.min.js'/>"></script>
<!--<![endif]-->
<!--[if lt IE 9]>
<script src="<c:url value='/resources/wet-boew/js/ie8-wet-boew2.min.js'/>"></script>
<![endif]-->
<script src="<c:url value='/resources/tinymce/tinymce.min.js'/>"></script>
<script src="<c:url value='/resources/js/pageEditor.js'/>"></script>
<script src="<c:url value='/resources/js/pageContent.js'/>"></script>
<script src="<c:url value='/resources/js/djn.js'/>"></script>
<script type="text/javascript">
 var win = (!window.frameElement && window.dialogArguments) || opener || parent || top;

var carousel = win.carousel;
var newCarousel = "",newGallery="";
var carelement = document.getElementById("carousel");
if(carelement) {
	newCarousel = carelement.outerHTML;
}

var galleryelement = document.getElementById("gallery");
if(galleryelement) {
	newGallery = galleryelement.outerHTML;
}
var tinyMCE = win.tinymce;
var tinymce = tinyMCE;	
var contextPath = "${contentPath}";
if(contextPath=="/") contextPath="";
var input = "${input}";
var lbx_control = document.querySelectorAll('a.wb-lbx-edit');
for(var i=0; i<lbx_control.length;i++) {
	lbx_control[i].addEventListener('click',function(e) {
		e.preventDefault();
		e.stopPropagation();
		});
	}
var doc_control = document.querySelectorAll('a.-edit');
for(var i=0; i<doc_control.length;i++) {
	doc_control[i].addEventListener('click',function(e) {
		e.preventDefault();
		e.stopPropagation();
		});
	}	
var pdf_control = document.querySelectorAll('a.download-edit');
for(var i=0; i<pdf_control.length;i++) {
	pdf_control[i].addEventListener('click',function(e) {
		e.preventDefault();
		e.stopPropagation();
		});
	}	
var pdf_control = document.querySelectorAll('a.download');
for(var i=0; i<pdf_control.length;i++) {
	pdf_control[i].addEventListener('click',function(e) {
		e.preventDefault();
		e.stopPropagation();
		});
	}			
function returnFileUrl(fileUrl) {
	//preventDefault();
	var message = win.document.getElementById("header_message");
	if(message) {
		message.innerHTML="<section class=\"alert alert-success\"><h3>图像已加入</h3></section>";

		}
	if(input && input!="") {
		win.document.getElementById(input).value = fileUrl;
		
		close();
	} else {
		tinyMCE.activeEditor.selection.setContent('<img class="img-responsive" alt="" src="'+contextPath+fileUrl+'">');
		tinyMCE.activeEditor.setDirty(true);
	}

}

function returnFileUrl(fileUrl,uid) {
	//preventDefault();
	var message = win.document.getElementById("header_message");
	if(message) {
		message.innerHTML="<section class=\"alert alert-success\"><h3>资源已加入</h3></section>";

		}
	if(input && input!="") {
		win.document.getElementById(input).value = fileUrl;
		
		close();
	} else {
		var e_title = document.getElementById("title"+uid);
		var e_url = document.getElementById("url"+uid);
		var e_desc = document.getElementById("description"+uid);
		var e_type = document.getElementById("contentType"+uid).value;
		var e_size = document.getElementById("size"+uid).value;
		if(e_type.indexOf("image/")>=0) {
			tinyMCE.activeEditor.selection.setContent('<img class="img-responsive" alt="" src="'+fileUrl+'">');
		}else if(e_type.indexOf("video/")>=0) {
			var html = "<figure class=\"wb-mltmd-edit editable\"><video title=\""+e_title.value+"\" preload=\"metadata\">";
			if(e_size>10000000) {
				html +="<source type=\"video/mp4\" src=\""+fileUrl+"\"/>";
			}else {
				html +="<source type=\"video/mp4\" src=\""+fileUrl+"\"/>";
			}
			html +="</video><figcaption class=\"editable\"><p>"+e_desc.innerHTML+"</p></figcaption></figure>";
			tinyMCE.activeEditor.selection.setContent(html);
		} else if(e_type.indexOf("audio/")>=0) {
			var html = "<audio controls=\"controls\">";
			html +="<source type=\""+e_type+"\" src=\""+fileUrl+"\"/>";
			html +="</audio>";
			tinyMCE.activeEditor.selection.setContent(html);
		}else if(e_type.indexOf("msword")>=0) {
		     $.ajax({
				    url: contentPath+"/importWord.html?path="+fileUrl,
				    type: "GET", //ADDED THIS LINE
				    // THIS MUST BE DONE FOR FILE UPLOADING
				    contentType: "text/html",
				    processData: false,
				    success: function(data) {
				    	tinyMCE.activeEditor.selection.setContent(data);
				    },
				    error: function() {
					    alert("出错："+fileUrl);

				    }
				    // ... Other options like success and etc
				}); 	    
		} else {
			var html = $("#href"+uid).html();
			html = "<a title='下载' href='file?uid="+uid+"'>"+html+"</a><p><a title='下载' href='file?uid="+uid+"'>"+e_title.value+"("+e_size+")</a><p>";
			tinyMCE.activeEditor.selection.setContent(html);
			}
		
		if(e_url && e_url.value != "") {
			tinyMCE.activeEditor.insertContent("<a href=\""+e_url.value+">"+"<h3 class=\"h5\">"+e_url.value+"</h3></a>");
			}
		if(e_desc && e_desc.value !="") {
			tinyMCE.activeEditor.insertContent(e_desc.innerHTML);
			}
		tinyMCE.activeEditor.setDirty(true);
	}

}

function returnFileUrl(fileUrl,uid,poster) {
	//preventDefault();
	var message = win.document.getElementById("header_message");
	if(message) {
		message.innerHTML="<section class=\"alert alert-success\"><h3>资源已加入</h3></section>";

		}
	if(input && input!="") {
		win.document.getElementById(input).value = fileUrl;
		
		close();
	} else {
		var e_title = document.getElementById("title"+uid);
		var e_url = document.getElementById("url"+uid);
		var e_desc = document.getElementById("description"+uid);
		var e_type = document.getElementById("contentType"+uid).value;
		var e_size = document.getElementById("size"+uid).value;
		if(e_type.indexOf("image/")>=0) {
			tinyMCE.activeEditor.selection.setContent('<img class="img-responsive" alt="" src="'+fileUrl+'">');
		}else if(e_type.indexOf("video/")>=0) {
			var html = "<figure class=\"wb-mltmd-edit editable\"><video poster=\""+poster+"\" title=\""+e_title.value+"\" preload=\"none\">";
			if(e_size>10000000) {
				html +="<source type=\"video/mp4\" src=\""+fileUrl+"\"/>";
			}else {
				html +="<source type=\"video/mp4\" src=\""+fileUrl+"\"/>";
			}
			html +="</video><figcaption class=\"editable\"><p>"+e_desc.innerHTML+"</p></figcaption></figure>";
			tinyMCE.activeEditor.selection.setContent(html);
		} else if(e_type.indexOf("audio/")>=0) {
			var html = "<audio controls=\"controls\">";
			html +="<source type=\""+e_type+"\" src=\""+fileUrl+"\"/>";
			html +="</audio>";
			tinyMCE.activeEditor.selection.setContent(html);
		}else if(e_type.indexOf("msword")>=0) {
		     $.ajax({
				    url: contentPath+"/importWord.html?path="+fileUrl,
				    type: "GET", //ADDED THIS LINE
				    // THIS MUST BE DONE FOR FILE UPLOADING
				    contentType: "text/html",
				    processData: false,
				    success: function(data) {
				    	tinyMCE.activeEditor.selection.setContent(data);
				    },
				    error: function() {
					    alert("出错："+fileUrl);

				    }
				    // ... Other options like success and etc
				}); 	    
		} else {
			var html = $("#href"+uid).html();
			html = "<a title='下载' href='file?uid="+uid+"'>"+html+"</a><p><a title='下载' href='file?uid="+uid+"'>"+e_title.value+"("+e_size+")</a><p>";
			tinyMCE.activeEditor.selection.setContent(html);
			}
		
		if(e_url && e_url.value != "") {
			tinyMCE.activeEditor.insertContent("<a href=\""+e_url.value+">"+"<h3 class=\"h5\">"+e_url.value+"</h3></a>");
			}
		if(e_desc && e_desc.value !="") {
			tinyMCE.activeEditor.insertContent(e_desc.innerHTML);
			}
		tinyMCE.activeEditor.setDirty(true);
	}

}
function returnCarousel(fileUrl) {
	var message = win.document.getElementById("header_message");
	if(message) {
		message.innerHTML="<section class=\"alert alert-success\"><h3>加入广告</h3></section>";

		}
	var data = document.getElementById("carousel").outerHTML;
	win.carousel = newCarousel;	
	var car = tinyMCE.activeEditor.dom.select('.carousel');
	if(car.length>0) {
    	car[0].innerHTML = data;
	}else 
		tinyMCE.activeEditor.selection.setContent('<div id="'+fileUrl+'" class="carousel noneditable">'+data+'</div>');
	tinyMCE.activeEditor.setDirty(true);	
/*       $.ajax({
		    url: "carousel.html?path="+fileUrl,
		    type: "GET", //ADDED THIS LINE
		    // THIS MUST BE DONE FOR FILE UPLOADING
		    contentType: "text/html",
		    processData: false,
		    success: function(data) {

			    
		    	var car = tinyMCE.activeEditor.dom.select('.carousel');
		    	if(car.length>0)
			    	car[0].innerHTML = data;
			    else 
		    		tinyMCE.activeEditor.selection.setContent('<div id="'+fileUrl+'" class="carousel noneditable">'+data+'</div>');
		    	tinyMCE.activeEditor.setDirty(true); 
		    	win.carousel = data;

				if(message) {
					message.innerHTML="";
					var node = document.createElement("section"); 
					node.classList.add("alert");
					node.classList.add("alert-success");
					var h3 = document.createElement("h3");
					node.appendChild(h3);   
					var textnode = document.createTextNode("广告已加入:"+fileUrl);
					h3.appendChild(textnode);  
					message.appendChild(node);
					}
		    },
		    error: function() {
			    alert("出错："+fileUrl);

		    }
		    // ... Other options like success and etc
		});  
 */


}

function returnGallery(fileUrl) {
	var message = win.document.getElementById("header_message");
	if(message) {
		message.innerHTML="<section class=\"alert alert-success\"><h3>加入画廊</h3></section>";

		}
	var data = document.getElementById("gallery").outerHTML;
	win.gallery = newGallery;	
	var gallery = tinyMCE.activeEditor.dom.select('.gallery');
	if(gallery.length>0) {
		gallery[0].innerHTML = data;
	}else 
		tinyMCE.activeEditor.selection.setContent('<div id="'+fileUrl+'" class="gallery noneditable">'+data+'</div>');
	tinyMCE.activeEditor.setDirty(true);	

}
function close() {
    var editor = tinymce.EditorManager.activeEditor;
	if(editor) {
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
	}

  }

</script>

</body>
</html>

