<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<main role="main" property="mainContentOfPage" class="container">
<section class="modal-dialog modal-content overlay-def">
	<header class="modal-header">
		<h2 class="modal-title">${user.title }-${user.userName }</h2>
	</header>
	<div class="modal-body">
	<div id="messageDiv"></div>
	<div class="form-group" ondrop="drop(event)" ondragover="allowDrop(event)" style="border:1px solid #aaaaaa;">
	<a class="btn btn-default pull-right" onclick="openIcon()">
	<img id="uploadIcon" class="img-responsive well-lg" src="/site/file/icon.jpg?path=/${user.userName }/assets/icon/x120.jpg" alt="图标"/>
	</a>
	<input class="form-control wb-inv" type="file" id="iconUpload" name="file" size="60" required="required"/>
	</div>
	<div class="wb-frmvld">
	<form action='<c:url value="/protected/profile.html" />' method="post" id="validation-signup">
	<input type="hidden" id="count" name="count" value="0"/>
		<div class="form-group">
			<label for="title" class="required"><span class="field-name">网名</span> <strong class="required">(必需)</strong></label>
			<input class="form-control" id="title" value="${user.title }" type="text" required="required" pattern=".{2,}" data-rule-minlength="2" size="40"  id="title${user.uid}" name="title" size="25" uid="${user.uid}" path="${user.path }" onchange="updateNode(this)"  placeholder="输入笔名"/>
		</div>
		<div class="form-group">
			<label for="email" class="required"><span class="field-name">电子邮件地址</span></label>
			<input class="form-control" id="email" name="email" value="${user.email }" type="email" size="40" placeholder="电子邮件地址为找回密码"/>
		</div>
		<div class="form-group">
			<label for="phone" class="required"><span class="field-name">电话号码</span></label>
			<input class="form-control" id="phoneNumber" name="phoneNumber" value="${user.phoneNumber }" type="phone" pattern="[0-9\-]{9,}" size="40"  placeholder="电话号码为找回密码"/>
		</div>
		<div class="form-group">
			<label for="userName" class="required"><span class="field-name">用户名</span> <strong class="required">(必需)</strong></label>
			<input class="form-control" id="userName" name="userName" value="${user.userName }" type="text" required="required" pattern="[A-Za-z0-9\s]{4,}" data-rule-alphanumeric="true" data-rule-minlength="4" size="40" placeholder="输入用户名(只能拼音字母和数字)" disabled/>
		</div>	
		<div class="form-group">
			<details id="details_pass">
			<summary>
			<label for="password" class="required">
			<span class="field-name"><spring:message code="djn.dajana"/><spring:message code="djn.password"/></span> (<spring:message code="djn.select_4_icon"/>)
			</label>
			<input class="form-control" id="password" name="password" type="password" onkeypress="this.value=''" required="required" maxlength="32" size="40" pattern=".{4,}" data-rule-rangelength="[4,32]" placeholder="<spring:message code="djn.select_4_icon"/>"/>
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
			<input class="form-control" id="passwordconfirm" name="passwordconfirm" type="password" maxlength="32" size="40" required="required" pattern=".{4,}" data-rule-rangelength="[4,32]" data-rule-equalTo="#password" placeholder="<spring:message code="djn.reselect_4_password_to_confirm"/>"/>
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
			<a class="btn btn-default confirm" id='${ids[11] }'><img class="img-responsive" alt="" width="64" src='<c:url value="/resources/images/security/${imgs[11]}.png"></c:url>'/></a>
			</div>

		</details>
		</div>					
		<div class="form-group">
			<label for="lastIp" class="required"><span class="field-name">登入IP</span> <strong class="required"></strong></label>
			<input class="form-control" id="lastIp" name="lastIp" value="${user.lastIp }" type="text" required="required" data-rule-alphanumeric="true" data-rule-minlength="4" size="40" disabled/>
		</div>	
		<div class="form-group">
			<label for="hostIp" class="required"><span class="field-name">云地址</span> <strong class="required"></strong></label>
			<input class="form-control" id="hostIp" name="hostIp" value="${user.hostIp }" type="text" required="required" data-rule-alphanumeric="true" data-rule-minlength="4" size="40" disabled/>
		</div>				
		<div class="form-group">
			<label for="localIp" class="required"><span class="field-name">内网地址</span> <strong class="required"></strong></label>
			<input class="form-control" id="localIp" name="localIp" value="${user.localIp }" type="text" required="required" data-rule-alphanumeric="true" data-rule-minlength="4" size="40" disabled/>
		</div>	
		<div class="form-group">
			<label for="usage" class="required"><span class="field-name">可用云空间/总空间</span> <strong class="required"></strong></label>
			<input class="form-control" id="usage" name="usage" value="${usage}" type="text" required="required" data-rule-alphanumeric="true" data-rule-minlength="4" size="40" disabled/>
		</div>
		<div class="form-group">
		<label for="lastModified${item.uid }"><spring:message code="djn.lastModified"/>&nbsp;</label>
		<fmt:formatDate pattern="yyy-MM-dd HH:mm:ss" value="${user.lastModified }"/>
		</div>				
	<input type="submit" id="submit" value="确认" class="btn btn-primary"> <input type="reset" value="重填" class="btn btn-default">
	</form>
	</div>
</div>
</section>
</main>
