<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<div class="container">
<div class="row">
        <main role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
        <h1>用户管理 - ${user.title }</h1>
              <div class="col-md-8">   
<div class="wb-frmvld">
	<form action='<c:url value="/" />' method="post" id="validation-signup">
	<input type="hidden" id="count" name="count" value="0"/>
		<div class="form-group">
			<label for="title" class="required"><span class="field-name">网名</span> <strong class="required">(必需)</strong></label>
			<input class="form-control" id="title" value="${user.title }" type="text" required="required" pattern=".{2,}" data-rule-minlength="2" size="40"  id="title${user.uid}" name="title" size="25" uid="${user.uid}" path="${user.path }" onchange="updateNode(this)"  placeholder="输入笔名"/>
		</div>
		<div class="form-group">
			<label for="email" class="required"><span class="field-name">电子邮件地址</span></label>
			<input class="form-control" id="email" name="email" value="${user.email }" type="text" pattern="email" type="email" size="40" placeholder="电子邮件地址为找回密码"/>
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
			<select id="role" name="role" path="${user.path }" onchange="updateNode(this)">
				<option value="User" <c:if test="${user.role=='User' }">selected</c:if> ><spring:message code="djn.user"/></option>
				<option value="Adminstrator" <c:if test="${user.role=='Administrator' }">selected</c:if> ><spring:message code="djn.adminstrator"/></option>
				<option value="Owner" <c:if test="${user.role=='Owner' }">selected</c:if> ><spring:message code="djn.owner"/></option>
			</select>
		</div>
		<div class="form-group">
			<label for="host"><span class="field-name">域名</span> <strong class="required"></strong></label>
			<input class="form-control" id="host" name="host" value="${user.host }" type="text" path="${user.path }" data-rule-minlength="4" size="40" onchange="updateNode(this)"/>
		</div>
		<div class="form-group">
			<label for="port"><span class="field-name">端口</span> </label>
			<input class="form-control" id="port" name="port" value="${user.port }" type="text" path="${user.path }" data-rule-minlength="4" size="40" onchange="updateNode(this)"/>
		</div>
		<div class="form-group">
			<label for="signingKey" class="required"><span class="field-name"><strong class="required">Signing Key</strong></span></label>
			<input class="form-control" id="signingKey" name="signingKey" value="${user.signingKey }" type="text" path="${user.path }" uid="${user.uid}" required="required" data-rule-minlength="4" size="40" onchange="updateNode(this)"/>
		</div>
		<div class="form-group">
			<label for="lastIp" class="required"><span class="field-name">登入IP</span> <strong class="required"></strong></label>
			<input class="form-control" id="lastIp" name="lastIp" value="${user.lastIp }" type="text" required="required" data-rule-alphanumeric="true" data-rule-minlength="4" size="40" disabled/>
		</div>	
		<div class="form-group">
			<label for="hostIp" class="required"><span class="field-name">云地址</span> <strong class="required"></strong></label>
			<input class="form-control" id="hostIp" name="hostIp" value="${user.hostIp }" type="text" required="required" data-rule-alphanumeric="true" data-rule-minlength="4" size="40" disabled/>
		</div>				
	<input type="submit" id="submit" value="确认" class="btn btn-primary"> <input type="reset" value="重填" class="btn btn-default">
	</form>
</div>
</div>
           <div class="col-md-4 well">
			<ul class="list-group menu list-unstyled">
				<li><a href="usermanager.html">用户管理</a></li>
				<li><a href="devicemanager.html">硬盘管理</a></li>
				<li><a href="export.html">用户输出</a></li>
				<li><a href="importuser.html">用户输入</a></li>
				
			</ul>
		</div>
 		</main>
        <nav class="wb-sec col-md-3 col-md-pull-9" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
        <h3 class="wb-navcurr"><img alt=""  width="32" class="img-responsive pull-right" src="<c:url value="/resources/images/usericon.png"></c:url>"> 用户目录</h3>
        <ul class="list-group menu list-unstyled">
	        <c:forEach items="${users.items}" var="item" varStatus="loop">
	            <li class="list-group-item"><a href='<c:url value="/admin/usereditor.html?path=${item.path}"></c:url>'>${item.title}</a><a href="javascript:deleteUser('${item.path}')"><span class="glyphicon glyphicon-trash pull-right"></span></a>
	        </c:forEach> 
        </ul>     
        </nav>

</div>
</div>

