export function initEventOrders() {
	$(document).on('click', '#eventOrderTabs a', function(e) {
		e.preventDefault();
		$('#eventOrderTabs a').removeClass('active text-white').addClass('text-muted');
		$(this).addClass('active text-white').removeClass('text-muted');
	});

	$(document).on("click", ".page-link", function() {
		let pageNumber = $(this).attr("data-page");
		fetchOrderData(pageNumber);
		

	})
	
	$(document).on("click", "#searchBtn", function() {
	    fetchOrderData(0);
	});
	
	$(document).on("click", ".orderInformation", function(){
		let eventOrderId = $(this).attr("data-id");

		fetch(`/admin/dashboard/order-detail?eventOrderId=${eventOrderId}`, {
					method: "GET"

				}).then(res => res.text())
					.then(htmlFragment => {
						// 把抓回來的 HTML 塞進 Modal 的內容區
						$("#orderDetailModal .modal-body").html(htmlFragment);
						// 顯示 Modal
						$("#orderDetailModal").modal('show');
					})		
	})
	
	$(document).on("click", ".refund", function(){
		let eventOrderId = $(this).attr("data-id");

		$("#refundDetailModal").modal("show");
	})
}

function fetchOrderData(pageNumber) {
    // 獲取所有搜尋欄位的值
	let rawId = $("input[name='eventOrderId']").val();
	
	let eventOrderId = rawId ? rawId : "";
	let memberName = encodeURIComponent($("input[name='memberName']").val() || "");
	let eventTitle = encodeURIComponent($("input[name='eventTitle']").val() || "");

    // 組合 URL
    let url = `/admin/dashboard/eventOrders?page=${pageNumber}` + 
              `&eventOrderId=${eventOrderId}&memberName=${memberName}&eventTitle=${eventTitle}`;
			  
			  console.log("Request URL:", url);

    // 執行局部載入更新表格
    $("#table-container").load(url);
}