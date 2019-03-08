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


<link rel="apple-touch-icon" sizes="180x180" href="/resources/apple-touch-icon.png">
<link rel="icon" type="image/png" sizes="32x32" href="/resources/favicon-32x32.png">
<link rel="icon" type="image/png" sizes="16x16" href="/resources/favicon-16x16.png">
<link rel="manifest" href="/resources/site.webmanifest">
<link rel="mask-icon" href="/resources/safari-pinned-tab.svg" color="#5bbad5">
<meta name="msapplication-TileColor" content="#da532c">
<meta name="theme-color" content="#ffffff">


<noscript><link rel="stylesheet" href="<c:url value='/resources/wet-boew/css/noscript.min.css'/>"/></noscript>
<script>dataLayer1=[];</script>
</head>
