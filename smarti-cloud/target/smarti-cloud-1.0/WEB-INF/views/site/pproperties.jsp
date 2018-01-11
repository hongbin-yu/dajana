<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<section class="modal-dialog modal-content overlay-def">
	<header class="modal-header">
		<h2 class="modal-title">页面属性</h2>
	</header>
	<div class="modal-body wb-frmvld">
	<form  method="POST" id="page-modify" accept-charset="UTF-8">
		<input type="hidden" name="path" value="${page.path }"/>
		<div id="modal_message">${message }</div>
		<label for="path">路径 ${page.path}</label>
		<div class="form-group">
		<label for="title">标题&nbsp;<strong class="required">(必填)</strong></label><input class="form-control form-editable" id="title" required="required" name="jcr:title" value="${page.title }" size="55" uid="${page.uid }" onchange="updateProperty(this)"/>
		</div>
		<c:if test="${page.depth>3 }">
		<div class="checkbox">
		<label for="showThememenu"><input type="checkbox" name="showThememenu" value="true" id="showThememenu" uid="${page.uid }" <c:if test="${page.showThememenu=='true' }">checked</c:if> onchange="updateProperty(this)"> 显示主题菜单</label>
		</div>					
		</c:if>			
		<div class="checkbox">
		<label for="showLeftmenu"><input type="checkbox" name="showLeftmenu" value="true" id="showLeftmenu" <c:if test="${page.showLeftmenu=='true' }">checked</c:if>  uid="${page.uid }" onchange="updateProperty(this)"> 显示左菜单</label>
		</div>		
		<div class="checkbox">
		<label for="hideNav"><input type="checkbox" name="hideNav" value="true" id="hideNav" <c:if test="${page.hideNav=='true' }">checked</c:if>  uid="${page.uid }" onchange="updateProperty(this)"> 隐藏导航菜单</label>
		</div>		
		<div class="checkbox">
		<label for="showFilter"><input type="checkbox" name="showFilter" value="true" id="showFilter" <c:if test="${page.showFilter=='true' }">checked</c:if>  uid="${page.uid }" onchange="updateProperty(this)"> 打开查询</label>
		</div>		
		<div class="checkbox">
		<label for="showChat"><input type="checkbox" name="showChat" value="true" id="showChat" <c:if test="${page.showChat=='true' }">checked</c:if>  uid="${page.uid }" onchange="updateProperty(this)"> 在线通讯</label>
		</div>		
		<div class="checkbox">
		<label for="showComment"><input type="checkbox" name="showComment" value="true" id="showComment" <c:if test="${page.showComment=='true' }">checked</c:if>   uid="${page.uid }" onchange="updateProperty(this)"> 打开评论</label>
		</div>
<%-- 		<div class="checkbox">
		<label for="status"><input type="checkbox" name="status" value="true" id="status" <c:if test="${page.status=='true' }">checked</c:if>   uid="${page.uid }" onchange="updateProperty(this)"> 出版 <time><fmt:formatDate type = "date" pattern = "yyyy-MM-dd HH:mm:ss" value = "${page.lastPublished}" /></time> </label>
		</div> --%>
		<c:if test="${page.status=='true' }">
		<div class="form-group">
		<label for="status">出版日期： <time><fmt:formatDate type = "date" pattern = "yyyy-MM-dd HH:mm:ss" value = "${page.lastPublished}" /></time> </label>		
		<a class="btn btn-danger btn-xs" onclick="javascript:unpublishFolder('${page.uid}')"> 下线目录(含所有子页)</a>
		</div>
		</c:if>
		<a class="btn btn-warning btn-xs" onclick="javascript:publishFolder('${page.uid}')"> 出版目录(最多500页)</a>
		<div class="form-group">
		<label for="order">排序</label><input class="form-control" id="order" name="order" value="${page.order }" size="55"  uid="${page.uid }" onchange="updateProperty(this)"/>
		</div>	
		<div class="form-group">
		<label for="order">口令</label><input class="form-control" id="passcode" name="passcode" value="${page.passcode }" size="55"  uid="${page.uid }" onchange="updateProperty(this)"/>
		</div>	
		<div class="checkbox">
		<label for="secured"><input type="checkbox" name="secured" value="true" id="secured" <c:if test="${page.secured=='true' }">checked</c:if>   uid="${page.uid }" onchange="updateProperty(this)"> 加密</label>
		</div>		
		<div class="checkbox">
		<label for="intranet"><input type="checkbox" name="intranet" value="true" id="intranet" <c:if test="${page.intranet=='true' }">checked</c:if>   uid="${page.uid }" onchange="updateProperty(this)"> 内网</label>
		</div>	
		<div class="form-group">
		<label for="title">重定向/共享</label><input class="form-control" id="redirectTo" name="redirectTo" value="${page.redirectTo }" size="55"  uid="${page.uid }" onchange="updateProperty(this)"/>
		</div>				
		<div class="form-group">
			<label for="description">描述 </label><br/>
			<textarea class="form-control" id="description" name="description" cols="55"  uid="${page.uid }" onchange="updateProperty(this)">${page.description }</textarea>
		</div>		
		<div class="form-group">
			<label for="description">关键词 </label><br/>
			<textarea class="form-control" id="keywords" name="keywords" cols="55"  uid="${page.uid }" onchange="updateProperty(this)">${page.keywords }</textarea>
		</div>	
		<button class="btn btn-primary popup-modal-dismiss" onclick="window.location.reload()">完成</button>  <button class="btn btn-primary popup-modal-dismiss" type="button">关闭</button>
	</form>
	</div>
</section>
