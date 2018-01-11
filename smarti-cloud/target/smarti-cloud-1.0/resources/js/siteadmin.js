
function deleteNode(path) {
    if(confirm("Are you sure to delete all files under this folder?")) {
    	var url =path+".delete";

    	$.ajax({
    			type : 'GET',	
    			url : url,
    			success: function(result) {
        			window.location.reload();
    		},
    		error:errorException
    	});

        

        }

}
function archiveNode(path) {
    if(confirm("Are you sure to archive all files under this folder?")) {
    	var url =path+".move";

    	$.ajax({
    			type : 'GET',	
    			url : url,
    			success: function(result) {
        			if(result=='') {
    		      		 window.location="${currentNode.path}.html";
        			} else {
            			$("#header_message").html(result);
            		}
    		},
    		error:errorException
    	});

        

        }

   
}

function getContent(uuid) {

	$.ajax({
		type : "GET",
		url : "${host}content/${appPath}/"+uuid,
		cache : false,
    	success: function( data ) { 
			$("#content").html(data);	
			if(CKEDITOR.instances.content) {
				CKEDITOR.instances.content.setData(data);
			}else {
				CKEDITOR.replace( 'content', {
						filebrowserBrowseUrl: '${host}assets/${appPath}.html',
					    filebrowserUploadUrl: '${host}assets/${appPath}.upload'
					
				});
    		}
		},
   		error: errorException
    	});	

}

function editNote(path) {
	$("#pagePath").val(path);
	$.ajax({
		type : "GET",
		url : "${host}note?path="+path,
		cache : false,
    	success: function( data ) { 
    		$("textarea#pageNote").val(data);	
		},
   		error: errorException
    	});	

}

function updateNote() {
	var path = $("#pagePath").val();
	var note = $("textarea#pageNote").val();
	$.ajax({
		type : "POST",
		url : "${host}/note",
		data: {
			path:path,
			note:note
			},
		cache : false,
    	success: function( data ) { 
    		$("textarea#pageNote").val(data);
		},
   		error: errorException
    	});	

}
function openPdf(type) {
    $("#jcr-pages").attr("action","${path}."+type);
	return true;
}

function updateProfile() {
	$.ajax({
		url: "${host}/users",
		data: $("#user_profile").serialize(),
		success: function(result) {
			$("#updateResult").html(result);
		},
		error: errorException
		});
	
}
$(function () {
	 $("#sample").change(function () {
		   var url = "${currentNode.path}.position";
		   if($("#sample").val().length > 2) {
				$.ajax({
					type : "POST",
					url : url,
					data : {
						sample: $("#sample").val()
						},
					cache: false,
					success: function(result) {
						$("#page").val(result.page);
						$("#row").val(result.irow);
						$("#col").val(result.coln);
						$("#length").val(result.length);
					},
			   		error: errorException
					});
			   
			}		

		});
	});

function errorException(jqXHR, exception) {
    busy = 0;
    if (jqXHR.status === 0) {
    	$("#pageinfo").html('Not connect.\n Verify Network.');
    	location.reload();
        //alert('Not connect.\n Verify Network.');
    } else if (jqXHR.status == 404) {
    	//$("#pageinfo").html('Requested page not found. [404]');
        alert('Requested page not found. [404]');
    } else if (jqXHR.status == 500) {
    	//$("#pageinfo").html('Internal Server Error [500].');
        alert('Internal Server Error [500].');
    } else if (exception === 'parsererror') {
    	//$("#pageinfo").html('Requested JSON parse failed.');
        alert('Requested JSON parse failed.');
    } else if (exception === 'timeout') {
    	//$("#pageinfo").html('Time out error.');
        alert('Time out error.');
    } else if (exception === 'abort') {
    	//$("#pageinfo").html('Ajax request aborted.');
        alert('Ajax request aborted.');
    } else {
    	//$("#pageinfo").html('Uncaught Error.\n' + jqXHR.responseText);
        alert('Uncaught Error.\n' + jqXHR.responseText);
    }

}
