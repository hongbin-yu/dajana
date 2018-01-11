<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<!DOCTYPE html><!--[if lt IE 9]><html class="no-js lt-ie9" lang="zh" dir="ltr"><![endif]--><!--[if gt IE 8]><!-->
<html class="no-js" lang="zh" dir="ltr">
<!--<![endif]-->
<tiles:insertAttribute name="header" />
<body class="secondary" vocab="http://schema.org/" typeof="WebPage">
<tiles:insertAttribute name="topmenu" />
<tiles:insertAttribute name="content" />
<tiles:insertAttribute name="footer" />
</body>
</html>