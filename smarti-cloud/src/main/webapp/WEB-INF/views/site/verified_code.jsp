<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<main role="main" property="mainContentOfPage" class="container">
<section class="modal-dialog modal-content overlay-def">
	<header class="modal-header">
		<h2 class="modal-title"><spring:message code="djn.verified_code"></spring:message></h2>
	</header>
	<div class="modal-body">

	<div class="wb-frmvld">
	<form action='<c:url value="/site/yzm.html" />' method="get" id="validation-signup">
		<h1 id="verified_code">${user.code }</h1>
		<p><span id="countdown" class="badge">120</span>秒内输入以上验证码有效</p>		
<!-- 	<input type="submit" id="submit" value="确认" class="btn btn-primary"> <input type="reset" value="重填" class="btn btn-default"> -->
	</form>
	</div>
</div>
</section>
</main>
<script>

//  javaScript
window.onload = function () {
	updateVerifiedCode(120);
}
</script>