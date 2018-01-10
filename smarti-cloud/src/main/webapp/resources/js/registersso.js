function p(s) {
	var v=$("#password").val();
	if(v=="") {
		count=0;
		$("#count").val("0");
	}
	count = $("#count").val();
	if(count==0) {
		$("#password").val(s);		
	}else {
		$("#password").val(v+s);
	}	

	count++;
	$("#count").val(count);
	if(count==4) {
		$("#details_pass").removeAttr("open");
		$("#details_confirm").attr("open","open");
		}	

}
function c(s) {
	var v=$("#passwordconfirm").val();

	count = $("#count").val();
	if(count==4) {
		$("#passwordconfirm").val(s);		
	}else {
		$("#passwordconfirm").val(v+s);
	}	

	count++;
	$("#count").val(count);
	if(count==8) {
		$("#submit").click();
		}

}