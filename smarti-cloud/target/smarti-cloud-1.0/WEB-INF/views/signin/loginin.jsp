<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<!DOCTYPE html><!--[if lt IE 9]><html class="no-js lt-ie9" lang="en" dir="ltr"><![endif]--><!--[if gt IE 8]><!-->
<html class="no-js" lang="en" dir="ltr">
<!--<![endif]-->
<%@include file="../wet/header.jsp" %>
<body vocab="http://schema.org/" typeof="WebPage">
<%@include file="../wet/navigator.jsp" %>
<main role="main" property="mainContentOfPage" class="container">
<div class="row">
<h2>Login to SMARTi</h2>
<c:if test="${param.error eq 'bad_credentials'}">
<div class="container">
<section class="alert alert-warning">
<h2>Login error!</h2>
<div class="error">
  			Your sign in information was incorrect.
  			Please try again.
 </div>
</section>
</div>
</c:if>
<c:if test="${param.error eq 'multiple_users'}">
<div class="container">
<div class="row">
<section class="alert alert-info">
<div class="error">
  			Your sign in information was incorrect.
  			Please try again.
</div>
</section>
</div>
</div>
</c:if>

<div class="wb-frmvld">
	<form action="<c:url value="/signin/authenticate" />" method="post" id="validation-example">
		<div class="form-group">
			<label for="j_username" class="required"><span class="field-name">Username</span> <strong class="required">(required)</strong></label>
			<input class="form-control" id="j_username" name="j_username" type="text" required="required" pattern=".{2,}" data-rule-minlength="2" />
		</div>
		<div class="form-group">
			<label for="password"><span class="field-name">Password</span> (between 5 and 10 characters)</label>
			<input class="form-control" id="password" name="password" type="password" maxlength="10" size="10" pattern=".{5,10}" data-rule-rangelength="[5,10]" />
		</div>
	</form>
	<input type="submit" value="Submit" class="btn btn-primary"> <input type="reset" value="Reset" class="btn btn-default">
</div>

</main>

<%@include file="../wet/footer.jsp" %>
</body>
</html>



	
