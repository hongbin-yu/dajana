var count = 0;
function p(s) {
	var v=$("#j_password").val();
	if(v=="") {
		count=0;
		$("#count").val("0");
	}

	count = $("#count").val();
	if(count==0) {
		$("#j_password").val(s);		
	}else {
		$("#j_password").val(v+s);
	}
	count++;
	$("#count").val(count);
	if(count==4) {
		$("#submit").click();
		}
}
var j_password = document.querySelector('#j_password');
if(j_password)
	j_password.addEventListener("focus",function(e) {
	j_password.value="";
});

	