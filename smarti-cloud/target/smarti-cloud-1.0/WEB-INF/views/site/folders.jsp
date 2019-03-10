<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<!DOCTYPE html><!--[if lt IE 9]><html class="no-js lt-ie9" lang="zh-Hans" dir="ltr"><![endif]--><!--[if gt IE 8]><!-->
<html class="no-js" lang="zh-Hans" dir="ltr">
<!--<![endif]-->
<%@include file="../wet/header.jsp" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>
<body class="secondary" vocab="http://schema.org/" typeof="WebPage">
<main role="main" property="mainContentOfPage" class="container">
<c:if test="${folders.availablePages>1 }">
<section class="wb-inview show-none bar-demo" data-inview="top-bar">
     <ul class="pager pagination-sm">
     <li class='previous<c:if test="${folders.pageNumber==0}"> disabled</c:if>' ><a href="<c:url value='/folder.html${path }?type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber-1}'/>"><span class="glyphicon glyphicon-chevron-left"></span>上页</a></li>
     <li class="text-center">${folders.pageNumber+1}/${folders.availablePages }(${folders.pageCount })</li>    
     <c:if test="${folders.pageNumber+1<folders.availablePages}">
		<li class="next"><a rel="next" id="nextpageb" href="<c:url value='/folder.html${path}?type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber+1}'/>"><span class="glyphicon glyphicon-chevron-right"></span>下页</a></li>
     </c:if>    
     </ul>
</section>
</c:if>
<div class="row">
<div class="col-md-4 wb-frmvld">
	<form action="upload.html" method="POST" id="form-upload" enctype="multipart/form-data">
		<input type="hidden" name="path" value="${path}"/>
		<input type="hidden" name="type" value="${type}"/>
		<input type="hidden" name="input" value="${input}"/>		
		<div id="uploadBox" class="form-group" ondrop="drop(event)" ondragover="allowDrop(event)" style="border:1px solid #aaaaaa;">
			<label for="fileUpload" class="required"><a onclick="openFiles()"><span id="openFiles" class="field-name">选择或拖入文件 <img src="<c:url value='/resources/images/upload32x32.png'/>"/></span></a></label>
			<input class="form-control wb-inv" type="file" id="fileUpload" name="file" size="60" required="required"  multiple/>
			<div class="form-group" id="selectedFiles">
			</div>
			<input id="submit_upload" type="submit" value="上载" class="btn btn-primary" disabled> <input type="reset" value="清除" onclick="resetSelDiv()" class="btn btn-default">
		</div>
	</form>
</div>
<form action="folder.html" name="assets" method="GET">
<input type="hidden" name="type" value="${type}"/>
<input type="hidden" name="path" value="${path}"/>
<input type="hidden" name="input" value="${input}"/>
<div class="form-group col-md-4"> 
<label for="kw">关键词 :</label>
<input id="kw" name="kw" value="${kw}" size="30"> <input id="submit_search" type="submit" value="搜索" title="搜索，创建文件夹如果文件夹不存在" class="btn btn-primary"> 
</div>
</form>
<div class="form-group col-md-4">
<label>
<c:if test="${folder.name!='assets' }">
<a href='<c:url value="/folder.html?path=${folder.parent}&type=${type }&input=${input }"/>'><img src='<c:url value="/resources/images/arrowleft.png"/>'/></a>
</c:if>
${path }<input type="text" class="form-editable" id="title${folder.uid}" name="jcr:title" value="${folder.title}" uid="${folder.uid} }"/></label>
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
	<input class="form-control" id="name" required="required" name="name" pattern="[A-Za-z0-9\-]{2,}" value="" size="25" placeholder="只能填拼音或数字"/>
	</div>
	<div class="form-group">
	<label for="title">标题<strong class="required">(必填)</strong></label><input class="form-control" id="title" required="required"  name="title" value="" size="25"/>
	</div>
		<input id="submit" type="submit" value="提交" class="btn btn-primary"> <input type="reset" value="重填" class="btn btn-default">
	</form>
	</div>	
</details>
</div>

<%-- <datalist id="suggestions">
<!--[if lte IE 9]><select><![endif]-->
<c:forEach items="${folders.items }" var="item" varStatus="loop">
<option value="${item.path }">${item.path}</option>
</c:forEach>
</datalist>
<!--[if lte IE 9]></select><![endif]--> --%>
</div>
<div class="row">
<div id="top_insert">
</div>
<c:forEach items="${folders.items }" var="item" varStatus="loop">
<div id="${item.uid}" class="col-md-4">
<a id="link${item.uid}" href="javascript:addCarousel('${item.path}')">
	<img src="<c:url value='/viewfolder?path=${item.path}'></c:url>" class="img-responsive"/>
