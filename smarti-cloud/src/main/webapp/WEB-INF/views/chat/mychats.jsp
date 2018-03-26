<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="authz" %>
<div class="container">
<div class="row">
        <main role="main" property="mainContentOfPage" class="col-md-8 col-md-push-4">
        <h1>优信<c:if test="${folder.title != null}"> - ${folder.title }</c:if><a href="#" title="${user.title }" onclick="ftrClose('/protected/profile.html')"><img id="uploadIcon" class="img-responsive pull-right" src="${user.icon}" alt="图标"/></a></h1>
	<c:if test="${folder==null || folder.path == '/chat'}"><div data-ajax-replace="/templates/assets/html/youchat.html"></div></c:if>        
	<c:if test="${folder.title != null && folder.path != '/chat'}">
        <div class="row text-center"><img id="online_chat_loading" width="120" height="120" class="wb-inv" src="/resources/images/loadingx400.gif" alt="下载"/></div>
        <div id="online_chat">
		</div>
			<div class="panel panel-primary"><header class="panel-heading">${folder.title }<img class="wb-inv" id="online_chat_running" src="/resources/images/loading16x16.gif" alt=""/><a href="#" title="${user.title }"><img id="uploadIcon" class="img-responsive pull-right" src="/protected/file/icon.jpg?path=/${username }/assets/icon/x48.jpg" alt="图标"/></a>
			</header>
				<div id="uploadBox" class="panel-body" ondrop="drop(event)" ondragover="allowDrop(event)" style="border:1px solid #aaaaaa;">
					<form action="upload.html" method="POST" id="form-upload" enctype="multipart/form-data">
					<input type="hidden" id="path" name="path" value="/${user.userName}/assets/youchat"/>
					<input class="wb-inv" type="checkbox" id="override" name="override" value="true" checked/>

					<div class="panel" id="selectedFiles"  onchange="javascript:uploadFiles()">
					</div>
					<div id="online_chat_editor" class="panel panel-default online_editor"></div>

					<input class="form-control wb-inv" type="file" id="fileUpload" name="file" size="60" required="required"  multiple/>
					<div class="btn-group btn-group-justified">
						<a id="online_chat_send" class="btn btn-default btn-block" title="发送" href="javascript:sendChat('${folder.path}')"><span class="glyphicon glyphicon-send"></span></a>
						<a class="btn btn-default btn-block" title="打开云资源" href="javascript:openOverlay('online_chat_editor','left-bar')"  aria-controls="left-panel" role="button"><span class="glyphicon glyphicon-cloud"></span></a>
						<a class="btn btn-default btn-block" title="打开网站" href="javascript:openOverlay('online_chat_editor','right-bar')"   aria-controls="left-panel" role="button" ><span class="glyphicon glyphicon-globe"></span></a>
						<a class="btn btn-default btn-block" title="打开本机资源" href="javascript:openFiles()"   aria-controls="left-panel" role="button" ><span class="glyphicon glyphicon-picture"></span></a>
						<c:if test="${user.role =='Owner' || user.role == 'Administrator'}">	            
						<a id="fswebcam" class="btn btn-default btn-block" title="网络相机" href="javascript:fswebcam()"   aria-controls="left-panel" role="button"><span class="glyphicon glyphicon-camera"></span></a>
						<a id="webvideo" class="btn btn-default btn-block" title="网络直播" href="javascript:webvideo('${video_url}',300)"   aria-controls="left-panel" role="button"><span class="glyphicon glyphicon-facetime-video"></span></a>
						</c:if>
					</div>
					</form>
				</div>
			</div>
	</c:if>			
 		</main>
        <nav class="wb-sec col-md-4 col-md-pull-8" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
        		<h3 class="wb-navcurr"><a href="/protected/chat.html"><span class="glyphicon glyphicon-th-list"></span> 通讯目录</a></h3>
		<c:if test="${user.role =='Owner' || user.role == 'Administrator'}">
			<details>
			<summary><label for="path"><span class="glyphicon glyphicon-folder-close"></span><spring:message code="djn.create_group"/></label></summary>
			<div class="wb-frmvld">
			<form action="javascript:createFolder()" id="createFolder" method="POST">
			<input type="hidden" id="folderPath" name="path" value="/chat"/>	
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
		</c:if>
        <ul class="list-group menu list-unstyled">
	        <c:forEach items="${folders.items}" var="item" varStatus="loop">
	            <li class="list-group-item" id="${item.uid }"><a  href='<c:url value="/protected/chat.html?path=${item.path}"></c:url>'><span class="glyphicon glyphicon-user">${item.title}</span><span id="unread-${item.uid }" class="badge"></span></a>
				<c:if test="${user.role =='Owner' || user.role == 'Administrator'}">	            
	            <button class="btn btn-warning btn-xs pull-right" onclick="javascript:removeTag('${item.uid}')"><span class="glyphicon glyphicon-trash"></span></button>
	            <a class="wb-lbx" href="groupedit.html?path=${item.path }"><button class="btn btn-primary btn-xs pull-right"><span class="glyphicon glyphicon-cog"></span></button></a>
	            </c:if>
	            </li>           
	        </c:forEach> 
        </ul>
        </nav>
        
</div>
</div>
<section id="left-bar" class="wb-overlay modal-content overlay-def wb-panel-l col-md-4">
	<header class="modal-header">
		<h2 class="modal-title">云站</h2>
	</header>
	<div class="modal-body">
    <iframe id="left-iframe" src="/protected/browse.html" scrolling="yes" style="height: 600px; border: 0px none; width: 360px; margin-bottom: 0px; margin-left: 10px;">
    </iframe>
 	</div>
</section> 
<section id="right-bar" class="wb-overlay modal-content overlay-def wb-panel-r col-md-4">
	<header class="modal-header">
		<h2 class="modal-title">网站</h2>
	</header>
	<div class="modal-body">
    <iframe id="right-iframe" src="/site/file.html?type=file" scrolling="yes" style="height: 600px; border: 0px none; width: 360px; margin-bottom: 0px; margin-left: 10px;">
    </iframe>
 	</div>
</section> 
<section id="left-float" style="left: 0px; border: 0px none; height: 300px; position: fixed; width: 0px; overflow: hidden; top: 10px; left: 10px; bottom: 0px">
	<div class="modal-body">
    <iframe id="video-iframe" src="" scrolling="no" style="height: 300px; border: 0px none; width: 400px; margin-bottom: 0px; margin-left: 0px;">
    </iframe>
 	</div>
</section> 
<input type="hidden" id="pagePath" name="pathPath" value="${folder.path}"/>
<input type="hidden" id="pageName" name="pathName" value="youchat"/>
<input type="hidden" id="username" name="username" value="${username}"/>
<c:if test="${user.role =='Owner' || user.role == 'Administrator'}">
<input type="hidden" id="userrole" name="userrole" value="Administrator"/>
</c:if>
