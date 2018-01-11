<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:if test="${page.showLeftmenu}">
<div class="container">
<div class="row">
        <main role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
			 <div id="wb-cont">${content }</div>
			 <%@include file="../wet/pagedetails.jsp" %>		
 		</main>
        <nav class="wb-sec col-md-3 col-md-pull-9" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
            <div data-ajax-replace="<c:url value='/content/leftmenu.html?uid=${page.uid}'/>"></div>
        </nav>
        
</div>
</div>
</c:if>
<c:if test="${!page.showLeftmenu}">
        <main role="main" property="mainContentOfPage" class="container">
			 ${content }
			 <%@include file="../wet/pagedetails.jsp" %>		
 		</main>
</c:if>