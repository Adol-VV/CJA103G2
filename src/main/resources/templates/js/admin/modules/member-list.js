export function initMemberList() {
    $(document).on('click', '.btn-view-member', function () {
        const id = $(this).data('id');
        $('#memberDetailModal').modal('show');
    });
}
