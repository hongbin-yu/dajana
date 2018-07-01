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
<section id="wb-lng" class="visible-md visible-lg text-right">
<h2><spring:message code="djn.select_menu"/></h2>
<div class="row">
<div class="col-md-12">
<ul class="list-inline margin-bottom-none">
<authz:authorize ifAnyGranted="ROLE_USER,ROLE_ADMINISTRATOR,ROLE_OWNER">
<li><a href="<c:url value="/site/assets.html"/>" title="<spring:message code="djn.goto_cloud"/>"><span class="glyphicon glyphicon-cloud"></span><spring:message code="djn.cloud"/></a></li>
<li><a href='<c:url value="/site/youchat.html"></c:url>' title="<spring:message code="djn.online_chat"/>"><span class="glyphicon glyphicon-envelope"></span><spring:message code="djn.chat"/><span class="badge"></span></a></li>
<li><a href='<c:url value="/site/youlook.html"></c:url>' title="<spring:message code="djn.online_chat"></spring:message>"><span class="glyphicon glyphicon-facetime-video"></span><spring:message code="djn.youlook"></spring:message><span class="badge"></span></a></li>
<li><a href='<c:url value="/site/preview.html?path=${page.path }"></c:url>' target="_BLANK" title="点击预览"><span class="glyphicon glyphicon-eye-open"></span><spring:message code="djn.preview"/></a></li>
<li><a href="javascript:publish('${page.uid}')"  title="<spring:message code="djn.publish"/>"><span class="glyphicon glyphicon-cloud-upload"></span><spring:message code="djn.publish"/></a></li>
<li><button disabled class="btn btn-success btn-xs" title="<spring:message code="djn.edit_properties"/>" onclick="javascript:ftrClose('<c:url value="/site/editpp.html?uid=${page.uid}"/>')"><span class="glyphicon glyphicon-list"></span><spring:message code="djn.properties"/></button></li>
<li><button disabled class="btn btn-primary btn-xs" title="<spring:message code="djn.create_page"/>" onclick="javascript:ftrClose('<c:url value="/site/createPage.html?uid=${page.uid}"/>')"><span class="glyphicon glyphicon-plus"></span><spring:message code="djn.create"/></button></li>
<c:if test="${page.status!='true' }">
<li>
<button class="btn btn-danger btn-xs" title="<spring:message code="djn.delete_page"/>" onclick="javascript:ftrClose('<c:url value="/site/deletePage.html?path=${page.path}&redirect=${page.parent }"/>')"><span class="glyphicon glyphicon-remove"></span><spring:message code="djn.delete"/></button>
</li>
</c:if>
<c:if test="${page.status=='true' }">
<li><a href="<c:url value="${page.path }.html"/>"><span class="glyphicon glyphicon-open"></span><spring:message code="djn.goback"/></a></li>
</c:if>
<li><a href="<c:url value="/logout"/>"><span class="glyphicon glyphicon-log-out"></span><spring:message code="djn.logout"/></a></li>
</authz:authorize>


</ul>
</div>
</div>
</section>
<!-- <div id="data-inview" class="row wb-inview reverse bar-demo" data-inview="left-bar">
 -->
 <div class="row">
  <div class="brand col-xs-8 col-sm-9 col-md-6">
<a href="<c:url value="/"/>">
<object type="image/svg+xml" tabindex="-1" data="<c:url value="/resources/images/djnlogo.svg" />"></object>
</a>
</div>

<section class="wb-mb-links col-xs-4 col-sm-3 visible-sm visible-xs" id="wb-glb-mn">
<h2><spring:message code="djn.search_menu"/></h2>
<ul class="list-inline text-right chvrn">
<li><a href="#mb-pnl" title="查询与菜单" aria-controls="mb-pnl" class="overlay-lnk" role="button"><span class="glyphicon glyphicon-search"><span class="glyphicon glyphicon-th-list"><span class="wb-inv"><spring:message code="djn.search_menu"/></span></span></span></a></li>
</ul>
<div id="mb-pnl"></div>
</section>
<section id="wb-srch" class="col-xs-6 text-right visible-md visible-lg">
<h2><spring:message code="djn.search"/></h2>
<form action="#" method="post" name="cse-search-box" role="search" class="form-inline" accept-charset="utf-8">
<div class="form-group">
<label for="wb-srch-q" class="wb-inv"><spring:message code="djn.search"/></label>
<input id="wb-srch-q" list="wb-srch-q-ac" class="wb-srch-q form-control" name="kw" type="search" value="" size="27" maxlength="150" placeholder="输入关键词查询"/>
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

<nav role="navigation" id="wb-sm" data-trgt="mb-pnl" class="wb-menu visible-md visible-lg" typeof="SiteNavigationElement">
<%-- data-ajax-replace='<c:url value="/site/menu.html?path=${page.path}"></c:url>' --%>
<div class="container nvbar">
<h2><spring:message code="djn.main_menu"/></h2>
<div class="row">
<ul class="list-inline menu">
${navigation }
</ul>
</div>
</div>
</nav>
<!-- <section class="wb-inview show-none bar-demo" data-inview="top-bar"> -->
<nav role="navigation" id="wb-bc" property="breadcrumb">
	<h2><spring:message code="djn.you_are_here"/>:</h2>
	<div class="container">
	<div class="row">
	<div class="btn-group btn-group-justified">
	 <button class="btn btn-default btn-sm pull-right" onclick="javascript:openOverlay('${page.uid }','right-bar')" title="打开/关闭微网"><span class="glyphicon glyphicon-globe pull-right"></span></button>
	 <button class="btn btn-default btn-sm pull-right" onclick="javascript:openOverlay('${page.uid }','left-bar')" title="打开/关闭微云"><span class="glyphicon glyphicon-cloud pull-right"></span></button>
<!-- 	 <button class="btn btn-default btn-block pull-right" title="打开本机资源" onclick="javascript:openFiles()"><span class="glyphicon glyphicon-picture pull-right"></span></button>		 -->		 
	${page.breadcrumb}
    </div>
	</div>
	</div>
</nav>
<!-- </section> -->
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
<span class="wb-sessto" data-wb-sessto='{logouturl: "../logout"}'></span>
</header>

