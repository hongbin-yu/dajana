<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>
 

     <main role="main" property="mainContentOfPage" class="container">
     <div class="row profile">
     <div class="col-md-12">
        <h1 id="wb-cont" class="wb-inview bar-demo" data-inview="bottom-bar">
        ${folder.title}
        <a class="btn btn-default pull-right" href="javascript:downloadAll()" title="下载本页全部文件"><span id="downloadIcon" class="glyphicon glyphicon-download"></span></a>	
        <a class="btn btn-default pull-right" href="view.html?path=${folder.path}&type=${type}&r=1&w=${param.w}" title="刷屏"><span class="glyphicon glyphicon-refresh"></span></a>	
		<a href="javascript:openPdf()" class="btn btn-primary pull-right" title="打开PDF"><span class="glyphicon glyphicon-open"></span></a>

        </h1>
	    <span id="pagetag">${folder.description }</span>         
     </div>
     </div>
     <div class="row">
     <div class="col-md-9 col-md-push-3">
     <span id="top_insert">
	</span>
	<section class="cn-search-dataTable" id="view_insert"> 
	        <h2 class="wb-inv">查询结果</h2>
	        <div class="mrgn-tp-xl"></div> <!-- /*&quot;aoColumns&quot;: [ { &quot;data&quot;: &quot;title&quot;,&quot;sClass&quot;: &quot;product-name h4&quot; },  { &#34;data&#34;: &#34;url&#34;,&quot;sClass&quot;: &quot;product-platforms&quot; }, { &#34;data&#34;: &#34;description&#34;,&quot;sClass&quot;: &quot;product-shortdescription&quot; },  { &quot;data&quot;: &quot;lastPublished&quot;,&quot;sClass&quot;: &quot;product-platforms&quot; },{&#34;data&#34;: &#34;contentType&#34;, &quot;sClass&quot;: &quot;product-longdescription&quot; }, { &#34;data&#34;: &#34;location&#34;, &quot;sClass&quot;: &quot;product-department&quot; },{&#34;data&#34;: &#34;lastModified&#34;,  &quot;sClass&quot;: &quot;product-link-container&quot; } ]&quot;sAjaxSource&quot;: &quot;getassets.json?path=${folder.path}&type=${type}&w=${param.w}&r=${param.r}&quot;,*/ -->
  <table id="mobile-centre" class="product-listing wb-tables" data-wb-tables="{&quot;bDeferRender&quot;: true, &quot;pageLength&quot;: 12, &quot;aLengthMenu&quot;: [[12, 24, 48, 96, -1], [12, 24, 48, 98, &quot;全部&quot;]], &quot;aaSorting&quot;: [[1,&quot;desc&quot;]], &quot;sDom&quot;: &quot;<\&quot;wrapper\&quot;flitp>&quot; }"> 
   <thead class="wb-inv"> 
    <tr> 
     <th>Name</th> 
     <th>Publication date</th> 
     <th>Platform(s)</th> 
     <th>Short description</th> 
     <th>Long description</th> 
     <th>Department</th> 
     <th>Download link(s)</th> 
    </tr> 
   </thead> 
   <tbody data-ajax-replace="getassets.html?path=${folder.path}&type=${type}&r=${param.r}"> 

   </tbody> 
  </table>      
	</section>	
</div>
    <nav class="wb-sec col-md-3 col-md-pull-9" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
		<input type="hidden" id="folderpath" name="path" value="${folder.path}"/>
    <h2 id="wb-sec-h" class="wb-inv">左菜单</h2>

    <section class="list-group menu list-unstyled">	
            <form action='<c:url value="view.html"></c:url>' method="get" name="cse-search-box" role="search" class="form-inline" accept-charset="UTF-8">
			<input type="hidden" id="path" name="path" value="${folder.path}"/>
			<input type="hidden" id= "input" name="input" value="${input}"/>
			<input type="hidden" id="kw" name="kw" value="${kw}"/>		
			<input type="hidden" id="pageNumber" name="pageNumber" value="${assets.pageNumber}"/>	
			<input type="hidden" id="availablePages" name="availablePages" value="${assets.availablePages}"/>				
			<input type="hidden" id="topage" name="topage" value="assetsmore"/>			    
					<div class="form-group pull-right">
					<label for="type"><spring:message code="djn.display"/></label>
					<select id="type" name="type" onchange="this.form.submit()">
					<option value="" <c:if test="${type=='' }">selected</c:if> ><spring:message code="djn.all"/></option>
					<option value="child" <c:if test="${type=='child' }">selected</c:if> ><spring:message code="djn.child"/></option>
					<option value="image" <c:if test="${type=='image' }">selected</c:if> ><spring:message code="djn.image"/></option>
					<option value="video" <c:if test="${type=='video' }">selected</c:if> ><spring:message code="djn.video"/></option>
					<option value="audio" <c:if test="${type=='audio' }">selected</c:if> ><spring:message code="djn.audeo"/></option>
					<option value="application" <c:if test="${type=='application' }">selected</c:if> ><spring:message code="djn.file"/></option>
					</select>
					</div>
			</form>
        <h3 id="leftmenu_h3">
        <c:if test="${folder.parent!='/assets' }">
        <a href='<c:url value="view.html?path=${folder.parent}&type=${type }"></c:url>'>${folder.parentTitle}<span class="glyphicon glyphicon-backward"></span>
        </a>
        </c:if> 
        <c:if test="${folder.parent=='/assets' }">
        <spring:message code="djn.cloud"/>
        </c:if>               
        </h3>    	       
        <ul id="leftmenu_ul" class="list-group menu list-unstyled">
        <c:forEach items="${leftmenu.subfolders}" var="item" varStatus="loop">
            <li>
             <a  class="list-group-item" href='<c:url value="view.html?path=${item.path }&type=${type }"></c:url>'><c:if test="${item.path==folder.path }"><span class="glyphicon glyphicon-folder-open"></span></c:if><c:if test="${item.path!=folder.path }"><span class="glyphicon glyphicon-folder-close"></span></c:if> ${item.title}</a>
