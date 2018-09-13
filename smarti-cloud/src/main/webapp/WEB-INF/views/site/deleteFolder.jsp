<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<section class="modal-dialog modal-content overlay-def">
	<header class="modal-header">
		<H1 class="modal-title">删除目录</H1>
	</header>
	<div class="modal-body">
	<form  method="POST" action="javascript:deleteNode('${smartiNode.path }','${smartiNode.uid}')" id="deleteNode" accept-charset="UTF-8">
		<input type="hidden" name="path" value="${smartiNode.path }"/>
		<input type="hidden" name="redirect" value="${redirect}"/>
		<h3>你确信要删除（${smartiNode.path }）”${smartiNode.title }“和目录下所有文件？</h3>
		<button type="button" onclick="javascript:deleteFolder('${smartiNode.path }','${redirect}')" class="btn btn-primary popup-modal-dismiss">删除</button>  <button class="btn btn-primary popup-modal-dismiss" type="button">关闭</button>
	</form>
	</div>
</section>
