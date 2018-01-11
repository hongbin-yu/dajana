<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<section class="modal-dialog modal-content overlay-def">
	<header class="modal-header">
		<h2 class="modal-title">页面属性</h2>
	</header>
	<div class="modal-body wb-frmvld">
	<form:form  method="POST" id="page-modify" accept-charset="UTF-8">
		<input type="hidden" name="path" value="${page.path }"/>
		${message }
		<label for="path">路径 ${page.path}</label>
		<div class="form-group">
		<label for="title">标题&nbsp;<strong class="required">(必填)</strong></label><input class="form-control form-editable" id="title" required="required" name="jcr:title" value="${page.title }" size="35" uid="${page.uid }" onchange="updateProperty(this)"/>
		</div>
		<div class="checkbox">
		<label for="showLeftmenu"><input type="checkbox" name="showLeftmenu" value="true" id="showLeftmenu" <c:if test="${page.showLeftmenu=='true' }">checked</c:if>  uid="${page.uid }" onchange="updateProperty(this)"> 显示左菜单</label>
		</div>		
		<div class="checkbox">
		<label for="hideNav"><input type="checkbox" name="hideNav" value="true" id="hideNav" <c:if test="${page.hideNav=='true' }">checked</c:if>  uid="${page.uid }" onchange="updateProperty(this)"> 隐藏导航菜单</label>
		</div>		
		<div class="checkbox">
		<label for="showChat"><input type="checkbox" name="showChat" value="true" id="showChat" <c:if test="${page.showChat=='true' }">checked</c:if>  uid="${page.uid }" onchange="updateProperty(this)"> 在线通讯</label>
		</div>		
		<div class="checkbox">
		<label for="showComment"><input type="checkbox" name="showComment" value="true" id="showComment" <c:if test="${page.showComment=='true' }">checked</c:if>   uid="${page.uid }" onchange="updateProperty(this)"> 打开评论</label>
		</div>
		<div class="checkbox">
		<label for="status"><input type="checkbox" name="status" value="true" id="status" <c:if test="${page.status=='true' }">checked</c:if>   uid="${page.uid }" onchange="updateProperty(this)"> 上线</label>
		</div>
		<div class="form-group">
		<label for="order">排序</label><input class="form-control" id="order" name="order" value="${page.order }" size="35"  uid="${page.uid }" onchange="updateProperty(this)"/>
		</div>	
		<div class="form-group">
		<label for="order">口令</label><input class="form-control" id="passcode" name="passcode" value="${page.passcode }" size="35"  uid="${page.uid }" onchange="updateProperty(this)"/>
		</div>	
		<div class="form-group">
		<label for="title">重定向/共享</label><input class="form-control" id="redirectTo" name="redirectTo" value="${page.redirectTo }" size="35"  uid="${page.uid }" onchange="updateProperty(this)"/>
		</div>				
		<div class="form-group">
			<label for="description">描述 </label><br/>
			<textarea class="form-control" id="description" name="description" cols="35"  uid="${page.uid }" onchange="updateProperty(this)">${page.description }</textarea>
		</div>		
		<button class="btn btn-primary popup-modal-dismiss" onclick="window.location.reload()">完成</button>  <button class="btn btn-primary popup-modal-dismiss" type="button">关闭</button>
	</form:form>
	</div>
</section>
