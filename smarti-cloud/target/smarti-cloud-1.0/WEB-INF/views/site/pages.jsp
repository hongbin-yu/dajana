<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="authz" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<!DOCTYPE html><!--[if lt IE 9]><html class="no-js lt-ie9" lang="zh" dir="ltr"><![endif]--><!--[if gt IE 8]><!-->
<html class="no-js" lang="zh" dir="ltr">
<!--<![endif]-->
<%@include file="../wet/header.jsp" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>
<body class="secondary" vocab="http://schema.org/" typeof="WebPage">
<input type="hidden" name="contentPath" value="${contentPath}" id="contentPath"/>

<main role="main" property="mainContentOfPage" class="container">
<div class="row">
<c:if test="${pages.availablePages>1 }">
<section class="wb-inview show-none bar-demo" data-inview="top-bar">
     <ul class="pager pagination-sm">
     <li class='previous<c:if test="${pages.pageNumber==0}"> disabled</c:if>' ><a href="<c:url value='/folder.html${path }?type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber-1}'/>"><span class="glyphicon glyphicon-chevron-left"></span>上页</a></li>
     <li class="text-center">${pages.pageNumber+1}/${pages.availablePages }(${pages.pageCount })</li>    
     <c:if test="${pages.pageNumber+1<pages.availablePages}">
		<li class="next"><a rel="next" id="nextpageb" href="<c:url value='/folder.html${path}?type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber+1}'/>"><span class="glyphicon glyphicon-chevron-right"></span>下页</a></li>
     </c:if>    
     </ul>
</section>
</c:if>

</div>
<form action="page.html" name="assets" method="GET">
<input type="hidden" name="type" value="${type}"/>
<input type="hidden" name="input" value="${input}"/>
<input id="kw" name="kw" value="${kw}" size="22" placeholder="${path}"> <input id="submit_search" type="submit" value="搜索" title="搜索，创建文件夹如果文件夹不存在" class="btn btn-primary"> 
 </form>   

<c:forEach items="${pages.items }" var="item" varStatus="loop">
<div id="${item.uid}" class="col-md-4 panel panel-default">
<a href="editor.html?uid=${item.uid }" target="_editor">${item.title }</a>
<p>${item.description }</p>
</div>
<c:if test="${(loop.index + 2) % 3 ==1} "><div class="clearfix"></div></c:if>
</c:forEach>


<c:if test="${pages.availablePages>0 }">
<section id="top-bar" class="container wb-overlay modal-content overlay-def wb-bar-t">
     <ul class="pager pagination-sm">
     <li class='previous<c:if test="${pages.pageNumber==0}"> disabled</c:if>' ><a href="<c:url value='/folder.html${path }?type=${type }&input=${input }&kw=${kw }&p=${pages.pageNumber-1}'/>"><span class="glyphicon glyphicon-chevron-left"></span>上页</a></li>
     <li class="text-center">${pages.pageNumber+1}/${folders.availablePages }(${pages.pageCount })</li>    
     <c:if test="${pages.pageNumber+1<assets.availablePages}">
		<li class="next"><a rel="next" id="nextpageb" href="<c:url value='/folder.html${path}?type=${type }&input=${input }&kw=${kw }&p=${folders.pageNumber+1}'/>"><span class="glyphicon glyphicon-chevron-right"></span>下页</a></li>
     </c:if>    
     </ul>
</section>
</c:if>
</main>

<!--[if gte IE 9 | !IE ]><!-->
<script src="<c:url value='/resources/js/jquery/2.1.1/jquery.js'/>"></script>
<script src="<c:url value='/resources/dist/js/wet-boew.min.js'/>"></script>
<!--<![endif]-->
<!--[if lt IE 9]>
<script src="<c:url value='/resources/dist/js/ie8-wet-boew2.min.js'/>"></script>
<![endif]-->
<script type="text/javascript" src="<c:url value='/resources/tinymce/tinymce.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/resources/js/pageEditor.js'/>"></script>

</body>
</html>
 
