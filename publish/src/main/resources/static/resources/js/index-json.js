/*	$(function () {	
	
		    $( "#index0" ).autocomplete({
		    	open: function() {
		    		$('.ui-menu').width(300);
		    	},
		    	source: function(request,response) {
		    		getIndexes(request,response,1);
		    	},
		    	minLength:1
		    });
		    
		    $( "#index1" ).autocomplete({
		    	open: function() {
		    		$('.ui-menu').width(300);
		    	},

		    	source: function(request,response) {
		    		getIndexes(request,response,2);
		    	},
		    	minLength:1
		    });
		    $( "#index2" ).datepicker({
		    	dateFormat: 'yy-mm-dd',
		    	beforeShow : function() {
		    		$('.ui-datepicker').css('font-size',11);
		    		$('.ui-datepicker').width('180');
		    	}
		    }).val();
		    
		    
	});
*/
	var cache = {};
	function getIndexes(request,response,tableLevel) {
		var term = tableLevel+request.term;
		if( term in cache ) {
			response(cache[term]);
			return;
		}
	
		var url  = "/smarti/image/indexes?appId="+$('#appId').val()+"&draw.name="+$('#index0').val()+"&fold.name="+$('#index1').val()
		+"&doc.name="+$('#index2').val()+"&tableLevel="+tableLevel;
		$.getJSON(url,request,function(data,status,xhr) {
			if(data != null)
				cache[term] = data;
			response(data);
		});
	
	}
	

