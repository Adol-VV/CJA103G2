export function initProductApprovals() {
    $(document).on('click', '#productReviewTabs a', function (e) {
        e.preventDefault();
        $('#productReviewTabs a').removeClass('active text-white').addClass('text-muted');
        $(this).addClass('active text-white').removeClass('text-muted');
    });

    $(document).on('click', '.btn-review-product', function () {
        $('#productReviewModal').modal('show');
    });


}
