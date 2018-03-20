<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<!DOCTYPE html><!--[if lt IE 9]><html class="no-js lt-ie9" lang="zh" dir="ltr"><![endif]--><!--[if gt IE 8]><!-->
<html class="no-js" lang="zh" dir="ltr">
<!--<![endif]-->
<%@include file="../wet/header.jsp" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>
<body class="secondary" vocab="http://schema.org/" typeof="WebPage">
<main role="main" property="mainContentOfPage" class="container">
  <nav class="wb-sec row" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
    <h2 id="wb-sec-h" class="wb-inv">左菜单</h2>
    <section class="list-group menu list-unstyled">
<%--         <c:if test="${page.parent=='/content' }">
        <a href='<c:url value="/templates.html?path=${page.path }"></c:url>'>模板</a>
        </c:if> --%>
        <c:if test="${page.parent!='/content' }">
	        <h3 class="wb-navcurr">
	        <a href="file.html?type=file&input=${input }&path=${parent.path}"><span class="glyphicon glyphicon-backward"></span>${parent.title}<button class="btn btn-default btn-xs pull-right" onclick="javascript:returnPage('${parent.path}','${parent.title}')"><span class="glyphicon glyphicon-ok pull-right"></span></button></a>
	        </h3>
        </c:if>        
        <ul class="list-group menu list-unstyled">
        <c:forEach items="${menu.items}" var="item" varStatus="loop">
            <c:if test="${item.childPages!=null}">
            <li>
            <a class="list-group-item${item.cssClass }" href="file.html?type=file&input=${input }&path=${item.path}">${item.title} <button class="btn btn-default btn-xs pull-right" onclick="javascript:returnPage('${item.path}','${item.title}')"><span class="glyphicon glyphicon-ok pull-right"></span></button></a>
                <ul class="list-group menu list-unstyled">
                    <c:forEach items="${item.childPages}" var="child" varStatus="loop">
                    	<li class="list-group-item"><a href="file.html?type=file&input=${input }&path=${child.path}">${child.title}</a><button class="btn btn-default btn-xs pull-right" onclick="javascript:returnPage('${child.path}','${child.title}')"><span class="glyphicon glyphicon-ok pull-right"></span></button></li>
                    </c:forEach>
                </ul>
            </li>           
            </c:if>
            <c:if test="${item.childPages==null}">
            <li class="list-group-item${item.cssClass }">
	            <a href="file.html?type=file&input=${input }&path=${item.path}">${item.title}<button class="btn btn-default btn-xs pull-right" onclick="javascript:returnPage('${item.path}','${item.title}')"><span class="glyphicon glyphicon-ok pull-right"></span></button></a>
            </li>           
            </c:if>            
        </c:forEach>    
        </ul>
    </section> 
</nav>    
</main>
<!--[if gte IE 9 | !IE ]><!-->
<script src="<c:url value='/resources/wet-boew/js/jquery/2.1.4/jquery.js'/>"></script>
<script src="<c:url value='/resources/wet-boew/js/wet-boew.min.js'/>"></script>
<!--<![endif]-->
<!--[if lt IE 9]>
<script src="<c:url value='/resources/wet-boew/js/ie8-wet-boew2.min.js'/>"></script>
<![endif]-->
<script src="<c:url value='/resources/tinymce/tinymce.min.js'/>"></script>
<script src="<c:url value='/resources/js/pageEditor.js'/>"></script>
<script src="<c:url value='/resources/js/pageContent.js'/>"></script>
<script src="<c:url value='/resources/js/djn.js'/>"></script>
<script type="text/javascript">
var win = (!window.frameElement && window.dialogArguments) || opener || parent || top;

var carousel=win.carousel;
var tinyMCE;
var tinymce = tinyMCE = win.tinymce;
var input = "${input}";
var contextPath = "${contentPath}";
if(contextPath=="/") contextPath="";

function returnFileUrl(fileUrl) {


	if(input && input!="") {
		win.document.getElementById(input).value = (contextPath+fileUrl).replace("//","/")+".html";
		
		close();
	} else {
		tinyMCE.activeEditor.selection.setContent('<img class="img-responsive" alt="" src="'+fileUrl+'">');
		tinyMCE.activeEditor.setDirty(true);
	}
	closeSelf();	
}

function returnPage(fileUrl,title) {

		if(input && input!="") {
			win.document.getElementById(input).value =(contextPath+fileUrl).replace("//","/")+".html";
			close();
		}else if(message) {
	   		tinyMCE.activeEditor.selection.setContent('<a href="'+(contextPath+fileUrl).replace("//","/")+'.html">'+title+'</a>');
	    	tinyMCE.activeEditor.setDirty(true);
			var message = win.document.getElementById("header_message");

			message.innerHTML="";
			var node = document.createElement("section"); 
			node.classList.add("alert");
			node.classList.add("alert-info");
			var h3 = document.createElement("h3");
			node.appendChild(h3);   
			var textnode = document.createTextNode(title+"网页已加入");
			h3.appendChild(textnode);  
			message.appendChild(node);
		}else {
	   		tinyMCE.activeEditor.selection.setContent('<a href="'+(contextPath+fileUrl).replace("//","/")+'.html">'+title+'</a>');
	    	tinyMCE.activeEditor.setDirty(true);
			var message = win.document.getElementById("header_message");
			if(message) {
				message.innerHTML="";
				var node = document.createElement("section"); 
				node.classList.add("alert");
				node.classList.add("alert-info");
				var h3 = document.createElement("h3");
				node.appendChild(h3);   
				var textnode = document.createTextNode(title+"网页已加入");
				h3.appendChild(textnode);  
				message.appendChild(node);
			}

			}	
		
		closeSelf();			
}

function close() {
    var editor = tinymce.EditorManager.activeEditor;
	if(editor) {
	    // To avoid domain relaxing issue in Opera
	    function close() {
	      editor.windowManager.close(window);
	      tinymce = tinyMCE = editor =  null; // Cleanup
	    }

	    if (tinymce.isOpera) {
	      win.setTimeout(close, 0);
	    } else {
	      close();
	    }
	}

  }

function closeSelf() {
	var left_float = win.document.getElementById("left-float");

	if(left_float!=null && left_float !='undefined') {
		if(left_float.getAttribute("style")=="left: 0px; border: 0px none; height: 600px; position: fixed; width: 400px; overflow: hidden; top: 10px; bottom: 30px")
			left_float.setAttribute("style", "left: 0px; border: 0px none; height: 600px; position: fixed; width: 0px; overflow: hidden; top: 10px; bottom: 30px");
		else
			left_float.setAttribute("style", "left: 0px; border: 0px none; height: 600px; position: fixed; width: 400px; overflow: hidden; top: 10px; bottom: 30px");
		
	}
}
</script>
</body>
</html>