export function initProductCreate() {
    $('#btnAddVariant').click(function () {
        const newRow = `
            <tr>
                <td><input type="text" name="variantName[]" class="form-control form-control-sm bg-dark text-white border-secondary" placeholder="例如：S, M, 紅色..." value=""></td>
                <td><input type="number" name="variantStock[]" min="0" class="form-control form-control-sm bg-dark text-white border-secondary" value="0"></td>
                <td>
                    <button type="button" class="btn btn-sm btn-outline-danger btn-remove-variant">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `;
        $('#variantTable tbody').append(newRow);
    });

    $(document).on('click', '.btn-remove-variant', function () {
        const rowCount = $('#variantTable tbody tr').length;
        if (rowCount > 1) {
            $(this).closest('tr').remove();
        } else {
            if (window.showToast) window.showToast('至少需要保留一個規格', 'warning');
        }
    });

    $(document).on('submit', '#formProductCreate', function (e) {
        e.preventDefault();
        const productName = $('input[name="productName"]').val().trim();
        const productCategory = $('select[name="productCategory"]').val();
        const productPrice = $('input[name="productPrice"]').val();
        const productDescription = $('textarea[name="productDescription"]').val().trim();
        const hasMainImage = $('#productMainImagePreview').attr('src');

        if (!productName || productName.length < 2) {
            if (window.showToast) window.showToast('商品名稱至少需要 2 個字', 'error');
            return;
        }
        if (!productCategory) {
            if (window.showToast) window.showToast('請選擇商品分類', 'error');
            return;
        }
        if (!productPrice || productPrice < 1) {
            if (window.showToast) window.showToast('請輸入有效的售價', 'error');
            return;
        }
        if (!productDescription || productDescription.length < 10) {
            if (window.showToast) window.showToast('商品描述至少需要 10 個字', 'error');
            return;
        }
        if (!hasMainImage) {
            if (window.showToast) window.showToast('請上傳商品主圖', 'error');
            return;
        }

        let hasValidVariant = false;
        $('#variantTable tbody tr').each(function () {
            const name = $(this).find('input[name="variantName[]"]').val().trim();
            const stock = $(this).find('input[name="variantStock[]"]').val();
            if (name && stock >= 0) hasValidVariant = true;
        });

        if (!hasValidVariant) {
            if (window.showToast) window.showToast('請設定至少一個有效規格', 'error');
            return;
        }

        if (window.showToast) window.showToast('商品已送出審核，請等待審核結果', 'success');
        setTimeout(() => {
            if (window.showSection) window.showSection('product-list');
        }, 2000);
    });

    $(document).on('click', '#btnSaveProductDraft', function () {
        if (window.showToast) window.showToast('草稿已儲存', 'success');
    });
}
