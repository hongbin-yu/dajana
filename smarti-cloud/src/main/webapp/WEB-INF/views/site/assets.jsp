<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>

<!-- <div class="container">
<div class="row"> -->
        <main role="main" property="mainContentOfPage" class="container">
	    <div class="col-md-4 well" id="wb-sec">
		    <section>
		        <h3><c:if test="${folder.name!='assets' }"><a href="assets.html?path=${folder.parent}"><span class="glyphicon glyphicon-backward"></span>${folder.parent}</a></c:if>
		        <a href="?path=${folder.path}"><span class="glyphicon glyphicon-refresh"></span>${folder.path}</a>
		        </h3>
            <details id="${folder.uid }">
            <summary>${folder.title}</summary>
            	<c:if test="${assets.availablePages==0}">
    			    <a class="wb-lbx" title="<spring:message code="djn.delete"/>" href="<c:url value="/delete.html?uid=${folder.uid }&redirect=/assets.html?path=${folder.parent }"/>"><span class="glyphicon glyphicon-remove"></span><spring:message code="djn.delete"/></a>
	      		</c:if>
				<div class="form-group">
				<label for="foldertitle"><spring:message code="djn.title"/>&nbsp;</label><input class="form-control" id="foldertitle" name="jcr:title" value="${folder.title}" size="25" uid="${folder.uid}"  onchange="updateNode(this)"/>
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
				<label for="sharing">共享-用户名@</label><input class="form-control" id="sharing" name="sharing" value="${folder.sharing }" size="35"  uid="${folder.uid }" onchange="updateNode(this)" placeholder="输入’用户名@‘"/>
				</div>	
				<div class="checkbox">
				<label for="readonly"><input type="checkbox" name="readonly" value="true" id="readonly" <c:if test="${folder.readonly=='true' }">checked</c:if>  size="35"  uid="${folder.uid }" onchange="updateNode(this)"> 共享只读（共享用户名不能修改目录下文件）</label>
				</div>								 
<%-- 				<div class="form-group">
				<label for="order">口令</label><input class="form-control" id="passcode" name="passcode" value="${folder.passcode }" size="35"  uid="${folder.uid }" onchange="updateNode(this)"/>
				</div>	 --%>
				<div class="checkbox">
				<label for="intranet"><input type="checkbox" name="intranet" value="true" id="intranet" <c:if test="${folder.intranet=='true' }">checked</c:if> size="35"  uid="${folder.uid }" onchange="updateProperty(this)"> 内部网（外网不能访问目录下文件）</label>
				</div>									  
            </details>
			</section>
			    
	    </div>	
		<div class="wb-frmvld col-md-4">
			<form action="upload.html" method="POST" id="form-upload" enctype="multipart/form-data">
				<input type="hidden" id="path" name="path" value="${folder.path}"/>
				<input type="hidden" id="type"  name="type" value="${type}"/>
				<input type="hidden" id="input" name="input" value="${input}"/>
				<input type="hidden" name="redirect" value="assets.html?path=${folder.path}&type=${type}&input=${input}"/>
						
				<div class="form-group" ondrop="drop(event)" ondragover="allowDrop(event)" style="border:1px solid #aaaaaa;">
					<label for="fileUpload" class="required"><a href="#" onclick="openFiles()"><span id="openFiles" class="field-name"><spring:message code="djn.select_dragging_drop"/> </span></a></label>
					<br/><a href="#" onclick="openFiles()"><img id="uploadImg" src="<c:url value='/resources/images/upload.png'/>"/></a>
					<div class="panel" id="selectedFiles">
					</div>	
					<div class="checkbox">
					<label for="override"><input type="checkbox" name="override" value="true" id="override" size="35"> 覆盖旧文件如果重名</label>
					</div>					

					<input class="form-control wb-inv" type="file" id="fileUpload" name="file" size="60" required="required"  multiple/>
					<input id="submit_upload" type="button" onclick="javascript:uploadFiles()" value="<spring:message code="djn.upload"/>" class="btn btn-primary" disabled> <input type="reset" value="<spring:message code="djn.clear"/>" onclick="resetSelDiv()" class="btn btn-default">
				</div>
			</form>
	
		</div>

		<div class="wb-frmvld col-md-4 well">
		<details>
			<summary>
				<label for="path"><span class="glyphicon glyphicon-folder-close"></span> <spring:message code="djn.create_folder"/></label>
				<input type="hidden" id="folder-path" name="path" value="${folder.path }" >
			</summary>
			<div class="wb-frmvld">
			<form action="javascript:createFolder()" id="createFolder" method="POST">
			<input type="hidden" id="folderPath" name="path" value="${folder.path }"/>	
			<div class="form-group">
			<label for="foldername"><spring:message code="djn.path"/><strong class="required">(<spring:message code="djn.required"/>)</strong></label>
			
			<input class="form-control" id="foldername" required="required" name="name" pattern="[A-Za-z0-9\-]{2,}" value="" size="80" placeholder="只能填拼音或数字" placeholder="<spring:message code="djn.alpha_number_only"/>"/>
<!-- 
			<input class="form-control" id="foldername" required="required" name="name" pattern="[A-Za-z0-9\s]{4,}"  data-rule-alphanumeric="true" data-rule-minlength="4" size="40" value="" size="25" placeholder="<spring:message code="djn.alpha_number_only"/>"/>
 -->
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
					<input id="submit_upload_url" type="button" value="<spring:message code="djn.submit"/>" class="btn btn-primary"> <input type="reset" value="<spring:message code="djn.reset"/>" class="btn btn-default">
				</form>
				</div>	
			</details>	
	    </div>

