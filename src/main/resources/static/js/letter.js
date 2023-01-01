$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
	    "/community/letter/send",
	    {"toName":toName,"content":content},
	    function(data){
	        data = $.parseJSON(data);
	        //在提示框中显示返回的消息
	        if(data.code==0){
	            $("#hintBody").text("发送成功!");
	        }else{
	            $("#hintBody").text(data.msg);
	        }
	        //显示提示框
	        $("#hintModal").modal("show");
	        //两秒后自动隐藏提示框
	        setTimeout(function(){
	        	$("#hintModal").modal("hide");
	        	//刷新页面
	        	if(data.code==0){
	        	    location.reload();
	        	}
	        }, 2000);
	    }
	);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}