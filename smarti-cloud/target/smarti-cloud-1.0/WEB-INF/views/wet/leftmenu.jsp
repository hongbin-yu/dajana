<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<div class="col-md-3 col-md-pull-9">
<c:if test="${menu.pageCount>0 }">
<table class='<c:if test="${menu.pageCount>20 }">wb-tables </c:if>table table-striped table-hover' data-wb-tables='{ "ordering": false }'>
<thead>
<tr>
<th>

</th>
</tr>
</thead>
<tbody>
<c:forEach items="${menu.items}" var="item">
<tr class="gradeX">
<td>
<img alt="" src='<c:url value="/resources/images/${item.ocm_classname}.gif"/>'>
<a href='${item.path}.html'>${item.title}</a>
<c:if test="${currentNode.level>1 }">
<authz:authorize ifAnyGranted="ROLE_ADMINISTRATOR,ADMINISTRATOR">
<a href="javascript:deleteNode('${item.path }')"><img src='<c:url value="/resources/images/cut.gif"></c:url>' alt="delete" title ="delete"></a>
</authz:authorize>
</c:if>
</td>
</tr>
</c:forEach>

</tbody>
</table>
<c:if test="${menu.availablePages>1 }">
<nav class="row pagntn-prv-nxt" role="navigation">
     <h2 class="wb-inv">Document navigation</h2>
     <ul class="list-unstyled">
     <c:if test="${menu.pageNumber>1}">
         <li class="col-sm-4"><a href='${path}.html?s=${menu.pageNumber-1}'><span class="glyphicon glyphicon-chevron-left"></span>Previous</a></li>
     </c:if>
     <c:if test="${menu.pageNumber==0}">
         <li class="col-sm-4"></li>
     </c:if>
     <li class="col-sm-4">${menu.pageNumber+1}/${menu.availablePages }(${menu.pageCount })</li>    
     <c:if test="${menu.pageNumber<menu.availablePages}">
		<li class="col-sm-4 text-right"><a rel="next" id="nextpageb" href="${path}.html?s=${menu.pageNumber+1}"><span class="glyphicon glyphicon-chevron-right"></span>Next</a></li>
     </c:if>    
     </ul>
</nav>
</c:if>
</c:if>
</div>

<script>
function deleteNode(path) {
    if(confirm("Are you sure to delete all files under this folder?")) {
    	var url =path+".delete";

    	$.ajax({
    			type : 'GET',	
    			url : url,
    			success: function(result) {
        			if(result=='') {
    		      		 window.location="${currentNode.path}.html";
        			} else {
            			$("#header_message").html(result);
            		}
    		},
    		error:errorException
    	});

        

        }
   
}

function errorException(jqXHR, exception) {
    busy = 0;
    if (jqXHR.status === 0) {
    	$("#pageinfo").html('Not connect.\n Verify Network.');
    	location.reload();
        //alert('Not connect.\n Verify Network.');
    } else if (jqXHR.status == 404) {
    	//$("#pageinfo").html('Requested page not found. [404]');
        alert('Requested page not found. [404]');
    } else if (jqXHR.status == 500) {
    	//$("#pageinfo").html('Internal Server Error [500].');
        alert('Internal Server Error [500].');
    } else if (exception === 'parsererror') {
    	//$("#pageinfo").html('Requested JSON parse failed.');
        alert('Requested JSON parse failed.');
    } else if (exception === 'timeout') {
    	//$("#pageinfo").html('Time out error.');
        alert('Time out error.');
    } else if (exception === 'abort') {
    	//$("#pageinfo").html('Ajax request aborted.');
        alert('Ajax request aborted.');
    } else {
    	//$("#pageinfo").html('Uncaught Error.\n' + jqXHR.responseText);
        alert('Uncaught Error.\n' + jqXHR.responseText);
    }

}

</script>