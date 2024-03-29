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
<c:if test="${param.error eq 'barcode:0' || error eq 'barcode:0'}">
<div class="container">
<div class="col-md-4">
<section class="alert alert-warning">
<h3><spring:message code="djn.login_fail"/>!</h3>
<p>
  	<spring:message code="djn.login_barcodenotfound"/>。 <spring:message code="djn.try_again"/>。
 </p>
</section>
</div>
</div>
</c:if>

<c:if test="${param.error eq 'bad_credentials' || not empty error}">
<div class="container">
<div class="col-md-4">
<section class="alert alert-warning">
<p>
  <spring:message code="djn.try_again"/>。
 </p>
</section>
</div>
</div>
</c:if>
<c:if test="${param.error eq 'multiple_users'}">
<div class="container">
<div class="col-md-4">
<%-- <section class="alert alert-danger">
<h3><spring:message code="djn.login_fail"/>!</h3>
<p>
  ${error} <spring:message code="djn.try_once_more"/>。
</p>
</section> --%>
</div>
</div>
</c:if>

<div class="wb-frmvld">
			<form action="/forget" method="POST" name="formUpload" id="form-upload" enctype="multipart/form-data">
				<input class="wb-inv" name="path" id="path" value="/forget">
				<c:if test="${qrimage !=null}">
				<img class="img-responsive" src="${qrimage}" alt="">
				</c:if>
			<div class="form-group">
				<label for="username" class="required"><span class="field-name"><spring:message code="djn.username"/></span> <strong class="required">(<spring:message code="djn.required"/>)</strong></label>
				<input class="form-control" id="username" name="j_username" type="text" value="${j_username }" required="required" pattern=".{2,}" data-rule-minlength="2" size="40"  placeholder="<spring:message code="djn.input_username"/>"/>
			</div>
			<div class="form-group">
				<label for="password" class="required">
				<span class="field-name"><spring:message code="djn.verified_code"/></span> (<spring:message code="djn.select_verified_code"/>)
				</label>
				<input class="form-control" id="j_code" name="j_code" type="password" maxlength="32" size="40" required="required" pattern=".{3,}" data-rule-rangelength="[4,32]" placeholder="<spring:message code="djn.verified_code"/>:<spring:message code="djn.select_verified_code"/>"/>
			</div>					
<%-- 			<label for="fileUpload">或点击选择二维密码图</label>
				<div class="form-group text-center">
					<br/><a href="#" onclick="openFiles()">
					<img id="uploadImg" alt="" src="<c:url value='/resources/images/upload.png'/>"/>
					</a>
					<div class="panel" id="selectedFiles">
					</div>	
					
					<input class="form-control wb-inv" type="file" id="fileUpload" name="file" size="60" required="required"  multiple/>
				</div>
 --%>
				<input id="submit_upload" type="submit"  value="<spring:message code="djn.submit"/>" class="btn btn-primary"> <input type="reset" value="<spring:message code="djn.reset"/>" class="btn btn-default">

			</form>
</div>

</div>
</main>

	
