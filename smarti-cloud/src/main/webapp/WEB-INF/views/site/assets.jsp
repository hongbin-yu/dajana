<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>
<nav role="navigation" id="wb-bc" property="breadcrumb">
<h2>你在这里:</h2>
<div class="container">
<div class="row">
        <ol class="breadcrumb">
        <c:forEach items="${breadcrumb}" var="item" varStatus="loop">
        	<li><a href="assets.htmp?path=${item.path }">${item.title}</a>
        </c:forEach>
        </ol>
</div>
</div>
</nav>        
<div class="container">
<div class="row">
     <main role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
        <h1 id="wb-cont">		        <c:if test="${folder.name!='assets' }"><a href="assets.html?path=${folder.parent}&type=${type}"><span class="glyphicon glyphicon-backward"></span></a></c:if>
        <spring:message code="djn.cloud"/><spring:message code="djn.edit"/> - ${folder.title} <a href="?path=${folder.path}&type=${type}" title="刷屏"><span class="glyphicon glyphicon-refresh"></span></a></h1>
        ${folder.description }
<div class="row">
	<div id="top_insert">
	</div>
	<c:forEach items="${assets.items }" var="item" varStatus="loop">
	<div id="${item.uid}" class="col-md-4 well">
	<div class="checkbox"><input type="checkbox" class="checkbox" name="puid" value="${item.uid }">
	<a class="download pull-right" href="file${item.ext}?path=${item.path}" target="_BLANK" download="${item.title }"><span class="glyphicon glyphicon-download pull-right">下载</span></a>
	</div>	
		<c:if test="${item.pdf}">
		<a title="<spring:message code="djn.open"/>PDF" href="viewpdf.pdf?uid=${item.uid}" target="_BLANK"><span class="glyphicon glyphicon-open"></span> <spring:message code="djn.open"/>PDF</a>
		</c:if>
        <c:if test="${item.text}">
			<a  class="wb-lbx" title="<spring:message code="djn.edit"/>" href="<c:url value="texteditor.html?uid=${item.uid}"/>"><span class="glyphicon glyphicon-pencil"></span><spring:message code="djn.onlineEdit"/></a>
		</c:if>
        <c:if test="${item.doc2pdf}">
        <p>&nbsp;</p>
<%--         	<a class="download" href="file/${item.name}?path=${item.path}" target="_BLANK" download><span class="glyphicon glyphicon-download">下载</span></a> --%>
		    <a class="${item.cssClass }" href="doc2pdf.pdf?path=${item.path }" target="_BLANK">
				<img id="img${item.uid}" src="<c:url value='${item.icon }'></c:url>" class="img-responsive" draggable="true"/>
			</a>
		</c:if>	
		<c:if test="${item.mp4}">
		<p>&nbsp;</p>
<%-- 		<a class="download" href="file/${item.name}?path=${item.path}" target="_BLANK" download><span class="glyphicon glyphicon-download">下载</span></a> --%>
		<figure class="wb-mltmd">
				<video poster="video2jpg.jpg?path=${item.path }" title="${item.title }" controls="controls" preload="none">
					<source type="video/mp4" src="video.mp4?path=${item.path }"/>
				</video>
				<figcaption>
					<p>${item.title }</p>
				</figcaption>
		</figure>
		</c:if>
		<c:if test="${item.audio}">
