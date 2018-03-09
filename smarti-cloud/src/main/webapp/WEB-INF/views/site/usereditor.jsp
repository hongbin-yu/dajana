<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
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
	<form action='<c:url value="/site/profile.html" />' method="post" id="validation-signup">
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
			<label for="signingKey" class="required"><span class="field-name"><strong class="required" style="FONT-FAMILY: 'Arial';">微云密码</strong></span></label>
			<input class="form-control" id="signingKey" name="signingKey" value="${user.signingKey }" type="password" path="${user.path }" uid="${user.uid}" required="required" data-rule-minlength="4" size="40"/>
		</div>
		<div class="form-group">
			<label for="passwordconfirm">
			<span class="field-name"><spring:message code="djn.confirm"/></span>
			</label>			
			<input class="form-control" id="passwordconfirm" name="passwordconfirm" type="password" maxlength="32" size="40" data-rule-equalTo="#signingKey" placeholder="把以上密码再输一遍"/>
		</div>			
		<div class="form-group">
			<label for="host"><span class="field-name">域名</span> <strong class="required"></strong></label>
			<input class="form-control" id="host" name="host" value="${user.host }" type="text" path="${user.path }" data-rule-minlength="4" size="40"  disabled/>
		</div>
		<div class="form-group">
			<label for="port"><span class="field-name">端口</span> </label>
			<input class="form-control" id="port" name="port" value="${user.port }" type="text" path="${user.path }" data-rule-minlength="4" size="40"  disabled/>
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
			<label for="usage" class="required"><span class="field-name">可用云空间/总空间</span> <strong class="required"></strong></label>
			<input class="form-control" id="usage" name="usage" value="${usage}" type="text" required="required" data-rule-alphanumeric="true" data-rule-minlength="4" size="40" disabled/>
		</div>		
	<input type="submit" id="submit" value="确认" class="btn btn-primary"> <input type="reset" value="重填" class="btn btn-default">
	</form>
	</div>
</div>
</section>
</main>
