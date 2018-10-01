<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>
 
<div class="container">
     <main role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
        <h1 id="wb-cont">
        ${folder.title}
        <c:if test="${param.w eq '1'}">
        <a class="btn btn-default pull-right" href="/site/view.html?path=${folder.path}&type=${type }&w=4" title="<spring:message code="djn.bigicon"/>"><span class="glyphicon glyphicon-th-large"></span></a>
        </c:if>		
        <c:if test="${param.w ne '1' }">
        <a class="btn btn-default pull-right" href="/site/view.html?path=${folder.path}&type=${type }&w=1" title="<spring:message code="djn.smallicon"/>"><span class="glyphicon glyphicon-th-list"></span></a>
        </c:if>	
        <a class="btn btn-default pull-right" href="view.html?path=${folder.path}&type=${type}&r=1&w=${param.w}" title="刷屏"><span class="glyphicon glyphicon-refresh"></span></a>	
        </h1>
	<span id="pagetag">${folder.description }</span>         
	<section class="cn-search-dataTable"> 
	        <h2 class="wb-inv">查询结果</h2>
	        <div class="mrgn-tp-xl"></div>
	        <table class="wb-tables table table-striped table-hover nws-tbl" id="dataset-filter" aria-live="polite" data-wb-tables="{
	            &#34;bDeferRender&#34;: true,
	            &#34;ajaxSource&#34;: &#34;getassets.json?path=${folder.path}&type=${type}&w=${param.w}&r=${param.r}&#34;,
	            &#34;order&#34;: [${ncolumn},&#34;${sorting}&#34;],
	             &#34;columns&#34;: [
	                { &#34;data&#34;: &#34;title&#34;, &#34;className&#34;: &#34;nws-tbl-ttl h4&#34; },
	                { &#34;data&#34;: &#34;lastPublished&#34;, &#34;className&#34;: &#34;nws-tbl-date&#34; },
	                { &#34;data&#34;: &#34;subjects&#34;, &#34;className&#34;: &#34;nws-tbl-dept&#34;},
	                { &#34;data&#34;: &#34;location&#34;, &#34;className&#34;: &#34;nws-tbl-type&#34; },
	                { &#34;data&#34;: &#34;description&#34;, &#34;className&#34;: &#34;nws-tbl-desc&#34; },
 	                { &#34;data&#34;: &#34;contentType&#34;,  &#34;visible&#34;: false },
	                { &#34;data&#34;: &#34;lastModified&#34;,  &#34;visible&#34;: false }
	      ]}
	">
	            <thead class="wb-inv">
	            <tr>
	              <th>标题</th>
	              <th>出版日期</th>
	              <th>图标</th>
	              <th>主题</th>
	              <th>描述</th>
	              <th>Location</th>
	              <th>Audience</th>
	           </tr>
	            </thead>
	<tbody></tbody></table>
	      
	     
	</section>	

	</main>
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
<%--             <a  class="list-group-item" href='<c:url value="view.html?path=${item.path }&type=${type }"></c:url>'><c:if test="${item.path==folder.path }"><span class="glyphicon glyphicon-folder-open"></span></c:if><c:if test="${item.path!=folder.path }"><span class="glyphicon glyphicon-folder-close"></span></c:if> ${item.title}</a>      --%>
   	    	    <a class="list-group-item" href='javascript:view("${item.path}","${type }","${param.w}")'><c:if test="${item.path == path }"><span class="glyphicon glyphicon-folder-open"></span></c:if><c:if test="${item.path != path }"><span class="glyphicon glyphicon-folder-close"></span></c:if> ${item.title}</a>
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