<%-- 		<a class="download" href="file/${item.name}?path=${item.path}" download><span class="glyphicon glyphicon-volume-up">下载</span></a>
 --%>		<figure class="wb-mltmd">
				<audio title="${item.title }" preload="none">
					<source type="${item.contentType }" src="file/${item.name}?path=${item.path}"/>
				</audio>
		</figure>
		</c:if>		
		<c:if test="${!item.mp4 && !item.doc2pdf && !item.audio}">
				<c:if test="${item.contentType=='application/pdf'}">
				<p>&nbsp;</p>
				    <a href="<c:url value='${item.link}'></c:url>">
						<img id="img${item.uid}" src="<c:url value='${item.icon }'></c:url>" class="img-responsive" draggable="true"/>
					</a>
				</c:if>	
				<c:if test="${item.contentType!='application/pdf'}">
				    <a href="javascript:openImage('<c:url value='${item.link}'></c:url>')">
						<img id="img${item.uid}" src="<c:url value='${item.icon }'></c:url>" class="img-responsive" draggable="true"/>
					</a>
				</c:if>	
		</c:if>

	<div class="panel panel-default description" id="description${item.uid }" property="description"  uid="${item.uid}" placeholder="description">${item.description}</div>
	
	<details>
		<summary><span class="glyphicon glyphicon-edit"></span> ${item.title}</summary>
		<span class="glyphicon glyphicon-remove"></span>
		<a class="wb-lbx" title="<spring:message code="djn.delete"/>" href="<c:url value='deleteasset.html?path=${item.path}&redirect=/site/assets.html?path=${folder.path }'/>"><spring:message code="djn.delete"/>(${item.path })</a>
		<div class="form-group">
		<label for="title${item.uid  }"><spring:message code="djn.title"/>&nbsp;</label><input class="form-control" id="title${item.uid  }" name="jcr:title" value="${item.title}" size="25" uid="${item.uid}"  onchange="updateNode(this)"/>
		</div>
		<div class="form-group">
		<label for="url${item.uid  }"><spring:message code="djn.link"/>&nbsp;</label><input class="form-control" id="url${item.uid  }" name="url" value="${item.url}" size="25" uid="${item.uid}"  onchange="updateNode(this)"/>
		</div>
		<div class="form-group">
		<select id="rotate${item.uid }" name="rotate">
			<option value="0" selected>0</option>
			<option value="90">90</option>
			<option value="-90">-90</option>
			<option value="180">180</option>
		</select>
		<a class="btn btn-default btn-sm" href="javascript:rotate('${item.uid }')"><spring:message code="djn.rotate"/><img class="wb-inv" id="rotate_running${item.uid }" src='<c:url value="/resources/images/ui-anim_basic_16x16.gif"></c:url>' alt="<spring:message code="djn.rotate"/>"/></a>		
		</div>
		<div class="form-group">
		<label for="contentType${item.uid }"><spring:message code="djn.content_type"/>&nbsp;</label><input class="form-control" id="contentType${item.uid }" name="contentType" value="${item.contentType}" size="24" uid="${item.uid}" disabled/>
		</div>
		<div class="form-group">
			<label for="size${item.uid}"><spring:message code="djn.length"/>&nbsp;</label><input class="form-control" id="size${item.uid}" name="size" value="${item.size}(${item.width}x${item.height})" size="24" uid="${item.uid}" disabled/>
		</div>	
		<div class="form-group">
			<label for="device${item.uid}"><spring:message code="djn.location"/>&nbsp;</label><input class="form-control" id="device${item.uid}" name="size" value="${item.device}" size="60" uid="${item.uid}" disabled/>
		</div>	
		<div class="form-group">
		<label for="lastModified${item.uid }"><spring:message code="djn.upload_date"/>&nbsp;</label>
		<fmt:formatDate pattern="yyy-MM-dd HH:mm:ss" value="${item.lastModified }"/>
		</div>
		<div class="form-group">
		<label for="originalDate${item.uid }"><spring:message code="djn.doc_date"/>&nbsp;</label>
		<fmt:formatDate pattern="yyy-MM-dd HH:mm:ss" value="${item.originalDate }"/>
		</div>
		<div class="form-group">
		<label for="modifiedDate${item.uid }"><spring:message code="djn.modified_date"/>&nbsp;</label>
		<fmt:formatDate pattern="yyy-MM-dd HH:mm:ss" value="${item.modifiedDate }"/>
		</div>
	</details>
	
	</div>
	<c:if test="${(loop.index+1) % 3 ==0  }"><div class="clearfix"></div></c:if>
	</c:forEach>
	<div class="clearfix"></div>
</div>
<!-- <div class="row" id="contentmore"></div> -->
		<c:if test="${assets.availablePages>1 }">
		<c:if test="${assets.pageNumber > 6 }">
		<c:set var="startPage">${assets.pageNumber-5}</c:set>
		</c:if>
		<c:if test="${assets.pageNumber <= 6 }">
		<c:set var="startPage">1</c:set>
		</c:if>
		<c:if test="${assets.pageNumber + 10 < assets.availablePages}">
		<c:set var="endPage">${assets.pageNumber + 10}</c:set>
		</c:if>		
		<c:if test="${assets.pageNumber + 10 >= assets.availablePages}">
		<c:set var="endPage">${assets.availablePages}</c:set>
		</c:if>		

