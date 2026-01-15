/**
 * 系統通知模組
 */
import { showToast } from './utils.js';

export function initNotifications() {
    $(document).on('click', '.btn-mark-read', function () {
        markAsRead(this);
    });

    $('#btnMarkAllRead').click(function () {
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
    item.removeClass('unread');
    item.find('.badge.bg-primary').remove();
    item.find('.text-white').removeClass('text-white').addClass('text-muted');
    item.find('.bg-opacity-10').removeClass('bg-opacity-10').addClass('bg-opacity-25');
    $(btn).remove();
    updateNotificationBadge();
    showToast('已標為已讀', 'success');
}

function markAllAsRead() {
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

function updateNotificationBadge() {
    const unreadCount = $('.notification-item.unread').length;
    if (unreadCount > 0) {
        $('#notificationBadge').text(unreadCount).show();
    } else {
        $('#notificationBadge').hide();
    }
}
