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
	$(document).on("click", ".nav-link", function(e) {
	    e.preventDefault();
	    $(".nav-link").removeClass("active"); // 先把大家洗白
	    $(this).addClass("active");           // 自己變亮（這很重要，loadOrders 會抓這個 active）
	    
	    fetchOrderData(0); // 帶上搜尋框的值與新的狀態進行查詢
	});

	$(document).on("click", ".orderInformation , .refund", function() {
		let eventOrderId = $(this).attr("data-id");

		fetch(`/admin/dashboard/order-detail?eventOrderId=${eventOrderId}`, {
			method: "GET"

		}).then(response => {
			// 取得後端設定的片段類型
			const fragmentType = response.headers.get("X-Fragment-Type");

			// 將 HTML 內容與類型一起傳下去
			return response.text().then(html => ({ html, type: fragmentType }));
		}).then(({ html, type }) => {
			// 在這裡你就可以根據 type 分別處理邏輯
			if (type === "refund") {
				$("#refundDetailModal .modal-body").html(html);
				$("#refundDetailModal").modal('show');
			} else {
				// 填充 Modal 內容並顯示
				$("#orderDetailModal .modal-body").html(html);
				$("#orderDetailModal").modal('show');
			}

		});
	})

	$(document).on("click", ".refundRefuse, .refundAccept", function() {

		let refundResult = false;
		const eventOrderId = $("#refundId").text();
		console.log(eventOrderId);

		if ($(this).attr("class").includes("refundRefuse")) {
			refundResult = false;
		} else {
			refundResult = true;
		}

		fetch(`/admin/dashboard/refund?eventOrderId=${eventOrderId}&refundResult=${refundResult}`, {
			method: "GET"
		}).then(response => {
			if (response.ok) {
				alert("申請結果已送出");
				location.reload();
			} else {
				alert("送出失敗");
			}

		}).catch(error => {
			console.error("Fetch error:", error);
		});
	})

	function fetchOrderData(pageNumber) {
		// 獲取所有搜尋欄位的值
		let rawId = $("input[name='eventOrderId']").val();

		let eventOrderId = rawId ? rawId : "";
		let memberName = encodeURIComponent($("input[name='memberName']").val() || "");
		let eventTitle = encodeURIComponent($("input[name='eventTitle']").val() || "");
		let payStatus = $(".nav-link.active").attr("data-status") || "";

		// 組合 URL
		let url = `/admin/dashboard/eventOrders?page=${pageNumber}` +
			`&eventOrderId=${eventOrderId}&memberName=${memberName}&eventTitle=${eventTitle}&payStatus=${payStatus}`;

		// 執行局部載入更新表格
		$("#table-container").load(url);
	}
}
