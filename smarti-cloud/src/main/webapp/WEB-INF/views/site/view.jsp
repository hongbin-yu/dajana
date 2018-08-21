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
        	<li><a href="view.htmp?path=${item.path }">${item.title}</a>
        </c:forEach>
        </ol>
</div>
</div>
</nav>  
<div class="container">
     <main role="main" property="mainContentOfPage" class="col-md-9 col-md-push-3">
        <h1 id="wb-cont">
        <spring:message code="djn.browse"/> - ${folder.title} <a href="view.html?path=${folder.path}&type=${type}" title="刷屏"><span class="glyphicon glyphicon-refresh"></span></a></h1>
        ${folder.description }         
	<div id="view_insert">
	</div>
	<section class="cn-search-dataTable"> 
	        <h2 class="wb-inv">查询结果</h2>
	        <div class="mrgn-tp-xl"></div>
	        <table class="wb-tables table table-striped table-hover nws-tbl" id="dataset-filter" aria-live="polite" data-wb-tables="{
	            &#34;bDeferRender&#34;: true,
	            &#34;ajaxSource&#34;: &#34;getassets.json?path=${folder.path}&#34;,
	            &#34;order&#34;: [1, &#34;desc&#34;],
	             &#34;columns&#34;: [
	                { &#34;data&#34;: &#34;title&#34;, &#34;className&#34;: &#34;nws-tbl-ttl h4&#34; },
	                { &#34;data&#34;: &#34;lastPublished&#34;, &#34;className&#34;: &#34;nws-tbl-date&#34; },
	                { &#34;data&#34;: &#34;subjects&#34;, &#34;className&#34;: &#34;nws-tbl-dept&#34;},
	                { &#34;data&#34;: &#34;location&#34;, &#34;className&#34;: &#34;nws-tbl-type&#34; },
	                { &#34;data&#34;: &#34;description&#34;, &#34;className&#34;: &#34;nws-tbl-desc&#34; },
	                { &#34;data&#34;: &#34;contentType&#34;,  &#34;visible&#34;: false },
	                { &#34;data&#34;: &#34;url&#34;,  &#34;visible&#34;: false },
	                { &#34;data&#34;: &#34;subjects&#34;, &#34;visible&#34;: false },
	                { &#34;data&#34;: &#34;url&#34;, &#34;visible&#34;: false }
	      ]}
	">
	            <thead class="wb-inv">
	            <tr>
	              <th>标题</th>
	              <th>出版日期</th>
	              <th>图标</th>
	              <th>主题</th>
	              <th>描述</th>
	              <th>Location</th>
	              <th>Audience</th>
	              <th>Subject</th>
	              <th>Ministers</th>
	           </tr>
	            </thead>
	<tbody class=" wb-lbx lbx-gal"></tbody></table>
	      
	     
	</section>	

	</main>
    <nav class="wb-sec col-md-3 col-md-pull-9" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
		<input type="hidden" id="folderpath" name="path" value="${folder.path}"/>
<%-- 		<div class="wb-frmvld row well">
			<form action="upload.html" method="POST" id="form-upload" enctype="multipart/form-data">
				<input type="hidden" id="path" name="path" value="${folder.path}"/>
				<input type="hidden" id="type"  name="type" value="${type}"/>
				<input type="hidden" id="input" name="input" value="${input}"/>
				<input type="hidden" name="redirect" value="view.html?path=${folder.path}&type=${type}&input=${input}"/>
						
				<div class="form-group" ondrop="drop(event)" ondragover="allowDrop(event)" style="border:1px solid #aaaaaa;">
					<a href="#" onclick="openFiles()"><span id="openFiles" class="field-name"><spring:message code="djn.select_dragging_drop"/> </span>
					<img class="pull-left mrgn-rght-md" id="uploadImg" path="${folder.path}" alt="" src="<c:url value='/resources/images/upload100.png'/>"/></a>
					<div>
					<br/>
					<label for="override"><input type="checkbox" name="override" value="true" id="override" size="35"> 覆盖旧文件如果重名</label>
					</div>
					<br/>					
					<input class="form-control wb-inv" type="file" id="fileUpload" name="file" size="60" required="required"  multiple/>
				</div>
				<div class="clear-fix"></div>
			</form>
			<div class="panel" id="selectedFiles"></div>
		    <form action='<c:url value="assets.html"></c:url>' method="get" name="cse-search-box" role="search" class="form-inline" accept-charset="UTF-8">
			<input type="hidden" id="path" name="path" value="${folder.path}"/>
			<input type="hidden" id= "input" name="input" value="${input}"/>
			<input type="hidden" id="kw" name="kw" value="${kw}"/>		
			<input type="hidden" id="pageNumber" name="pageNumber" value="${assets.pageNumber}"/>	
			<input type="hidden" id="availablePages" name="availablePages" value="${assets.availablePages}"/>				
			<input type="hidden" id="topage" name="topage" value="assetsmore"/>			    
			<a class="btn btn-default pull-right" href="/site/assets.html?path=${folder.path}" title="快速阅览"><span class="glyphicon glyphicon-edit pull-right"></span></a>
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
		 			
            <details id="${folder.uid }">
            <summary>${folder.title}
            </summary>

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
			</div>	
				<div class="checkbox">
				<label for="readonly"><input type="checkbox" name="readonly" value="true" id="readonly" <c:if test="${folder.readonly=='true' }">checked</c:if>  size="35"  uid="${folder.uid }" onchange="updateNode(this)"> 共享只读（共享用户名不能修改目录下文件）</label>
				</div>								 
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
	    </div> --%>
		
<%-- 	    <div class="clearfix"></div>	
		<div class="row">
		        <c:forEach items="${folders.items}" var="item" varStatus="loop">
		            <div class="well">
			           <a href="view.html?path=${item.path}&type=${type}"> <img id="folder${item.uid }" path="${item.path }"  ondrop="drop(event)" ondragover="allowDrop(event)" alt="${item.title}" class="img-responsive pull-left mrgn-rght-md" src='<c:url value="/resources/images/folder32X32.png"></c:url>'/>
		            	${item.title}</a>
		            	<div class="clearfix"></div>
		            <div id="selectFiles${item.uid }"></div>	
		            </div>  
		            	<c:if test="${(loop.index+1) % 3 ==0  }"><div class="clearfix"></div></c:if>         
		        </c:forEach> 
		</div>  --%>
    <h2 id="wb-sec-h" class="wb-inv">左菜单</h2>
    <section class="list-group menu list-unstyled">	
			<a class="btn btn-default pull-right" href="/site/assets.html?path=${folder.path}&type=${type }" title="<spring:message code="djn.cloud"/><spring:message code="djn.edit"/>"><span class="glyphicon glyphicon-edit pull-right"></span></a>
    
        <h3>
        <c:if test="${folder.parent!='/assets' }">
        <a href='<c:url value="view.html?path=${folder.parent}&type=${type }"></c:url>'>${folder.parentTitle}<span class="glyphicon glyphicon-backward"></span>
        </a>
        </c:if> 
        <c:if test="${folder.parent=='/assets' }">
        <spring:message code="djn.cloud"/>
        </c:if>               
        </h3>    	       
        <ul class="list-group menu list-unstyled">
        <c:forEach items="${leftmenu.subfolders}" var="item" varStatus="loop">
            <li>
            <a  class="list-group-item" href='<c:url value="view.html?path=${item.path }&type=${type }"></c:url>'>${item.title}</a>     
                <ul class="list-group menu list-unstyled">
                    <c:forEach items="${item.subfolders}" var="child" varStatus="loop">
                    	<li><a class="list-group-item" href='<c:url value="view.html?path=${child.path}&type=${type }"></c:url>'>${child.title}</a></li>
                    </c:forEach>
                </ul>
            </li>           
        </c:forEach>    
        </ul>
   </section>     
    </nav>
</div>

