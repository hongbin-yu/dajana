<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>

<!DOCTYPE html>
<html class="no-js" lang="zh" dir="ltr">
<head>
    
<meta http-equiv="Content-type" content="text/html;charset=UTF-8">      
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>大家拿</title>
<meta content="width=device-width,initial-scale=1" name="viewport">
<meta name="description" content="The Government of Canada website is a single point of access to all programs, services, departments, ministries and organizations of the Government of Canada.">

<link href="<c:url value='/resources/images/favicon.ico'></c:url>" rel="icon" type="image/x-icon">
<link rel="stylesheet" href="<c:url value='/resources/wet-boew/css/wet-boew.min.css'></c:url>"/>


<link rel="stylesheet" href="<c:url value='/resources/wet-boew/css/theme.css'></c:url>"/>
<link rel="stylesheet" href="<c:url value='/resources/wet-boew/css/messages.min.css'></c:url>"/>



<script>dataLayer = []; dataLayer1 = [];</script>
    </head>

    <body class="splash" vocab="http://schema.org/" typeof="WebPage">
	        
<div id="bg"><img id="splash-image" src='<c:url value="/resources/images/splash4.jpg"></c:url>' alt=""></div><main role="main" property="mainContentOfPage">
<div class="sp-hb">
<div class="sp-bx col-xs-12">
<h1 property="name" class="wb-inv">dajana.cn</h1>
<div class="row">
<div class="col-xs-11 col-md-8">
<object type="image/svg+xml" tabindex="-1" role="img" data='<c:url value="/resources/images/logo.svg"></c:url>' width="283" aria-label="大家拿"></object>
</div>
</div>
<div class="row">
<section class="col-xs-12">
<c:if test="${message eq 'error'}">
<section class="alert alert-warning">
<h3><spring:message code="djn.passcode_fail"/>!</h3>
<div class="error">
  	<spring:message code="djn.passcode_error"/>!
 </div>
</section>
</c:if>

<form action='<c:url value="${path}.passcode"></c:url>' method="POST" >
<div class="form-group wb-frmvld">
	<label for="order">${title }-口令:<input class="form-control" required="required" id="passcode" type="password" name="passcode" value="" size="35"/></label>
    <button type="submit" class="btn btn-primary">确认</button>
</div>	
</form>
</section>
</div>
</div>
</div>
</main>
<script>
    var splashImagePaths = [];
    var numImages = 0;
    var selectedImageIndex = 1;
    var imagePath ="/templates/assets/splash";
    if("${contentPath}".indexOf("/smarti-cloud")>=0) 
        imagePath = "/smarti-cloud"+imagePath;
                splashImagePaths.push(imagePath+'/splash1.jpg');
                numImages++;
                
                splashImagePaths.push(imagePath+'/splash2.jpg');
                numImages++;
                
                splashImagePaths.push(imagePath+'/splash3.jpg');
                numImages++;

                splashImagePaths.push(imagePath+'/splash4.jpg');
                numImages++;

                splashImagePaths.push(imagePath+'/splash5.jpg');
                numImages++;
                
                splashImagePaths.push(imagePath+'/splash6.jpg');
                numImages++;
                
                splashImagePaths.push(imagePath+'/splash7.jpg');
                numImages++;
                
                splashImagePaths.push(imagePath+'/splash8.jpg');
                numImages++;

    document.getElementById("splash-image").src = splashImagePaths[Math.floor((Math.random() * numImages))];

</script>
        

<script src="/resources/wet-boew/js/jquery/2.1.4/jquery.min.js"></script>
<script src="/resources/wet-boew/js/wet-boew.min.js"></script>


<script src="$/resources/wet-boew/js/theme.min.js"></script>
    </body>
</html>


