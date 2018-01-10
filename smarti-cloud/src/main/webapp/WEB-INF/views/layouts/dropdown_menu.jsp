<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="authz" %>

<c:set var="uri"><c:url value="/"></c:url></c:set>
<c:set var="username"><authz:authentication property="name" /></c:set>
<script type="text/javascript">
<!--

//-->
$(document).ready(function () {
	$("a#profile").click(function(e) {
		var x = e.pageX;
		var width = $("body").width();
		if(x+360 > width)
			x = width - 360;
/* 		alert("${username}");
    	$.ajax({
			type : "GET",
    		url : "${uri}query/getuser",
	    	data: {
				userName : "${username}"
		    	},
			cache : false,
	    	datatype : "json",
	    	success: function( data ) { 
				$("#username").val(data.userName);
				$("#email").val(data.email);
			},
	   		error: errorException
	    	}).done(function() {


    		});			 */			
		
		$('div#pop-up-profile').show()
	      .css('top', e.pageY)
	      .css('left', x)
	      .css('width',250)
	      .appendTo('body');
		});	
	
		$("#close_pop_up_profile").click(function() {
			$('div#pop-up-profile').hide()
			});

		$("#submitProfile").click(function() {
			if($("#password").val() != $("#confirm").val()) {
				alert("Password does not match. please confirm again!");
				return;
				}
	    	$.ajax({
				type : "POST",
	    		url : "${uri}updateloginpassword",
		    	data: { password: $("#password").val() },
				cache : false,
		    	success: function( data ) { 
					$("#formInfo").html(data);	
				},
		   		error: errorException
		    	}).done(function() {

	        	
	    		$('div#pop-up-profile').hide()

	    		});		
		});		
});

function errorException(jqXHR, exception) {
    if (jqXHR.status === 0) {
        alert('Not connect.\n Verify Network.');
    } else if (jqXHR.status == 404) {
        alert('Requested page not found. [404]');
    } else if (jqXHR.status == 500) {
        alert('Internal Server Error [500].');
    } else if (exception === 'parsererror') {
        alert('Requested JSON parse failed.');
    } else if (exception === 'timeout') {
        alert('Time out error.');
    } else if (exception === 'abort') {
        alert('Ajax request aborted.');
    } else {
        alert('Uncaught Error.\n' + jqXHR.responseText);
    }

}

