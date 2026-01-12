export function initEventList() {
    // --- Event Takedown & Relist ---
    $(document).on('click', '.btn-takedown-event', function () {
        const $row = $(this).closest('tr');
        const eventName = $row.find('.fw-bold').first().text();

        if (confirm(`確定要下架「${eventName}」嗎？\n\n下架後：\n• 活動將不再顯示於前台\n• 已購票用戶仍可正常入場\n• 可隨時重新上架`)) {
            $row.find('.badge').removeClass('bg-success bg-warning').addClass('bg-dark').text('已下架');
            $(this).removeClass('btn-outline-warning').addClass('btn-outline-success')
                .attr('title', '重新上架').html('<i class="fas fa-eye"></i>')
                .removeClass('btn-takedown-event').addClass('btn-relist-event');
            if (window.showToast) window.showToast('活動已下架', 'success');
        }
    });

    $(document).on('click', '.btn-relist-event', function () {
        const $row = $(this).closest('tr');
        const eventName = $row.find('.fw-bold').first().text();

        if (confirm(`確定要重新上架「${eventName}」嗎？`)) {
            $row.find('.badge').removeClass('bg-dark').addClass('bg-success').text('銷售中');
            $(this).removeClass('btn-outline-success').addClass('btn-outline-warning')
                .attr('title', '下架活動').html('<i class="fas fa-eye-slash"></i>')
                .removeClass('btn-relist-event').addClass('btn-takedown-event');
            if (window.showToast) window.showToast('活動已重新上架', 'success');
        }
    });

    // --- Draft Operations (transferred from dashboard.js) ---
    $(document).on('click', '.btn-publish-event', function () {
        const $row = $(this).closest('tr');
        const eventName = $row.find('.fw-bold').first().text();

        if (confirm(`確定要上架「${eventName}」嗎？\n\n上架後活動將開始銷售。`)) {
            $row.find('.badge').removeClass('bg-secondary').addClass('bg-success').text('銷售中');
            $row.find('td:last .btn-group').html(`
                <button class="btn btn-sm btn-outline-light btn-edit-event" title="編輯活動">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-outline-info" title="查看數據" onclick="showSection('reports')">
                    <i class="fas fa-chart-bar"></i>
                </button>
                <button class="btn btn-sm btn-outline-primary btn-notify-event" title="通知購票者" onclick="showSection('notify-members')">
                    <i class="fas fa-bell"></i>
                </button>
                <button class="btn btn-sm btn-outline-warning btn-takedown-event" title="下架活動">
                    <i class="fas fa-eye-slash"></i>
                </button>
            `);
            if (window.showToast) window.showToast('活動已上架，開始銷售！', 'success');
        }
    });

    $(document).on('click', '.btn-delete-event', function () {
        const $row = $(this).closest('tr');
        const eventName = $row.find('.fw-bold').first().text();

        if (confirm(`確定要刪除草稿「${eventName}」嗎？\n\n此操作無法復原。`)) {
            $row.fadeOut(300, function () { $(this).remove(); });
            if (window.showToast) window.showToast('草稿已刪除', 'success');
        }
    });

    $(document).on('click', '.btn-edit-event', function () {
        const $row = $(this).closest('tr');
        const eventName = $row.find('.fw-bold').first().text();
        if (window.showToast) window.showToast(`正在載入「${eventName}」編輯頁面...`, 'info');
        setTimeout(() => {
            if (window.showSection) window.showSection('event-create');
            $('input[name="eventName"]').val(eventName);
        }, 500);
    });
}
