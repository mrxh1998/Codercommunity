$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	//获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	var files = $("#head-image").prop('files');
	var image = files[0];
	$.post(
	    "/community/discuss/add",
	    {"title":title,"content":content,"image":image},
	    function(data){
	        data = $.parseJSON(data);
	        //在提示框中显示返回的消息
	        $("#hintBody").text(data.msg);
	        //显示提示框
	        $("#hintModal").modal("show");
	        //两秒后自动隐藏提示框
	        setTimeout(function(){
	        	$("#hintModal").modal("hide");
	        	//刷新页面
	        	if(data.code==0){
	        	    `window.location.reload();`
	        	}
	        }, 2000);
	    }
	);
}