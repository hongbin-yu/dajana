<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<div class="container">
<div class="row">
        <main role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
        <h1>输出 - ${user.title}(${user.userName })</h1>
        <div class="col-md-8">
        <c:if test="${user.userName !=null}">
        <div class="wb-frmvld">
			<form action='<c:url value="/admin/export.html" />' method="post" id="validation-signup">
				<input type="hidden" name="userName" value="${user.userName }"/>
				<div class="form-group">
					<label for="title" class="required"><span class="field-name">源地址</span> <strong class="required">(必需)</strong></label>
					<input class="form-control" id="title" value="/content/${user.userName }" type="text" required="required" pattern=".{2,}" data-rule-minlength="2" size="80"  name="path" size="25" placeholder="输入源地址"/>
				</div>	
				<div class="form-group">
					<label for="location" class="required"><span class="field-name">目的地址</span> <strong class="required">(必需)</strong></label>
					<input class="form-control" id="location" name="location" value="/${user.userName }/assets" type="text" required="required" data-rule-minlength="4" size="40"/>
				</div>	
				<div class="form-group">
					<label for="filename" class="required"><span class="field-name">文件名</span> <strong class="required">(必需)</strong></label>
					<input class="form-control" id="filename" name="filename" value="systemview.xml" type="text" required="required" data-rule-minlength="4" size="40"/>
				</div>			
				<input type="submit" id="submit" value="确认" class="btn btn-primary"> <input type="reset" value="重填" class="btn btn-default">
			</form>
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
        <h3 class="wb-navcurr"><img alt=""  width="32" class="img-responsive pull-right" src="<c:url value="/resources/images/usericon.png"></c:url>"> 用户目录</h3>
        <ul class="list-group menu list-unstyled">
	        <c:forEach items="${users.items}" var="item" varStatus="loop">
	            <li id="${item.uid }" class="list-group-item"><a href='<c:url value="/admin/export.html?userName=${item.userName}"></c:url>'>${item.title}</a><img title="点击删除" onclick="javascript:removeTag('${item.uid}')" class="pull-right" src='<c:url value="/resources/images/cut.gif"></c:url>'></li>           
	        </c:forEach> 
        </ul>     
        </nav>

</div>
</div>

