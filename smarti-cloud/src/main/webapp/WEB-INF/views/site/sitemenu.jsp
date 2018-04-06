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
<h2><spring:message code="djn.search_menu"/></h2>
<!-- <div class="row wb-inview show-none bar-demo" data-inview="left-bar"> -->
<div class="row">
<div class="col-md-12">
<ul class="list-inline margin-bottom-none">
<authz:authorize ifAnyGranted="ROLE_USER,ROLE_ADMINISTRATOR,ROLE_OWNER">
<li><a href="<c:url value="/site/assets.html"/>" target='_blank'><img title="<spring:message code="djn.goto_cloud"/>" alt="" src='<c:url value="/resources/images/cloud-icon.png"></c:url>'><spring:message code="djn.cloud"/></a></li>
<li><a href='<c:url value="/protected/youchat.html"></c:url>'><img title="<spring:message code="djn.online_chat"/>" alt="" src='<c:url value="/resources/images/chat16X16.png"></c:url>'><spring:message code="djn.chat"/><span class="badge"></span></a></li>
<li><a href='<c:url value="/protected/youlook.html"></c:url>' title="<spring:message code="djn.online_chat"></spring:message>"><span class="glyphicon glyphicon-facetime-video"></span><spring:message code="djn.youlook"></spring:message><span class="badge"></span></a></li>

<li><a href="<c:url value="/site/editProperties"/>"><img title="<spring:message code="djn.publish"/>" src='<c:url value="/resources/images/up.gif"></c:url>'><spring:message code="djn.publish"/></a></li>
<li class="wb-inv"><img title="<spring:message code="djn.edit_properties"/>" alt="" src='<c:url value="/resources/images/editIcon.gif"></c:url>'><a class="wb-lbx" title="<spring:message code="djn.edit_properties"/>" href="<c:url value="/editpp.html?uid=${page.uid}"/>"><spring:message code="djn.properties"/></a></li>
<li class="wb-inv"><img title="<spring:message code="djn.create_page"/>" alt="" src='<c:url value="/resources/images/add.gif"></c:url>'><a class="wb-lbx" title="<spring:message code="djn.create_page"/>" href="<c:url value="/site/createPage.html?uid=${page.uid}"/>"><spring:message code="djn.create"/></a></li>
<c:if test="${page.status!='true' }">
<li><img title="<spring:message code="djn.delete_page"/>" alt="" src='<c:url value="/resources/images/cut.gif"></c:url>'><a class="wb-lbx" title="delete_page" href="<c:url value="/delete.html?path=${page.path}&redirect=${page.parent }"/>"><spring:message code="djn.delete"/></a></li>
</c:if>
<li><a href="<c:url value="${page.path }.html"/>"><spring:message code="djn.goback"/></a></li>
<li><a class="wb-lbx lbx-modal" href="#" title="<authz:authentication property='name' />"><authz:authentication property='name' /></a></li>
</authz:authorize>
</ul>
</div>
</div>
</section>
<div class="row">
<div class="brand col-xs-8 col-sm-9 col-md-6">
<a href="<c:url value="/"/>">
<object type="image/svg+xml" tabindex="-1" data="<c:url value="/resources/images/logo.svg" />"></object>
</a>
</div>

<section id="wb-srch" class="col-xs-6 text-right visible-md visible-lg">
<h2 class="wb-inv"><spring:message code="djn.search"/></h2>
          

<form action="/en/sr.html" method="get" name="cse-search-box" role="search" class="form-inline">
<div class="form-group">
<label for="wb-srch-q" class="wb-inv"><spring:message code="djn.search"/></label>

<input id="wb-srch-q" list="wb-srch-q-ac" class="wb-srch-q form-control" name="q" type="search" value="" size="27" maxlength="150" placeholder="输入关键词查询"/>
<input type="hidden" name="_charset_" value="UTF-8"/>

<datalist id="wb-srch-q-ac">
<!--[if lte IE 9]><select><![endif]-->
<!--[if lte IE 9]></select><![endif]-->
</datalist>
</div>
<div class="form-group submit">
<button type="submit" id="wb-srch-sub" class="btn btn-primary btn-small" name="wb-srch-sub"><span class="glyphicon-search glyphicon"></span><span class="wb-inv"><spring:message code="djn.search"/></span></button>

</div>
</form>

</section>

        <section class="wb-mb-links col-xs-4 col-sm-3 visible-sm visible-xs" id="wb-glb-mn">
            <h2><spring:message code="djn.search_menu"/></h2>
            <ul class="list-inline text-right chvrn">
                <li>
                    <a href="#mb-pnl" title="<spring:message code="djn.search_menu"/>" aria-controls="mb-pnl" class="overlay-lnk" role="button">
                <span class="glyphicon glyphicon-search">
                    <span class="glyphicon glyphicon-th-list">
                        <span class="wb-inv"><spring:message code="djn.search"/></span>
                    </span>
                </span>
                    </a>
                </li>
            </ul>
            <div id="mb-pnl"></div>
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

