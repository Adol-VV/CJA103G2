export function initFavorites() {
    $(document).on('click', '.btn-remove-favorite', function () {
        if (confirm('確定要取消收藏此活動嗎？')) {
            $(this).closest('.col-md-6').fadeOut(300, function () {
                $(this).remove();
                if ($('#favoritesList .col-md-6').length === 0) {
                    $('#favoritesList').html('<div class="col-12 text-center py-5 text-muted">尚無收藏活動</div>');
                }
            });
        }
    });
}
