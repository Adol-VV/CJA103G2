export function initEventOrders() {
	    const counters = document.querySelectorAll('.counter');
		
		const animate = (counter) => {
		        const target = parseInt(counter.getAttribute('data-target')) || 0;
		        counter.innerText = "0"; // 每次觸發都先歸零

		        const updateCount = () => {
		            const count = parseInt(counter.innerText) || 0;
		            const increment = Math.max(Math.ceil(target / 100), 1);

		            if (count < target) {
		                counter.innerText = count + increment;
		                setTimeout(updateCount, 50);
		            } else {
		                counter.innerText = target;
		            }
		        };
		        updateCount();
		 };
		 
		 // 使用 IntersectionObserver 監測元素是否出現在螢幕上
		     const observer = new IntersectionObserver((entries) => {
		         entries.forEach(entry => {
		             // 當元素進入視窗 (isIntersecting 為 true)
		             if (entry.isIntersecting) {
		                 animate(entry.target);
		                 // 如果你希望每次切換回來都跑一次動畫，就不要取消觀察
		                 // 如果只想跑一次，請加上下面這行：
		                  observer.unobserve(entry.target);
		             }
		         });
		     }, { threshold: 1 }); // 只要露出 10% 就觸發

		     counters.forEach(counter => observer.observe(counter));
		    
			
			

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

	$(document).on("click", ".orderInformation", function() {
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
				// 填充 Modal 內容並顯示
				$("#orderDetailModal .modal-body").html(html);
				$("#orderDetailModal").modal('show');
			

		});
	})

	function fetchOrderData(pageNumber) {
		// 獲取所有搜尋欄位的值
		let rawId = $("input[name='eventOrderId']").val();

		let eventOrderId = rawId ? rawId : "";
		let memberName = encodeURIComponent($("input[name='memberName']").val() || "");
		let eventTitle = encodeURIComponent($("input[name='eventTitle']").val() || "");
		let payStatus = $("select[name='payStatus']").val() || "";
		let isHistory = $("select[name='historyOrders']").val() || "";
		let isHistoryBool = (isHistory === "true");

		// 組合 URL
		let url = `/admin/dashboard/eventOrders?page=${pageNumber}` +
			`&eventOrderId=${eventOrderId}&memberName=${memberName}&eventTitle=${eventTitle}&payStatus=${payStatus}&isHistory=${isHistoryBool}`;

		// 執行局部載入更新表格
		$("#table-container").load(url);
	}
	
}
