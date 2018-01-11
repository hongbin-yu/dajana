<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=GB18030" pageEncoding="UTF-8" session="false" %>
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
<section id="wb-lng" class="visible-md visible-lg text-right wb-inv">
<h2>选择管理菜单</h2>
<div class="row">
<div class="col-md-12">
<ul class="list-inline margin-bottom-none">
<li><a class="btn btn-primary bnt-xs" href="javascript:openPdf()">打开PDF</a>
<authz:authorize ifAnyGranted="ROLE_USER,ROLE_ADMINISTRATOR,ROLE_OWNER">
<li><img title="点击编辑打开首页" src='<c:url value="/resources/images/web-icon.png"></c:url>'><a title="编辑首页" href="<c:url value="/editor.html"/>">网站</a></li>
<li><a href="<c:url value="/logout"/>">退出</a></li>
</authz:authorize>
<li><a class="wb-lbx lbx-modal" href="#" title="${user.name }">${user.title }</a></li>
</ul>
</div>
</div>
</section>
<div class="row">
<div class="brand col-xs-8 col-sm-9 col-md-6">
<a href="<c:url value="/"/>">
<object type="image/svg+xml" tabindex="-1" data="<c:url value="/resources/images/logo.png" />"></object>
</a>
</div>

<section id="wb-srch" class="col-xs-6 text-right visible-md visible-lg">
<h2 class="wb-inv">查询</h2>
          

<form action="/assets.html" method="post" name="cse-search-box" role="search" class="form-inline" accept-charset="utf-8">
<div class="form-group">
<label for="wb-srch-q" class="wb-inv">查询</label>

<input id="wb-srch-q" list="wb-srch-q-ac" class="wb-srch-q form-control" name="q" type="search" value="" size="27" maxlength="150" placeholder="输入关键词查询"/>
<input type="hidden" name="_charset_" value="UTF-8"/>

<datalist id="wb-srch-q-ac">
<!--[if lte IE 9]><select><![endif]-->
<!--[if lte IE 9]></select><![endif]-->
</datalist>
</div>
<div class="form-group submit">
<button type="submit" id="wb-srch-sub" class="btn btn-primary btn-small" name="wb-srch-sub"><span class="glyphicon-search glyphicon"></span><span class="wb-inv">查询</span></button>

</div>
</form>

</section>

        <section class="wb-mb-links col-xs-4 col-sm-3 visible-sm visible-xs" id="wb-glb-mn">
            <h2>查询和菜单</h2>
            <ul class="list-inline text-right chvrn">
                <li>
                    <a href="#mb-pnl" title="查询和菜单" aria-controls="mb-pnl" class="overlay-lnk" role="button">
                <span class="glyphicon glyphicon-search">
                    <span class="glyphicon glyphicon-th-list">
                        <span class="wb-inv">查询和菜单</span>
                    </span>
                </span>
                    </a>
                </li>
            </ul>
            <div id="mb-pnl"></div>
        </section>
</div>
</div>

<nav role="navigation" id="wb-sm" data-trgt="mb-pnl" class="wb-menu visible-md visible-lg" typeof="SiteNavigationElement">
<div class="pnl-strt container visible-md visible-lg nvbar">
<h2>主题菜单</h2>
<div class="row">
<ul class="list-inline menu" role="menubar">
${navigation }
</ul>
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

