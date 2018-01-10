<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<section class="modal-dialog modal-content overlay-def wb-popup-full">
	<header class="modal-header">
		<h2 class="modal-title">文本编辑</h2>
	</header>
	<div class="modal-body wb-frmvld">
	<form  method="POST" action="javascript:updateContent()" id="page-modify" accept-charset="UTF-8">
		<input type="hidden" name="uid" id= "content_uid" value="${asset.uid }"/>
		<div class="row text-center">
		<textarea name="content" id ="content_value" cols="145" rows="20">${content }</textarea>
		</div>
		<button class="btn btn-primary popup-modal-dismiss" onclick="javascript:updateContent()">完成</button>  <button class="btn btn-primary popup-modal-dismiss" type="button">关闭</button>
	</form>
	</div>
</section>
