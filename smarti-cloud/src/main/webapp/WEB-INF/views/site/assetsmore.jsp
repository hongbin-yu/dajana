<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
	<div class="clearfix"></div>
	<c:forEach items="${assets.items }" var="item" varStatus="loop">
	<div id="${item.uid}" class="col-md-4 well">
<%-- 	<div class="checkbox"><input type="checkbox" class="checkbox" name="puid" value="${item.uid }">
	<a class="download pull-right" href="file${item.ext}?path=${item.path}" target="_BLANK" download="${item.title }"><span class="glyphicon glyphicon-download pull-right">下载</span></a>
	</div>	 --%>
        <c:if test="${item.text}">
			<a  class="wb-lbx" title="<spring:message code="djn.edit"/>" href="<c:url value="texteditor.html?uid=${item.uid}"/>"><span class="glyphicon glyphicon-pencil"></span><spring:message code="djn.onlineEdit"/></a>
		</c:if>
        <c:if test="${item.doc2pdf}">
		    <a class="${item.cssClass }" href="doc2pdf.pdf?path=${item.path }" target="_BLANK">
				<img id="img${item.uid}" src="<c:url value='${item.iconSmall }'></c:url>" class="img-responsive pull-left mrgn-rght-md" draggable="true"/>
			</a>
		</c:if>	
		<c:if test="${item.mp4}">
<!--  		<figure class="wb-mltmd"> -->
 		<figure class="pull-left mrgn-md">
				<video poster="video2jpg.jpg?path=${item.path }&w=1" title="${item.title }" controls="controls" preload="metadata" width="150" height="100" width="150" height="100">
					<source type="video/mp4" src="video.mp4?path=${item.path }"/>
				</video>
<%-- 				<figcaption>
					<p>${item.title }</p>
				</figcaption> --%>
		</figure>
		</c:if>
		<c:if test="${item.audio}">
		<a class="download" href="file/${item.name}?path=${item.path}" download><span class="glyphicon glyphicon-volume-up">下载</span></a>
		<figure class="wb-mltmd mrgn-md">
				<audio title="${item.title }" preload="none">
					<source type="${item.contentType }" src="file/${item.name}?path=${item.path}"/>
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
				    <a href="javascript:openImage('<c:url value='${item.link}'></c:url>')">
						<img id="img${item.uid}" src="<c:url value='${item.iconSmall }'></c:url>" class="img-responsive pull-left mrgn-rght-md" draggable="true"/>
					</a>
				</c:if>	
		</c:if>
		<div><input type="checkbox"  name="puid" value="${item.uid }"> ${item.title} </div>
        <c:if test="${item.pdf}">
		<a title="<spring:message code="djn.open"/>PDF" href="<c:url value="/site/viewpdf.pdf?uid=${item.uid}"/>" target="_BLANK"><span class="glyphicon glyphicon-open"></span> <spring:message code="djn.open"/>PDF</a>
		</c:if>
		<a class="download pull-right" href="file${item.ext}?path=${item.path}" target="_BLANK" download="${item.title }"><span class="glyphicon glyphicon-download pull-right">下载</span></a>
			
<%-- 		<div class="form-group">
		<input class="form-control" id="description${item.uid  }" name="jcr:description" value="${item.description}" size="42" uid="${item.uid}"  onchange="updateNode(this)"/>
		</div> --%>

<%-- 	<details>
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

	</details> --%>
	<div class="clearfix"></div>
	</div>
	<c:if test="${(loop.index+1) % 3 ==0  }"><div class="clearfix"></div></c:if>
	</c:forEach>