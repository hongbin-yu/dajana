<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@page import="com.filemark.jcr.service.JcrServices"%>


<div class="col-md-9 col-md-push-3">
<div class="wb-frmvld">
<table class='<c:if test="${pages.pageCount>10 }">wb-tables </c:if>table table-striped table-hover' data-wb-tables='{ "ordering": true }'>
<thead>
<tr>
<th></th>
<th></th>
<th></th>
</tr>
</thead>
<tbody>
<c:forEach items="${pages.items }" var="item">
<tr class="gradeX">
<td class="center">
</td>
<td>
<a href="${item.content }" target="_pdf"></a>
</td>
</tr>
</c:forEach>
</tbody>
</table>
<c:if test="${pages.availablePages>1 }">
<nav class="row pagntn-prv-nxt" role="navigation">
     <h2 class="wb-inv">Document navigation</h2>
     <ul class="list-unstyled">
     <c:if test="${pages.pageNumber>0}">
         <li class="col-sm-4"><a href="${path}?${jcrForm.searchString}&p=${pages.pageNumber-1}"><span class="glyphicon glyphicon-chevron-left"></span>Previous</a></li>
     </c:if>
     <c:if test="${pages.pageNumber==0}">
         <li class="col-sm-4 text-center"></li>
     </c:if>
     <li class="col-sm-4 text-center">${pages.pageNumber+1}/${pages.availablePages }(${pages.pageCount })</li>    
     <c:if test="${pages.pageNumber<pages.availablePages}">
		<li class="col-sm-4 text-right"><a rel="next" id="nextpageb" href="${path }?${jcrForm.searchString}&p=${pages.pageNumber+1}"><span class="glyphicon glyphicon-chevron-right"></span>Next</a></li>
     </c:if>    
     </ul>
</nav>
</c:if>
</div>
</div>




