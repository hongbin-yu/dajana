<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page session="false" %>
<div class="col-md-9 col-md-push-3">
	<iframe src="${currentNode.path}.pdf" width="98%" height="400" ></iframe>
	<c:if test="${page.rank>0}">
	<ul class="pagination">
		<c:forEach items="${pages.items}" var="item" varStatus="loop">
			<c:if test="${item.rank>page.rank-9 && item.rank < page.rank + 9 }">
				<li class="${page.rank==loop.count?'active':'' }"><a href="${item.path }.edit">${item.rank}</a></li>
			</c:if>
		</c:forEach>
	</ul>
	</c:if>
</div>
