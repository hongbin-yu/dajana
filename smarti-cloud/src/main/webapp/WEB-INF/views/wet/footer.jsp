<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:set var="host"><c:url value="/"></c:url></c:set>
<footer role="contentinfo" id="wb-info">
<nav role="navigation" class="container wb-navcurr visible-md visible-lg">
<h2><spring:message code="djn.about_this_site"/></h2>
<section class="col-sm-3">
<h3><spring:message code="djn.about_this_site"/></h3>
<ul class="list-unstyled">
<li><a href='<c:url value="/assets/templates/help/termnuse.html"></c:url>'><spring:message code="djn.term_use"/></a></li>
</ul>
</section>
<section class="col-sm-3">
<h3><spring:message code="djn.contact_us"/></h3>
<ul class="list-unstyled">
<li><a href='<c:url value="/assets/templates/help/qna.html"></c:url>'><spring:message code="djn.qna"/></a></li>
</ul>
</section>
<section class="col-sm-3">
<h3><spring:message code="djn.customer_service"/></h3>
<ul class="list-unstyled">
<li><a href='<c:url value="/assets/templates/help/help.html"></c:url>'><spring:message code="djn.online_help"/></a></li>
</ul>
</section>
 <section class="col-xs-3 col-sm-3 text-right">
 <ul class="list-unstyled">
<li><a href="http://home.dajana.net" title="优鸿云"><img src="/resources/images/yuhong-icon100.png" alt="优鸿云"></a></li>
</ul>
<%-- 	<object type="image/svg+xml" tabindex="-1" role="img" data="<c:url value="/resources/images/hongicon.png" />" aria-label="优鸿网络"></object>
 --%></section>	
</nav>

<div class="brand">
            <div class="container">
    <div class="row">
                    <div class="col-xs-6 visible-sm visible-xs tofpg text-center">
                        <a href="#wb-cont"><spring:message code="djn.goto_top"/> <span class="glyphicon glyphicon-chevron-up"></span></a>
                    </div>
               </div>
				<div class="col-xs-6 col-md-12 text-center"><spring:message code="djn.copy_right"/></div>
           </div>
</div>
</footer>

<!--[if gte IE 9 | !IE ]><!-->
<script src="<c:url value='/resources/wet-boew/js/jquery/2.1.4/jquery.min.js'/>"></script>
<script src="<c:url value='/resources/wet-boew/js/wet-boew.min.js'/>"></script>
<!--<![endif]-->
<!--[if lt IE 9]>
<script src="<c:url value='/resources/wet-boew/js/ie8-wet-boew2.min.js'/>"></script>
<![endif]-->
<script src='<c:url value="/resources/wet-boew/js/theme.min.js"></c:url>'></script>
<script src="<c:url value='/resources/tinymce/tinymce.min.js'/>"></script>
<script src="<c:url value='/resources/js/pageContent.js'/>"></script>
<script src="<c:url value='/resources/js/djn.js'/>"></script>


