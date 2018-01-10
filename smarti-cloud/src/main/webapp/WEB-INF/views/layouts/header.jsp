<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<div style="width : 25%">
<h2>Test</h2>
</div>
<div>
<tiles:insertAttribute name="query" />
</div>