/**
 * 系統通知模組
 */
import { showToast } from './utils.js';

export function initNotifications() {
    fetchBellNotifications();

    $('#bellDropdown').on('show.bs.dropdown', function () {
        fetchBellNotifications();
    });

    $(document).on('click', '.btn-mark-read', function (e) {
        e.preventDefault();
        e.stopPropagation();
        markAsRead(this);
    });

    $(document).on('click', '#btnMarkAllRead', function () {
        markAllAsRead();
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
    updateNotificationBadge();
}

function fetchBellNotifications() {
    $.post('/organizer/notify/list', function (res) {
        if (res.success) {
            const listContainer = $('#bellNotificationList');
            listContainer.empty();

            if (!res.notifications || res.notifications.length === 0) {
                listContainer.append('<li class="text-center py-4 text-muted small">目前沒有新通知</li>');
            } else {
                res.notifications.forEach(notify => {
                    const isUnread = notify.isRead === 0;
                    const unreadStyle = isUnread ? 'style="background: rgba(13, 110, 253, 0.05);"' : '';
                    const dot = isUnread ? '<span class="badge bg-primary rounded-circle p-1 ms-1"> </span>' : '';

                    listContainer.append(`
                        <li class="p-2 border-bottom border-secondary" ${unreadStyle}>
                            <div class="small text-white d-flex justify-content-between">
                                <span>${notify.title} ${dot}</span>
                            </div>
                            <div class="text-muted text-truncate" style="font-size: 0.75rem;">${notify.content}</div>
                            <div class="text-end text-muted" style="font-size: 0.65rem;">${notify.createdAt}</div>
                        </li>
                    `);
                });
            }
            // 更新紅點計數
            updateNotificationBadge(res.unreadCount);
        }
    });
}

function markAsRead(btn) {
    const item = $(btn).closest('.notification-item');
    const notifyId = item.data('id');
    // 【修正】路徑由 /organizer/dashboard/notifications/mark-read 改為 /organizer/notify/markAsRead
    $.post('/organizer/notify/markAsRead', { notifyId: notifyId }, function (res){
    // $.post('/organizer/dashboard/notifications/mark-read', { notifyId: notifyId }, function (res){
        if (res.success){
            item.removeClass('unread');
            item.find('.badge.bg-primary').remove();
            item.find('.text-white').removeClass('text-white').addClass('text-muted');
            item.find('.bg-opacity-10').removeClass('bg-opacity-10').addClass('bg-opacity-25');
            $(btn).remove();

            // 重新整理鈴鐺與計數
            fetchBellNotifications();
            // updateNotificationBadge();
            showToast('已標為已讀', 'success');
        }
    });
}

function markAllAsRead() {
    // if (!confirm('確定要將所有通知標為已讀嗎?')) return;
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
            // 【修正】路徑改為 /organizer/notify/markAllAsRead
            $.post('/organizer/notify/markAllAsRead', function (res) {
            // $.post('/organizer/dashboard/notifications/mark-all-read', function (res) {
                if (res.success) {
                    $('.notification-item.unread').each(function () {
                        $(this).removeClass('unread');
                        $(this).find('.badge.bg-primary').remove();
                        $(this).find('.text-white').removeClass('text-white').addClass('text-muted');
                        $(this).find('.bg-opacity-10').removeClass('bg-opacity-10').addClass('bg-opacity-25');
                        $(this).find('.btn-outline-secondary').remove();
                    });
                    // 重新整理鈴鐺與計數
                    fetchBellNotifications();
                    // updateNotificationBadge();
                    showToast('所有通知已標為已讀', 'success');
                }
            });
        }
    });
}
            // }).fail(function (err) {
            //     console.error("全部已讀失敗:", err);
            //     alert("操作失敗，錯誤代碼: " + err.status);

// function updateNotificationBadge() {
//     const unreadCount = $('.notification-item.unread').length;
//     if (unreadCount > 0) {
//         $('#notificationBadge, .sidebar-notify-badge').text(unreadCount).show();
//     } else {
//         $('#notificationBadge, .sidebar-notify-badge').hide();
//     }
// }
function updateNotificationBadge(count) {
    // 如果有傳入 count 則直接使用，否則從頁面元素計算
    const unreadCount = (count !== undefined) ? count : $('.notification-item.unread').length;

    // 對應 dashboard.html 中的 #bellBadge 與 sidebar 中的 .sidebar-notify-badge
    const badgeElements = $('#bellBadge, #notificationBadge, .sidebar-notify-badge');

    if (unreadCount > 0) {
        badgeElements.text(unreadCount).show();
    } else {
        badgeElements.hide();
    }
}
