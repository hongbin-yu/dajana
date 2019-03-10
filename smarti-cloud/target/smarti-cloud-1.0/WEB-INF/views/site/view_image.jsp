<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>

<div class="container">
     <main role="main" property="mainContentOfPage" class="col-md-4 col-pull-8">
		<input type="hidden" id="folderpath" name="path" value="${folder.path}"/>
	    <div class="well" id="wb-sec">
		    <section>
				<div class="checkbox btn btn-default pull-right">
		    		<label for="toggle"><input id="toggle" type="checkbox" onClick="toggle(this)" title="选择切换"/>全选切换</label>
				</div>
		        <h3 id="wb-cont">
		        <c:if test="${folder.name!='assets' }"><a href="view.html?path=${folder.parent}&type=${type}"><span class="glyphicon glyphicon-backward"></span>${folder.parent}</a></c:if>
		        <a href="?path=${folder.path}&type=${type}" title="刷屏"><span class="glyphicon glyphicon-refresh"></span>${folder.path}</a>
		        </h3>
		    <form action='<c:url value="view.html"></c:url>' method="get" name="cse-search-box" role="search" class="form-inline" accept-charset="UTF-8">
			<input type="hidden" id="path" name="path" value="${folder.path}"/>
			<input type="hidden" id= "input" name="input" value="${input}"/>
			<input type="hidden" id="kw" name="kw" value="${kw}"/>		
			<input type="hidden" id="pageNumber" name="pageNumber" value="${assets.pageNumber}"/>	
			<input type="hidden" id="availablePages" name="availablePages" value="${assets.availablePages}"/>				
			<input type="hidden" id="topage" name="topage" value="assetsmore"/>			    
			<div class="btn btn-default-lg form-group pull-right">
			<label for="wb-srch-q" class="wb-inv"><spring:message code="djn.search"/></label>
			<select id="type" name="type" onchange="this.form.submit()">
			<option value="" <c:if test="${type=='' }">selected</c:if> ><spring:message code="djn.all"/></option>
			<option value="child" <c:if test="${type=='child' }">selected</c:if> ><spring:message code="djn.child"/></option>
			<option value="image" <c:if test="${type=='image' }">selected</c:if> ><spring:message code="djn.image"/></option>
			<option value="video" <c:if test="${type=='video' }">selected</c:if> ><spring:message code="djn.video"/></option>
			<option value="audio" <c:if test="${type=='audio' }">selected</c:if> ><spring:message code="djn.audeo"/></option>
			<option value="application" <c:if test="${type=='application' }">selected</c:if> ><spring:message code="djn.file"/></option>
			</select>
			</div>	
			<a class="btn btn-default pull-right" href="/site/assets.html?path=${folder.path}" title="编辑"><span class="glyphicon glyphicon-edit pull-right"></span></a>
			<a href="javascript:deleteFiles()" class="btn bnt-default btn-danger visible-xs pull-right" title="删除"><span class="glyphicon glyphicon-remove"></span></a>
			<a href="javascript:openPdf()" class="btn btn-primary visible-xs pull-right" title="打开PDF"><span class="glyphicon glyphicon-open"></span></a>
			</form>       
            <details id="${folder.uid }">
            <summary>${folder.title}
            </summary>
            	<c:if test="${assets.availablePages==0}">
    			    <a class="wb-lbx" title="<spring:message code="djn.delete"/>" href="<c:url value="/site/delete.html?uid=${folder.uid }&redirect=/site/assets.html?path=${folder.parent }"/>"><span class="glyphicon glyphicon-remove"></span><spring:message code="djn.delete"/></a>
	      		</c:if>
				<div class="form-group">
				<label for="foldertitle"><spring:message code="djn.title"/>&nbsp;</label><input class="form-control" id="foldertitle" name="jcr:title" value="${folder.title}" size="25" uid="${folder.uid}"  onchange="updateNode(this)"/>
				</div>            
				<div class="form-group">
				<label for="folderorderby"><spring:message code="djn.order"/>&nbsp;</label>
				<select class="form-control" id="folderorderby" name="orderby" value="${folder.orderby}" uid="${folder.uid}"  onchange="updateNode(this)">
					<option value="lastModified desc"><spring:message code="djn.lastModified_desc"/></option>
					<option value="lastModified asc" <c:if test="${folder.orderby=='lastModified asc'}">selected</c:if>><spring:message code="djn.lastModified_asc"/></option>
					<option value="originalDate desc" <c:if test="${folder.orderby=='originalDate desc'}">selected</c:if>><spring:message code="djn.docDate_desc"/></option>
					<option value="originalDate asc" <c:if test="${folder.orderby=='originalDate asc'}">selected</c:if>><spring:message code="djn.docDate_asc"/></option>
				</select>
				</div>
				<div class="form-group">
				<label for="folderresolution"><spring:message code="djn.resolution"/>&nbsp;</label>
				<select class="form-control" id="folderresolution" name="resolution" value="${folder.resolution}" uid="${folder.uid}"  onchange="updateNode(this)">
					<option value="540x360"><spring:message code="djn.540x360"/></option>
					<option value="720x540" <c:if test="${folder.resolution=='720x540'}">selected</c:if>><spring:message code="djn.720x540"/></option>
					<option value="360x280" <c:if test="${folder.resolution=='360x360'}">selected</c:if>><spring:message code="djn.360x360"/></option>
					<option value="1080x720" <c:if test="${folder.resolution=='720x540'}">selected</c:if>><spring:message code="djn.1080x720"/></option>
				</select>
				</div>			
				<div class="form-group">
				<label for="sharing">共享用户</label><a class="wb-lbx" href="groupedit.html?path=${folder.path }"><button class="btn btn-primary btn-xs pull-right"><span class="glyphicon glyphicon-cog"></span></button></a>
		</div>	
				<div class="checkbox">
				<label for="readonly"><input type="checkbox" name="readonly" value="true" id="readonly" <c:if test="${folder.readonly=='true' }">checked</c:if>  size="35"  uid="${folder.uid }" onchange="updateNode(this)"> 共享只读（共享用户名不能修改目录下文件）</label>
				</div>								 
				<div class="checkbox">
				<label for="intranet"><input type="checkbox" name="intranet" value="true" id="intranetfolder" <c:if test="${folder.intranet=='true' }">checked</c:if> size="35"  uid="${folder.uid }" onchange="updateProperty(this)"> 内部网（外网不能访问目录下文件）</label>
				</div>									  
            </details>
			</section>
	    </div>	

		<div class="row">
		        <c:forEach items="${folders.items}" var="item" varStatus="loop">
		            <div class="well">
			           <a href="view.html?path=${item.path}&type=${type}"> <img id="folder${item.uid }" path="${item.path }"  ondrop="drop(event)" ondragover="allowDrop(event)" alt="${item.title}" class="img-responsive pull-left mrgn-rght-md" src='<c:url value="/resources/images/folder32X32.png"></c:url>'/>
		            	${item.title} (${item.path })</a>
		            	<div class="clearfix"></div>
		            <div id="selectFiles${item.uid }"></div>	
		            </div>  
		            	<c:if test="${(loop.index+1) % 3 ==0  }"><div class="clearfix"></div></c:if>         
		        </c:forEach> 
		</div>   

