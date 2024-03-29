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
<link href='<c:url value="/resources/favicon.ico"></c:url>' rel="icon" type="image/x-icon">
<link rel="stylesheet" href='<c:url value="/resources/wet-boew/css/wet-boew.min.css"></c:url>'/>

<link rel="stylesheet" href='<c:url value="/resources/wet-boew/css/theme.css"></c:url>'/>
<link rel="stylesheet" href='<c:url value="/resources/wet-boew/css/messages.min.css"></c:url>'/>

<link rel="apple-touch-icon" sizes="180x180" href="/resources/apple-touch-icon.png">
<link rel="icon" type="image/png" sizes="32x32" href="/resources/favicon-32x32.png">
<link rel="icon" type="image/png" sizes="16x16" href="/resources/favicon-16x16.png">
<link rel="manifest" href="/resources/site.webmanifest">
<link rel="mask-icon" href="/resources/safari-pinned-tab.svg" color="#5bbad5">
<meta name="msapplication-TileColor" content="#da532c">
<meta name="theme-color" content="#ffffff">

<script>dataLayer = []; dataLayer1 = [];</script>
    </head>

    <body class="splash" vocab="http://schema.org/" typeof="WebPage">
		
        
<div id="bg"><img id="splash-image" src='<c:url value="/resources/images/splash1.jpg"></c:url>' alt=""></div>
<main role="main" property="mainContentOfPage" >
<div class="sp-hb">
<div class="sp-bx col-xs-12">
<h1 property="name" class="wb-inv">dajana.net</h1>
<div class="row">

<div class="col-xs-11 col-md-8">
<a href="http://dajana.net/content/home.html">
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
<p><a href='<c:url value="/protected/mycloud"></c:url>' class="btn btn-danger" onclick="javascript:itemClick($(this))"><spring:message code="djn.cloud"/></a></p>
</section>
<section class="col-xs-6">
<h2 class="wb-inv"><spring:message code="djn.dajana"/></h2>
<p><a href='<c:url value="/protected/content.html"></c:url>' class="btn btn-primary"><spring:message code="djn.website"/></a></p>
</section>
</div>
</div>
<div class="sp-bx-bt col-xs-12">
<div class="row">
<c:if test="${not empty param.error}">
<div class="alert alert-warning">
<p>${param.ip} 不是优鸿云，只能从内网登入。</p>
</div>
</c:if>
</div>
<div class="row">
<div class="col-xs-4 text-right">
<a class="btn btn-default" href='<c:url value="/signup"></c:url>'>
<img title="<spring:message code="djn.scan_qr"/><spring:message code="djn.dajana"/>" class="img-responsive" src="/content/signup.qrb.jpg?path=https://${pageContext.request.serverName }:${pageContext.request.serverPort }<c:url value='/signup'/>" alt="<spring:message code="djn.dajana"/>" width="100" height="100"/>
</a>
</div>
<div class="col-xs-4 col-md-4 text-center">
<%-- <ul class="list-unstyled lst-spcd">
<li><a class="btn btn-success" href='<c:url value="/protected/youchat.html"></c:url>' title="<spring:message code="djn.online_chat"></spring:message>"><spring:message code="djn.chat"></spring:message></a></li>
<li><a class="btn btn-warning" href='<c:url value="/protected/youlook.html"></c:url>' title="<spring:message code="djn.youlook"></spring:message>"><spring:message code="djn.youlook"></spring:message></a></li>
</ul> --%>
 </div>
<div class="col-xs-4 pull-right">
<a class="btn btn-default" id="yuhongyun-href" href="http://home.dajana.net" title="内网登入"><img class="img-responsive" id="yuhongyun" src ="/resources/images/yuhong-icon100.png" alt="内网登入"/></a>
</div>

</div>
</div>
</div>
</main>

        

<!--[if gte IE 9 | !IE ]><!-->



<script src='<c:url value="/resources/wet-boew/js/jquery/2.1.4/jquery.min.js"></c:url>'></script>
<script src='<c:url value="/resources/wet-boew/js/wet-boew.min.js"></c:url>'></script>
<!--<![endif]-->

<!--[if lt IE 9]>
        <script src='<c:url value="/resources/wet-boew/js/ie8-wet-boew2.min.js"></c:url>'></script>
<![endif]-->

<script src='<c:url value="/resources/wet-boew/js/theme.min.js"></c:url>'></script>
<script src="/resources/js/upup.min.js"></script>

    </body>

  <script>
    UpUp.start({
       'content-url': '/resources/offline.html',
      'assets' : ['/resources/wet-boew/css/wet-boew.min.css',
                  '/resources/wet-boew/js/wet-boew.min.js'
                  ] 
    });
  </script>    
<script>
    var splashImagePaths = "${splashImagePaths}".split(",");
    var numImages = ${numImages};
    var selectedImageIndex = 1;
    document.getElementById("splash-image").src = splashImagePaths[Math.floor((Math.random() * numImages))]+"/origin.jpg";
  	$(document).ready(function() {
	
		if(location.hostname.indexOf("home.dajana.net")<0 && location.hostname.indexOf(".dajana.net")>0)
		$.ajax({
	          url: '//home.dajana.net',
	          success: function(result){
		          $("#yuhongyun").attr("src","/resources/images/lock-secure.png");
		          //$("#yuhongyun-href").attr("href","home."+location.origin);
	             //location.href=location.origin.replace("dajana.net","home.dajana.net");
	          },     
	          error: function(result){
	              //alert('timeout/error');
	          },
	          timeout: 3000
	       });
		}); 
	
	function itemClick(item) {
		item.append("<img src=\"/resources/images/loading16x16.gif\" alt=\"\">");
	}	
			
</script>    
</html>


