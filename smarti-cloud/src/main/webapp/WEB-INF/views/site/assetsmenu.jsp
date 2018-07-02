<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="authz" %>

<ul id="wb-tphp">
<li class="wb-slc">
<a class="wb-sl" href="#wb-cont"><spring:message code="djn.goto_main"></spring:message></a>
</li>
<li class="wb-slc visible-sm visible-md visible-lg">
<a class="wb-sl" href="#wb-info"><spring:message code="djn.goto_about_this_site"></spring:message></a>
</li>
</ul>
<header role="banner">
<div id="wb-bnr" class="container">
<section id="wb-lng" class="visible-md visible-lg text-right">
<h2><spring:message code="djn.select_menu"></spring:message></h2>
<div class="row">
<div class="col-md-12">
<ul class="list-inline margin-bottom-none">
<authz:authorize ifAnyGranted="ROLE_USER,ROLE_ADMINISTRATOR,ROLE_OWNER">
<li><a title="<spring:message code="djn.website"></spring:message>" href="<c:url value="editor.html"/>"><span class="glyphicon glyphicon-globe"></span><spring:message code="djn.website"></spring:message></a></li>
<%-- <li><a title="<spring:message code="djn.b2c"></spring:message>" href="<c:url value="b2c.html"/>"><span class="glyphicon glyphicon-gift"></span><spring:message code="djn.b2c"></spring:message></a></li>
 --%><li><a href='<c:url value="/protected/youchat.html"></c:url>' title="<spring:message code="djn.online_chat"></spring:message>"><span class="glyphicon glyphicon-envelope"></span><spring:message code="djn.chat"></spring:message><span class="badge"></span></a></li>
<li><a href='<c:url value="/protected/youlook.html"></c:url>' title="<spring:message code="djn.online_chat"></spring:message>"><span class="glyphicon glyphicon-facetime-video"></span><spring:message code="djn.youlook"></spring:message><span class="badge"></span></a></li>
<li><a href="javascript:openPdf()" class="btn btn-primary btn-xs"><span class="glyphicon glyphicon-open"></span>PDF</a>
<li><a href="javascript:deleteFiles()" class="btn btn-danger btn-xs"><span class="glyphicon glyphicon-remove"></span><spring:message code="djn.delete"></spring:message></a>
<li><a href="/protected/profile.html"><button class="btn btn-danger btn-xs" title="${user.role }"><span class="glyphicon glyphicon-user"></span><authz:authentication property='name' /></button></a></li>
<li><a href="<c:url value="/logout"/>"><span class="glyphicon glyphicon-log-out"></span><spring:message code="djn.logout"/></a></li>
</authz:authorize>
<%-- <li><a class="wb-lbx lbx-modal" href="#" title="${user.name }">${user.title }</a></li>
 --%></ul>
</div>
</div>
</section>
<div class="row">
<div class="brand col-xs-8 col-sm-9 col-md-6">
<a href="<c:url value="/"/>">
<object type="image/svg+xml" tabindex="-1" data="<c:url value="/resources/images/djnlogo.svg" />"></object>
</a>
</div>

<section id="wb-srch" class="col-xs-6 text-right visible-md visible-lg">
<h2 class="wb-inv"><spring:message code="djn.search"/></h2>
<form action='<c:url value="assets.html"></c:url>' method="get" name="cse-search-box" role="search" class="form-inline" accept-charset="UTF-8">
<div class="form-group">
<label for="wb-srch-q" class="wb-inv"><spring:message code="djn.search"/></label>
<%-- <select name="type" onchange="this.form.submit()">
<option value="" <c:if test="${type=='' }">selected</c:if> ><spring:message code="djn.all"/></option>
<option value="child" <c:if test="${type=='child' }">selected</c:if> ><spring:message code="djn.child"/></option>
<option value="image" <c:if test="${type=='image' }">selected</c:if> ><spring:message code="djn.image"/></option>
<option value="video" <c:if test="${type=='video' }">selected</c:if> ><spring:message code="djn.video"/></option>
<option value="audio" <c:if test="${type=='audio' }">selected</c:if> ><spring:message code="djn.audeo"/></option>
<option value="application" <c:if test="${type=='application' }">selected</c:if> ><spring:message code="djn.file"/></option>
</select> --%>
<input id="wb-srch-q" list="wb-srch-q-ac" class="wb-srch-q form-control" name="kw" type="search" value="${kw}" size="22" maxlength="150" placeholder="<spring:message code="djn.input_keyword_search"/>"/>
<input type="hidden" name="path" value="${folder.path}"/>
<input type="hidden" name="input" value="${input}"/>
<input type="hidden" name="type" value="${type}"/>
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
                        <span class="wb-inv"><spring:message code="djn.search_menu"/></span>
                    </span>
                </span>
                    </a>
                </li>
            </ul>
            <div id="mb-pnl"></div>
        </section>
</div>
</div>
<nav role="navigation" id="wb-sm" data-trgt="mb-pnl" class="wb-menu visible-md visible-lg" style="border-top:8px solid #335075" typeof="SiteNavigationElement">
</nav>
<nav role="navigation" id="wb-bc" property="breadcrumb">
	<h2><spring:message code="djn.you_are_here"/>:</h2>
	<div class="container">
	<div class="row">

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
<span class="wb-sessto" data-wb-sessto='{logouturl: "../logout"}'></span>
</header>