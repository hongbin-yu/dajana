<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"  %>
<head>
<meta http-equiv="Content-type" content="text/html;charset=UTF-8">
<title><spring:message code="djn.dajana"/>-${page.title}</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width,initial-scale=1" name="viewport">
<!-- Meta data -->
<meta name="description" content="${page.description}">
<meta name="keywords" content="${page.keywords}">
<meta name="author" content="<spring:message code="djn.dajana"/>">
<!-- Meta data-->
<!--[if gte IE 9 | !IE ]><!-->
<link href="<c:url value='/resources/favicon.ico'/>" rel="icon" type="image/x-icon">
<link rel="stylesheet" href="<c:url value='/resources/wet-boew/css/wet-boew.min.css'/>">
<!--<![endif]-->

<link rel="stylesheet" href="<c:url value='/resources/wet-boew/css/theme.min.css'/>">
<!--[if lt IE 9]>
<link href="<c:url value='/resources/images/favicon.ico'/>">
<link rel="stylesheet" href="<c:url value='/resources/wet-boew/css/ie8-wet-boew.min.css'/>">
<script src="<c:url value='/resources/wet-boew/js/jquery/1.11.1/jquery.min.js'/>"></script>
<script src="<c:url value='/resources/wet-boew/js/ie8-wet-boew.min.js'/>"></script>
<![endif]-->



<noscript><link rel="stylesheet" href="<c:url value='/resources/wet-boew/css/noscript.min.css'/>"/></noscript>
<script>dataLayer1=[];</script>
</head>
