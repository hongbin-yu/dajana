<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:set var="contentPath"><c:url value="/"></c:url></c:set>

<div class="container">
     <main role="main" property="mainContentOfPage" class="col-md-8 col-md-push-4">
    <h1 id="wb-cont">文件浏览，查询</h1>      
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
	                { &#34;data&#34;: &#34;url&#34;, &#34;className&#34;: &#34;nws-tbl-type&#34; },
	                { &#34;data&#34;: &#34;description&#34;, &#34;className&#34;: &#34;nws-tbl-desc&#34; },
	                { &#34;data&#34;: &#34;url&#34;,  &#34;visible&#34;: false },
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
<%-- 	<c:forEach items="${assets.items }" var="item" varStatus="loop">
	<div id="${item.uid}" class="col-md-4 well">
        <c:if test="${item.text}">
			<a  class="wb-lbx" title="<spring:message code="djn.edit"/>" href="<c:url value="texteditor.html?uid=${item.uid}"/>"><span class="glyphicon glyphicon-pencil"></span><spring:message code="djn.onlineEdit"/></a>
		</c:if>
        <c:if test="${item.doc2pdf}">
		    <a class="${item.cssClass }" href="doc2pdf.pdf?path=${item.path }&w=1" target="_BLANK">
				<img id="img${item.uid}" src="<c:url value='${item.iconSmall }'></c:url>" class="img-responsive pull-left" draggable="true"/>
			</a>
		</c:if>	
		<c:if test="${item.mp4}">
		<figure class="pull-left"><!-- class="wb-mltmd"> -->
				<video poster="video2jpg.jpg?path=${item.path }&w=1" width="150" height="100" controls="controls"  preload="none">
					<source type="video/mp4" src="video.mp4?path=${item.path }"/>
				</video>
		</figure>
		</c:if>
		<c:if test="${item.audio}">
		<figure class="wb-mltmd">
				<audio title="${item.title }" preload="none">
					<source type="${item.contentType }" src="file/${item.name}?path=${item.path}&w=1"/>
				</audio>
		</figure>
		</c:if>		
		<c:if test="${!item.mp4 && !item.doc2pdf && !item.audio}">
				<c:if test="${item.contentType=='application/pdf'}">
				    <a href="<c:url value='${item.link}'></c:url>">
						<img id="img${item.uid}" src="<c:url value='${item.iconSmall }'></c:url>" class="img-responsive pull-left mrgn-rght-md" draggable="true"/>
					</a>
				</c:if>	
				<c:if test="${item.contentType!='application/pdf'}">
				    <a href="javascript:openImage('<c:url value='${item.link}&w=12'></c:url>')">
						<img id="img${item.uid}" src="<c:url value='${item.iconSmall }'></c:url>" class="img-responsive pull-left mrgn-rght-md" draggable="true"/>
					</a>
				</c:if>	
		</c:if>

		<c:if test="${item.pdf}">
			<input type="checkbox" name="puid" value="${item.uid }"> <a title="<spring:message code="djn.open"/>PDF" href="viewpdf.pdf?uid=${item.uid}" target="_BLANK"><span class="glyphicon glyphicon-open"></span> <spring:message code="djn.open"/>PDF</a>
		</c:if>
		<a class="download pull-right" href="file${item.ext}?path=${item.path}" target="_BLANK" download="${item.title }"><span class="glyphicon glyphicon-download pull-right">下载</span></a>

		<p>${item.title}</p>
		<c:if test="${item.description}"><p>${item.description}</p></c:if>

	<div class="clearfix"></div>	
	</div>
	<c:if test="${(loop.index+1) % 3 ==0  }"><div class="clearfix"></div></c:if>
	</c:forEach>
	<div class="clearfix"></div>
</div>
<div class="row" id="contentmore"></div>
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

		<section id="loading" class="text-center">
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
		</c:if> --%>
	</main>
    <nav class="wb-sec col-md-4 col-md-pull-8" typeof="SiteNavigationElement" id="wb-sec" role="navigation">
		<input type="hidden" id="folderpath" name="path" value="${folder.path}"/>
	    <div class="row well" id="wb-sec">
		    <section>
				<div class="checkbox btn btn-default pull-right">
		    		<label for="toggle"><input id="toggle" type="checkbox" onClick="toggle(this)" title="选择切换"/>全选切换</label>
				</div>
		        <h3 id="wb-cont">
		        <c:if test="${folder.name!='assets' }"><a href="view.html?path=${folder.parent}&type=${type}"><span class="glyphicon glyphicon-backward"></span>${folder.parent}</a></c:if>
		        <a href="?path=${folder.path}&type=${type}" title="刷屏"><span class="glyphicon glyphicon-refresh"></span>${folder.path}</a>
		        </h3>
		    <form action='<c:url value="view.html"></c:url>' method="get" name="cse-search-box" role="search" class="form-inline" accept-charset="UTF-8">
			<input type="hidden" id="path" name="path" value="${folder.path}"/>
			<input type="hidden" id= "input" name="input" value="${input}"/>
			<input type="hidden" id="kw" name="kw" value="${kw}"/>		
			<input type="hidden" id="pageNumber" name="pageNumber" value="${assets.pageNumber}"/>	
			<input type="hidden" id="availablePages" name="availablePages" value="${assets.availablePages}"/>				
			<input type="hidden" id="topage" name="topage" value="assetsmore"/>			    
			<div class="btn btn-default-lg form-group pull-right">
			<label for="wb-srch-q" class="wb-inv"><spring:message code="djn.search"/></label>
			<select id="type" name="type" onchange="this.form.submit()">
			<option value="" <c:if test="${type=='' }">selected</c:if> ><spring:message code="djn.all"/></option>
			<option value="child" <c:if test="${type=='child' }">selected</c:if> ><spring:message code="djn.child"/></option>
			<option value="image" <c:if test="${type=='image' }">selected</c:if> ><spring:message code="djn.image"/></option>
			<option value="video" <c:if test="${type=='video' }">selected</c:if> ><spring:message code="djn.video"/></option>
			<option value="audio" <c:if test="${type=='audio' }">selected</c:if> ><spring:message code="djn.audeo"/></option>
			<option value="application" <c:if test="${type=='application' }">selected</c:if> ><spring:message code="djn.file"/></option>
			</select>
			</div>	
			<a class="btn btn-default pull-right" href="/site/assets.html?path=${folder.path}" title="编辑"><span class="glyphicon glyphicon-edit pull-right"></span></a>
			<a href="javascript:deleteFiles()" class="btn bnt-default btn-danger visible-xs pull-right" title="删除"><span class="glyphicon glyphicon-remove"></span></a>
			<a href="javascript:openPdf()" class="btn btn-primary visible-xs pull-right" title="打开PDF"><span class="glyphicon glyphicon-open"></span></a>
			</form>       
            <details id="${folder.uid }">
            <summary>${folder.title}
            </summary>
            	<c:if test="${assets.availablePages==0}">
    			    <a class="wb-lbx" title="<spring:message code="djn.delete"/>" href="<c:url value="/site/delete.html?uid=${folder.uid }&redirect=/site/assets.html?path=${folder.parent }"/>"><span class="glyphicon glyphicon-remove"></span><spring:message code="djn.delete"/></a>
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
			</section>
	    </div>	
		<div class="wb-frmvld row">
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
<%-- 					<input id="submit_upload" type="button" onclick="javascript:uploadFiles()" value="<spring:message code="djn.upload"/>" class="btn btn-primary" disabled> <input type="reset" value="<spring:message code="djn.clear"/>" onclick="resetSelDiv()" class="btn btn-default">
 --%>				</div>
				<div class="clear-fix"></div>
			</form>
			<div class="panel" id="selectedFiles"></div>	
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
		
	    <div class="clearfix"></div>	
		<div class="row">
		        <c:forEach items="${folders.items}" var="item" varStatus="loop">
		            <div class="well">
			           <a href="view.html?path=${item.path}&type=${type}"> <img id="folder${item.uid }" path="${item.path }"  ondrop="drop(event)" ondragover="allowDrop(event)" alt="${item.title}" class="img-responsive pull-left mrgn-rght-md" src='<c:url value="/resources/images/folder32X32.png"></c:url>'/>
		            	${item.title} (${item.path })</a>
		            	<div class="clearfix"></div>
		            <div id="selectFiles${item.uid }"></div>	
		            </div>  
		            	<c:if test="${(loop.index+1) % 3 ==0  }"><div class="clearfix"></div></c:if>         
		        </c:forEach> 
		</div>        

    </nav>
	
</div>

