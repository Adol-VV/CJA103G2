/**
 * 系統通知模組
 */
import { showToast } from './utils.js';

export function initNotifications() {
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

function markAsRead(btn) {
    const item = $(btn).closest('.notification-item');
    const notifyId = item.data('id');
    $.post('/organizer/dashboard/notifications/mark-read', { notifyId: notifyId }, function (res){
        if (res.success){
            item.removeClass('unread');
            item.find('.badge.bg-primary').remove();
            item.find('.text-white').removeClass('text-white').addClass('text-muted');
            item.find('.bg-opacity-10').removeClass('bg-opacity-10').addClass('bg-opacity-25');
            $(btn).remove();

            updateNotificationBadge();
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
            $.post('/organizer/dashboard/notifications/mark-all-read', function (res) {
                if (res.success) {
                    $('.notification-item.unread').each(function () {
                        $(this).removeClass('unread');
                        $(this).find('.badge.bg-primary').remove();
                        $(this).find('.text-white').removeClass('text-white').addClass('text-muted');
                        $(this).find('.bg-opacity-10').removeClass('bg-opacity-10').addClass('bg-opacity-25');
                        $(this).find('.btn-outline-secondary').remove();
                    });
                    updateNotificationBadge();
                    showToast('所有通知已標為已讀', 'success');
                }
            });
        }
    });
}
            // }).fail(function (err) {
            //     console.error("全部已讀失敗:", err);
            //     alert("操作失敗，錯誤代碼: " + err.status);

        function updateNotificationBadge() {
            const unreadCount = $('.notification-item.unread').length;
            if (unreadCount > 0) {
                $('#notificationBadge, .sidebar-notify-badge').text(unreadCount).show();
            } else {
                $('#notificationBadge, .sidebar-notify-badge').hide();
            }
        }

