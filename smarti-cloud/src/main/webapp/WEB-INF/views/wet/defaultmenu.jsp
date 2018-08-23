<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="authz" %>

<ul id="wb-tphp">
<li class="wb-slc">
<a class="wb-sl" href="#wb-cont">跳到主内容</a>
</li>
<li class="wb-slc visible-sm visible-md visible-lg">
<a class="wb-sl" href="#wb-info">跳到 "关于本网站"</a>
</li>
</ul>
<header role="banner">
<div id="wb-bnr" class="container">
<section id="wb-lng" class="visible-md visible-lg text-right">
<h2 class="wb-inv">选择管理菜单</h2>
<div class="row">
<div class="col-md-12">
<ul class="list-inline margin-bottom-none">
<authz:authorize ifAnyGranted="ROLE_USER,ROLE_ADMINISTRATOR,ROLE_OWNER">
<li><a href="<c:url value="/logout"/>"><img title="点击退出" alt="" src='<c:url value="/resources/images/unlock.png"></c:url>'>退出</a></li>
<li><a href="/mycart.html" title="<authz:authentication property="name" />"><authz:authentication property="name" /><img title="点击进入购物车" alt="" src='<c:url value="/resources/images/mycart.png"></c:url>'><span class="badge">0</span></a></li>
</authz:authorize>
<authz:authorize ifNotGranted="ROLE_USER">
<li><a href="<c:url value="/login"/>"><img title="点击打开登入页面" alt="" src='<c:url value="/resources/images/unlock.png"></c:url>'>登入</a></li>
<li><a href="<c:url value="/signup"/>"><img title="点击打开注册页面" alt="" src='<c:url value="/resources/images/tool_app.gif"></c:url>'>注册</a></li>
</authz:authorize>
</ul>
</div>
</div>
</section>
<div class="row">
<div class="brand col-xs-8 col-sm-9 col-md-6">
<a href="<c:url value="/"/>">
<object type="image/svg+xml" tabindex="-1" data="<c:url value="/resources/images/djnlogo.svg" />"></object>
</a>
</div>

<section class="wb-mb-links col-xs-4 col-sm-3 visible-sm visible-xs" id="wb-glb-mn">
<h2>查询与菜单</h2>
<ul class="list-inline text-right chvrn">
<li><a href="#mb-pnl" title="Search and menus" aria-controls="mb-pnl" class="overlay-lnk" role="button"><span class="glyphicon glyphicon-search"><span class="glyphicon glyphicon-th-list"><span class="wb-inv">Search and menus</span></span></span></a></li>
</ul>
<div id="mb-pnl"></div>
</section>
<section id="wb-srch" class="col-xs-6 text-right visible-md visible-lg">
<h2>搜索</h2>
<form action="#" method="post" name="cse-search-box" role="search" class="form-inline">
<div class="form-group">
<label for="wb-srch-q" class="wb-inv">查询</label>
<input id="wb-srch-q" list="wb-srch-q-ac" class="wb-srch-q form-control" name="q" type="search" value="" size="27" maxlength="150" placeholder="输入关键词查询"/>
<datalist id="wb-srch-q-ac">
<!--[if lte IE 9]><select><![endif]-->
<!--[if lte IE 9]></select><![endif]-->
</datalist>
</div>
<div class="form-group submit">
<button type="submit" id="wb-srch-sub" class="btn btn-primary btn-small" name="wb-srch-sub"><span class="glyphicon-search glyphicon"></span><span class="wb-inv">搜索</span></button>
</div>
</form>
</section>
</div>
</div>
<nav role="navigation" id="wb-sm" data-trgt="mb-pnl" class="wb-menu visible-md visible-lg">
	<div class="pnl-strt container nvbar">
	<h2 class="wb-inv"><spring:message code="djn.you_are_here"/>:</h2>
	<div class="container">
	<div class="row">
		<ul class="list-inline menu" role="menubar">
			<li><a title="<spring:message code="djn.goto_cloud"></spring:message>" href="<c:url value="/site/view.html"/>"><span class="glyphicon glyphicon-cloud"></span><spring:message code="djn.cloud"></spring:message></a></li>
			<li><a title="<spring:message code="djn.website"></spring:message>" href="<c:url value="/site/editor.html"/>"><span class="glyphicon glyphicon-globe"></span><spring:message code="djn.website"></spring:message></a></li>
			<li><a href='<c:url value="/protected/youchat.html"></c:url>' title="<spring:message code="djn.online_chat"></spring:message>"><span class="glyphicon glyphicon-envelope"></span><spring:message code="djn.chat"></spring:message><span class="badge"></span></a></li>
			<li><a href='<c:url value="/protected/youlook.html"></c:url>' title="<spring:message code="djn.online_chat"></spring:message>"><span class="glyphicon glyphicon-facetime-video"></span><spring:message code="djn.youlook"></spring:message><span class="badge"></span></a></li>
		
		</ul>
	</div>
	</div>
	</div>
</nav>
<nav role="navigation" id="wb-bc" property="breadcrumb">
	<h2>你在这里:</h2>
	<div class="container">
	<div class="row">
	${page.breadcrumb}
	</div>
	</div>
</nav>
<div class="container" id="header_message">
</div>
<c:if test="${error !=null }">
<div class="container">
<section class="alert alert-warning">
<h2>系统出错!</h2>
<div class="error" id="error">${error}</div>
</section>
</div>
</c:if>
<c:if test="${info !=null }">
<div class="container">
<div class="row">
<section class="alert alert-info">
<div class="error" id="info">${info}</div>
</section>
</div>
</div>
</c:if>

</header>