<%--    	    <a class="list-group-item" href='javascript:view("${item.path}","${type }","${param.w}")'><c:if test="${item.path == path }"><span class="glyphicon glyphicon-folder-open"></span></c:if><c:if test="${item.path != path }"><span class="glyphicon glyphicon-folder-close"></span></c:if> ${item.title}</a>--%>
                <ul class="list-group menu list-unstyled">
                    <c:forEach items="${item.subfolders}" var="child" varStatus="loop">
                    	<li>
                    	<a class="list-group-item" href='<c:url value="view.html?path=${child.path}&type=${type }&w=${param.w}"></c:url>'><span class="glyphicon glyphicon-folder-close"></span> ${child.title}</a>
<%--                   	    	<a class="list-group-item" href='javascript:view("${child.path}","${child.title }","${type }","${param.w}")'><span class="glyphicon glyphicon-folder-close"></span> ${child.title}</a> --%>
						</li>
                    </c:forEach>
                </ul>
            </li>           
        </c:forEach>    
        </ul>
   </section>


</nav>
</div>
</main>

		
<section id="left-bar" class="wb-overlay modal-content overlay-def wb-panel-l col-md-4 col-xs-12">
			<header class="modal-header">
				<h2 class="modal-title">通讯目录  (在线： ${onlineCount} )</h2>
			</header>
			<div class="modal-body">
                <ul class="list-group menu list-unstyled">
                    <c:forEach items="${presences}" var="presence" varStatus="loop">
                    	<li class="list-group-item" >
                    	<a class="bnt bnt-default" href="javascript:sendAsset('${presence.name}')" title="发送到：${presence.username}"><span class="glyphicon glyphicon-user"></span> ${presence.username}(${presence.status})</a>
						</li>
                    </c:forEach>
                </ul>	
           </div>     
</section> 
<section id="bottom-bar" class="wb-overlay modal-content overlay-def wb-bar-b text-center">
	<form action="upload.html" method="POST" id="form-upload" enctype="multipart/form-data">
		<input type="hidden" id="path" name="path" value="${folder.path}"/>
		<input type="hidden" id="type"  name="type" value="${type}"/>
		<input type="hidden" id="input" name="input" value="${input}"/>
		<a class="btn btn-default btn-xs" href=""><span class="glyphicon glyphicon-align-justify"></span></a>
		<a class="btn btn-default btn-xs" href="#wb-cont" onclick="javascript:openFiles()"><span class="glyphicon glyphicon-camera"></span></a>
		<input name="q" id="elk_q" size="20" type="text" value=""/>
		<a class="btn btn-default btn-xs" href="javascript:elasticsearch(0)"><span class="glyphicon glyphicon-search"></span></a>
		<input class="form-control wb-inv" type="file" id="fileUpload" name="file" size="60" required="required"  multiple/>
		<div class="wb-inv" id="selectedFiles"></div>
	</form>	

</section>		
		<script type="text/javascript">
			var selected_uid = "";
			function setUid(uid) {
				
				selected_uid = uid;
				$( "#left-bar" ).trigger( "open.wb-overlaylbx" );
/* 				$( document ).trigger( "open.wb-overlaylbx", [
				                                   	[
				                                   		{
				                                   			src: "#left-bar"
				                                   		}
				                                   	]
				                                   ]); */
				}
			function sendAsset(to) {
				
				alert(to +"/"+selected_uid);
				};
	
		</script>