</a>
<details>
	<summary><img title="点击打开或合上编辑窗口" src='<c:url value="/resources/images/editIcon.gif"></c:url>'>${item.title}</summary>
	<div class="panel panel-primary">
	<div class="form-group">
	<label for="title${item.uid}">标题&nbsp;</label><input class="form-control, form-editable" id="title${item.uid}" name="jcr:title" value="${item.title}" size="30" uid="${item.uid}"/>
	</div>
	</div>
</details>
</div>
</c:forEach>
</div>
<div class="row">
<div id="top_insert">
</div>
<c:forEach items="${assets.items }" var="item" varStatus="loop">
<div id="${item.uid}" class="col-md-4">
	<img src="<c:url value='/viewimage?uid=${item.uid}&w=4'></c:url>" class="img-responsive" draggable="true" uid="${item.uid }"/>
<details>
	<summary><img title="点击打开或合上编辑窗口" src='<c:url value="/resources/images/editIcon.gif"></c:url>'>${item.path}</summary>
	<div class="checkbox">
	<label for="delete${item.uid }"><input type="checkbox" class="form-editable" name="delete" value="true" id="delete${item.uid }" uid="${item.uid}"> 删除</label>
	</div>
	<div class="form-group">
	<label for="title${loop.index }">标题&nbsp;</label><input class="form-control, form-editable" id="title${loop.index }" name="jcr:title" value="${item.title}" size="30" uid="${item.uid}"/>
	</div>
	<div class="form-group">
	<label for="url${loop.index }">链接&nbsp;</label><input class="form-control, form-editable" id="url${loop.index }" name="url" value="${item.url}" size="30" uid="${item.uid}"/>
	</div>
	<div class="form-group">
	<label for="description${loop.index }">描述 </label><br/>
	<textarea class="form-control, form-editable" id="description${loop.index }" name="description" cols="35"  uid="${item.uid}">${item.description}</textarea>
	</div>
</details>

</div>
</c:forEach>
</div>
<c:if test="${folders.availablePages>1 }">
<section id="top-bar" class="container wb-overlay modal-content overlay-def wb-bar-t">
     <ul class="pager pagination-sm">
     <li class='previous<c:if test="${folders.pageNumber==0}"> disabled</c:if>' ><a href="<c:url value='/folder.html${path }?type=${type }&input=${input }&kw=${kw }&p=${folders.pageNumber-1}'/>"><span class="glyphicon glyphicon-chevron-left"></span>上页</a></li>
     <li class="text-center">${folders.pageNumber+1}/${folders.availablePages }(${folders.pageCount })</li>    
     <c:if test="${folders.pageNumber+1<assets.availablePages}">
		<li class="next"><a rel="next" id="nextpageb" href="<c:url value='/folder.html${path}?type=${type }&input=${input }&kw=${kw }&p=${folders.pageNumber+1}'/>"><span class="glyphicon glyphicon-chevron-right"></span>下页</a></li>
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
<!--
//-->
var contentPath = "";
if(window.location.pathname.indexOf("/smarti-cloud")>=0) {
	contentPath = "/smarti-cloud";
}
var win = (!window.frameElement && window.dialogArguments) || opener || parent || top;

var tinyMCE;
var tinymce = tinyMCE = win.tinymce;

function addCarousel(fileUrl) {
	var editor = tinymce.EditorManager.activeEditor;
	if(editor)
// 	editor.insertContent('<div class="carousel noneditable" data-ajax-replace="'+contentPath+'/carousel.html?path='+fileUrl+'" id="'+fileUrl+'"></div>');
// 	close();
 	      $.ajax({
			    url: contentPath+"/carousel.html?path="+fileUrl,
			    type: "GET", //ADDED THIS LINE
			    // THIS MUST BE DONE FOR FILE UPLOADING
			    contentType: "text/html",
			    processData: false,
			    success: function(data) {
			    	tinyMCE.activeEditor.dom.remove(tinyMCE.activeEditor.dom.select('.carousel'));
				    
					editor.insertContent('<div id="'+fileUrl+'" class="carousel">'+data+'</div>');
					close();
			    },
			    error: function() {
				    alert("出错："+fileUrl);
			    	close();
			    }
			    // ... Other options like success and etc
			}); 

	
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
  }

	

</script>

</body>
</html>

	