<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<main role="main" property="mainContentOfPage" class="container">
<div class="col-md-4 col-md-push-3">
<h2><spring:message code="djn.login_dajana"/></h2>
<c:if test="${param.error eq 'bad_credentials'}">
<div class="container">
<section class="alert alert-warning">
<h2><spring:message code="djn.login_fail"/>!</h2>
<div class="error">
  	<spring:message code="djn.login_error"/>!
 </div>
</section>
</div>
</c:if>
<c:if test="${param.error eq 'multiple_users'}">
<div class="container">
<div class="row">
<section class="alert alert-info">
<div class="error">
	<spring:message code="djn.login_error"/>!.
</div>
</section>
</div>
</div>
</c:if>

<div class="wb-frmvld">
	<form action="<c:url value="/login" />" method="post" id="validation-example">
		<input type="hidden" id="count" name="count" value="${loginCount}"/>
		<input type="hidden" id="redirect" name="redirect" value="${redirect}"/>		
		<div class="form-group">
			<label for="username" class="required"><span class="field-name"><spring:message code="djn.username"/></span> <strong class="required">(<spring:message code="djn.required"/>)</strong></label>
			<input class="form-control" id="username" name="j_username" type="text" value="${j_username }" required="required" pattern=".{2,}" data-rule-minlength="2" size="40"  placeholder="<spring:message code="djn.input_username"/>"/>
		</div>
		<div class="form-group">
			<details>
			<summary>
			<label for="password">
			<span class="field-name"><spring:message code="djn.password"/></span> (<spring:message code="djn.select_password"/>)
			</label>
			<input class="form-control" id="j_password" name="j_password" type="password" maxlength="32" size="40" pattern=".{4,}" data-rule-rangelength="[4,32]" placeholder="<spring:message code="djn.select_4_icon"/>"/>
			</summary>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default" onclick="javascript:p('A0')"><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/mouse.png"></c:url>'/></a>
			<a class="btn btn-default" onclick="javascript:p('A1')"><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/cow.png"></c:url>'/></a>
			<a class="btn btn-default" onclick="javascript:p('A2')"><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/tiger.png"></c:url>'/></a>
			</div>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default" onclick="javascript:p('B0')"><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/rabit.png"></c:url>'/></a>
			<a class="btn btn-default" onclick="javascript:p('B1')"><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/dragan.png"></c:url>'/></a>
			<a class="btn btn-default" onclick="javascript:p('B2')"><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/snake.png"></c:url>'/></a>
			</div>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default" onclick="javascript:p('C0')"><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/horse.png"></c:url>'/></a>
			<a class="btn btn-default" onclick="javascript:p('C1')"><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/sheep.png"></c:url>'/></a>
			<a class="btn btn-default" onclick="javascript:p('C2')"><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/monkey.png"></c:url>'/></a>
			</div>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default" onclick="javascript:p('D0')"><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/chiken.png"></c:url>'/></a>
			<a class="btn btn-default" onclick="javascript:p('D1')"><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/dog.png"></c:url>'/></a>
			<a class="btn btn-default" onclick="javascript:p('D2')"><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/pig.png"></c:url>'/></a>
			</div>
			</details>
		</div>

               
	<input type="submit" id="submit" value="<spring:message code="djn.submit"/>" class="btn btn-primary"> <input type="reset" value="<spring:message code="djn.reset"/>" class="btn btn-default">
    </form>
</div>



</div>
</main>
<script type="text/javascript">
var count = 0;
function p(s) {
	var v=$("#j_password").val();
	if(v=="") {
		count=0;
		$("#count").val("0");
	}

	count = $("#count").val();
	if(count==0) {
		$("#j_password").val(s);		
	}else {
		$("#j_password").val(v+s);
	}
	count++;
	$("#count").val(count);
	if(count==4) {
		$("#submit").click();
		}
}
var j_password = document.querySelector('#j_password');
if(j_password)
	j_password.addEventListener("focus",function(e) {
	j_password.value="";
});
</script>
	
