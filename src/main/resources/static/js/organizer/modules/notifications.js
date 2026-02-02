/**
 * 系統通知模組
 */
import { showToast } from './utils.js';

export function initNotifications() {
    fetchBellNotifications();

    $('#bellDropdown').on('show.bs.dropdown', function () {
        fetchBellNotifications();
    });

    // 「查看所有通知」按鈕 - 導航至通知中心面板
    $(document).on('click', '#btnViewAllNotifications', function (e) {
        e.preventDefault();
        // 切換到通知中心面板
        $('.content-panel').removeClass('active');
        $('#panel-notifications').addClass('active');
        // 更新側邊欄 active 狀態
        $('.nav-link, .mobile-nav').removeClass('active');
        $('.nav-link[data-section="notifications"], .mobile-nav[data-section="notifications"]').addClass('active');
        // 關閉下拉選單
        const dropdown = bootstrap.Dropdown.getInstance($('#bellDropdown')[0]);
        if (dropdown) dropdown.hide();
    });

    $(document).on('click', '.btn-mark-read', function (e) {
        e.preventDefault();
        e.stopPropagation();
        markAsRead(this);
    });

    $(document).on('click', '#btnMarkAllRead', function (e) {
        e.stopPropagation(); // 防止下拉選單關閉
        markAllAsRead();
    });

    // 全部刪除
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

function fetchBellNotifications() {
    $.post('/organizer/notify/list', function (res) {
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
                    // 根據類型顯示不同圖示：PLATFORM=📢平台, MEMBER=👤會員
                    const typeIcon = notify.type === 'PLATFORM' ? '📢' : '👤';

                    listContainer.append(`
                        <li class="p-2 border-bottom border-secondary notification-bell-item"
                            data-id="${notify.id}" data-type="${notify.notifyType}" ${unreadStyle}>
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

// 標記通知中心面板的單則通知為已讀
function markAsRead(btn) {
    // 從按鈕本身取得資料
    const $btn = $(btn);
    const notifyId = $btn.data('id');
    const notifyType = $btn.data('notify-type'); // ORG 或 SYS

    // 找到通知項目
    const $item = $btn.closest('.notification-item');

    // API
    const url = notifyType === 'SYS' ? '/organizer/notify/markSysAsRead' : '/organizer/notify/markAsRead';

    $.post(url, { notifyId: notifyId }, function (res) {
        if (res.success) {
            // 更新UI
            $item.removeClass('unread');
            $item.find('.badge.bg-danger').remove(); // 移除"未讀"標籤
            $item.find('.position-absolute.bg-warning, .position-absolute.bg-primary').fadeOut(); // 移除左側色條
            $btn.fadeOut(function() { $(this).remove(); }); // 移除"標記已讀"按鈕

            // 重新整理鈴鐺與計數 (同步更新側邊欄數量)
            fetchBellNotifications();
            showToast('已標為已讀', 'success');
        }
    }).fail(function() {
        showToast('標記失敗', 'error');
    });
}

function markAllAsRead() {
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
            $.post('/organizer/notify/markAllAsRead', function (res) {
                if (res.success) {
                    $('.notification-item.unread').each(function () {
                        $(this).removeClass('unread');
                        $(this).find('.badge.bg-danger').remove(); // 移除「未讀」標籤
                        $(this).find('.position-absolute.bg-warning, .position-absolute.bg-primary').fadeOut(); // 移除左側色條
                        $(this).find('.btn-mark-read').remove(); // 移除「標記已讀」按鈕
                    });
                    // 重新整理鈴鐺與計數 (同步更新側邊欄數量)
                    fetchBellNotifications();
                    showToast('所有通知已標為已讀', 'success');
                }
            }).fail(function() {
                showToast('操作失敗', 'error');
            });
        }
    });
}

// 刪除所有通知
function deleteAllNotifications() {
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
            $.post('/organizer/notify/deleteAll', function (res) {
                if (res.success) {
                    // 清空所有通知項目
                    $('.notification-item').fadeOut(function() {
                        $(this).remove();
                    });

                    // 顯示無通知訊息
                    setTimeout(function() {
                        const listContainer = $('#notificationList');
                        if (listContainer.find('.notification-item').length === 0) {
                            listContainer.html(`
                                <div class="text-center text-muted py-5">
                                    <i class="fas fa-inbox fa-3x mb-3"></i>
                                    <p class="mb-0">目前沒有通知</p>
                                </div>
                            `);
                        }
                    }, 300);

                    // 重新整理鈴鐺與計數
                    fetchBellNotifications();
                    showToast('已刪除所有通知', 'success');
                } else {
                    showToast(res.message || '刪除失敗', 'error');
                }
            }).fail(function() {
                showToast('刪除通知失敗', 'error');
            });
        }
    });
}

function updateNotificationBadge(count) {
    // 如果有傳入count直接使用
    const unreadCount = (count !== undefined) ? count : $('.notification-item.unread').length;

    // 對應 dashboard.html的#bellBadge與sidebar的.sidebar-notify-badge
    const badgeElements = $('#bellBadge, #notificationBadge, .sidebar-notify-badge');

    if (unreadCount > 0) {
        badgeElements.text(unreadCount).show();
    } else {
        badgeElements.hide();
    }
}
