/**
 * Order Management UI
 * Handles Cancel/Refund UI events.
 */

const Orders = {
    init() {
        this.bindEvents();
    },

    bindEvents() {
        // Cancel Order UI
        $('#btnConfirmCancelOrder').click(function () {
            // UI Update only
            $('#cancelOrderModal').modal('hide');
            if (window.showToast) window.showToast('訂單已取消', 'success');
        });

        // Refund UI
        $('#btnSubmitRefund').click(function () {
            // UI Update only
            $('#refundRequestModal').modal('hide');
            if (window.showToast) window.showToast('退貨申請已提交', 'success');
        });
    }
};

export default Orders;
