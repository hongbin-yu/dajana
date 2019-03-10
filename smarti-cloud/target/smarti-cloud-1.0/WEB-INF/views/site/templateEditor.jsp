<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<div class="container">
<div class="row">
        <main role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
        
	        <c:if test="${page.redirectTo!=null && page.redirectTo!=''}">
	        <h1>${page.title }</h1>
	        <p>此页重定向到 <a href="${page.redirectTo }">${page.redirectTo }</a></p>
	        </c:if>
	         <c:if test="${page.redirectTo==null || page.redirectTo==''}">
			 <div class="caneditable" property="content" id="${page.uid }">${page.content }</div>
			 </c:if>
	<div class="wb-frmvld">
	         <form name="template" id="template" action="javascript:updateTemplate()" method="POST">
	         <input type="hidden" id="template_uid" name="uid" value="${page.uid }"/>
	         <div class="form-group">
	         <label for="template_content">原码</label><textarea id="template_content" class="form-control" name="content" cols="80" rows="10">${page.content }</textarea> 
	         </div>
	         <input type="submit" name="sunmit" value="提交" class="btn btn-primary"/>
	         </form>
	</div>		 
			 <%@include file="../wet/pagedetails.jsp" %>		
 		</main>
<!--  		 <div class="wb-sec col-md-3 col-md-pull-9">
 		 <div class="btn-group btn-group-justified">
 		 <a class="btn btn-primary btn-xs" href="javascript:showImages(this)">图像</a>
 		 <a class="btn btn-primary btn-xs" href="javascript:showTemplates(this)">模板</a>
 		 <a class="btn btn-primary btn-xs" href="javascript:showPages(this)">网页</a>
 		 <a class="btn btn-primary btn-xs" href="javascript:showVideos(this)">视频</a>
 		 <a class="btn btn-primary btn-xs" href="javascript:showProducts(this)">产品</a>
 		</div>
 		</div>
 		 <div id="left-iframe" class="wb-sec col-md-3 col-md-pull-9">

 		 </div> -->
        <nav class="wb-sec col-md-3 col-md-pull-9" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
            <%@include file="leftmenu.jsp" %>
        </nav>
		
</div>
</div>
<!-- <section id="left-bar" class="container wb-overlay modal-content overlay-def wb-panel-l col-md-3">
	<header class="modal-header">
		<h2 class="modal-title">云站</h2>
	</header>
	<div class="modal-body">
 		 <div class="btn-group btn-group-justified">
 		 <a class="btn btn-primary btn-xs" href="javascript:showImages(this)">图像</a>
 		 <a class="btn btn-primary btn-xs" href="javascript:showTemplates(this)">模板</a>
 		 <a class="btn btn-primary btn-xs" href="javascript:showPages(this)">网页</a>
 		 <a class="btn btn-primary btn-xs" href="javascript:showVideos(this)">视频</a>
 		 <a class="btn btn-primary btn-xs" href="javascript:showProducts(this)">产品</a>
 		</div>
 		
 		 <div id="left-iframe">

 		 </div>
 	</div>	 
</section> -->



						