<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=GB18030" pageEncoding="UTF-8" session="false" %>
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
<%-- <li><img title="<spring:message code="djn.edit"/>" lat="" src='<c:url value="/resources/images/editIcon.gif"></c:url>'><a title="<spring:message code="djn.edit"/>" href="<c:url value="/editor.html?path=${page.path }"/>"><spring:message code="djn.edit"/></a></li>
<li><a href='<c:url value="/protected/chat.html"></c:url>'><img title="<spring:message code="djn.online_chat"/>" alt="" src='<c:url value="/resources/images/chat16X16.png"></c:url>'><spring:message code="djn.chat"/><span class="badge"></span></a></li>
 --%><li><a href="<c:url value="/logout"/>"><span class="glyphicon glyphicon-log-out"></span><spring:message code="djn.logout"/></a></li>
<%-- <li><a href="/mycart.html" title="<authz:authentication property="name" />"><authz:authentication property="name" /><img title="<spring:message code="djn.shopping_cart"/>" alt="" src='<c:url value="/resources/images/mycart.png"></c:url>'><span class="badge"></span></a></li>
 --%></authz:authorize>
<authz:authorize ifNotGranted="ROLE_USER">
<li><a href="<c:url value="/login"/>"><span class="glyphicon glyphicon-log-in"></span><spring:message code="djn.login"/></a></li>
<li><a href="<c:url value="/signup"/>"><span class="glyphicon glyphicon-flag"></span><spring:message code="djn.register"/></a></li>
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

<section id="wb-srch" class="col-xs-6 text-right visible-md visible-lg">
<h2><spring:message code="djn.search"/></h2>
<form action="#" method="post" name="cse-search-box" role="search" class="form-inline">
<div class="form-group">
<label for="wb-srch-q" class="wb-inv"><spring:message code="djn.search"/></label>
<input id="wb-srch-q" list="wb-srch-q-ac" class="wb-srch-q form-control" name="q" type="search" value="" size="27" maxlength="150" placeholder="<spring:message code="djn.input_keyword_search"/>"/>
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

