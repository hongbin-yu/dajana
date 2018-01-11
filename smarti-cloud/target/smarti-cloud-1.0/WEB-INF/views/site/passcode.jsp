<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>


<!DOCTYPE html><!--[if lt IE 9]><html class="no-js lt-ie9" lang="zh" dir="ltr"><![endif]--><!--[if gt IE 8]><!-->
<html class="no-js" lang="zh" dir="ltr">
<!--<![endif]-->

    <head>
    
<meta http-equiv="Content-type" content="text/html;charset=UTF-8">      
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>大家拿</title>
<meta content="width=device-width,initial-scale=1" name="viewport">
<meta name="description" content="The Government of Canada website is a single point of access to all programs, services, departments, ministries and organizations of the Government of Canada.">
        <!--[if gte IE 9 | !IE ]><!-->
<link href="${contentPath}resources/images/favicon.ico" rel="icon" type="image/x-icon">
<link rel="stylesheet" href="${contentPath}resources/dist/css/wet-boew.min.css"/>
<!--<![endif]-->

<link rel="stylesheet" href="${contentPath}resources/dist/css/theme.css"/>
<link rel="stylesheet" href="${contentPath}resources/dist/css/messages.min.css"/>

<!--[if lt IE 9]>
<link href="${contentPath}resources/images/favicon.ico" rel="shortcut icon" />
<link rel="stylesheet" href="${contentPath}resources/dist/css/messages-ie.min.css">
<link rel="stylesheet" href="${contentPath}resources/dist/css/ie8-wet-boew.min.css" />
<script src="${contentPath}resources/dist/js/jquery/1.11.1/jquery.min.js"></script>
<script src="${contentPath}resources/dist/wet-boew/js/ie8-wet-boew.min.js"></script>
<![endif]-->



<script>dataLayer = []; dataLayer1 = [];</script>
    </head>

    <body class="splash" vocab="http://schema.org/" typeof="WebPage">
		
        
<div id="bg"><img id="splash-image" src="${contentPath}resources/images/changcheng.jpg" alt=""></div><main role="main" property="mainContentOfPage">
<div class="sp-hb">
<div class="sp-bx col-xs-12">
<h1 property="name" class="wb-inv">dajana.ca</h1>
<div class="row">
<div class="col-xs-11 col-md-8">
<object type="image/svg+xml" tabindex="-1" role="img" data="${contentPath}resources/images/logo.svg" width="283" aria-label="大家拿"></object>
</div>
</div>
<div class="row">
<section class="col-xs-12">
<form action='<c:url value="/passcode.html"></c:url>' method="POST" >
<input type="hidden" name="path" value="${path}">
<div class="form-group wb-frmvld">
	<label for="order">${title }-口令:<input class="form-control" required="required" id="passcode" type="password" name="passcode" value="${page.passcode }" size="35"  uid="${page.uid }" onchange="updateProperty(this)"/></label>
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
                splashImagePaths.push('${contentPath}resources/images/changcheng.jpg');
                numImages++;
                
                splashImagePaths.push('${contentPath}resources/images/changjian.jpg');
                numImages++;
                
                splashImagePaths.push('${contentPath}resources/images/huangshan.jpg');
                numImages++;

                splashImagePaths.push('${contentPath}resources/images/huangshan1.jpg');
                numImages++;

                splashImagePaths.push('${contentPath}resources/images/huangshan2.jpg');
                numImages++;
                
                splashImagePaths.push('${contentPath}resources/images/huangshan3.jpg');
                numImages++;
                
                splashImagePaths.push('${contentPath}resources/images/huanghe.jpg');
                numImages++;
                
                splashImagePaths.push('${contentPath}resources/images/huanghe2.jpg');
                numImages++;

    document.getElementById("splash-image").src = splashImagePaths[Math.floor((Math.random() * numImages))];

</script>
        

<!--[if gte IE 9 | !IE ]><!-->



<script src="${contentPath}resources/dist/js/jquery/2.1.4/jquery.min.js"></script>
<script src="${contentPath}resources/dist/wet-boew/js/wet-boew.min.js"></script>
<!--<![endif]-->

<!--[if lt IE 9]>
        <script src="${contentPath}resources/dist/wet-boew/js/ie8-wet-boew2.min.js"></script>
<![endif]-->

<script src="${contentPath}resources/dist/wet-boew/js/theme.min.js"></script>
    </body>
</html>


