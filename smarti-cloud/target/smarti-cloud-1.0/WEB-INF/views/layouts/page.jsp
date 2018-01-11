<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=GB18030" pageEncoding="UTF-8" session="false"  %>
<html lang="zho">
	<head>
		<title>大家网</title>
		<link rel="stylesheet" href="<c:url value="/resources/css/base.css" />" type="text/css" media="screen" />
		<link rel="stylesheet" href="<c:url value="/resources/page.css" />" type="text/css" media="screen" />
		<link rel="stylesheet" href="<c:url value="/resources/form.css" />" type="text/css" media="screen" />
		<link rel="stylesheet" href="<c:url value="/resources/messages/messages.css" />" type="text/css" media="screen" />
 		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/images/pro_drop_1.css"/>" />
 		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/images/query.css"/>" />
		<link rel="stylesheet" href="<c:url value="/resources/css/jquery-ui.css"/>" type="text/css" media="screen" />
	<!-- 	<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" type="text/css" media="screen" />
 	-->
		<script src="<c:url value="/resources/stuHover.js"/>" type="text/javascript"></script>
	 	<script src="<c:url value="/resources/js/jquery-1.9.1.js"/>" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-ui.js"/>" type="text/javascript"></script>


	</head>
	<body>

		<div class="page">
			<div id="dropddownmenu">
				<tiles:insertTemplate template="dropdown_menu.jsp" />
			</div>
			<div class="core">
				<div class="header">
					<div id="smarti">大家拿</div>
					<div id="query">
						<tiles:insertAttribute name="query" />
					</div>				
				</div>
				<div class="collayout">
					<div class="leftmenu">
						<tiles:insertAttribute name="menu" />
					</div>
					<div class="content">
						<div class="formInfo" id="formInfo">
					  		<c:if test="${not empty errors}">
					  		<div class="pageerror" id="error">
					  		<c:out value="${errors}"/>
					  		</div>
					  		</br/>
					  		</c:if>
					  		<c:if test="${not empty info}">
					  		<div class="pagemessage" id ="info">
					  		<c:out value="${info}"/>
					  		</div>
					  		<br/>
					 	 	</c:if>
					 	</div>	

						<tiles:insertAttribute name="content" />
					</div>
				<div class="footer">
					<div class="footerline"></div>
					<div class="foot1">Publish on 2014-08-15</div>
					<div class="foot2">
					<A title="Return to Top of Page" href="#top"><IMG src='<c:url value="/resources/images/tphp.gif"></c:url>' width=19 height=12></A>
					</div>
					<div class="foot3">SMARTi 5.3 FileMark Corporation</div>
				</div>
				</div>

			</div>		
		</div>
	</body>
</html>
