<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
    <h2 id="wb-sec-h" class="wb-inv">左菜单</h2>
    <section class="list-group menu list-unstyled">
        <h3 class="wb-navcurr">
        <c:if test="${page.parent=='/content' }">
        <a href='<c:url value="/content/home.html"></c:url>'><img src='<c:url value="/resources/images/arrowleft.png"/>'/>大家拿</a>
        </c:if>
        <c:if test="${page.parent!='/content' }">
        <a href='<c:url value="${parent.path}.html"></c:url>'><img src='<c:url value="/resources/images/arrowleft.png"/>'/>${parent.title}</a>
        </c:if>        
        </h3>
        <ul class="list-group menu list-unstyled">
        <c:forEach items="${menu.items}" var="item" varStatus="loop">
            <li>
            <a class="list-group-item${item.cssClass }" href='<c:url value="${item.path}.html"></c:url>'>${item.title}</a>
                <ul class="list-group menu list-unstyled">
                    <c:forEach items="${item.childPages}" var="child" varStatus="loop">
                    	<li><a class="list-group-item" href='<c:url value="${child.path}.html"></c:url>'>${child.title}</a></li>
                    </c:forEach>
                </ul>
            </li>           
        </c:forEach>    
        </ul>
    </section> 
