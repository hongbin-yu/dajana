<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<main role="main" property="mainContentOfPage" class="container">
<div class="col-md-9 col-md-push-3">
<h2>登入本地网</h2>
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
	<form action="<c:url value="/signin/authenticate" />" method="post" id="validation-example">
		<div class="form-group">
			<label for="username" class="required"><span class="field-name"><spring:message code="djn.username"/></span> <strong class="required">(<spring:message code="djn.required"/>)</strong></label>
			<input class="form-control" id="username" name="j_username" type="text" required="required" pattern=".{2,}" data-rule-minlength="2" size="40"/>
		</div>
		<div class="form-group">
			<label for="password" class="required"><span class="field-name"><spring:message code="djn.password"/></span> (<spring:message code="djn.5_to_10"/>)<strong class="required">(<spring:message code="djn.required"/>)</strong></label>
			<input class="form-control" id="password" name="j_password" type="password" maxlength="10" required="required"  size="10" pattern=".{5,10}" data-rule-rangelength="[5,10]" />
		</div>
    
	<input type="submit" value="<spring:message code="djn.submit"/>" class="btn btn-primary"> <input type="reset" value="<spring:message code="djn.reset"/>" class="btn btn-default">
    </form>
</div>

</div>
</main>

	