<!-- 		<section id="loading" class="text-center"> -->
		<section class="text-center">
		     <ul class="pagination">
		     <c:if test="${assets.pageNumber>0}"> 
		     	<li><a rel='prev' href="<c:url value='?path=${path }&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber-1}'/>"><spring:message code="djn.last_page"/></a></li>
		     </c:if>
			 <c:forEach var="i" begin="${startPage}" end="${endPage}">
			 <c:if test="${assets.pageNumber==i-1}">
			 <li class="active"><a href="<c:url value='?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${i-1}'/>">${i}</a></li>
			 </c:if>
			 <c:if test="${assets.pageNumber!=i-1}">
			 <li><a href="<c:url value='?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${i-1}'/>">${i}</a></li>
			 </c:if>			 
			 </c:forEach>
		     <c:if test="${assets.pageNumber+1<assets.availablePages}">
				<li><a rel="next" href="<c:url value='?path=${path}&type=${type }&input=${input }&kw=${kw }&p=${assets.pageNumber+1}'/>"><spring:message code="djn.next_page"/></a></li>
		     </c:if>    
		     </ul>
		</section>
		</c:if>
</main>
<nav class="wb-sec col-md-3 col-md-pull-9" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
		<input type="hidden" id="folderpath" name="path" value="${folder.path}"/>
		<div class="wb-frmvld row well">
			<form action="upload.html" method="POST" id="form-upload" enctype="multipart/form-data">
				<input type="hidden" id="path" name="path" value="${folder.path}"/>
				<input type="hidden" id="type"  name="type" value="${type}"/>
				<input type="hidden" id="input" name="input" value="${input}"/>
				<input type="hidden" name="redirect" value="assets.html?path=${folder.path}&type=${type}&input=${input}"/>
						
				<div class="form-group" ondrop="drop(event)" ondragover="allowDrop(event)" style="border:1px solid #aaaaaa;">
					<label for="fileUpload" class="required"><a href="#" onclick="openFiles()"><span id="openFiles" class="field-name"><spring:message code="djn.select_dragging_drop"/> </span></a></label>
					<br/><a href="#" onclick="openFiles()"><img id="uploadImg" path="${folder.path}" alt="" src="<c:url value='/resources/images/upload.png'/>"/></a>
					<div class="panel" id="selectedFiles">
					</div>	
					<div class="checkbox">
					<label for="override"><input type="checkbox" name="override" value="true" id="override" size="35"> 覆盖旧文件如果重名</label>
					</div>
					<input class="form-control wb-inv" type="file" id="fileUpload" name="file" size="60" required="required"  multiple/>
<%-- 				<input id="submit_upload" type="button" onclick="javascript:uploadFiles()" value="<spring:message code="djn.upload"/>" class="btn btn-primary" disabled> <input type="reset" value="<spring:message code="djn.clear"/>" onclick="resetSelDiv()" class="btn btn-default"> --%>
				</div>
			</form>
		    <form action='<c:url value="assets.html"></c:url>' method="get" name="cse-search-box" role="search" class="form-inline" accept-charset="UTF-8">
			<input type="hidden" id="path" name="path" value="${folder.path}"/>
			<input type="hidden" id= "input" name="input" value="${input}"/>
			<input type="hidden" id="kw" name="kw" value="${kw}"/>		
			<input type="hidden" id="pageNumber" name="pageNumber" value="${assets.pageNumber}"/>	
			<input type="hidden" id="availablePages" name="availablePages" value="${assets.availablePages}"/>				
			<input type="hidden" id="topage" name="topage" value="assetsmore"/>			    
			<a class="btn btn-default pull-right" href="/site/view.html?path=${folder.path}" title="快速阅览"><span class="glyphicon glyphicon-eye-open pull-right"></span></a>
			<a href="javascript:deleteFiles()" class="btn bnt-default btn-danger visible-xs pull-right" title="删除"><span class="glyphicon glyphicon-remove"></span></a>
			<a href="javascript:openPdf()" class="btn btn-primary visible-xs pull-right" title="打开PDF"><span class="glyphicon glyphicon-open"></span></a>
					<div class="form-group">
					<label for="type"><spring:message code="djn.display"/></label>
					<select id="type" name="type" onchange="this.form.submit()">
					<option value="" <c:if test="${type=='' }">selected</c:if> ><spring:message code="djn.all"/></option>
					<option value="child" <c:if test="${type=='child' }">selected</c:if> ><spring:message code="djn.child"/></option>
					<option value="image" <c:if test="${type=='image' }">selected</c:if> ><spring:message code="djn.image"/></option>
					<option value="video" <c:if test="${type=='video' }">selected</c:if> ><spring:message code="djn.video"/></option>
					<option value="audio" <c:if test="${type=='audio' }">selected</c:if> ><spring:message code="djn.audeo"/></option>
					<option value="application" <c:if test="${type=='application' }">selected</c:if> ><spring:message code="djn.file"/></option>
					</select>
					</div>
			</form>
					<div class="checkbox form-group">
			    		<label for="toggle"><input id="toggle" type="checkbox" onClick="toggle(this)" title="选择切换"/>全选切换</label>
					</div>				 			
            <details id="${folder.uid }">
            <summary>${folder.title}
            </summary>
