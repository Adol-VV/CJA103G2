export function initNotifications() {
    $(document).on('click', '.btn-mark-read', function () {
        const $item = $(this).closest('.list-group-item');
        $item.removeClass('bg-dark').addClass('bg-transparent opacity-75');
        $(this).remove();

        // Update badge if any
        const currentCount = parseInt($('#notificationBadge').text() || '0');
        if (currentCount > 0) {
            const newCount = currentCount - 1;
            $('#notificationBadge').text(newCount || '');
            if (newCount === 0) $('#notificationBadge').hide();
        }
    });

    $(document).on('click', '#btnReadAll', function () {
        $('.list-group-item.bg-dark').removeClass('bg-dark').addClass('bg-transparent opacity-75');
        $('.btn-mark-read').remove();
        $('#notificationBadge').text('').hide();
    });
}
