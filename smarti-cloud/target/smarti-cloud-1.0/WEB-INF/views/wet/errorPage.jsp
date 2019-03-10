<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" session="false"  %>
<html>
	<head>
		<title>SMARTi Web</title>
		<link rel="stylesheet" href="<c:url value="/resources/page.css" />" type="text/css" media="screen" />
		<link rel="stylesheet" href="<c:url value="/resources/form.css" />" type="text/css" media="screen" />
		<link rel="stylesheet" href="<c:url value="/resources/messages/messages.css" />" type="text/css" media="screen" />
 		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/images/pro_drop_1.css"/>" />
 		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/images/query.css"/>" />
		<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" type="text/css" media="screen" />

		<script src="<c:url value="/resources/stuHover.js"/>" type="text/javascript"></script>
		<script src="http://code.jquery.com/jquery-1.9.1.js" type="text/javascript"></script>
		<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js" type="text/javascript"></script>


	</head>
	<body>
		<div id="dropddownmenu">
			<tiles:insertTemplate template="dropdown_menu.jsp" />
		</div>

		<div id="header">
			<div id="smarti">SMARTi</div>
			<div id="query">
				<tiles:insertAttribute name="query" />
			</div>			
		</div>
		<div id="content">
			<div class="formInfo">
		  		<c:if test="${not empty errors}">
		  		<div class="error">
		  		<c:out value="${errors}"/>
		  		</div>
		  		</c:if>
		  		<c:if test="${not empty info}">
		  		<div class="info">
		  		<c:out value="${info}"/>
		  		</div>
		 	 	</c:if>
		 	</div>		
			<tiles:insertAttribute name="content" />
		</div>		
		<div id="footer">
			<tiles:insertTemplate template="footer.jsp" />
		</div>		
	</body>
</html>
