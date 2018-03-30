<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<section class="modal-dialog modal-content overlay-def">
	<header class="modal-header">
		<h2 class="modal-title">共享群编辑</h2>
	</header>
	<div class="modal-body wb-frmvld">
	<form  method="POST" id="page-modify" accept-charset="UTF-8">
		<input type="hidden" name="path" value="${folder.path }"/>
		<div id="modal_message">${message }</div>
		<label for="path">路径 ${folder.path}</label>
		<div class="form-group">
		<label for="title">标题&nbsp;<strong class="required">(必填)</strong></label><input class="form-control form-editable" id="title" required="required" name="jcr:title" value="${folder.title }" size="55" uid="${folder.uid }" onchange="updateNode(this)"/>
		</div>
		<div class="form-group">
		<label for="status">创建日期： <time><fmt:formatDate type = "date" pattern = "yyyy-MM-dd HH:mm:ss" value = "${folder.lastModified}" /></time> </label>		
		</div>
		<div class="form-group">
		<label for="status">创建者： ${folder.createdBy }</label>		
		</div>	
		<div class="checkbox">
		<label for="intranet"><input type="checkbox" name="intranet" value="true" id="intranetfolder" <c:if test="${folder.intranet=='true' }">checked</c:if> size="35"  uid="${folder.uid }" onchange="updateProperty(this)"> <span class="text-danger">向大家拿用户开放</span></label>
		</div>
		<div class="checkbox">
		<label for="readonly"><input type="checkbox" name="readonly" value="true" id="readonly" <c:if test="${folder.readonly=='true' }">checked</c:if>  size="35"  uid="${folder.uid }" onchange="updateNode(this)"> <span class="text-primary">群外用户只读</span></label>
		</div>							
		<div class="panel panel-success">
		<header class="panel-heading">群里用户</header>
			<div id="inGroup" class="panel panel-body">
				<c:forEach items="${usersInGroup.items }" var="item" varStatus="loop">
					<div id="${item.uid}" class="col-md-3">
					<a href="javascript:removeUser('${item.uid }','${item.path }')" title="退群"><img class="img-responsive" src="file/icon.jpg?path=/${item.name}/assets/icon/x120.jpg" alt="删除"></a>
					<p>${item.title } (${item.name})
					</div>
					<c:if test="${(loop.index+1) % 4 ==0  }"><div class="clearfix"></div></c:if>
				</c:forEach>
			</div>	
		</div>	
		<div class="panel panel-default">
		<header class="panel-heading">云里用户</header>
		<div class="panel panel-body">
			<c:forEach items="${users.items }" var="item" varStatus="loop">
				<div id="${item.uid}" class="col-md-3">
				<a href="javascript:addUser('${folder.path }','${item.path }')" title="入群"><img class="img-responsive" src="file/icon.jpg?path=/${item.name}/assets/icon/x120.jpg" alt="加入"></a>
				<p>${item.title } (${item.name})
				</div>
				<c:if test="${(loop.index+1) % 4 ==0  }"><div class="clearfix"></div></c:if>
			</c:forEach>
		</div>

		</div>			
		<button class="btn btn-primary popup-modal-dismiss" onclick="window.location.reload()">完成</button>  <button class="btn btn-primary popup-modal-dismiss" type="button">关闭</button>
	</form>
	</div>
</section>
