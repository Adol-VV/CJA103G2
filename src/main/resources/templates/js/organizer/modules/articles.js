/**
 * 文章管理模組
 */
export function initArticleManagement() {
    $(document).on('click', '#btnCreateArticle', function () {
        $('.content-panel').removeClass('active');
        $('#panel-article-editor').addClass('active');
    });

    $(document).on('click', '.btn-back-article-list', function () {
        $('.content-panel').removeClass('active');
        $('#panel-article-list').addClass('active');
    });
}