</main>
<nav class="wb-sec col-md-8 col-push-4" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
<h1 id="wb-cont">照片浏览</h1>   	      
<div class="col-dm-12 wb-lbx lbx-gal">
	<ul id="contentmore" class="list-inline wb-eqht">
	<c:forEach items="${assets.items }" var="item" varStatus="loop">
		<li class="col-md-1">
	    <a href="<c:url value='${item.link}&w=12'></c:url>">
			<img id="img${item.uid}" src="<c:url value='${item.iconSmall }'></c:url>" class="img-responsive pull-left mrgn-rght-md img-rounded" draggable="true"/>
		</a>
		</li>
	</c:forEach>
	</ul>
	<div class="clearfix"></div>
</div>
		<c:if test="${assets.availablePages>1 }">
		<c:if test="${assets.pageNumber > 6 }">
		<c:set var="startPage">${assets.pageNumber-5}</c:set>
		</c:if>
		<c:if test="${assets.pageNumber <= 6 }">
		<c:set var="startPage">1</c:set>
		</c:if>
		<c:if test="${assets.pageNumber + 10 < assets.availablePages}">
		<c:set var="endPage">${assets.pageNumber + 10}</c:set>
		</c:if>		
		<c:if test="${assets.pageNumber + 10 >= assets.availablePages}">
		<c:set var="endPage">${assets.availablePages}</c:set>
		</c:if>		

		<section id="loading" class="text-center">
		     <ul class="pagination">
		     <c:if test="${assets.pageNumber>0}"> 
		     	<li><a rel='prev' href="<c:url value='?path=${path }&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber-1}'/>"><spring:message code="djn.last_page"/></a></li>
		     </c:if>
			 <c:forEach var="i" begin="${startPage}" end="${endPage}">
			 <c:if test="${assets.pageNumber==i-1}">
			 <li class="active"><a href="<c:url value='?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${i-1}'/>">${i}</a></li>
			 </c:if>
			 <c:if test="${assets.pageNumber!=i-1}">
			 <li><a href="<c:url value='?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${i-1}'/>">${i}</a></li>
			 </c:if>			 
			 </c:forEach>
		     <c:if test="${assets.pageNumber+1<assets.availablePages}">
				<li><a rel="next" href="<c:url value='?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber+1}'/>"><spring:message code="djn.next_page"/></a></li>
		     </c:if>    
		     </ul>
		</section>
		</c:if>
</nav>
</div>