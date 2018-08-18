<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
	<c:forEach items="${assets.items }" var="item" varStatus="loop">
	<li id="${item.uid}" class="col-md-2">
	    <a href="<c:url value='${item.link}&w=12'></c:url>">
			<img id="img${item.uid}" src="<c:url value='${item.iconSmall }'></c:url>" class="img-responsive pull-left mrgn-rght-md img-rounded" draggable="true"/>
		</a>
	</li>
	</c:forEach>