<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<section class="modal-dialog modal-content overlay-def">
	<header class="modal-header">
		<H1 class="modal-title">删除网页</H1>
	</header>
	<div class="modal-body">
	<form  method="POST" action="javascript:deletePage('${page.path }','${page.parent}')" id="deleteNode" accept-charset="UTF-8">
		<input type="hidden" name="path" value="${page.path }"/>
		<h3>你确信要删除（${page.path }）”${page.title }“和它所有子目录？</h3>
		<button type="button" onclick="javascript:deletePage('${page.path }','${page.parent}')" class="btn btn-primary popup-modal-dismiss">删除</button>  <button class="btn btn-primary popup-modal-dismiss" type="button">关闭</button>
	</form>
	</div>
</section>
