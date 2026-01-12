export function initProductOrders() {
    $(document).on('click', '.btn-mark-shipped', function () {
        const id = $(this).data('id');
        if (confirm('確定要標記此訂單為「已出貨」嗎？')) {
            const $row = $(this).closest('tr');
            $row.find('.badge').removeClass('bg-warning text-dark').addClass('bg-info text-white').text('已出貨');
            $(this).remove();
            if (window.showToast) window.showToast('訂單狀態已更新為已出貨', 'success');
        }
    });

    $(document).on('click', '.btn-view-order-detail', function () {
        $('#orderDetailModal').modal('show');
    });
}
