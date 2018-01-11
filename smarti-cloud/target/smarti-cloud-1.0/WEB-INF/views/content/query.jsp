<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page session="false" %>
<div class="wb-frmvld">
<form action="${path}.html" method="get" id="validation-example">
<input type="hidden" name="q" value="${jcrForm.q}">
<input type="hidden" name="l" value="${currentNode.level-3}">
<div class="form-group">
<select class="form-control" name="n">
<c:forEach items="${appConfigs}" var="config" varStatus="status">
<c:choose>
<c:when test="${status.index == jcrForm.n}">
	<option value="${status.index}" selected>${config.name}</option>
</c:when>
<c:when test="${status.index >= currentNode.level-3}">
	<option value="${status.index}">${config.name}</option>
</c:when>
</c:choose>
</c:forEach>
</select>
</div>
<div class="form-group">
<label for="value" class="required">LIKE<span class="field-name"></span> <strong class="required">(required)</strong></label>
<input id="value" class="form-control" name="v" required="required" value="${jcrForm.v}" size="27" maxlength="150">
</div>
<button type="submit" id="wb-query-sub" class="btn btn-primary">Query</button>
</form>
</div>