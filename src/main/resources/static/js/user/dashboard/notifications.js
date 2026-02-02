/**
 * 會員通知模組
 */
export function initNotifications() {
    // 頁面載入時取得通知
    fetchBellNotifications();

    // 點擊小鈴鐺時重新載入
    $('#bellDropdown').on('show.bs.dropdown', function () {
        fetchBellNotifications();
    });

    // 單則已讀按鈕
    $(document).on('click', '.btn-mark-read', function (e) {
        e.preventDefault();
        e.stopPropagation();
        markAsRead(this);
    });

    // 全部已讀按鈕
    $(document).on('click', '#btnReadAll', function () {
        markAllAsRead();
    });

    // 全部刪除按鈕
    $(document).on('click', '#btnDeleteAll', function () {
        deleteAllNotifications();
    });

    // 通知篩選
    $('[data-filter]').click(function () {
        const filter = $(this).data('filter');
        $('[data-filter]').removeClass('active');
        $(this).addClass('active');

        if (filter === 'all') {
            $('.notification-item').show();
        } else {
            $('.notification-item').hide();
            $(`.notification-item[data-type="${filter}"]`).show();
        }
    });
}

// 取得小鈴鐺通知列表
function fetchBellNotifications() {
    $.post('/member/notifications/list', function (res) {
        if (res.success) {
            const listContainer = $('#bellNotificationList');
            listContainer.empty();

            if (!res.notifications || res.notifications.length === 0) {
                listContainer.append('<li class="text-center py-4 text-muted small">目前沒有新通知</li>');
            } else {
                // 只顯示最新 5 則
                const recentNotifications = res.notifications.slice(0, 5);
                recentNotifications.forEach(notify => {
                    const isUnread = notify.isRead === 0;
                    const unreadStyle = isUnread ? 'style="background: rgba(13, 110, 253, 0.05);"' : '';
                    const dot = isUnread ? '<span class="badge bg-primary rounded-circle p-1 ms-1"> </span>' : '';
                    const typeIcon = notify.type === 'SYSTEM' ? '📢' : '🏪';

                    listContainer.append(`
                        <li class="p-2 border-bottom border-secondary notification-bell-item"
                            data-id="${notify.id}" data-type="${notify.type}" ${unreadStyle}>
                            <div class="small text-white d-flex justify-content-between">
                                <span>${typeIcon} ${notify.title} ${dot}</span>
                            </div>
                            <div class="text-muted text-truncate" style="font-size: 0.75rem;">${notify.content}</div>
                            <div class="text-end text-muted" style="font-size: 0.65rem;">${formatDateTime(notify.createdAt)}</div>
                        </li>
                    `);
                });
            }
            // 更新紅點計數
            updateNotificationBadge(res.unreadCount);
        }
    }).fail(function() {
        console.error('載入通知失敗');
    });
}

// 標記單則為已讀
function markAsRead(btn) {
    // 從按鈕取得data-id和data-type
    const $btn = $(btn);
    const notifyId = $btn.data('id');
    const notifyType = $btn.data('type') || 'ORGANIZER';

    // 找到通知項目
    const $item = $btn.closest('.notification-item');

    $.post('/member/notifications/read', {
        notifyId: notifyId,
        type: notifyType
    }, function (res) {
        if (res.success) {
            // 更新UI
            $item.removeClass('unread');
            $item.find('.badge.bg-danger').remove(); // 移除「未讀」標籤
            $item.find('.position-absolute.bg-warning, .position-absolute.bg-primary').fadeOut(); // 移除左側色條
            $btn.fadeOut(function() { $(this).remove(); }); // 移除「標記已讀」按鈕

            // 重新載入鈴鐺通知(同步更新側邊欄數量)
            fetchBellNotifications();
            showToast('已標為已讀', 'success');
        }
    }).fail(function() {
        showToast('標記失敗', 'danger');
    });
}

