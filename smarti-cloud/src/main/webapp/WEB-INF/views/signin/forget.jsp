<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<main role="main" property="mainContentOfPage" class="container">
<div class="col-md-4 col-md-push-3">
<h2>忘记密码</h2>
<c:if test="${param.info eq 'pwchanged'}">
<div class="container">
<div class="col-md-4">
<section class="alert alert-success">
<h3><spring:message code="djn.password_changed"/>!</h3>
<p>
  	<spring:message code="djn.login_again"/>。
</p>
</section>
</div>
</div>
</c:if>
<c:if test="${param.error eq 'bad_credentials'}">
<div class="container">
<div class="col-md-4">
<section class="alert alert-warning">
<h3><spring:message code="djn.login_fail"/>!</h3>
<p>
  	<spring:message code="djn.login_error"/>。 <spring:message code="djn.try_again"/>。
 </p>
</section>
</div>
</div>
</c:if>
<c:if test="${param.error eq 'multiple_users'}">
<div class="container">
<div class="col-md-4">
<section class="alert alert-danger">
<h3><spring:message code="djn.login_fail"/>!</h3>
<p>
	<spring:message code="djn.login_error"/>。 <spring:message code="djn.try_once_more"/>。
</p>
</section>
</div>
</div>
</c:if>

<div class="wb-frmvld">
			<form action="upload.html" method="POST" id="form-upload" enctype="multipart/form-data">
				<input type="hidden" id="path" name="path" value="${folder.path}"/>
				<input type="hidden" id="type"  name="type" value="${type}"/>
				<input type="hidden" id="input" name="input" value="${input}"/>
				<input type="hidden" name="redirect" value="assets.html?path=${folder.path}&type=${type}&input=${input}"/>
						
				<div class="form-group" ondrop="drop(event)" ondragover="allowDrop(event)" style="border:1px solid #aaaaaa;">
					<label for="fileUpload" class="required"><a href="#" onclick="openFiles()"><span id="openFiles" class="field-name"><spring:message code="djn.select_dragging_drop_qr"/> </span></a></label>
					<br/><a href="#" onclick="openFiles()"><img id="uploadImg" src="<c:url value='/resources/images/upload.png'/>"/></a>
					<div class="panel" id="selectedFiles">
					</div>	
					<input class="form-control wb-inv" type="file" id="fileUpload" name="file" size="60" required="required"  multiple/>
					<input id="submit_upload" type="button" onclick="javascript:uploadFiles()" value="<spring:message code="djn.upload"/>" class="btn btn-primary" disabled> <input type="reset" value="<spring:message code="djn.clear"/>" onclick="resetSelDiv()" class="btn btn-default">
				</div>
			</form>
</div>

</div>
</main>

	
