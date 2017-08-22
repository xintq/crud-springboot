$(function() {
	// 同步侧边栏选择项目的状态
	$(".nav-sidebar li").removeClass("active");
	$(".navbar-nav li").removeClass("active");

	// 只有和当前页面ID对应的侧边栏是active状态
	$("#sidebar-" + $("#view-id").val()).addClass("active");
	$("#navbar-" + $("#view-id").val()).addClass("active");

	// 全局搜索框绑定回车提交事件，并设置默认焦点，全选状态
	$("#g-search-form-q").keydown(function(e) {
		var e = e || event, keycode = e.which || e.keyCode;
		if (keycode == 13) {
			$("#g-search-form").submit();
		}
	}).focus().select();

	// 设置当前页面的搜索URL
	//$("#g-search-form").attr("action", $("#search-url").val())
	
	// 输入区域大小自适应
	//autosize($(".text-box"));
	//autosize($(".autoExpand"));
});

// 分页链接点击时，也按照搜索模式进行分页查询
function goPage(url) {
	$("#g-search-form").attr("action", url);
	$("#g-search-form").submit();
}

/*
// 输入区域根据输入内容自动调整高度
$(document).one('focus.textarea', '.autoExpand', function() {
	var savedValue = this.value;
	this.value = '';
	this.baseScrollHeight = this.scrollHeight;
	this.value = savedValue;
}).on('input.textarea', '.autoExpand', function() {
	var minRows = this.getAttribute('data-min-rows') | 0, rows;
	this.rows = minRows;
	rows = Math.ceil((this.scrollHeight - this.baseScrollHeight) / 17);
	this.rows = minRows + rows;
});
*/
