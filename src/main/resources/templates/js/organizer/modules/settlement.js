export function initSettlement() {
    $(document).on('click', '.btn-request-settlement', function () {
        if (confirm('確定要申請本期結算嗎？')) {
            if (window.showToast) window.showToast('申請已送出，我們將在 3 個工作天內核發', 'success');
            $(this).prop('disabled', true).text('申請處理中');
        }
    });
}
