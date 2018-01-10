<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@include file="../wet/header.jsp" %>
<body class="secondary" vocab="http://schema.org/" typeof="WebPage">
<main role="main" property="mainContentOfPage" class="container">
  <nav class="wb-sec row" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
    <h2 id="wb-sec-h" class="wb-inv">左菜单</h2>
    <section class="list-group menu list-unstyled">
        <h3 class="wb-navcurr">
        <c:if test="${page.parent=='/content' }">
        <a href='<c:url value="/templates.html?path=${page.path }"></c:url>'><img src='<c:url value="/resources/images/arrowleft.png"/>'/>模板</a>
        </c:if>
        <c:if test="${page.parent!='/content' }">
        <a href="templates.html?path=${parent.path}"><img src='<c:url value="/resources/images/arrowleft.png"/>'/>${parent.title}</a>
        </c:if>        
        </h3>
        <ul class="list-group menu list-unstyled">
        <c:forEach items="${menu.items}" var="item" varStatus="loop">
            <c:if test="${item.childPages!=null}">
            <li>
            <a class="list-group-item${item.cssClass }" href="templates.html?path=${item.path}">${item.title}</a>
                <ul class="list-group menu list-unstyled">
                    <c:forEach items="${item.childPages}" var="child" varStatus="loop">
                    	<li class="list-group-item"><a href="templates.html?path=${child.path}">${child.title}</a><img title="插入模板" alt="" class="pull-right" src="<c:url value="/resources/images/play.png"></c:url>" onclick="javascript:returnTemplate('${child.path}')"></li>
                    </c:forEach>
                </ul>
            </li>           
            </c:if>
            <c:if test="${item.childPages==null}">
            <li class="list-group-item${item.cssClass }">
	            <a href="templates.html?path=${item.path}">${item.title}</a><img title="插入模板" alt="" class="pull-right" src="<c:url value="/resources/images/play.png"></c:url>" onclick="javascript:returnTemplate('${item.path}')">
            </li>           
            </c:if>            
        </c:forEach>    
        </ul>
    </section> 
</nav>    
</main>
<!--[if gte IE 9 | !IE ]><!-->
<script src="<c:url value='/resources/dist/js/jquery/2.1.4/jquery.js'/>"></script>
<script src="<c:url value='/resources/dist/js/wet-boew.min.js'/>"></script>
<!--<![endif]-->
<!--[if lt IE 9]>
<script src="<c:url value='/resources/dist/js/ie8-wet-boew2.min.js'/>"></script>
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

function returnFileUrl(fileUrl) {
	var message = win.document.getElementById("header_message");
	if(message) {
		message.innerHTML="<section class=\"alert alert-success\"><h3>加入:"+fileUrl+"</h3></section>";

		}

	if(input && input!="") {
		win.document.getElementById(input).value = fileUrl;
		
		close();
	} else {
		tinyMCE.activeEditor.selection.setContent('<img class="img-responsive" alt="" src="'+fileUrl+'">');
		tinyMCE.activeEditor.setDirty(true);
	}

}

function returnTemplate(fileUrl) {
	var message = win.document.getElementById("header_message");
	if(message) {
		message.innerHTML="<section class=\"alert alert-info\"><h3>加入:"+fileUrl+"</h3></section>";

		}
     $.ajax({
		    url: contentPath+"/template.json?path="+fileUrl,
		    type: "GET", //ADDED THIS LINE
		    // THIS MUST BE DONE FOR FILE UPLOADING
		    contentType: "application/json",
		    processData: false,
		    success: function(data) {
	    		tinyMCE.activeEditor.selection.setContent(data.content);
		    	tinyMCE.activeEditor.setDirty(true);
				var message = win.document.getElementById("header_message");
				
				if(message) {
					message.innerHTML="";
					var node = document.createElement("section"); 
					node.classList.add("alert");
					node.classList.add("alert-success");
					var h3 = document.createElement("h3");
					node.appendChild(h3);   
					var textnode = document.createTextNode(data.title+"模板已加入");
					h3.appendChild(textnode);  
					message.appendChild(node);
					}				
		    },
		    error: function() {
			    alert("出错："+fileUrl);

		    }
		    // ... Other options like success and etc
		}); 



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
</script>