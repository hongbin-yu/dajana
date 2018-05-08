<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<main role="main" property="mainContentOfPage" class="container">
<div class="col-md-4 col-md-push-3">
<h2><spring:message code="djn.register_dajana"/></h2>
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
<spring:message code="djn.username_exists"/>!.
</div>
</section>
</div>
</div>
</c:if>

<div class="wb-frmvld">
	<form action='<c:url value="/signup" />' method="post" id="validation-signup">
	<input type="hidden" id="count" name="count" value="0"/>
	<input type="hidden" id="host" name="host" value="${user.host }"/>
		<div class="form-group">
			<label for="title" class="required"><span class="field-name"><spring:message code="djn.webname"/></span> <strong class="required">(<spring:message code="djn.required"/>)</strong></label>
			<input class="form-control" id="title" name="title" value="${user.title }" type="text" required="required" pattern=".{2,}" data-rule-minlength="2" size="40"  placeholder="<spring:message code="djn.input_webname"/>"/>
		</div>
<%-- 		<div class="form-group">
			<label for="email" class="required"><span class="field-name"><spring:message code="djn.email"/></span></label>
			<input class="form-control" id="email" name="email"  value="${user.email }" type="email" placeholder="<spring:message code="djn.email_for_password"/>" style="FONT-FAMILY: 'Arial';"/>
		</div> --%>
		<div class="form-group">
			<label for="phone" class="required"><span class="field-name"><spring:message code="djn.phone_number"/></span></label>
			<input class="form-control" id="phoneNumber"  value="${user.phoneNumber }" name="phoneNumber" type="phone" pattern="[0-9\-]{9,}"  placeholder="<spring:message code="djn.phone_number_for_password"/>"/>
		</div>
		<div class="form-group">
			<label for="userName" class="required"><span class="field-name"><spring:message code="djn.username"/></span> <strong class="required">(<spring:message code="djn.required"/>)</strong></label>
			<input class="form-control" id="userName" name="userName"  value="${user.userName }" type="text" required="required" pattern="[A-Za-z0-9\s]{4,}" data-rule-alphanumeric="true" data-rule-minlength="4" size="40" placeholder="<spring:message code="djn.username_alpha_number"/>"/>
		</div>		
		<div class="form-group">
			<details id="details_pass">
			<summary>
			<label for="password">
			<span class="field-name"><spring:message code="djn.password"/></span> (<spring:message code="djn.select_4_icon"/>)
			</label>
			<input class="form-control" id="password" name="password" type="password" onkeypress="this.value=''" maxlength="32" size="40" pattern=".{4,}" data-rule-rangelength="[4,32]" placeholder="<spring:message code="djn.select_4_icon"/>"/>
			</summary>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default password" id='A0'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/mouse.png"></c:url>'/></a>
			<a class="btn btn-default password" id='A1'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/cow.png"></c:url>'/></a>
			<a class="btn btn-default password" id='A2'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/tiger.png"></c:url>'/></a>
			</div>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default password" id='B0'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/rabit.png"></c:url>'/></a>
			<a class="btn btn-default password" id='B1'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/dragan.png"></c:url>'/></a>
			<a class="btn btn-default password" id='B2'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/snake.png"></c:url>'/></a>
			</div>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default password" id='C0'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/horse.png"></c:url>'/></a>
			<a class="btn btn-default password" id='C1'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/sheep.png"></c:url>'/></a>
			<a class="btn btn-default password" id='C2'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/monkey.png"></c:url>'/></a>
			</div>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default password" id='D0'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/chiken.png"></c:url>'/></a>
			<a class="btn btn-default password" id='D1'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/dog.png"></c:url>'/></a>
			<a class="btn btn-default password" id='D2'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/pig.png"></c:url>'/></a>
			</div>
			</details>

		<div class="clearfix"></div>
		<details id="details_confirm">
		<summary>
			<label for="passwordconfirm">
			<span class="field-name"><spring:message code="djn.confirm"/></span> (<spring:message code="djn.reselect_password"/>)
			</label>			
			<input class="form-control" id="passwordconfirm" name="passwordconfirm" type="password" maxlength="32" size="40" pattern=".{4,}" data-rule-rangelength="[4,32]" data-rule-equalTo="#password" placeholder="<spring:message code="djn.reselect_4_password_to_confirm"/>"/>
		</summary>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default confirm" id='${ids[0] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[0]}.png"></c:url>'/></a>
			<a class="btn btn-default confirm" id='${ids[1] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[1]}.png"></c:url>'/></a>
			<a class="btn btn-default confirm" id='${ids[2] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[2]}.png"></c:url>'/></a>
			</div>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default confirm" id='${ids[3] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[3]}.png"></c:url>'/></a>
			<a class="btn btn-default confirm" id='${ids[4] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[4]}.png"></c:url>'/></a>
			<a class="btn btn-default confirm" id='${ids[5] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[5]}.png"></c:url>'/></a>
			</div>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default confirm" id='${ids[6] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[6]}.png"></c:url>'/></a>
			<a class="btn btn-default confirm" id='${ids[7] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[7]}.png"></c:url>'/></a>
			<a class="btn btn-default confirm" id='${ids[8] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[8]}.png"></c:url>'/></a>
			</div>
			<div class="btn-group btn-group-justified">
			<a class="btn btn-default confirm" id='${ids[9] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[9]}.png"></c:url>'/></a>
			<a class="btn btn-default confirm" id='${ids[10] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[10]}.png"></c:url>'/></a>
			<a class="btn btn-defaultn confirm" id='${ids[11] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[11]}.png"></c:url>'/></a>
			</div>

		</details>

	<input type="submit" id="submit" value="<spring:message code="djn.submit"/>" class="btn btn-primary"> <input type="reset" value="<spring:message code="djn.reset"/>" class="btn btn-default">
</div>
    </form>
</div>


</div>
</main>
<!-- <script type="text/javascript">

function p(s) {
	var v=$("#password").val();
	if(v=="") {
		count=0;
		$("#count").val("0");
	}
	count = $("#count").val();
	if(count==0) {
		$("#password").val(s);		
	}else {
		$("#password").val(v+s);
	}	

	count++;
	$("#count").val(count);
	if(count==4) {
		$("#details_pass").removeAttr("open");
		$("#details_confirm").attr("open","open");
		}	

}
function c(s) {
	var v=$("#passwordconfirm").val();

	count = $("#count").val();
	if(count==4) {
		$("#passwordconfirm").val(s);		
	}else {
		$("#passwordconfirm").val(v+s);
	}	

	count++;
	$("#count").val(count);
	if(count==8) {
		$("#submit").click();
		}

}
</script>	 -->