<div class="wb-inv" id="div_uid">    
<div id="{uid}" class="col-md-4">
	<div class="checkbox">
		<input type="checkbox" class="checkbox" name="puid" value="{uid}"><a title="打开PDF" href="<c:url value="viewpdf?uid={uid}"/>" target="_BLANK"><img title="点击选中" src='<c:url value="/resources/images/pdf.gif"></c:url>'></a>
		<a class="wb-lbx-edit" href="<c:url value='{link}'></c:url>" target="_BLANK"><imgreplace id="img{uid}" src="<c:url value='{icon}'></c:url>" class="img-responsive" draggable="true"></a>
	</div>
	<details>
		<summary><span class="glyphicon glyphicon-edit"></span> {title}</summary>
 		<div class="form-group">
		<label for="title{uid}"><spring:message code="djn.title"/>&nbsp;</label><input class="form-control" id="title{uid}" name="jcr:title" value="" size="25" uid="{uid}"  onchange="updateNode(this)"/>
		</div>
		<div class="form-group">
		<label for="url{uid}"><spring:message code="djn.link"/>&nbsp;</label><input class="form-control" id="url{uid}" name="url" value="" size="25" uid="{uid}"  onchange="updateNode(this)"/>
		</div>
		<div class="form-group">
		<select id="rotate{uid}" name="rotate">
			<option value="0" selected>0</option>
			<option value="90">90</option>
			<option value="-90">-90</option>
			<option value="180">180</option>
		</select>
<%-- <a class="btn btn-default btn-sm" href="javascript:rotate('{uid}')"><spring:message code="djn.rotate"/><img class="wb-inv" id="rotate_running{uid}" src='<c:url value="/resources/images/ui-anim_basic_16x16.gif"></c:url>' alt="<spring:message code="djn.rotate"/>"/></a>		
		</div>		
		<div class="form-group">
		<label for="contentType{uid}"><spring:message code="djn.content_type"/>&nbsp;</label><input class="form-control" id="contentType{uid}" name="contentType" value="" size="24" uid="{uid}" disabled/>
		</div>
		<div class="form-group">
			<label for="size{uid}"><spring:message code="djn.length"/>&nbsp;</label><input class="form-control" id="size{uid}" name="size" value="" size="24" uid="{uid}" disabled/>
		</div>	 --%>
		</div>				
	</details>
</div>	 
</div>	   
	    <div class="clearfix"></div>	
        <c:forEach items="${folders.items}" var="item" varStatus="loop">
            <div class="col-md-4">
            <a title="<spring:message code="djn.open"/>PDF" href="<c:url value="viewf2p?path=${item.path}"/>" target="_BLANK"><img title="<spring:message code="djn.open"/>PDF" src='<c:url value="/resources/images/pdf.gif"></c:url>'></a>
            <a href="assets.html?path=${item.path}"> <img id="folder${item.uid }" path="${item.path }"  ondrop="drop(event)" ondragover="allowDrop(event)" alt="${item.title}" class="img-responsive" src='<c:url value="viewfolder?path=${item.path}"></c:url>'/></a>
			<div class="panel" id="selectFiles${item.uid }">
			</div>	            
            <details>
            <summary>${item.title} (${item.path })</summary>
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
        <div class="clearfix"></div>	    
<div class="row">
	<div id="top_insert">
	</div>
	<c:forEach items="${assets.items }" var="item" varStatus="loop">
	<div id="${item.uid}" class="col-md-4 well">
	<div class="checkbox">
        <c:if test="${item.pdf}">
		<input type="checkbox" class="checkbox" name="puid" value="${item.uid }"><a title="<spring:message code="djn.open"/>PDF" href="<c:url value="viewpdf?uid=${item.uid}"/>" target="_BLANK"><span class="glyphicon glyphicon-open"></span> <spring:message code="djn.open"/>PDF</a>
		</c:if>
        <c:if test="${item.text}">
			<a  class="wb-lbx" title="<spring:message code="djn.edit"/>" href="<c:url value="texteditor.html?uid=${item.uid}"/>"><span class="glyphicon glyphicon-pencil"></span><spring:message code="djn.onlineEdit"/></a>
		</c:if>
	    <a class="${item.cssClass }" href="<c:url value='${item.link}'></c:url>">
			<img id="img${item.uid}" src="<c:url value='${item.icon }'></c:url>" class="img-responsive" draggable="true"/>
		</a>
		<div class="panel panel-default description" id="description${item.uid }" property="description"  uid="${item.uid}" placeholder="description">${item.description}</div>
		
<%-- 		<a class="btn bnt-default" href="editImage.html?uid=${item.uid}">Edit</a> --%>

	</div>
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
			<label for="size${item.uid}"><spring:message code="djn.length"/>&nbsp;</label><input class="form-control" id="size${item.uid}" name="size" value="${item.size}" size="24" uid="${item.uid}" disabled/>
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

	</details>
	
	</div>
	<c:if test="${(loop.index+1) % 3 ==0  }"><div class="clearfix"></div></c:if>
	</c:forEach>
	<div class="clearfix"></div>
</div>
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



