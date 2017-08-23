/*
 * Copyright (c) K.X(Kevin Xin) 2017.
 * Find more details in http://xintq.net
 *
 */
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
});

