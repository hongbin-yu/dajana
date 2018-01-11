<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
    <h2 id="wb-sec-h" class="wb-inv">左菜单</h2>
    <section class="list-group menu list-unstyled">

<%--         <c:if test="${page.parent=='/content' }">
        <a href='<c:url value="/content/templates.html"></c:url>'><img alt="" src='<c:url value="/resources/images/arrowleft.png"/>'/>模板</a>
        </c:if> --%>
        <c:if test="${page.parent!='/content' }">
        <h3 class="wb-navcurr">
        <a href='<c:url value="editor.html?path=${parent.path}"></c:url>'><span class="glyphicon glyphicon-backward"></span>${parent.title}</a>
        </h3>
        </c:if>        

        <ul class="list-group menu list-unstyled">
        <c:forEach items="${menu.items}" var="item" varStatus="loop">
            <li>
            <a class="list-group-item${item.cssClass }" href='<c:url value="editor.html?path=${item.path }"></c:url>'>${item.title}</a>
                <ul class="list-group menu list-unstyled">
                    <c:forEach items="${item.childPages}" var="child" varStatus="loop">
                    	<li><a class="list-group-item" href='<c:url value="editor.html?path=${child.path}"></c:url>'>${child.title}</a></li>
                    </c:forEach>
                </ul>
            </li>           
        </c:forEach>    
        </ul>
    </section> 
