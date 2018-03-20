<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="authz" %>
<div class="container">
<div class="row">
        <main role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
        <h1>优信 - ${folder.title } <img id="uploadIcon" class="img-responsive pull-right" src="/site/file/icon.jpg?path=/${username }/assets/icon/x48.jpg" alt="图标"/></h1>
        <div class="row text-center"><img id="online_chat_loading" width="120" height="120" class="wb-inv" src="/resources/images/loadingx400.gif" alt="下载"/></div>
        <div id="online_chat">
		</div>
			<div class="panel panel-default"><header class="panel-heading">${folder.title }</header>
				<div class="panel-body">
					<div id="online_chat_editor" class="panel panel-default online_editor"></div>
					<div class="btn-group btn-group-justified">
						<a class="btn btn-default btn-block" title="发送" href="javascript:sendChat('${page.path }')"><span class="glyphicon glyphicon-send"></span><img class="wb-inv" id="online_chat_running" src="/resources/images/loading16x16.gif" alt=""/></a>
						<a class="btn btn-default btn-block" title="打开云资源" href="javascript:setDataView('online_chat_editor','/protected/browse.html')"><span class="glyphicon glyphicon-cloud"></span></a>
						<a class="btn btn-default btn-block" title="打开网站" href="javascript:setDataView('online_chat_editor','/protected/file.html?type=file')"><span class="glyphicon glyphicon-globe"></span></a>
					</div>
				</div>
			</div>
 		</main>
        <nav class="wb-sec col-md-3 col-md-pull-9" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
        		<h3 class="wb-navcurr"><span class="glyphicon glyphicon-th-list"></span> 通讯目录</h3>
			<details>
			<summary><label for="path"><span class="glyphicon glyphicon-folder-close"></span><spring:message code="djn.create_group"/></label></summary>
			<div class="wb-frmvld">
			<form action="javascript:createFolder()" id="createFolder" method="POST">
			<input type="hidden" id="folderPath" name="path" value="${folder.path }"/>	
			<div class="form-group">
			<label for="foldername"><spring:message code="djn.path"/><strong class="required">(<spring:message code="djn.required"/>)</strong></label>
			
			<input class="form-control" id="foldername" required="required" name="name" pattern="[A-Za-z0-9\-]{2,}" value="" size="80" placeholder="<spring:message code="djn.alpha_number_only"/>"/>
			</div>
			<div class="form-group">
			<label for="titlefolder"><spring:message code="djn.title"/><strong class="required">(<spring:message code="djn.required"/>)</strong></label><input class="form-control" id="titlefolder" required="required"  name="title" value="" size="25"/>
			</div>
				<input id="submit" type="submit"  value="<spring:message code="djn.submit"/> " class="btn btn-default">
			</form>
			</div>	
		</details>	
        <ul class="list-group menu list-unstyled">
	        <c:forEach items="${folders.items}" var="item" varStatus="loop">
	            <li class="list-group-item" id="${item.uid }"><a href='<c:url value="/protected/chat.html?path=${item.path}"></c:url>'><span class="glyphicon glyphicon-user">${item.title}</span></a><button class="btn btn-warning btn-xs pull-right" onclick="javascript:removeTag('${item.uid}')"><span class="glyphicon glyphicon-trash"></span></button>
	            </li>           
	        </c:forEach> 
        </ul>
        </nav>
        
</div>
</div>
<div id="left-float" style="left: 0px; border: 0px none; height: 600px; position: fixed; width: 0px; overflow: hidden; bottom: 30px;">
    <div style="overflow: hidden;">
    </div>
    <iframe id="left-iframe" src="" scrolling="yes" style="height: 600px; border: 0px none; width: 400px; margin-bottom: 0px; margin-left: 10px;">
    </iframe>
 </div> 
<input type="hidden" id="pagePath" name="pathPath" value="${folder.path}"/>
<input type="hidden" id="username" name="username" value="${username}"/>
<authz:authorize ifAnyGranted="ROLE_ADMINISTRATOR,ROLE_OWNER">
<input type="hidden" id="userrole" name="userrole" value="Administrator"/>
</authz:authorize>