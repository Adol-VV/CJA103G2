export function initNotifications() {
    //  AJAX 同步已讀狀態
    $(document).on('click', '.btn-mark-read', function (e) {
        e.preventDefault();
        e.stopPropagation();

        const $btn = $(this);
        const $item = $btn.closest('[data-id]');
        // 抓取標籤上的 ID
        const notifyId = $item.data('id');

        //  發送請求更新資料庫
        $.post('/member/dashboard/notifications/mark-read', {
            notifyId: notifyId
        }, function (res) {
            if (res.success) {
                // 成功後執行原有的 UI 變更
                $item.find('.bg-primary').fadeOut(function() { $(this).remove(); });
                // 使用 .find() 確保只移除這則通知內的按鈕
                $btn.fadeOut(function() { $(this).remove(); });
                // 移除左側的藍色未讀提示條
                updateAllBadges();
                console.log("資料庫狀態更新成功，已讀率將會同步更新");
            }
        });
    });

    function updateAllBadges() {
        $('.badge.bg-danger').each(function() {
            let count = parseInt($(this).text() || '0');
            if (count > 1) {
                $(this).text(count - 1);
            } else {
                $(this).fadeOut(function() { $(this).hide(); }); // 數字變 0 就隱藏
            }
        });
    }
    $(document).on('click', '#btnReadAll', function () {
        $('.list-group-item.bg-dark').removeClass('bg-dark').addClass('bg-transparent opacity-75');
        $('.bg-primary').fadeOut(); // 移除所有藍條
        $('.btn-mark-read').remove();
        $('#notificationBadge').text('').hide();
    });

    $(document).on('change', '#templateSelect', function() {
        // 取得目前選中的 option
        const selectedOption = $(this).find(':selected');

        // 抓取 data-title 與 data-content 屬性
        const title = selectedOption.data('title');
        const content = selectedOption.data('content');

        // 如果資料存在，則填入標題與內容輸入框
        if (title && content) {
            $('#notificationTitle').val(title);
            $('#notificationContent').val(content);
            console.log("範本套入成功");
        }
    });
}