// 標記所有為已讀
function markAllAsRead() {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: '確定要將所有通知標為已讀嗎?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '確定',
            cancelButtonText: '取消',
            background: '#1a1d20',
            color: '#fff'
        }).then((result) => {
            if (result.isConfirmed) {
                doMarkAllAsRead();
            }
        });
    } else if (confirm('確定要將所有通知標為已讀嗎?')) {
        doMarkAllAsRead();
    }
}

function doMarkAllAsRead() {
    $.post('/member/notifications/read-all', function (res) {
        if (res.success) {
            // 更新所有通知項目的UI
            $('.notification-item.unread').each(function () {
                $(this).removeClass('unread');
                $(this).find('.badge.bg-danger').remove(); // 移除未讀 badge
                $(this).find('.badge.bg-primary').remove();
                $(this).find('.position-absolute.bg-warning, .position-absolute.bg-primary').fadeOut(); // 移除左側色條
                $(this).find('.btn-mark-read').remove();
            });

            // 重新載入鈴鐺通知
            fetchBellNotifications();
            showToast('所有通知已標為已讀', 'success');
        }
    });
}

// 刪除所有通知
function deleteAllNotifications() {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            title: '確定要刪除所有通知嗎?',
            text: '此操作無法復原',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: '確定刪除',
            cancelButtonText: '取消',
            confirmButtonColor: '#dc3545',
            background: '#1a1d20',
            color: '#fff'
        }).then((result) => {
            if (result.isConfirmed) {
                doDeleteAllNotifications();
            }
        });
    } else if (confirm('確定要刪除所有通知嗎？此操作無法復原')) {
        doDeleteAllNotifications();
    }
}

function doDeleteAllNotifications() {
    $.post('/member/notifications/delete-all', function (res) {
        if (res.success) {
            // 清空所有通知項目
            $('.notification-item').fadeOut(function() {
                $(this).remove();
            });

            // 顯示無通知訊息
            setTimeout(function() {
                $('.list-group-flush').each(function() {
                    if ($(this).find('.notification-item').length === 0) {
                        $(this).html(`
                            <div class="text-center text-muted py-4">
                                <i class="fas fa-inbox fa-2x mb-2"></i>
                                <p class="mb-0">目前沒有通知</p>
                            </div>
                        `);
                    }
                });
            }, 300);

            // 重新載入鈴鐺通知
            fetchBellNotifications();
            showToast('已刪除所有通知', 'success');
        } else {
            showToast(res.message || '刪除失敗', 'danger');
        }
    }).fail(function() {
        showToast('刪除通知失敗', 'danger');
    });
}

// 更新通知紅點數字 (同步更新小鈴鐺和側邊欄)
function updateNotificationBadge(count) {
    const unreadCount = (count !== undefined) ? count : 0;
    // 同時更新小鈴鐺和側邊欄的通知數量
    const badgeElements = $('#bellBadge, #notificationBadge, .notification-badge, .sidebar-notify-badge');

    if (unreadCount > 0) {
        badgeElements.text(unreadCount).show();
    } else {
        badgeElements.hide();
    }
}

// 格式化時間
function formatDateTime(dateStr) {
    if (!dateStr) return '';
    try {
        const date = new Date(dateStr);
        const now = new Date();
        const diff = now - date;
        const minutes = Math.floor(diff / 60000);
        const hours = Math.floor(diff / 3600000);
        const days = Math.floor(diff / 86400000);

        if (minutes < 1) return '剛剛';
        if (minutes < 60) return `${minutes} 分鐘前`;
        if (hours < 24) return `${hours} 小時前`;
        if (days < 7) return `${days} 天前`;

        return dateStr.replace('T', ' ').substring(0, 16);
    } catch (e) {
        return dateStr;
    }
}

// 顯示Toast 訊息
function showToast(message, type = 'info') {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            toast: true,
            position: 'top-end',
            icon: type === 'success' ? 'success' : (type === 'danger' ? 'error' : 'info'),
            title: message,
            showConfirmButton: false,
            timer: 2000
        });
    } else {
        console.log(`[${type}] ${message}`);
    }
}