</script>
	<ul id="nav">
	<authz:authorize ifNotGranted="ROLE_INDEX,ROLEDATABASE,ROLE_ADMINISTRATOR">
		<li class="top"><a href="<c:url value="/query/smart"/>" class="top_link"><span><s:message code="Smart.Query"/></span></a></li>
		<li class="top"><a href="<c:url value="/query/simple"/>" class="top_link"><span><s:message code="Simple.Query"/></span></a></li>
		<li class="top"><a href="<c:url value="/query/saved"/>" class="top_link"><span><s:message code="Saved.Query"/></span></a></li>
		<li class="top"><a href="<c:url value="/query/setting"/>" class="top_link"><span><s:message code="Query.Setting"/></span></a></li>	
	</authz:authorize>	
	<authz:authorize ifAnyGranted="ROLE_INDEX,ROLEDATABASE,ROLE_ADMINISTRATOR">	
		<li class="top"><a href="<c:url value="/query/smart"/>" class="top_link"><span class="down">Query</span></a>
			<ul class="sub">
				<li><a href="<c:url value="/query/smart"/>"><s:message code="Smart.Query"/></a></li>
				<li><a href="<c:url value="/query/simple"/>"><s:message code="Simple.Query"/></a></li>
				<li><a href="<c:url value="/query/saved"/>"><s:message code="Saved.Query"/></a></li>
				<li><a href="<c:url value="/applications"/>">Fulltext Search</a></li>
				<li><a href="<c:url value="/query/setting"/>"><s:message code="Query.Setting"/></a></li>				
			</ul>
		</li>
	</authz:authorize>	
	<authz:authorize ifAnyGranted="ROLE_INDEX,ROLE_ADMINISTRATOR">			
		<li class="top"><a id="recordclass" class="top_link"><span class="down">Image</span></a>
			<ul class="sub">
				<li><a href="<c:url value="/image/recordclass"/>">Record Class</a></li>
				<li><a href="<c:url value="/image/scannersetting"/>">Scanner Setting</a></li>
				<li><a href="<c:url value="/image/scan"/>">Scan</a></li>
				<li><a href="<c:url value="/image/import"/>">Import</a></li>
				<li><a href="<c:url value="/image/importfile"/>">Upload</a></li>
				<li><a href="<c:url value="/image/index"/>">Index</a></li>
				<li><a href="<c:url value="/image/autoimport"/>">Auto Import</a></li>
				<li><a href="<c:url value="/image/simpleindex"/>">Simple Index</a></li>
				<li><a href="<c:url value="/image/log?pageNo=1"/>">Logs</a></li>
			</ul>
		</li>
		<li class="top"><a id="COLD Report" class="top_link"><span class="down">Text & PDF</span></a>
			<ul class="sub">
				<li><a href="<c:url value="/cold/transtable"/>">Translate Table</a></li>
				<li><a href="<c:url value="/cold/coldforms"/>">Forms</a></li>
				<li><a href="<c:url value="/cold/coldreport"/>">COLD Report</a></li>
				<li><a href="<c:url value="/cold/upload"/>">Upload</a></li>
				<li><a href="<c:url value="/cold/eventgroup/new"/>">EventGroup</a></li>
				<li><a href="<c:url value="/cold/reindex"/>">ReIndex</a></li>
				<li><a href="<c:url value="/cold/log?pageNo=1"/>">Logs</a></li>
			</ul>
		</li>
	</authz:authorize>	
	<authz:authorize ifAnyGranted="ROLE_DATABASE,ROLE_ADMINISTRATOR">			
		<li class="top"><a id="database" class="top_link"><span class="down">Database</span></a>
			<ul class="sub">
				<li><a href="<c:url value="/database/edit"/>">Management</a></li>
				<li><a href="<c:url value="/database/purge"/>">Purge</a></li>
				<li><a href="<c:url value="/database/export"/>">Export</a></li>
				<li><a href="<c:url value="/database/import"/>">Import</a></li>
				<li><a href="<c:url value="/database/log?pageNo=1"/>">Logs</a></li>
			</ul>
		</li>
	</authz:authorize>
	<authz:authorize ifAnyGranted="ROLE_ADMINISTRATOR">			
		<li class="top"><a id="report" class="top_link"><span class="down">Administration</span></a>
			<ul class="sub">
				<li><a href="<c:url value="/admin/application/new"/>">Application</a></li>
				<li><a href="<c:url value="/admin/device/new"/>">Device</a></li>
				<li><a href="<c:url value="/admin/security"/>">Query Security</a></li>
				<li><a href="<c:url value="/admin/user"/>">User & Group</a></li>
				<li><a href="<c:url value="/admin/log?pageNo=1"/>">Logs</a></li>
				<!--li><a href="<c:url value="/admin/reports"/>">Reports</a></li-->
			</ul>
		</li>
	</authz:authorize>	
	<authz:authorize ifNotGranted="ROLE_ANONYMOUS">
		<li class="top-right"><a href="<c:url value="/signout"/>" id="logout" class="top_link">Logout &nbsp;</a>
		</li>	
		<li class="top-right"><a class="top_link"><span id="username" class="down">${username }</span></a>
			<ul class="sub">
				<li><a id="profile">Profile</a></li>
			</ul>
		</li>
	</authz:authorize>	
	</ul>
<!-- HIDDEN / POP-UP DIV -->
    <div id="pop-up-profile" class="pop-up">
      <h3>Profile</h3>
      <form name="profile">
		<input type="hidden" value="${username }">
      	<fieldset>
      	<label for="username">Username</label><input type="text" id="user" name="userName" value="${username }" disabled>
      	<label for="password">Password</label><input type="password" id="password" name="passworde" value="">
      	<label for="confirm">Confirm</label><input type="password" id="confirm" name="confirm" value="">
        <label for="email">Email</label><input type="text" id="email" name="email" value="">
      	
      	</fieldset>
        <input type="button" id="submitProfile" name="submit" value="Submit"/>
        <input type="button" id="close_pop_up_profile" name="close" value="close"/>
      </form>
    </div>		
