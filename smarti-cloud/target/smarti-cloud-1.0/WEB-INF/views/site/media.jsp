<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<!DOCTYPE html><!--[if lt IE 9]><html class="no-js lt-ie9" lang="zh" dir="ltr"><![endif]--><!--[if gt IE 8]><!-->
<html class="no-js" lang="zh" dir="ltr">
<!--<![endif]-->
<%@include file="../wet/header.jsp" %>
<meta http-equiv="cache-control" content="max-age=0" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
<meta http-equiv="pragma" content="no-cache" />
<c:set var="contentPath"><c:url value="/"></c:url></c:set>
<c:set var="sources"></c:set>
<body class="secondary" vocab="http://schema.org/" typeof="WebPage">
<main role="main" property="mainContentOfPage" class="container">
<c:if test="${assets.availablePages>1 }">
<section class="wb-inview show-none bar-demo" data-inview="top-bar">
     <ul class="pager pagination-sm">
     <li class='previous<c:if test="${assets.pageNumber==0}"> disabled</c:if>' ><a href="<c:url value='/media.html?path=${path }&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber-1}'/>"><span class="glyphicon glyphicon-chevron-left"></span></a></li>
     <li class="text-center">${assets.pageNumber+1}/${assets.availablePages }(${assets.pageCount })</li>    
     <c:if test="${assets.pageNumber+1<assets.availablePages}">
		<li class="next"><a id="nextpageb" href="<c:url value='/media.html?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber+1}'/>"><span class="glyphicon glyphicon-chevron-right"></span></a></li>
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
		<input type="hidden" name="redirect" value="/media.html">	
		<div id="uploadBox" class="form-group" ondrop="drop(event)" ondragover="allowDrop(event)" style="border:1px solid #aaaaaa;">
			<label for="fileUpload" class="required"><a onclick="openFiles()"><span id="openFiles" class="field-name">选择或拖入文件 <img src="<c:url value='/resources/images/upload32x32.png'/>"/></span></a></label>
			<input class="form-control wb-inv" type="file" id="fileUpload" name="file" size="60" required="required"  multiple/>
			<div class="form-group" id="selectedFiles">
			</div>
			<input id="submit_upload" type="submit" value="上载" class="btn btn-primary" disabled> <input type="reset" value="清除" onclick="resetSelDiv()" class="btn btn-default">
		</div>
	</form>
</div>
<form action="media.html" id="assets" name="assets" method="POST">
<input type="hidden" name="type" value="${type}"/>
<input type="hidden" name="input" value="${input}"/>
<div class="form-group col-md-4"> 
<input id="kw" name="kw" value="${kw}" size="30" placeholder="输入关键词"> <input id="submit_search" type="submit" value="搜索" title="搜索，创建文件夹如果文件夹不存在" class="btn btn-primary"> 
</div>
</form>
<div class="form-group col-md-4">
<label>
<c:if test="${folder.name!='assets' }">
<a title="${folder.parent }" href='<c:url value="/media.html?path=${folder.parent}&type=${type }&input=${input }"/>'><img src='<c:url value="/resources/images/arrowleft.png"/>'/></a>
</c:if>
${path }<!-- img title="点击插入连续播放" alt="" class="pull-right" src="<c:url value="/resources/images/play.png"></c:url>" onclick="javascript:returnCarousel('${folder.path}')"--><input type="text" class="form-editable" id="title${folder.uid}" name="jcr:title" value="${folder.title}" uid="${folder.uid}"/></label>
<details>
	<summary>
		<label for="path"><img src='<c:url value="/resources/images/folder32X32.png"/>'/>创建子目录</label>
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
		<li class="list-group-item" ><a title="${item.path }" href="media.html?path=${item.path }">${item.title }</a></li>
		</c:forEach>        
      </ul>
     </div>
</div>
<div class="clearfix"></div>
<div class="row">
<div id="top_insert">
</div>
<c:forEach items="${assets.items }" var="item" varStatus="loop">
<c:set var="sources">${sources}<source type="${item.contentType}" src="viewimage?uid=${item.uid }"></c:set>
<div id="${item.uid}" class="col-md-4">

	<video  controls="controls" width="200" height="100" preload="none">
	<source type="${item.contentType }" src="viewimage?uid=${item.uid }"/>
	</video>

	<a title="点击选择此视频" href='javascript:returnFileUrl("viewimage?uid=${item.uid}","${item.uid}")'><img id="${item.uid}" src="<c:url value="/resources/images/play.png"></c:url>" class="img-responsive pull-right"/></a>
