<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<div class="container">
<div class="row">
        <main id="wb-cont" role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
        	<button class="btn btn-default pull-right" onclick="javascript:setDataView('data-inview','left-bar')" title="打开/关闭微云"><span class="glyphicon glyphicon-th-large pull-right"></span></button>
        	
	        <c:if test="${page.redirectTo!=null && page.redirectTo!=''}">
	        <h1>${page.title }</h1>
	        <p>此页重定向到 <a href="${page.redirectTo }">${page.redirectTo }</a></p>
	        </c:if>
	         <c:if test="${page.redirectTo==null || page.redirectTo==''}">
			 <div class="caneditable" property="content" id="${page.uid }">${page.content }</div>
			 </c:if>
			 <%@include file="../wet/pagedetails.jsp" %>		
 		</main>
        <nav class="wb-sec col-md-3 col-md-pull-9" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
            <div style="overflow: hidden;">
    		</div>
            <%@include file="leftmenu.jsp" %>
        </nav>
</div>
</div>

<section id="left-bar" class="wb-overlay modal-content overlay-def wb-panel-l col-md-3">
	<header class="modal-header">
		<h2 class="modal-title">云站</h2>
	</header>
	<div class="modal-body">
<!--  		 <div class="btn-group btn-group-justified">
 		 <a class="btn btn-primary btn-xs" href="javascript:showImages(this)">资源</a>
 		 <a class="btn btn-primary btn-xs" href="javascript:showTemplates(this)">模板</a>
 		 <a class="btn btn-primary btn-xs" href="javascript:showPages(this)">网页</a>
 		 <a class="btn btn-primary btn-xs" href="javascript:showVideos(this)">视频</a>
 		 a class="btn btn-primary btn-xs" href="javascript:showProducts(this)">产品</a
 		</div> -->
<!--  		<iframe src="" width="100%" height="600"  frameBorder="0"></iframe>
		 <div id="left-iframe">-->
 		 </div>
</section>
<!-- left: 0px; border: 0px none; height: 600px; position: fixed; width: 360px; overflow: hidden; bottom: 30px; -->

<div id="left-float" style="left: 0px; border: 0px none; height: 600px; position: fixed; width: 0px; overflow: hidden; bottom: 30px;">
    <div style="overflow: hidden;">
    </div>
    <iframe id="left-iframe" src="" scrolling="yes" style="height: 600px; border: 0px none; width: 400px; margin-bottom: 0px; margin-left: 10px;">
    </iframe>
 </div>  