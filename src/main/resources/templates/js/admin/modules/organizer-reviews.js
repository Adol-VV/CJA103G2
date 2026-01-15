export function initOrganizerReviews() {
    $(document).on('click', '.btn-review-application', function () {
        const id = $(this).data('id');
        $('#applicationReviewModal').modal('show');
    });
}
