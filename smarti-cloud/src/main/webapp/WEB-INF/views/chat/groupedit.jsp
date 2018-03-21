<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<section class="modal-dialog modal-content overlay-def">
	<header class="modal-header">
		<h2 class="modal-title">优信群编辑</h2>
	</header>
	<div class="modal-body wb-frmvld">
	<form  method="POST" id="page-modify" accept-charset="UTF-8">
		<input type="hidden" name="path" value="${folder.path }"/>
		<div id="modal_message">${message }</div>
		<label for="path">路径 ${folder.path}</label>
		<div class="form-group">
		<label for="title">标题&nbsp;<strong class="required">(必填)</strong></label><input class="form-control form-editable" id="title" required="required" name="jcr:title" value="${folder.title }" size="55" uid="${folder.uid }" onchange="updateNode(this)"/>
		</div>
		<div class="form-group">
		<label for="status">创建日期： <time><fmt:formatDate type = "date" pattern = "yyyy-MM-dd HH:mm:ss" value = "${folder.lastModified}" /></time> </label>		
		</div>
		<div class="form-group">
		<label for="status">创建者： ${folder.createdBy }</label>		
		</div>	
		<div class="panel panel-success">
		<header class="panel-heading">群里</header>
		<div class="panel panel-body"></div>
		</div>	
		<div class="panel panel-default">
		<header class="panel-heading">群外</header>
		<div class="panel panel-body"></div>
		</div>			
		<button class="btn btn-primary popup-modal-dismiss" onclick="window.location.reload()">完成</button>  <button class="btn btn-primary popup-modal-dismiss" type="button">关闭</button>
	</form>
	</div>
</section>