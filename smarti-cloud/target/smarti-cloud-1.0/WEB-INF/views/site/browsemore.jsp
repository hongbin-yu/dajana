<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<c:forEach items="${assets.items }" var="item" varStatus="loop">
<div id="${item.uid}" class="col-md-6 collg-4 well">
		<c:if test="${item.mp4}">
			<video poster="video2jpg.jpg?path=${item.path }" controls="controls" width="300" height="200" preload="metadata">
			<source type="video/mp4" src="video.mp4?path=${item.path }"/>
			</video>
			<button class="bnt btn-primary btn-sm" title="点击选择此视频" onclick='javascript:returnFileUrl("video.mp4?path=${item.path}","${item.uid}","video2jpg.jpg?path=${item.path }")'><span class="glyphicon glyphicon-film"></span>${item.title} 植入视频</button>
		</c:if>
        <c:if test="${item.doc2pdf}">
        	<a class="download" href="file/${item.name}?path=${item.path}" target="_BLANK" download><span class="glyphicon glyphicon-download">下载</span></a>
		    <a class="${item.cssClass }" href="doc2pdf.pdf?path=${item.path }">
				<img id="img${item.uid}" src="<c:url value='${item.icon }'></c:url>" class="img-responsive" draggable="true"/>
			</a>
		</c:if>			
		<c:if test="${item.audio}">
		<a class="download" href="file/${item.name}?path=${item.path}" download><span class="glyphicon glyphicon-volume-up">下载</span></a>
		<figure class="wb-mltmd">
				<audio title="${item.title }" preload="metadata">
					<source type="${item.contentType }" src="file/${item.name}?path=${item.path}"/>
				</audio>
				<figcaption>
					<button class="bnt btn-primary btn-sm" title="点击选择此音频" onclick='javascript:returnFileUrl("file/${item.name }?path=${item.path}","${item.uid}","file/${item.name }?path=${item.path }")'><span class="glyphicon glyphicon-volume-up"></span>${item.title} 植入音频</button>
				</figcaption>
		</figure>
		</c:if>	
        <c:if test="${item.doc2pdf}">
        	<a class="download" href="file/${item.name}?path=${item.path}" target="_BLANK" download><span class="glyphicon glyphicon-download">下载</span></a>
		    <a class="${item.cssClass }" href="doc2pdf.pdf?path=${item.path }">
				<img id="img${item.uid}" src="<c:url value='${item.icon }'></c:url>" class="img-responsive" draggable="true"/>
			</a>
		</c:if>					
		<c:if test="${!item.mp4 && !item.audio && !item.doc2pdf}">
<%-- 		<a class="${item.cssClass }-edit" id="href${item.uid }" href="<c:url value='${item.link}'></c:url>">		
 --%>		<img src="<c:url value='${item.icon }'></c:url>" class="img-responsive" draggable="true" onclick="javascript:returnFileUrl('${item.link}','${item.uid }')"/>
			<p>${item.description}</p>
<!-- 		</a> -->
		</c:if>
<div>
	<span class="strong glyphicon glyphicon-link">${item.title} (${item.path})</span>
	<p>	<a href="javascript:removeAsset('${item.path }','${item.uid}')"><button title="删除" class="btn btn-warning btn-xs pull-right"><span class="glyphicon glyphicon-trash"></span></button></a>${item.description}</p>
	<div class="row wb-inv">
	<div class="form-group">
	<label for="title${item.uid }">标题&nbsp;</label><input class="form-control" id="title${item.uid }" name="jcr:title" value="${item.title}" size="24" uid="${item.uid}" onchange="javascript:updateNode(this)"/>
	</div>
	<div class="form-group">
	<label for="url${item.uid }">链接&nbsp;</label><input class="form-control" id="url${item.uid }" name="url" value="${item.url}" size="24" uid="${item.uid}" onchange="javascript:updateNode(this)"/>
	</div>
	<div class="form-group">
		<label for="contentType${item.uid }">类型&nbsp;</label><input class="form-control" id="contentType${item.uid }" name="contentType" value="${item.contentType}" size="24" uid="${item.uid}" disabled/>
	</div>	
	<div class="form-group">
		<label for="size${item.uid }">长度&nbsp;</label><input class="form-control" id="size${item.uid}" name="size" value="${item.size}" size="24" uid="${item.uid}" disabled/>
	</div>	
	<div class="form-group wb-inv">
	<label for="description${item.uid }">描述 </label><br/>
	<div class="panel panel-default" id="description${item.uid }" property="description"  uid="${item.uid }">${item.description}</div>
<%-- 	<textarea class="form-control, form-editable" id="description${item.uid }" name="description" cols="22"  uid="${item.uid}">${item.description}</textarea> --%>
	</div>
	</div>
</div>
<%-- <details>
	<summary><span class="glyphicon glyphicon-edit"></span>${item.path}</summary>
	<div class="row">
	<div class="form-group">
	<label for="title${item.uid }">标题&nbsp;</label><input class="form-control" id="title${item.uid }" name="jcr:title" value="${item.title}" size="24" uid="${item.uid}" onchange="javascript:updateNode(this)"/>
	</div>
	<div class="form-group">
	<label for="url${item.uid }">链接&nbsp;</label><input class="form-control" id="url${item.uid }" name="url" value="${item.url}" size="24" uid="${item.uid}" onchange="javascript:updateNode(this)"/>
	</div>
	<div class="form-group">
		<label for="contentType${item.uid }">类型&nbsp;</label><input class="form-control" id="contentType${item.uid }" name="contentType" value="${item.contentType}" size="24" uid="${item.uid}" disabled/>
	</div>	
	<div class="form-group">
		<label for="size${item.uid }">长度&nbsp;</label><input class="form-control" id="size${item.uid}" name="size" value="${item.size}" size="24" uid="${item.uid}" disabled/>
	</div>	
	<div class="form-group wb-inv">
	<label for="description${item.uid }">描述 </label><br/>
	<div class="panel panel-default" id="description${item.uid }" property="description"  uid="${item.uid }">${item.description}</div>
	</div>
	</div>
</details> --%>
</div>
<%-- <c:if test="${(loop.index + 2) % 3 ==1  }"><div class="clearfix"></div></c:if> --%>
</c:forEach>
