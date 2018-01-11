<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<section class="modal-dialog modal-content overlay-def">
	<header class="modal-header">
		<h2 class="modal-title">创建新页面</h2>
	</header>
	<div class="modal-body wb-frmvld">
	<div id="error"></div>
	<form action="javascript:createPage()" id="createPage" method="POST">
		<input type="hidden" name="path" value="${path}"/>
		<c:if test="${themes!=null}">
		<div class="form-group">
		<label for="path">主题<strong class="required">(必填)</strong></label>
		<input  class="form-control" id="name" required="required" name="name" pattern="[A-Za-z0-9\-]{2,}" value="" size="35" placeholder="只能填拼音或数字" list="suggestions" >
		<datalist id="suggestions">
			<!--[if lte IE 9]><select><![endif]-->
			<c:forEach items="${themes}" var="item" varStatus="loop">
			<option value="${item.name}">${item.title}</option>
			</c:forEach>
			<!--[if lte IE 9]></select><![endif]-->
		</datalist>	

		<input type="hidden" id="templatePath" name="templatePath" value="/content/templates" size="35"/>
		</div>

		<div class="form-group">
		<label for="title">标题&nbsp;<strong class="required">(必填)</strong></label><input class="form-control" id="title" required="required" name="title" value="" size="35" list="themes"/>
		</div>
		<datalist id="themes">
			<!--[if lte IE 9]><select><![endif]-->
			<c:forEach items="${themes}" var="item" varStatus="loop">
			<option value="${item.title}">${item.title}</option>
			</c:forEach>
			<!--[if lte IE 9]></select><![endif]-->
		</datalist>	
		</c:if>
		
		<c:if test="${themes==null}">
		<label for="path">路径 <strong class="required">(必填)</strong></label><input class="form-control" id="name" required="required" name="name" pattern="[A-Za-z0-9\-]{2,}" value="" size="35" placeholder="只能填拼音或数字"/>
		<div class="form-group">
		<label for="title">标题&nbsp;<strong class="required">(必填)</strong></label><input class="form-control" id="title" required="required" name="title" value="" size="35"/>
		</div>
		<div class="form-group">
		<label for="templatePath">模板</label>
		<select class="form-control" id="templatePath" name="templatePath">
		<option value="">自由式</option>
		<c:forEach items="${templates.items}" var="item" varStatus="loop">
		    <option  value="${item.path}">${item.title}</option>
		</c:forEach>  		
		</select>
		</div>
		<div class="checkbox">
		<label for="showThememenu"><input type="checkbox" name="showThememenu" value="true" id="showThememenu"> 显示主题菜单</label>
		</div>					
		</c:if>		
		<div class="checkbox">
		<label for="showLeftmenu"><input type="checkbox" name="showLeftmenu" value="true" id="showLeftmenu"> 显示左菜单</label>
		</div>		
		<div class="checkbox">
		<label for="hideNav"><input type="checkbox" name="hideNav" value="true" id="hideNav"> 隐藏导航菜单</label>
		</div>		
		<div class="checkbox">
		<label for="showChat"><input type="checkbox" name="showChat" value="true" id="showChat"> 在线通讯</label>
		</div>		
		<div class="checkbox">
		<label for="showComment"><input type="checkbox" name="showComment" value="true" id="showComment"> 打开评论</label>
		</div>
		<div class="form-group">
		<label for="order">排序</label><input class="form-control" id="order" name="order" value="${page.order }" size="35"  uid="${page.uid }"/>
		</div>	
		<div class="form-group">
		<label for="title">重定向</label><input class="form-control" id="redirectTo" name="redirectTo" value="" size="35"/>
		</div>				
		<div class="form-group">
			<label for="description">描述 </label><br/>
			<textarea class="form-control" id="description" name="description" cols="35"></textarea>
		</div>		
		<input id="submit_upload" type="submit" value="提交" class="btn btn-primary"> <input type="reset" value="重填" class="btn btn-default">
	</form>
	</div>
</section>
