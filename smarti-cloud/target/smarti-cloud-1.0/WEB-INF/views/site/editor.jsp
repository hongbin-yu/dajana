<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<div class="container">
<!-- <div class="panel" id="selectedFiles"  onchange="javascript:uploadPages()">
</div> -->
<div class="row">
        <main id="wb-cont" role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
 	        <c:if test="${page.redirectTo!=null && page.redirectTo!=''}">
	        <h1>${page.title }</h1>
	        <p>此页重定向到 <a href="${page.redirectTo }">${page.redirectTo }</a></p>
	        </c:if>
	         <c:if test="${page.redirectTo==null || page.redirectTo==''}">
			 <div class="caneditable" property="content" id="${page.uid }">${page.content }</div>
 			 </c:if>
<%-- 			 <section id="menu-bar" class="btn-group btn-group-justified">
				<a class="btn btn-default btn-block" title="打开本机资源" href="javascript:openFiles()"   aria-controls="left-panel" role="button" ><span class="glyphicon glyphicon-picture"></span></a>				 
				 <a class="btn btn-default btn-block" onclick="javascript:openOverlay('${page.uid }','left-bar')" title="打开/关闭微云"><span class="glyphicon glyphicon-cloud"></span></a>
				 <a class="btn btn-default btn-block" onclick="javascript:openOverlay('${page.uid }','right-bar')" title="打开/关闭微网"><span class="glyphicon glyphicon-globe"></span></a>
			 </section> --%>
			 <%@include file="../wet/pagedetails.jsp" %>		
 		</main>
        <nav class="wb-sec col-md-3 col-md-pull-9" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
            <div style="overflow: hidden;">
    		</div>
            <%@include file="leftmenu.jsp" %>
        </nav>
</div>
</div>
<input type="hidden" id="pageId" name="pageId" value="${page.uid }"/>
<input type="hidden" id="pagePath" name="pagePath" value="${page.path }"/>
<form action="upload.html" method="POST" id="form-upload" enctype="multipart/form-data">
<input type="hidden" id="path" name="path" value="/assets/${user.userName}"/>
<input class="wb-inv" type="checkbox" id="override" name="override" value="true"/>
<input class="form-control wb-inv" type="file" id="fileUpload" name="file" size="60" required="required"  multiple/>
</form>
<!-- <section id="youchat-bar" class="wb-overlay modal-content overlay-def wb-panel-l col-md-4">
	<header class="modal-header">
		<h2 class="modal-title">优信</h2>
	</header>
	<div class="modal-body">
    <iframe id="youchat-iframe" src="/protected/youchat.html" scrolling="yes" style="height: 600px; border: 0px none; width: 360px; margin-bottom: 0px; margin-left: 10px;">
    </iframe>
 	</div>
</section> --> 
<section id="left-bar" class="wb-overlay modal-content overlay-def wb-panel-l col-md-3">
	<header class="modal-header">
		<h2 class="modal-title">云站</h2>
	</header>
	<div class="modal-body">
    <iframe id="left-iframe" src="/site/browse.html" scrolling="yes" style="height: 600px; border: 0px none; width: 360px; margin-bottom: 0px; margin-left: 10px;">
    </iframe>
 	</div>
</section> 
<section id="right-bar" class="wb-overlay modal-content overlay-def wb-panel-l col-md-3">
	<header class="modal-header">
		<h2 class="modal-title">网站</h2>
	</header>
	<div class="modal-body">
    <iframe id="right-iframe" src="/site/file.html?type=file" scrolling="yes" style="height: 600px; border: 0px none; width: 360px; margin-bottom: 0px; margin-left: 10px;">
    </iframe>
 	</div>
</section> 

