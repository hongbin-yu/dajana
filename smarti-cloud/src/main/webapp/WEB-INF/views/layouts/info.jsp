<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page session="false" %>

	<c:if test="${not empty errors}">
		<c:out value="${errors}"/>
	</c:if>
 	<c:if test="${not empty info}">
 		<c:out value="${info}"/>
	</c:if>