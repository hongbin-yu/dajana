<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"  %>
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
<div id="wb-bnr">
<div id="wb-bar">
<div class="container">
<div class="row">
<section id="wb-lng" class="visible-md visible-lg">
<h2>选择管理菜单</h2>
<ul class="text-right">
<li><a href="<c:url value="/logout"/>">Logout</a></li>
<li><a class="wb-lbx lbx-modal" href="#inline_profile" title='<authz:authentication property="name" />'><authz:authentication property="name" /></a></li>
</ul>
</section>
<section class="wb-mb-links col-xs-12 visible-sm visible-xs" id="wb-glb-mn">
<h2>Search and menus</h2>
<ul class="pnl-btn list-inline text-right">
<li><a href="#mb-pnl" title="Search and menus" aria-controls="mb-pnl" class="overlay-lnk btn btn-sm btn-default" role="button"><span class="glyphicon glyphicon-search"><span class="glyphicon glyphicon-th-list"><span class="wb-inv">Search and menus</span></span></span></a></li>
</ul>
<div id="mb-pnl"></div>
</section>
</div>
</div>
</div>
<div class="container">
<div class="row">
<div id="wb-sttl" class="col-md-8">
<a href="<c:url value="/"/>">
<object type="image/svg+xml" tabindex="-1" data="<c:url value="/resources/images/djnlogo.svg" />"></object>
<span>大家好<span class="wb-inv">, </span><small>共建，共享， 联锁云网</small></span>
</a>
</div>
<section id="wb-srch" class="col-md-4 visible-md visible-lg">

<h2>Search</h2>
<form action="${path}.html" method="get" role="search" class="form-inline">
<div class="form-group">
<label for="wb-srch-q">搜索</label>
<input id="wb-srch-q" class="form-control" name="q" type="search" value="${jcrForm.q}" size="27" maxlength="150">
<input type="hidden" name="n" value="${jcrForm.n}">
<input type="hidden" name="v" value="${jcrForm.v}">
<input type="hidden" name="s" value="${jcrForm.s}">
<input type="hidden" name="l" value="${currentNode.level-3}">
</div>
<button type="submit" id="wb-srch-sub" class="btn btn-default">搜索</button>
</form>

</section>
</div>
</div>
</div>
<nav role="navigation" id="wb-sm" data-trgt="mb-pnl" class="wb-menu visible-md visible-lg" typeof="SiteNavigationElement">
<div class="pnl-strt container visible-md visible-lg nvbar">
<h2>Topics menu</h2>
<div class="row">
<ul class="list-inline menu" role="menubar">
${navigation}
</ul>
</div>
</div>
</nav>
<%@include file="breadcrumb.jsp" %>
<div class="container" id="header_message">
</div>
<c:if test="${error !=null }">
<div class="container">
<section class="alert alert-warning">
<h2>System Error!</h2>
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
