<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="authz" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<!DOCTYPE html>
<html class="no-js" lang="zh" dir="ltr">

    <head>
    
<meta http-equiv="Content-type" content="text/html;charset=UTF-8">      
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><spring:message code="djn.dajana"/></title>
<meta content="width=device-width,initial-scale=1" name="viewport">
<meta name="description" content="DAJANA">
<link href='<c:url value="/resources/images/favicon.ico"></c:url>' rel="icon" type="image/x-icon">
<link rel="stylesheet" href='<c:url value="/resources/wet-boew/css/wet-boew.min.css"></c:url>'/>

<link rel="stylesheet" href='<c:url value="/resources/wet-boew/css/theme.css"></c:url>'/>
<link rel="stylesheet" href='<c:url value="/resources/wet-boew/css/messages.min.css"></c:url>'/>


<script>dataLayer = []; dataLayer1 = [];</script>
    </head>

    <body class="splash" vocab="http://schema.org/" typeof="WebPage">
		
        
<div id="bg"><img id="splash-image" src='<c:url value="/resources/images/splash1.jpg"></c:url>' alt=""></div><main role="main" property="mainContentOfPage">
<div class="sp-hb">
<div class="sp-bx col-xs-12">
<h1 property="name" class="wb-inv">dajana.ca</h1>
<div class="row">

<div class="col-xs-11 col-md-8">
<a href="http://dajana.cn/content/home.html">
<img alt="<spring:message code="djn.dajana"/>" src='<c:url value="/resources/images/logo.svg"></c:url>'/>
</a>
<!-- 
<object type="image/svg+xml" tabindex="-1" role="img" data='<c:url value="/resources/images/logo.svg"></c:url>' width="283" aria-label="<spring:message code="djn.dajana"/>"></object>
 -->
</div>
</div>
<div class="row">
<section class="col-xs-6 text-right">
<h2 class="wb-inv"><spring:message code="djn.cloud"/></h2>
<p><a href='<c:url value="/protected/mycloud"></c:url>' class="btn btn-primary"><spring:message code="djn.cloud"/></a></p>
</section>
<section class="col-xs-6">
<h2 class="wb-inv"><spring:message code="djn.dajana"/></h2>
<p><a href='<c:url value="/protected/mysite"></c:url>' class="btn btn-primary"><spring:message code="djn.website"/></a></p>
</section>
</div>
</div>
<div class="row">
<c:if test="${not empty param.error}">
<div class="alert alert-warning">
<p>${param.ip} 不是优鸿云，只能从内网登入。</p>
</div>
</c:if>
</div>
<div class="sp-bx-bt col-xs-12">
<div class="row">
<div class="col-xs-4 text-right">
<img title="<spring:message code="djn.scan_qr"/><spring:message code="djn.jiameng"/>" class="img-responsive" src="<c:url value="/content/home.qrb?path=http://sso.dajana.ca/signup%3Fhost%3D${host}"></c:url>" alt="<spring:message code="djn.jiameng"/>"/>
</div>
<div class="col-xs-4 col-md-4 text-center">
<%-- <authz:authorize ifAnyGranted="ROLE_USER,ROLE_ADMINISTRATOR,ROLE_OWNER"> --%>
<%--  </authz:authorize> --%>
 </div>
<div class="col-xs-4 full-right">
<a class="btn btn-default" href="http://dajana.cn:8888/yhyun"><img class="img-responsive" src ="/resources/images/yuhong-icon.png" alt="内网登入"/></a>
<%-- <img class="img-responsive" src="<c:url value="/resources/images/hongicon.png"></c:url>" title="<spring:message code="djn.yuhongweb"/>">
 --%></div>
</div>
</div>
</div>
</main>
<script>
    var splashImagePaths = "${splashImagePaths}".split(",");
    var numImages = ${numImages};
    var selectedImageIndex = 1;

    document.getElementById("splash-image").src = splashImagePaths[Math.floor((Math.random() * numImages))];

</script>
        

<!--[if gte IE 9 | !IE ]><!-->



<script src='<c:url value="/resources/wet-boew/js/jquery/2.1.4/jquery.min.js"></c:url>'></script>
<script src='<c:url value="/resources/wet-boew/js/wet-boew.min.js"></c:url>'></script>
<!--<![endif]-->

<!--[if lt IE 9]>
        <script src='<c:url value="/resources/wet-boew/js/ie8-wet-boew2.min.js"></c:url>'></script>
<![endif]-->

<script src='<c:url value="/resources/wet-boew/js/theme.min.js"></c:url>'></script>
    </body>
</html>