<details>
	<summary><img title="点击打开或合上编辑窗口" src='<c:url value="/resources/images/editIcon.gif"></c:url>'>${item.path}</summary>
	<div class="row">
	<div class="checkbox">
	<label for="delete${item.uid }"><input type="checkbox" class="form-editable" name="delete" value="true" id="delete${item.uid }" uid="${item.uid}"> 删除</label>
	</div>
	<div class="form-group">
	<label for="title${item.uid }">标题&nbsp;</label><input class="form-control, form-editable" id="title${item.uid }" name="jcr:title" value="${item.title}" size="24" uid="${item.uid}"/>
	</div>
	<div class="form-group">
	<label for="url${item.uid }">作者&nbsp;</label><input class="form-control, form-editable" id="url${item.uid }" name="url" value="${item.url}" size="24" uid="${item.uid}"/>
	</div>
	<div class="form-group">
	<label for="contentType${item.uid }">类型&nbsp;</label><input class="form-control, form-editable" id="contentType${item.uid }" name="contentType" value="${item.contentType}" size="24" uid="${item.uid}" disabled/>
	</div>
	<div class="form-group">
	<label for="description${item.uid }">描述 </label><br/>
	<textarea class="form-control, form-editable" id="description${item.uid }" name="description" cols="22"  uid="${item.uid}">${item.description}</textarea>
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
     <li class='previous<c:if test="${assets.pageNumber==0}"> disabled</c:if>' ><a href="<c:url value='/media.html?path=${path }&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber-1}'/>"><span class="glyphicon glyphicon-chevron-left"></span></a></li>
     <li class="text-center">${assets.pageNumber+1}/${assets.availablePages }(${assets.pageCount })</li>    
     <c:if test="${assets.pageNumber+1<assets.availablePages}">
		<li class="next"><a id="nextpageb" href="<c:url value='/media.html?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber+1}'/>"><span class="glyphicon glyphicon-chevron-right"></span></a></li>
     </c:if>    
     </ul>
</section>
</c:if>
</main>
<!--[if gte IE 9 | !IE ]><!-->
<script src="<c:url value='/resources/dist/js/jquery/2.1.4/jquery.js'/>"></script>
<script src="<c:url value='/resources/dist/js/wet-boew.min.js'/>"></script>
<!--<![endif]-->
<!--[if lt IE 9]>
<script src="<c:url value='/resources/dist/js/ie8-wet-boew2.min.js'/>"></script>
<![endif]-->
<script src="<c:url value='/resources/tinymce/tinymce.min.js'/>"></script>
<script src="<c:url value='/resources/js/pageEditor.js'/>"></script>
<script src="<c:url value='/resources/js/pageContent.js'/>"></script>
<script src="<c:url value='/resources/js/djn.js'/>"></script>
<script type="text/javascript">
 var win = (!window.frameElement && window.dialogArguments) || opener || parent || top;

var carousel=win.carousel;
var tinyMCE;
var tinymce = tinyMCE = win.tinymce;
var input = "${input}";

function returnFileUrl(fileUrl) {


	if(input && input!="") {
		win.document.getElementById(input).value = fileUrl;
		
		close();
	} else {
		tinyMCE.activeEditor.selection.setContent('<img class="img-responsive" alt="" src="'+fileUrl+'">');
		tinyMCE.activeEditor.setDirty(true);
	}
	var message = win.document.getElementById("header_message");
	if(message) {
		message.innerHTML="<section class=\"alert alert-success\"><h3>视频已加入</h3></section>";

		}
}

function returnFileUrl(fileUrl,uid) {

	if(input && input!="") {
		win.document.getElementById(input).value = fileUrl;
		
		close();
	} else {
		var e_title = document.getElementById("title"+uid);
		var e_url = document.getElementById("url"+uid);
		var e_type = document.getElementById("contentType"+uid);
		var e_desc = document.getElementById("description"+uid);
			
		var html = "<figure class=\"noneditable\"><video controls=\"controls\" width=\"300\" height=\"150\">";
		html +="<source type=\""+e_type.value+"\" src=\""+fileUrl+"\"/>";
		html +="</video><figcaption><p class=\"editable\">描述</p></figcaption></figure>";
		tinyMCE.activeEditor.selection.setContent(html);

		tinyMCE.activeEditor.setDirty(true);
		var message = win.document.getElementById("header_message");
		if(message) {
			message.innerHTML="<section class=\"alert alert-success\"><h3>视频已加入</h3></section>";
	
			}
	}
}

function returnCarousel(fileUrl) {
	
	var html = "<figure class=\"noneditable\"><video controls=\"controls\" width=\"300\" height=\"150\">";
	html +='${sources}';
	html +="</video><figcaption><p class=\"editable\">描述</p></figcaption></figure>";
	tinyMCE.activeEditor.selection.setContent(html);

	tinyMCE.activeEditor.setDirty(true);
	
	var message = win.document.getElementById("header_message");
	if(message) {
		message.innerHTML="<section class=\"alert alert-success\"><h3>加入连续播放</h3></section>";

		}
  


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

