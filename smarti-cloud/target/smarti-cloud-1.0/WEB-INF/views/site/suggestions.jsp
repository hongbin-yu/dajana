<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=GB18030" pageEncoding="UTF-8" session="false" %>
<option value="">自由式</option>
<c:forEach items="${templates.items}" var="item" varStatus="loop">
    <option  value="${item.path}">${item.title}</option>
</c:forEach>    
