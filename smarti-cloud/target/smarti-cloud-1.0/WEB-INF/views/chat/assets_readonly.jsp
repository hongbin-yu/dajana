<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>

<!-- <div class="container">
<div class="row"> -->
        <main role="main" property="mainContentOfPage" class="container">
	    <div class="col-md-4 well" id="wb-sec">
		    <section>
		        <h3><c:if test="${folder.name!='assets' }"><a href="?path=${folder.parent}"><span class="glyphicon glyphicon-backward"></span>${folder.parent }</a></c:if>
		        <a href="?path=${folder.path}">${folder.path}</a>
		        </h3>
            <details id="${folder.uid }">
            <summary>${folder.title}</summary>
				<div class="form-group">
				<label for="foldertitle"><spring:message code="djn.title"/>&nbsp;</label><input class="form-control" id="foldertitle" name="jcr:title" value="${folder.title}" size="25" uid="${folder.uid}"/>
				</div>            
            </details>
			</section>
			    
	    </div>	

		<div class="wb-frmvld col-md-4 well">
		<c:if test="${shares.availablePages>0 }">
			<details>
				<summary><label for="path"><span class="glyphicon glyphicon-folder-open"></span> <spring:message code="djn.sharing_folder"/></label></summary>
				<ul class="list-group menu list-unstyled">
			        <c:forEach items="${shares.items}" var="item" varStatus="loop">
			            <li id="${item.uid }" class="list-group-item"><a href='<c:url value="/protected/sharing.html?path=${item.path}"></c:url>'>${item.title} (${item.path})</a></li>           
			        </c:forEach> 
		        </ul> 

			</details>			
		</c:if>	
	    </div>

  
	    <div class="clearfix"></div>	
        <c:forEach items="${folders.items}" var="item" varStatus="loop">
            <div class="col-md-4">
            <a title="<spring:message code="djn.open"/>PDF" href="<c:url value="/site/viewf2p?path=${item.path}"/>" target="_BLANK"><span class="glyphicon glyphicon-open">PDF</span></a>
            <a href="?path=${item.path}"> <img id="folder${item.uid }" path="${item.path }" alt="${item.title}" class="img-responsive" src='<c:url value="/site/viewfolder?path=${item.path}"></c:url>'/></a>
			<div class="panel" id="selectFiles${item.uid }">
			</div>	  
            <p>${item.title} (${item.path })</p>
            </div>  
            	<c:if test="${(loop.index+1) % 3 ==0  }"><div class="clearfix"></div></c:if>         
        </c:forEach> 	
        <div class="clearfix"></div>	    
<div class="row">
	<div id="top_insert">
	</div>
	<c:forEach items="${assets.items }" var="item" varStatus="loop">
	<div id="${item.uid}" class="col-md-4 well">
	<div class="checkbox">
        <c:if test="${item.pdf}">
		<input type="checkbox" class="checkbox" name="puid" value="${item.uid }"><a title="<spring:message code="djn.open"/>PDF" href="<c:url value="viewpdf?uid=${item.uid}"/>" target="_BLANK"><span class="glyphicon glyphicon-open">PDF</span></a>
		</c:if>

	    <a class="${item.cssClass }" href="<c:url value='${item.link}'></c:url>">
			<img id="img${item.uid}" src="<c:url value='${item.icon }'></c:url>" class="img-responsive" draggable="true"/>
		</a>
		<div class="panel panel-default" id="description${item.uid }" property="description"  uid="${item.uid}" placeholder="description">${item.description}</div>
		
	</div>
		<p><span class="glyphicon glyphicon-eye-open"></span>${item.title}  (${item.path })</p>


	</div>
	<c:if test="${(loop.index+1) % 3 ==0  }"><div class="clearfix"></div></c:if>
	</c:forEach>
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
		<section class="text-center">
		     <ul class="pagination">
		     <c:if test="${assets.pageNumber>0}"> 
		     	<li><a rel='prev' href="<c:url value='/protected/sharing.html?path=${path }&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber-1}'/>"><spring:message code="djn.last_page"/></a></li>
		     </c:if>
			 <c:forEach var="i" begin="${startPage}" end="${endPage}">
			 <c:if test="${assets.pageNumber==i-1}">
			 <li class="active"><a href="<c:url value='/protected/sharing.html?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${i-1}'/>">${i}</a></li>
			 </c:if>
			 <c:if test="${assets.pageNumber!=i-1}">
			 <li><a href="<c:url value='/protected/sharing.html?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${i-1}'/>">${i}</a></li>
			 </c:if>			 
			 </c:forEach>
		     <c:if test="${assets.pageNumber+1<assets.availablePages}">
				<li><a rel="next" href="<c:url value='/protected/sharing.html?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber+1}'/>"><spring:message code="djn.next_page"/></a></li>
		     </c:if>    
		     </ul>
		</section>
		</c:if>
</main>



