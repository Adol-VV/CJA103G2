export function initEventOrders() {
    $(document).on('click', '#eventOrderTabs a', function (e) {
        e.preventDefault();
        $('#eventOrderTabs a').removeClass('active text-white').addClass('text-muted');
        $(this).addClass('active text-white').removeClass('text-muted');
    });
}
