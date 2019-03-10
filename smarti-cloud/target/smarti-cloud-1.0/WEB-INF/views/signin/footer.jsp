<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:set var="host"><c:url value="/"></c:url></c:set>
<footer role="contentinfo" id="wb-info">
<nav role="navigation" class="container wb-navcurr  visible-md visible-lg">
<h2>关于本网站</h2>
<section class="col-sm-3">
<h3>关于本网站</h3>
<ul class="list-unstyled">
<li><a href='<c:url value="/content/home/termnuse.html"></c:url>'>期限和使用</a></li>
</ul>
</section>
<section class="col-sm-3">
<h3>联系我们</h3>
<ul class="list-unstyled">
<li><a href='<c:url value="/content/home/qna.html"></c:url>'>问题与答案</a></li>
</ul>
</section>
<section class="col-sm-3">
<h3>客户服务</h3>
<ul class="list-unstyled">
<li><a href='<c:url value="/content/home/help.html"></c:url>'>在线帮助</a></li>
</ul>
</section>
 <section class="col-sm-3">
 <img title="扫描QR二维码" class="img-responsive" src="${contentPath}content/home.qrb.jpg?path=http://${pageContext.request.serverName }:${pageContext.request.serverPort }/signup" alt=""/>
  </section>
</nav>

<div class="brand">
            <div class="container">
    <div class="row">
                    <div class="col-xs-4 visible-sm visible-xs tofpg text-center">
                        <a href="#wb-cont">到页端 <span class="glyphicon glyphicon-chevron-up"></span></a>
                    </div>
               </div>
				<div class="col-xs-6 col-md-12 text-center">优鸿网络版权所有</div>
	              
           </div>
</div>
</footer>

<!--[if gte IE 9 | !IE ]><!-->
<script src="<c:url value='/resources/wet-boew/js/jquery/2.1.4/jquery.min.js'/>"></script>
<script src="<c:url value='/resources/wet-boew/js/wet-boew.min.js'/>"></script>
<!--<![endif]-->
<!--[if lt IE 9]>
<script src="<c:url value='/resources/dist/js/ie8-wet-boew2.min.js'/>"></script>
<![endif]-->
<script src='<c:url value="/resources/wet-boew/js/theme.min.js"></c:url>'></script>
<script src="<c:url value='/resources/tinymce/tinymce.min.js'/>"></script>
<script src="<c:url value='/resources/js/pageContent.js'/>"></script>
<script src="<c:url value='/resources/js/djn.js'/>"></script>