<%--             	<c:if test="${assets.availablePages==0}">
    			    <a class="wb-lbx" title="<spring:message code="djn.delete"/>" href="<c:url value="/site/delete.html?uid=${folder.uid }&redirect=/site/assets.html?path=${folder.parent }"/>"><span class="glyphicon glyphicon-remove"></span><spring:message code="djn.delete"/></a>
	      		</c:if> --%>
				<div class="form-group">
				<label for="foldertitle"><spring:message code="djn.title"/>&nbsp;</label><input class="form-control" id="foldertitle" name="jcr:title" value="${folder.title}" size="25" uid="${folder.uid}"  onchange="updateNode(this)"/>
				</div>
				<div class="form-group">   
				<label for="foldertitle"><spring:message code="djn.description"/>&nbsp;</label><div class="panel panel-default description form-control" id="description${folder.uid }" property="description"  uid="${folder.uid}" placeholder="description">${folder.description}</div>
				</div>
				<div class="form-group">
				<label for="folderorderby"><spring:message code="djn.order"/>&nbsp;</label>
				<select class="form-control" id="folderorderby" name="orderby" value="${folder.orderby}" uid="${folder.uid}"  onchange="updateNode(this)">
					<option value="lastModified desc"><spring:message code="djn.lastModified_desc"/></option>
					<option value="lastModified asc" <c:if test="${folder.orderby=='lastModified asc'}">selected</c:if>><spring:message code="djn.lastModified_asc"/></option>
					<option value="originalDate desc" <c:if test="${folder.orderby=='originalDate desc'}">selected</c:if>><spring:message code="djn.docDate_desc"/></option>
					<option value="originalDate asc" <c:if test="${folder.orderby=='originalDate asc'}">selected</c:if>><spring:message code="djn.docDate_asc"/></option>
				</select>
				</div>
				<div class="form-group">
				<label for="folderresolution"><spring:message code="djn.resolution"/>&nbsp;</label>
				<select class="form-control" id="folderresolution" name="resolution" value="${folder.resolution}" uid="${folder.uid}"  onchange="updateNode(this)">
					<option value="540x360"><spring:message code="djn.540x360"/></option>
					<option value="720x540" <c:if test="${folder.resolution=='720x540'}">selected</c:if>><spring:message code="djn.720x540"/></option>
					<option value="360x280" <c:if test="${folder.resolution=='360x360'}">selected</c:if>><spring:message code="djn.360x360"/></option>
					<option value="1080x720" <c:if test="${folder.resolution=='720x540'}">selected</c:if>><spring:message code="djn.1080x720"/></option>
				</select>
				</div>			
				<div class="form-group">
				<label for="sharing">共享用户</label><a class="wb-lbx" href="groupedit.html?path=${folder.path }"><button class="btn btn-primary btn-xs pull-right"><span class="glyphicon glyphicon-cog"></span></button></a>
