$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	if($(btn).hasClass("btn-info")) {
		// 关注TA
		$.post(
		    "/community/follow",
		    {"entityType":3,"entityId":$(btn).prev().val()},
		    function(data){
		        data = $.parseJSON(data);
		        if(data.code==0){
		            window.location.reload();
		        }
		        else{
		            alert(data.msg);
		        }
		    }
		)
	} else {
		// 取消关注
		$.post(
		    "/community/unFollow",
		    {"entityType":3,"entityId":$(btn).prev().val()},
		    function(data){
		        data = $.parseJSON(data);
		        if(data.code==0){
		            window.location.reload();
		        }
		        else{
		            alert(data.msg);
		        }
		    }
		)
	}
}