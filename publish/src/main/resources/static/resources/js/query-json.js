var busy = 0;
var uri = "/"+location.pathname.split("/")[1];
$(function () {	

	$( "#queryform" ).submit(function( event ) {
		// Stop form from submitting normally  
		event.preventDefault();   
		// Get some values from elements on the page:
		loadPage(1);
		});
		
		var cache = {};
	    $( "#value" ).autocomplete({
	    	open: function() {
	    		$('.ui-menu').width(600);
	    		$('.ui-menu').css('fontSize','13px');
	    	},

	    	source: function(request,response) {
	    		var tableLevel = $('#tableLevel').val();
	    		var term = tableLevel+request.term;
                if(tableLevel > 10 || busy == 1) {
                	return;
                }
/*	    		if( term in cache ) {
	    			response(cache[term]);
	    			return;
	    		}*/

	       		if( term in cache ) {
			    	response( $.map( cache[term], function( item ) {
			              return { 
				              label: item.label,
				              value: item.name,
				              id: item.id              
				              };					    	
		    		}));
	    		}

	    		var url  = uri+"/query/indexes";
	    		busy = 1;
	    		//?appId="+$('#appId').val()+"&drawId="+$('#drawId').val()+"&foldId="+$('#foldId').val()
	    		//+"&docId="+$('#docId').val()+"&tableLevel="+$('#tableLevel').val()+"&operator=BEGINS+WITH&value="+$('#value').val();
	    		$.ajax({
	    			type : 'GET',	
	    			url : url,
	    			data: {
	    				appId: $('#appId').val(),
	    				drawId: $('#drawId').val(),
	    				foldId: $('#foldId').val(),
	    				docId: $('#docId').val(),
	    				tableLevel: $('#tableLevel').val(),
	    				operator: $('#operator').val(),
	    				value: $('#value').val()+"%"
	    				//pageNo: pageNo
	    			},
	    			timeout:2000,
	    			success: function(data) {
	    				busy = 0;
	    				if(data != null)
	    					cache[term] = data;
	    				//response(data);
	    		    	response( $.map( data, function( item ) {
	    		              return { 
	    			              label: item.label,
	    			              value: item.name,
	    			              id: item.id              
	    			              };					    	
	    	    			}));

	    			},
	    			error:errorException
	    		});
//	    		$.getJSON(url,request,function(data,status,xhr) {
//	    			if(data != null)
//	    				cache[term] = data;
//	    			response(data);
//	    		});

	    	},
	    	minLength:5,      
	    	select: function( event, ui ) {
	    		$('#value').val(ui.item.value);
	    		loadPage(1);
	    	},
	    	error:errorException
	    });
	    
	
});

var pageNumber = 0;
function loadPage(pageNo) {
	var url =uri+ "/query/jsonpage";

	$.ajax({
			type : 'GET',	
			url : url,
			data: {
				appId: $('#appId').val(),
				drawId: $('#drawId').val(),
				foldId: $('#foldId').val(),
				docId: $('#docId').val(),
				tableLevel: $('#tableLevel').val(),
				operator: $('#operator').val(),
				value: $('#value').val(),
				pageNo: pageNo
			},
			success: function(result) {
				var html = "";
				if(!$("#append").is(":checked")) {
				$('#tbody').html(html);
				}
				$('#navpage').html(html);
					if(typeof(result.appConfigs) == 'undefined' || result.pageCount == 0) {
						$('#formInfo').html('<div class="pagemessage">No Entries found for '+$('#value').val()+'</div>');
						$('#pageinfo').html("0-0");
						return
					}
	
				$('#formInfo').html('');
				$('#pageinfo').html(result.pageNumber +"/" +result.availablePages+"-"+result.pageCount);
				var levelid = $('#levelid').val();
				var size = result.appConfigs.length;
		


				$.each(result.items,function(i,data) {
						html += '<tr class="pages">';
						html += '<td class="pages"><input type="checkbox" name="ids" value="'+data.pageId+'"/>';
						if(data.hasNote)
							html += '<img id="'+data.pageId+'" class="note" src="'+uri+'/resources/images/noted.gif" title="'+data.note+'"/>';
						else
							html += '<img id="'+data.pageId+'" class="note" src="'+uri+'/resources/images/note.gif"  title="add note"/>';
						html += '</td>'	
						html += '<td class="pages"><a href="'+uri+'/query/viewPdf?p='+data.pageId+'" class="pdf" target="_blank">'+(i+1+(result.pageNumber-1)*result.pageSize)+":"+data.externOffset+'</a></td>';
						$.each(data.indexes, function(n,index) {
							if(n>=levelid-1 && n<= size) {
								if(index == null)
									html += '<td></td>';
								else
									html += '<td>'+index+'</td>';
			
					}
	
				});
				html += '</tr>';
				});
			$('#tbody').append(html);
			if(result.pageCount>0) {
				html='From <input type="text" name = "from" value="0" size="2" style="display : inline;"/> to <input type="text" name = "to" value="0" size="3" style="display : inline;"/>';  
				html += '<input type="submit" name="submit" value="Open" style="display : inline;"/>';
				html +='<input type="submit" name="submit" value="Open All" style="display : inline;"/>';
				$('#openpages').html(html);
			}
			pageNumber = pageNo;
			if(result.pageNumber < result.availablePages) {
				$('#more').attr('disabled',false);
			}else {
				$('#more').attr('disabled','disabled');
			
			}
			html ="";
			
			if(result.pageNumber > 1) {
				html += '<a href="#" onclick="loadPage('+(result.pageNumber-1)+')">Previous</a> ';
			}
			for(var i= result.startPage; i<result.startPage+10 && i <=result.availablePages;i++) {
				if(result.pageNumber == i) 
					html +='<b> '+i+' </b>';
				else
					html += '<a href="#" onclick="loadPage('+i+')">'+i+'</a> ';
	
			}
			if(result.pageNumber < result.availablePages) {
				html += '<a href="#" onclick="loadPage('+(result.pageNumber+1)+')">Next</a>';
			}
			$('#navpage').html(html);
		},
		error:errorException
	});
}

