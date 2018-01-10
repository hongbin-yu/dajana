<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
	<h3>评论</h3>
	<c:forEach var="item" items="${chats.items }">
	<div class="panel panel-default">
		<header class="panel-heading"><h5 class="panel-title">${item.createdBy } <span class="small text-center">${item.lastPublished }</span></h5></header>
		<div class="panel-body">${item.content }</div>
	</div>
	</c:forEach>