/**
 * Ticket & QR Code UI
 * Handles QR Modal display and Download.
 * Removed backend simulation.
 */

const Tickets = {
	init() {
		this.bindEvents();
	},

	bindEvents() {
		// Show QR
		$(document).on('click', '.btn-show-qr', function() {
			const orderId = $(this).data('order');
			const qrCodeData = $(this).data('qr'); // Expecting QR data to be present in DOM or fetched via API

			$('#qrOrderId').text(orderId);

			// Assuming QRious is loaded globally or imported
			// If data is missing in "No Logic" mode, we might just show a placeholder or expect backend to have rendered it
			// For now, keeping the QR generation visual logic but removing the mock data creation.

			// If the button has no data, we cannot generate. 
			// In a real "Frontend Only" flow, the data usually comes from the backend api.
			// We will assume the backend passes the string to generate.

			const qrValue = qrCodeData || `MOMENTO-TICKET-${orderId}`; // Fallback for demo

			if (window.QRious) {
				new QRious({
					element: document.getElementById('qrCanvas'),
					value: qrValue,
					size: 200,
					foreground: '#000000',
					background: '#ffffff'
				});
			}

			$('#qrModal').modal('show');
		});

		// Download QR
		$('#btnDownloadQR').click(function() {
			const canvas = document.getElementById('qrCanvas');
			if (canvas) {
				const image = canvas.toDataURL("image/png");
				const link = document.createElement('a');
				link.download = `ticket-${$('#qrOrderId').text()}.png`;
				link.href = image;
				link.click();
			}
		});
	}

};
window.toggleOrder = function(element, targetId) {
	// 找到點擊目標最近的那個容器

	const container = element.closest('.order-container');
	if (container) {
		container.classList.toggle('active');
	}

	// 2. 根據 ID 切換顯示/隱藏
	const detailDiv = document.getElementById(targetId);
	if (detailDiv) {
		detailDiv.classList.toggle('d-none');
	}

};

window.handleRefund = function(event, element) {
	event.stopPropagation();
	$(".refundOrderId").val($(element).attr("data-order-id"));
	$(".refundEventName").val($(element).attr("data-event-title"));

}

$(document).ready(function() {

	$(document).on('click', '.btn-show-qr', function() {
		const eventOrderItemId = $(this).prev().attr("id");
		fetch(`/member/dashboard/get-uuid-by-id?eventOrderItemId=${eventOrderItemId}`)
			.then(response => {
				if (!response.ok) throw new Error("網路請求失敗");
				return response.json();
			})
			.then(data => {
				// 4. 取得後端回傳的 UUID (例如: QR-EVT009...)
				const uuidText = data.uuid;
				const eventName = data.eventName;

				// 5. 繪製 QR Code 到你的 <canvas id="qrCanvas">
				// 這裡假設你引入的是 qrcode.js
				const container = document.getElementById('qrCanvas');
				const h5_el = document.getElementById("eventName");

				// 清除舊圖案（如果需要）
				container.innerHTML = "";
				h5_el.innerText = eventName;

				// 3. 產生 QR Code (使用傳統 qrcode.js 語法)
				new QRCode(container, {
					text: uuidText,
					width: 180,
					height: 180,
					colorDark: "#000000",
					colorLight: "#ffffff",
					correctLevel: QRCode.CorrectLevel.H
				});
			})
			.catch(err => {
				console.error("錯誤:", err);
				alert("無法取得票券資訊");
			});


	});

	$(document).on("click", "#btnSubmitRefund", function() {
		
		const eventOrderId = $(".refundOrderId").val();
		const refundReason = $("#refundDescription").val();
		
		if(refundReason == ""){
			alert("退款原因請勿空白");
			return;
		}			
		
		const params = new URLSearchParams();

		params.append("eventOrderId", eventOrderId);
		params.append("refundReason", refundReason);

		fetch(`/member/dashboard/ticket-refund`, {
			method: "POST",
			body: params
		}).then(response => {
			if (response.ok) {
				alert("退票申請已送出");
				location.reload();
			} else {
				alert("申請失敗");
			}

		}).catch(error => {
			console.error("Fetch error:", error);
		});
	})


});
export default Tickets;
