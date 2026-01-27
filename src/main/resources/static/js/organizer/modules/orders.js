/**
 * 訂單與商品訂單管理模組
 */
import { showToast } from './utils.js';

export function initOrderManagement() {
    // Tab switching for product orders
    $('#productOrderTabs a').click(function (e) {
        e.preventDefault();
        $('#productOrderTabs a').removeClass('active text-white').addClass('text-muted');
        $(this).addClass('active text-white').removeClass('text-muted');
        // In real app, filter table by status
    });

    // Select all orders
    $('#selectAllOrders').change(function () {
        $('.order-checkbox').prop('checked', $(this).is(':checked'));
    });

    // Batch operations
    $('#btnBatchPrepare').click(function () {
        batchUpdateStatus('preparing');
    });

    $('#btnBatchShip').click(function () {
        batchUpdateStatus('shipping');
    });

    // Global functions for modal interactions (if used in HTML onclick)
    window.showOrderDetail = showOrderDetail;
    window.updateOrderStatus = updateOrderStatus;
    window.confirmShipping = confirmShipping;
    window.handleRefund = handleRefund;
	
	$("#btnApplyFilter").on("click", function(){
	    // 獲取所有參數值
	    const activeEvent = document.getElementById('activeEvent').value;
	    const buyer = document.querySelector('input[name="buyer"]').value; // 對應你後端的 buyer 參數
		const finishedEvent = document.getElementById("finishedEvent").value;
	    
	    // 組裝 URL (注意路徑要對應你 Controller 的 @GetMapping)
	    const url = `/organizer/dashboard/tickets?activeEvent=${activeEvent}&finishedEvent=${finishedEvent}&buyer=${buyer}`;

	    fetch(url, {
	        method: 'GET',
	        headers: {
	            'X-Requested-With': 'XMLHttpRequest' // 標記為非同步請求
	        }
	    })
	    .then(response => response.text())
	    .then(html => {
	        // 關鍵：將回傳的 HTML 直接替換掉原本的表格內容
	        document.getElementById('orderListContainer').innerHTML = html;
		console.log("回傳的內容：", html);
	    })
	    .catch(error => console.error('Error:', error));
	})
}

function batchUpdateStatus(status) {
    const selected = $('.order-checkbox:checked').length;
    if (selected === 0) {
        showToast('請先選擇訂單', 'warning');
        return;
    }

    if (status === 'preparing') {
        if (confirm(`確定要將 ${selected} 筆訂單標記為備貨中嗎？`)) {
            showToast(`已將 ${selected} 筆訂單標記為備貨中`, 'success');
        }
    } else if (status === 'shipping') {
        showToast('批次出貨功能需要先設定物流資訊', 'info');
    }
}

export function showOrderDetail(orderId) {
    $('#modalOrderId').text('#' + orderId);
    if (typeof bootstrap !== 'undefined') {
        const modal = new bootstrap.Modal(document.getElementById('orderDetailModal'));
        modal.show();
    } else {
        $('#orderDetailModal').modal('show');
    }
}

export function updateOrderStatus(orderId, newStatus) {
    const statusMap = {
        'preparing': { text: '備貨中', class: 'bg-info' },
        'shipping': { text: '運送中', class: 'bg-primary' },
        'completed': { text: '已完成', class: 'bg-success' }
    };

    if (confirm(`確定要將訂單 ${orderId} 更新為「${statusMap[newStatus].text}」嗎？`)) {
        showToast(`訂單 ${orderId} 已更新為${statusMap[newStatus].text}`, 'success');
        // In real app, call API to update status
    }
}

export function confirmShipping() {
    const carrier = $('#shippingCarrier').val();
    const tracking = $('#trackingNumber').val();

    if (!carrier) {
        showToast('請選擇物流業者', 'warning');
        return;
    }
    if (!tracking) {
        showToast('請輸入追蹤編號', 'warning');
        return;
    }

    // Hide modal - supports both bootstrap 5 vanilla and jquery if mix
    const modalEl = document.getElementById('orderDetailModal');
    const modal = bootstrap.Modal.getInstance(modalEl);
    if (modal) modal.hide();
    else $(modalEl).modal('hide');

    showToast('出貨成功！已發送通知給買家', 'success');
}

export function handleRefund() {
    if (confirm('確定要處理此訂單的退款嗎？')) {
        const modalEl = document.getElementById('orderDetailModal');
        const modal = bootstrap.Modal.getInstance(modalEl);
        if (modal) modal.hide();
        else $(modalEl).modal('hide');

        showToast('已進入退款流程', 'info');
    }
}