<%-- 				<input class="form-control" id="sharing" name="sharing" value="${folder.sharing }" size="35"  uid="${folder.uid }" onchange="updateNode(this)" placeholder="输入’用户名@‘"/>
 --%>			</div>	
				<div class="checkbox">
				<label for="readonly"><input type="checkbox" name="readonly" value="true" id="readonly" <c:if test="${folder.readonly=='true' }">checked</c:if>  size="35"  uid="${folder.uid }" onchange="updateNode(this)"> 共享只读（共享用户名不能修改目录下文件）</label>
				</div>								 
<%-- 				<div class="form-group">
				<label for="order">口令</label><input class="form-control" id="passcode" name="passcode" value="${folder.passcode }" size="35"  uid="${folder.uid }" onchange="updateNode(this)"/>
				</div>	 --%>
				<div class="checkbox">
				<label for="intranet"><input type="checkbox" name="intranet" value="true" id="intranetfolder" <c:if test="${folder.intranet=='true' }">checked</c:if> size="35"  uid="${folder.uid }" onchange="updateProperty(this)"> 内部网（外网不能访问目录下文件）</label>
				</div>	
												  
            </details>	
		</div>

		<div class="wb-frmvld row well">
		<details>
			<summary>
				<label for="path"><span class="glyphicon glyphicon-folder-close"></span> <spring:message code="djn.create_folder"/></label>
				<input type="hidden" id="folder-path1" name="path" value="${folder.path }" >
			</summary>
			<div class="wb-frmvld">
			<form action="javascript:createFolder()" id="createFolder" method="POST">
			<input type="hidden" id="folderPath" name="path" value="${folder.path }"/>	
			<div class="form-group">
			<label for="foldername"><spring:message code="djn.path"/><strong class="required">(<spring:message code="djn.required"/>)</strong></label>
			
			<input class="form-control" id="foldername" required="required" name="name" pattern="[A-Za-z0-9\-]{2,}" value="" size="80" placeholder="<spring:message code="djn.alpha_number_only"/>"/>
			</div>
			<div class="form-group">
			<label for="titlefolder"><spring:message code="djn.title"/><strong class="required">(<spring:message code="djn.required"/>)</strong></label><input class="form-control" id="titlefolder" required="required"  name="title" value="" size="25"/>
			</div>
				<input id="submit" type="submit"  value="<spring:message code="djn.submit"/> " class="btn btn-default">
			</form>
			</div>	
		</details>	
		<c:if test="${shares.availablePages>0 }">
			<details>
				<summary>
					<label for="path"><span class="glyphicon glyphicon-folder-open"></span> <spring:message code="djn.sharing_folder"/> </label>
				</summary>
				<ul class="list-group menu list-unstyled">
			        <c:forEach items="${shares.items}" var="item" varStatus="loop">
			            <li id="${item.uid }" class="list-group-item"><a href='<c:url value="/protected/sharing.html?path=${item.path}"></c:url>'>${item.title} (${item.path})</a></li>           
			        </c:forEach> 
		        </ul> 

			</details>
		</c:if>	
			<details>
		        <summary>
					<label for="path"><span class="glyphicon glyphicon-upload"></span> <spring:message code="djn.upload_url"/> </label>
					<input type="hidden" id="folder-path" name="path" value="${folder.path }" >
				</summary>
				<div class="wb-frmvld">
				<form action="javascript:uploadUrl()" id="uploadLInk">
				<input type="hidden" name="path" value="${folder.path }"/>	
				<div class="form-group">
				<label for="uploadLink"><spring:message code="djn.url"/> <strong class="required">(<spring:message code="djn.required"/> )</strong></label>
				<input class="form-control" id="uploadLink" required="required" name="uploadLink" data-rule-minlength="4" size="40" value="" size="25"/>
				</div>
					<input id="submit_upload_url" type="submit" value="<spring:message code="djn.submit"/>" class="btn btn-primary"> <input type="reset" value="<spring:message code="djn.reset"/>" class="btn btn-default">
				</form>
				</div>	
			</details>	
	    </div>

