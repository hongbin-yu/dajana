<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<div class="container">
<div class="row">
        <main role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
        <h1>用户管理</h1>
        <div class="col-md-8"></div>
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
	            <li class="list-group-item"><a href='<c:url value="/admin/usereditor.html?path=${item.path}"></c:url>'>${item.title}</a><a href="javascript:deleteUser('${item.path}')"><span class="glyphicon glyphicon-trash pull-right"></span></a>
	            </li>           
	        </c:forEach> 
        </ul>
        </nav>
        
</div>
</div>

