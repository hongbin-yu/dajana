<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page session="false" import="javax.jcr.ImportUUIDBehavior"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<div class="container">
<div class="row">
        <main role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
        <h1>输入 - ${user.title}(${user.userName })</h1>
        <div class="col-md-8">

        <c:if test="${user.userName !=null}">
        <div class="wb-frmvld">
	<fieldset>
	<form action="importuser.html" method="POST" id="validation-upload" enctype="multipart/form-data">
		<div class="form-group">
			<label for="fileUpload" class="required"><span class="field-name">Import system document files</span> <strong class="required">(required)</strong></label>
			<input class="form-control" type="file" id="fileUpload" name="file" size="30" required="required"  multiple/>
		</div>
		<div class="form-group" id="selectedFiles">
		</div>
		<div class="form-group">
			<label for="path" class="required"><span class="field-name">Path</span> <strong class="required">(required)</strong></label>
			<input class="form-control" id="path"  name="path" type="text" required="required"  value='/content/${user.userName }' pattern=".{2,}" data-rule-minlength="2" size="30"/>
		</div>	
		<div class="form-group">
			<label for="behavior" class="required"><span class="field-name">Behave on Exiting node</span> <strong class="required">(required)</strong></label>
			<select class="form-control" id="behavior"  name="behavior"  required="required" >
			<option value="">[Select item]</option>
			<option value="<%=ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW%>">Create new</option>
			<option value="<%=ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING%>">Replace existing</option>
			<option value="<%=ImportUUIDBehavior.IMPORT_UUID_COLLISION_REMOVE_EXISTING%>">Remove existing</option>
			<option value="<%=ImportUUIDBehavior.IMPORT_UUID_COLLISION_THROW%>">Throw exception</option>
			</select>
		</div>	
		<input type="submit" value="Import" class="btn btn-primary"> <input type="reset" value="Reset" class="btn btn-default">
	</form>
	</fieldset>
		</div>
		</c:if>  
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
        <h3 class="wb-navcurr"><span class="glyphicon glyphicon-user"></span> 用户目录</h3>
        <ul class="list-group menu list-unstyled">
	        <c:forEach items="${users.items}" var="item" varStatus="loop">
	            <li id="${item.uid }" class="list-group-item"><a href='<c:url value="/admin/importuser.html?userName=${item.userName}"></c:url>'>${item.title}</a></li>           
	        </c:forEach> 
        </ul>     
        </nav>

</div>
</div>