<%-- <div class="wb-inv" id="div_uid">    
<div id="{uid}" class="col-md-4">
	<div class="checkbox">
		<input type="checkbox" class="checkbox" name="puid" value="{uid}"><a title="打开PDF" href="<c:url value="viewpdf?uid={uid}"/>" target="_BLANK"><img title="点击选中" src='<c:url value="/resources/images/pdf.gif"></c:url>'></a>
		<a class="wb-lbx-edit" href="<c:url value='{link}'></c:url>" target="_BLANK"><imgreplace id="img{uid}" src="<c:url value='{icon}'></c:url>" class="img-responsive" draggable="true"></imgreplace></a>
	</div>
	<details>
		<summary><span class="glyphicon glyphicon-edit"></span> {title}</summary>
 		<div class="form-group">
		<label for="title{uid}"><spring:message code="djn.title"/>&nbsp;</label><input class="form-control" id="title{uid}" name="jcr:title" value="" size="25" uid="{uid}"  onchange="updateNode(this)"/>
		</div>
		<div class="form-group">
		<label for="url{uid}"><spring:message code="djn.link"/>&nbsp;</label><input class="form-control" id="url{uid}" name="url" value="" size="25" uid="{uid}"  onchange="updateNode(this)"/>
		</div>
	</details>
</div>	 
</div>	 --%>   
	    <div class="clearfix"></div>
	    <div id="top_folder">	
<%-- 	    <c:if test="${folders.pageCount>3 && assets.pageCount > 12}">
	    <details>
	    <summary>${folders.pageCount} 子目录</summary>
	    </c:if> --%>
        <c:forEach items="${folders.items}" var="item" varStatus="loop">
            <div id="${item.uid}" class="row well">
<%--             <a title="<spring:message code="djn.open"/>PDF" href="viewf2p?path=${item.path}" target="_BLANK"><img title="<spring:message code="djn.open"/>PDF" src='<c:url value="/resources/images/pdf.gif"></c:url>'></a> --%>
            <a href="assets.html?path=${item.path}&type=${type}"> <img id="folder${item.uid }" path="${item.path }"  ondrop="drop(event)" ondragover="allowDrop(event)" alt="${item.title}" class="img-responsive" src='<c:url value="viewfolder?path=${item.path}"></c:url>'/></a>
			<div class="panel" id="selectFiles${item.uid }">
			</div>	            
            <details>
            <summary>${item.title}</summary>
                <a class="wb-lbx btn btn-danger btn-xs" title="<spring:message code="djn.delete"/>" href="<c:url value="/site/deleteasset.html?path=${item.path }"/>"><span class="glyphicon glyphicon-remove"></span><spring:message code="djn.delete"/></a>
				<div class="form-group">
				<label for="foldertitle${loop.index }"><spring:message code="djn.title"/>&nbsp;</label><input class="form-control" id="foldertitle${loop.index }" name="jcr:title" value="${item.title}" size="25" uid="${item.uid}"  onchange="updateNode(this)"/>
				</div>            
				<div class="form-group">
				<label for="folderorderby${loop.index }"><spring:message code="djn.order"/>&nbsp;</label>
				<select class="form-control" id="folderorderby${loop.index }" name="orderby" value="${item.orderby}" uid="${item.uid}"  onchange="updateNode(this)">
					<option value="lastModified desc"><spring:message code="djn.lastModified_desc"/></option>
					<option value="lastModified asc" <c:if test="${item.orderby=='lastModified asc'}">selected</c:if>><spring:message code="djn.lastModified_asc"/></option>
					<option value="originalDate desc" <c:if test="${item.orderby=='originalDate desc'}">selected</c:if>><spring:message code="djn.docDate_desc"/></option>
					<option value="originalDate asc" <c:if test="${item.orderby=='originalDate asc'}">selected</c:if>><spring:message code="djn.docDate_asc"/></option>
				</select>
				<div class="checkbox">
				<label for="intranet"><input type="checkbox" name="intranet" value="true" id="intranet" <c:if test="${item.intranet=='true' }">checked</c:if> size="35"  uid="${item.uid }" onchange="updateNode(this)"> 内部网（外网不能访问目录下文件）</label>
				</div>					
				</div>   
            </details>
            </div>  
            	<c:if test="${(loop.index+1) % 3 ==0  }"><div class="clearfix"></div></c:if>         
        </c:forEach> 
<%-- 	    <c:if test="${folders.pageCount>3 && assets.pageCount > 12}">
        </details>	
        </c:if> --%>
        </div>	
        <div class="clearfix"></div>	    

</nav>
</div>
</div>
