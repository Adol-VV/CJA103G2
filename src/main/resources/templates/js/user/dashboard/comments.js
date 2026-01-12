/**
 * Comments Management UI
 * Handles Filtering, Editing, and Deleting UI interactions.
 */

const Comments = {
    init() {
        this.bindEvents();
    },

    bindEvents() {
        this.bindFiltering();
        this.bindEditing();
        this.bindDeleting();
    },

    bindFiltering() {
        $('#section-comments .btn-group button[data-filter]').click(function () {
            const filter = $(this).data('filter');

            // Toggle active class
            $(this).siblings().removeClass('active');
            $(this).addClass('active');

            // UI Filtering
            let visibleCount = 0;
            $('#section-comments .list-group-item').each(function () {
                const $item = $(this);
                const isArticle = $item.find('.badge').hasClass('bg-info'); // Heuristic based on badge color
                const isEvent = $item.find('.badge').hasClass('bg-primary');

                let show = false;
                if (filter === 'all') show = true;
                else if (filter === 'article' && isArticle) show = true;
                else if (filter === 'event' && isEvent) show = true;

                if (show) {
                    $item.show();
                    visibleCount++;
                } else {
                    $item.hide();
                }
            });

            // Empty State
            if (visibleCount === 0) {
                $('#section-comments .card').addClass('d-none');
                $('#emptyComments').removeClass('d-none');
            } else {
                $('#section-comments .card').removeClass('d-none');
                $('#emptyComments').addClass('d-none');
            }
        });
    },

    bindEditing() {
        // Open Modal
        $(document).on('click', '#section-comments .btn-outline-secondary', function () {
            const $item = $(this).closest('.list-group-item');

            // Populate Modal
            $('#editCommentType').text($item.find('.badge').text());
            $('#editCommentTarget').text($item.find('a.fw-bold').text());
            const content = $item.find('p.text-muted').first().text().trim();
            $('#editCommentContent').val(content);
            $('#editCommentCharCount').text(content.length);

            // Store reference to item being edited
            $('#editCommentModal').data('target-item', $item);
            $('#editCommentModal').modal('show');
        });

        // Char Count
        $('#editCommentContent').on('input', function () {
            $('#editCommentCharCount').text($(this).val().length);
        });

        // Save
        $('#btnSaveComment').click(function () {
            const newContent = $('#editCommentContent').val();
            const $item = $('#editCommentModal').data('target-item');

            if ($item && newContent) {
                $item.find('p.text-muted').first().text(newContent);
                $('#editCommentModal').modal('hide');
                if (window.showToast) window.showToast('留言已更新', 'success');
            }
        });
    },

    bindDeleting() {
        $(document).on('click', '#section-comments .btn-outline-danger', function () {
            const $item = $(this).closest('.list-group-item');
            $('#deleteCommentModal').data('target-item', $item);
            $('#deleteCommentModal').modal('show');
        });

        $('#btnConfirmDeleteComment').click(function () {
            const $item = $('#deleteCommentModal').data('target-item');
            if ($item) {
                $item.remove();
                $('#deleteCommentModal').modal('hide');
                if (window.showToast) window.showToast('留言已刪除', 'success');

                // Check empty state
                if ($('#section-comments .list-group-item:visible').length === 0) {
                    $('#section-comments .card').addClass('d-none');
                    $('#emptyComments').removeClass('d-none');
                }
            }
        });
    }
};

export default Comments;
