<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="authz" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<c:set var="username"><authz:authentication property="name" /></c:set>
<c:set var="pagePath"><c:url value="${page.path }"></c:url></c:set>

<%String username = (String)request.getAttribute("username");
String path = "${page.path}";
boolean isOwner = false;
if(path.startsWith("/content/"+username)) {
	isOwner = true;
}
%>

<div class="row pagedetails">
<c:if test="${page.showFilter=='true' && page.status=='true'}">
<section class="cn-search-dataTable"> 
        <h2 class="wb-inv">查询结果</h2>
        <div class="mrgn-tp-xl"></div>
        <table class="wb-tables table table-striped table-hover nws-tbl" id="dataset-filter" aria-live="polite" data-wb-tables="{
            &#34;bDeferRender&#34;: true,
            &#34;ajaxSource&#34;: &#34;${pagePath }.news.json&#34;,
            &#34;order&#34;: [1, &#34;desc&#34;],
             &#34;columns&#34;: [
                { &#34;data&#34;: &#34;title&#34;, &#34;className&#34;: &#34;nws-tbl-ttl h4&#34; },
                { &#34;data&#34;: &#34;lastPublished&#34;, &#34;className&#34;: &#34;nws-tbl-date&#34; },
                { &#34;data&#34;: &#34;subjects&#34;, &#34;className&#34;: &#34;nws-tbl-dept&#34;},
                { &#34;data&#34;: &#34;url&#34;, &#34;className&#34;: &#34;nws-tbl-type&#34; },
                { &#34;data&#34;: &#34;description&#34;,  &#34;className&#34;: &#34;nws-tbl-desc&#34; },
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
<tbody></tbody></table>
      
     
</section>
</c:if>
	<c:if test="${page.showChat=='true'  && page.status=='true'}">
	<div  class="brdr-0 col-sm-6 col-lg-6 mrgn-tp-sm pull-left">
			 <details id="chat">
			        <summary class="btn btn-default text-center">在线通讯<span id="online_notice" class="badge"></span></summary>
					<section id="djchat" class="modal-content">
						<p>发信息前要先<a href="https://sso.dajana.ca/login">登入</a>或<a href="https://sso.dajana.ca/register">注册</a>，要先发一条信息才能开始通讯。</p>
						<div id="chat_message"><p>你发的内容只是你和网站之间通讯，其他人看不见。发完信息后可继续浏览本站。回复内容将提示在“在线通讯”按钮上</p></div>
						<div id="online_chat">
						</div>
						<div id="online_chat_editor" class="panel panel-default online_editor"><p></p></div>
						<a id="online_chat_send" class="btn btn-default btn-block" title="发送" href="javascript:sendChat('${page.path }')"><span class="glyphicon glyphicon-send"></span><img class="wb-inv" id="online_chat_running" src='<c:url value="/resources/images/ui-anim_basic_16x16.gif"></c:url>' alt="发送"/></a>
					</section>
			</details>
			</div>
	</c:if>
	<c:if test="${page.showComment=='true'  && page.status=='true'}">	
	<div  class="brdr-0 col-sm-6 col-lg-6 mrgn-tp-0 mrgn-bttm-0 pull-right">
			<details>
			        <summary class="btn btn-default text-center">发表评论</summary>
					<section id="djcomment" class="modal-content">
						<div id="comment_message"><p>发表评论前要先<a href="https://sso.dajana.ca/login">登入</a>或<a href="https://sso.dajana.ca/register">注册</a>。<br/>你的评论将发表在此页</p></div>
						<div id="online_comment_editor" class="panel panel-default online_editor">
						<p></p>
						</div>
						<div id="online_comment_send" class="btn-group btn-group-justified">
						<a class="btn btn-default" title="发送" href="javascript:sendComment('${page.path }','1')"><span class="glyphicon glyphicon-thumbs-up"></span><img class="wb-inv" id="online_comment_running1" src='<c:url value="/resources/images/ui-anim_basic_16x16.gif"></c:url>' alt="发送"/></a>
						<a class="btn btn-default" title="发送" href="javascript:sendComment('${page.path }','2')"><span class="glyphicon glyphicon-bullhorn"></span><img class="wb-inv" id="online_comment_running2" src='<c:url value="/resources/images/ui-anim_basic_16x16.gif"></c:url>' alt="发送"/></a>
						<a class="btn btn-default" title="发送" href="javascript:sendComment('${page.path }','3')"><span class="glyphicon glyphicon-thumbs-down"></span><img class="wb-inv" id="online_comment_running3" src='<c:url value="/resources/images/ui-anim_basic_16x16.gif"></c:url>' alt="发送"/></a>
						</div>
					</section>
			</details>
		</div>	
	</c:if>		
	<div class="col-xs-12 mrgn-tp-lg">
	<div class="col-sm-4">
	    <dl id="wb-dtmd">
	    <dt>修改日期:</dt>
	    <dd><time><fmt:formatDate type = "date" pattern = "yyyy-MM-dd"
         value = "${page.lastPublished}" /></time></dd>
		</dl>
	</div>
<%-- 	<div class="col-sm-3 mrgn-tp-sm">
        <a href='<c:url value="/protected/share.html?path=${page.path}"></c:url>'  class="btn btn-default btn-block" id="wb-auto-8"><img alt="关注" src='<c:url value="/resources/images/eye.png"></c:url>'>关注此页</a>
	</div> --%>
    <div class="col-sm-3 mrgn-tp-sm pull-right">
        <a href='<c:url value="${page.path}.shr?ref=${username}"></c:url>'  class="wb-lbx btn btn-default btn-block" id="wb-auto-7"><span class="glyphicon glyphicon-qrcode"></span>分享此页</a>
    </div>	
	</div>
</div>
<c:if test="${page.showComment=='true'  && page.status=='true'}">
	<div class="col-xs mrgn-tp-sm" data-ajax-replace='<c:url value="${page.path }.comment?p=${p }"></c:url>'>
	</div>
</c:if>