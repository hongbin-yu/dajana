<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<main role="main" property="mainContentOfPage" class="container">
<div class="col-md-4 col-md-push-3">
<h2>登入微云</h2>
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
	<form action="<c:url value="/signin" />" method="post" id="validation-example">
		<input type="hidden" id="count" name="count" value="0"/>
		<input type="hidden" name="loginCount" value="${loginCount }"/>
<!-- 		<input type="hidden" id="reme" name="reme" value="86400"/>		 -->
		<div class="form-group">
			<label for="username" class="required visible-lg visible-md"><span class="field-name"><spring:message code="djn.username"/></span> <strong class="required">(<spring:message code="djn.required"/>)</strong></label>
			<input class="form-control" id="username" name="j_username" type="text" value="${j_username }" required="required" pattern=".{2,}" data-rule-minlength="2" size="40"  placeholder="<spring:message code="djn.input_username"/>"/>
		</div>
		<div class="form-group">
<!-- 			<details id="details_password" open="open">
			<summary> -->
			<label for="password" class="required visible-lg visible-md">
			<span class="field-name"><spring:message code="djn.password"/></span> (<spring:message code="djn.select_password"/>)
			</label>
			<input class="form-control" id="j_password" name="j_password" type="password" maxlength="32" size="40" pattern=".{3,}" data-rule-rangelength="[4,32]" placeholder="<spring:message code="djn.password"/>:<spring:message code="djn.select_4_icon"/>"/>

<!-- 			</summary> -->
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default security" id='鼠'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/mouse.png"></c:url>'/></a>
			<a class="btn btn-default security" id='牛'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/cow.png"></c:url>'/></a>
			<a class="btn btn-default security" id='虎'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/tiger.png"></c:url>'/></a>
			</div>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default security" id='兔'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/rabit.png"></c:url>'/></a>
			<a class="btn btn-default security" id='龙'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/dragan.png"></c:url>'/></a>
			<a class="btn btn-default security" id='蛇'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/snake.png"></c:url>'/></a>
			</div>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default security" id='马'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/horse.png"></c:url>'/></a>
			<a class="btn btn-default security" id='羊'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/sheep.png"></c:url>'/></a>
			<a class="btn btn-default security" id='猴'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/monkey.png"></c:url>'/></a>
			</div>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default security" id='鸡'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/chiken.png"></c:url>'/></a>
			<a class="btn btn-default security" id='狗'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/dog.png"></c:url>'/></a>
			<a class="btn btn-default security" id='猪'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/pig.png"></c:url>'/></a>
			</div>
			<div class="checkbox-inline"><label><input type ="checkbox" name="reme" value="true" checked><spring:message code="djn.remember_password"/></label></div> 
			<a class="pull-right" href="/forget"><spring:message code="djn.forget_password"/></a>	

<!-- 			</details> -->
		</div>

               
	<input type="submit" id="submit" value="<spring:message code="djn.submit"/>" class="btn btn-primary"> <input type="reset" value="<spring:message code="djn.reset"/>" class="btn btn-default">
    </form>
</div>

</div>
</main>

	