function appendPage() {
	var pageNo = pageNumber + 1;
	var url = uri+"/query/jsonpage";
	$.ajax({
			type : 'GET',	
			url : url,
			data: {
				appId: $('#appId').val(),
				drawId: $('#drawId').val(),
				foldId: $('#foldId').val(),
				docId: $('#docId').val(),
				tableLevel: $('#tableLevel').val(),
				operator: $('#operator').val(),
				value: $('#value').val(),
				pageNo: pageNo
			},
		success: function(result) {
			var html = "";
			if(!$("#append").is(":checked")) {
			$('#tbody').html(html);
			}
			$('#navpage').html(html);
				if(typeof(result.appConfigs) == 'undefined' || result.pageCount == 0) {
					$('#formInfo').html('<div class="pagemessage">No Entries found for '+$('#value').val()+'</div>');
					$('#pageinfo').html("0-0");
					return
				}
				$('#formInfo').html('');		
		$('#pageinfo').html(result.pageNumber +"/" +result.availablePages+"-"+result.pageCount);
		var levelid = $('#levelid').val();
		var size = result.appConfigs.length;
		var html ='';
		if(!$("#append").is(":checked")) {
			$('#tbody').html(html);
		}
		$.each(result.items,function(i,data) {
			html += '<tr class="pages">';
			html += '<td class="pages"><input type="checkbox" name="ids" value="'+data.pageId+'"/></td>';
			if(data.hasNote)
				html += '<img id="'+data.pageId+'" class="note" src="'+uri+'/resources/images/noted.gif" title="'+data.note+'"/>';
			else
				html += '<img id="'+data.pageId+'" class="note" src="'+uri+'/resources/images/note.gif"  title="add note"/>';
			html += '<td class="pages"><a href="'+uri+'/query/viewPdf?p='+data.pageId+'" class="pdf" target="pdfview">'+(i+1+(result.pageNumber-1)*result.pageSize)+'</a></td>';
			$.each(data.indexes, function(n,index) {
				if(n>=levelid-1 && n<= size) {
					if(index == null)
						html += '<td></td>';
					else
						html += '<td>'+index+'</td>';
		
				}

			});
			html += '</tr>';
		});
		$('#tbody').append(html);
		if(result.pageCount>0) {
			html='From <input type="text" name = "from" value="0" size="2" style="display : inline;"/> to <input type="text" name = "to" value="0" size="3" style="display : inline;"/>';  
			html += '<input type="submit" name="submit" value="Open" style="display : inline;"/>';
			html +='<input type="submit" name="submit" value="Open All" style="display : inline;"/>';
			$('#openpages').html(html);
		}
		pageNumber +=1;
		if(result.pageNumber < result.availablePages) {
			$('#more').attr('disabled',false);
		}else {
			$('#more').attr('disabled','disabled');
		
		}
		html ="";
		
		if(result.pageNumber > 1) {
			html += '<a href="#" onclick="loadPage('+(result.pageNumber-1)+')">Previous</a> ';
		}
		for(var i= result.startPage; i<result.startPage+10 && i <= result.availablePages;i++) {
			if(result.pageNumber == i) 
				html +='<b> '+i+' </b>';
			else
				html += '<a href="#" onclick="loadPage('+i+')">'+i+'</a> ';

		}
		if(result.pageNumber < result.availablePages) {
			html += '<a href="#" onclick="loadPage('+(result.pageNumber+1)+')">Next</a>';
		}
		$('#navpage').html(html);

		}
	});

}

function errorException(jqXHR, exception) {
    busy = 0;
    if (jqXHR.status === 0) {
    	$("#pageinfo").html('Not connect.\n Verify Network.');
    	location.reload();
        //alert('Not connect.\n Verify Network.');
    } else if (jqXHR.status == 404) {
    	$("#pageinfo").html('Requested page not found. [404]');
        //alert('Requested page not found. [404]');
    } else if (jqXHR.status == 500) {
    	$("#pageinfo").html('Internal Server Error [500].');
        //alert('Internal Server Error [500].');
    } else if (exception === 'parsererror') {
    	$("#pageinfo").html('Requested JSON parse failed.');
        //alert('Requested JSON parse failed.');
    } else if (exception === 'timeout') {
    	$("#pageinfo").html('Time out error.');
        //alert('Time out error.');
    } else if (exception === 'abort') {
    	$("#pageinfo").html('Ajax request aborted.');
        //alert('Ajax request aborted.');
    } else {
    	$("#pageinfo").html('Uncaught Error.\n' + jqXHR.responseText);
        //alert('Uncaught Error.\n' + jqXHR.responseText);
    }
	if(!$("#append").is(":checked")) {
		$('#tbody').html("");
	}
	

}

