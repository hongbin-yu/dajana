<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="authz" %>

<ul id="wb-tphp">
<li class="wb-slc">
<a class="wb-sl" href="#wb-cont"><spring:message code="djn.goto_main"/></a>
</li>
<li class="wb-slc visible-sm visible-md visible-lg">
<a class="wb-sl" href="#wb-info"><spring:message code="djn.goto_about_this_site"/></a>
</li>
</ul>
<header role="banner">
<div id="wb-bnr" class="container">
<section id="wb-lng" class="visible-md visible-lg text-right wb-inv">
<h2><spring:message code="djn.select_menu"/></h2>
<div class="row">
<div class="col-md-12">
<ul class="list-inline margin-bottom-none">
<authz:authorize ifAnyGranted="ROLE_USER,ROLE_ADMINISTRATOR,ROLE_OWNER">
<li><img title="点击编辑打开窗口" alt="" src='<c:url value="/resources/images/editIcon.gif"></c:url>'><a class="wb-lbx" title="编辑页面属性" href="<c:url value="/site/editpp.html?uid=${page.uid}"/>">编辑</a></li>
<li><img title="点击创建打开窗口" alt="" src='<c:url value="/resources/images/add.gif"></c:url>'><a class="wb-lbx" title="创建新页面" href="<c:url value="/site/createPage.html?uid=${page.uid}"/>">创建</a></li>
<li><img title="点击删除此页" alt="" src='<c:url value="/resources/images/cut.gif"></c:url>'><a class="wb-lbx" title="删除页面" href="<c:url value="/site/delete.html?path=${page.path}&redirect=${page.parent }"/>">删除</a></li>
<li><a href='<c:url value="/protected/youchat.html"></c:url>'><img title="点击打开在线通讯" alt="" src='<c:url value="/resources/images/chat22X22.png"></c:url>'>网信<span class="badge"></span></a></li>
<li><a href="<c:url value="/site/editProperties"/>"><img title="点击出版" alt="" src='<c:url value="/resources/images/up.gif"></c:url>'>出版</a></li>
<li><a href="<c:url value="/site/assets.html"/>" target='_blank'><img title="点击打开资源窗口,点击右键从下拉菜单选择打开新窗口" alt="" src='<c:url value="/resources/images/image.gif"></c:url>'>云站</a></li>
<li><button class="btn btn-warning btn-xs" onclick="javascript:ftrClose('/site/profile.html')" title="${user.role }"><span class="glyphicon glyphicon-user"></span><authz:authentication property='name' /></button></li>
<li><a href="<c:url value="/logout"/>"><spring:message code="djn.goback"/></a></li>
<li><a class="wb-lbx lbx-modal" href="#" title="<authz:authentication property='name' />"><authz:authentication property='name' /></a></li>
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
<nav role="navigation" id="wb-sm" data-ajax-replace='<c:url value="${page.path}.menu"></c:url>' data-trgt="mb-pnl" class="wb-menu visible-md visible-lg" typeof="SiteNavigationElement">
<div class="container nvbar">
<h2><spring:message code="djn.main_menu"/></h2>
<div class="row">
<ul class="list-inline menu">
${navigation }
</ul>
</div>
</div>
</nav>
<nav role="navigation" id="wb-bc" property="breadcrumb">
	<h2><spring:message code="djn.you_are_here"/>:</h2>
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
<h2><spring:message code="djn.system_error"/>!</h2>
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

