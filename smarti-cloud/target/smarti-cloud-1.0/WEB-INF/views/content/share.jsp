<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<section class="modal-dialog modal-content overlay-def">
	<header class="modal-header">
		<h2 class="modal-title">分享此页</h2>
	</header>
	<div class="modal-body">
	<figure>
	<img class="img-responsive" alt="分享到微信" src="${qrpath }"/>
	<figcaption>
	<p>打开微信Discover扫描QR二维码</p>
	</figcaption>
	</figure>
<%-- 		<div class="wb-frmvld">
			<form action="uploadBarCode.html" method="POST" id="form-upload" enctype="multipart/form-data">
						
				<div id="uploadBox" class="form-group" ondrop="drop(event)" ondragover="allowDrop(event)" style="border:1px solid #aaaaaa;">
					<label for="QRUpload" class="required"><a href="#" onclick="openFiles()"><span id="openFiles" class="field-name">点击选择或拖入二维码图像</span></a></label>
					<hr/>
					<input class="form-control wb-inv" type="file" id="fileUpload" name="file" size="60" required="required"/>
					<button type="button" onclick="submit()"><img id="uploadImage" src="<c:url value='/resources/images/upload.png'/>"/></button>
				</div>
			</form>
		</div>	 --%>
	</div>
</section>
