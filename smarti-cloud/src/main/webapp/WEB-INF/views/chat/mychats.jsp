<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<div class="container">
<div class="row">
        <main role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
        <h1>云信 - ${folder.title } <span class="glyphicon glyphicon-user pull-right"></span></h1>
        <div id="online_chat">
		</div>
			<div class="row">
				<div id="online_chat_editor" class="panel panel-default online_editor">
						<p></p>
				</div>
				<a class="btn btn-default btn-block" title="发送" href="javascript:sendChat('${page.path }')"><span class="glyphicon glyphicon-send"></span></a>
			</div>
 		</main>
        <nav class="wb-sec col-md-3 col-md-pull-9" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
        <h3 class="wb-navcurr"><span class="glyphicon glyphicon-th-list"></span> 通讯目录</h3>
        <ul class="list-group menu list-unstyled">
	        <c:forEach items="${folders.items}" var="item" varStatus="loop">
	            <li class="list-group-item" id="${item.uid }"><a href='<c:url value="/protected/chat.html?path=${item.path}"></c:url>'><span class="glyphicon glyphicon-user">${item.title}</span></a><button class="btn btn-warning btn-xs pull-right" onclick="javascript:removeTag('${item.uid}')"><span class="glyphicon glyphicon-trash"></span></button>
	            </li>           
	        </c:forEach> 
        </ul>
        </nav>
        
</div>
</div>
<input type="hidden" id="pagePath" name="pathPath" value="${folder.path}"/>
