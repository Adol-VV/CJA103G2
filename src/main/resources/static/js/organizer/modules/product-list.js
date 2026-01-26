export function initProductList() {
    $(document).on('click', '.btn-takedown-product', function () {
        const $row = $(this).closest('tr');
        const productName = $row.find('.fw-bold').first().text();

        if (confirm(`確定要下架「${productName}」嗎？`)) {
            $row.find('td:eq(4) .badge').removeClass('bg-success').addClass('bg-dark').text('已下架');
            $(this).removeClass('btn-outline-warning').addClass('btn-outline-success')
                .attr('title', '重新上架').html('<i class="fas fa-redo"></i>')
                .removeClass('btn-takedown-product').addClass('btn-relist-product');
            if (window.showToast) window.showToast('商品已下架', 'success');
        }
    });

//    $(document).on('click', '.btn-relist-product', function () {
//        const $row = $(this).closest('tr');
//        const productName = $row.find('.fw-bold').first().text();
//
//        if (confirm(`確定要重新上架「${productName}」嗎？`)) {
//            $row.find('td:eq(4) .badge').removeClass('bg-dark').addClass('bg-success').text('販售中');
//            $(this).removeClass('btn-outline-success').addClass('btn-outline-warning')
//                .attr('title', '下架商品').html('<i class="fas fa-eye-slash"></i>')
//                .removeClass('btn-relist-product').addClass('btn-takedown-product');
//            if (window.showToast) window.showToast('商品已重新上架', 'success');
//        }
//    });

    // Draft Operations
    $(document).on('click', '.btn-publish-product', function () {
        const $row = $(this).closest('tr');
        const productName = $row.find('.fw-bold').first().text();
        if (confirm(`確定要上架「${productName}」嗎？`)) {
            $row.find('td:eq(4) .badge').removeClass('bg-secondary').addClass('bg-success').text('上架中');
            $row.find('td:last .btn-group').html(`
                <button class="btn btn-sm btn-outline-light btn-edit-product" title="編輯商品">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-outline-warning btn-takedown-product" title="下架商品">
                    <i class="fas fa-eye-slash"></i>
                </button>
            `);
            if (window.showToast) window.showToast('商品已上架！', 'success');
        }
    });

    $(document).on('click', '.btn-delete-product', function () {
        const $row = $(this).closest('tr');
        const productName = $row.find('.fw-bold').first().text();
        if (confirm(`確定要刪除草稿「${productName}」嗎？\n\n此操作無法復原。`)) {
            $row.fadeOut(300, function () { $(this).remove(); });
            if (window.showToast) window.showToast('草稿已刪除', 'success');
        }
    });

    $(document).on('click', '.btn-edit-product', function () {
        const $row = $(this).closest('tr');
        const productName = $row.find('.fw-bold').first().text();
        const price = $row.find('td:eq(2)').text().replace(/[^\d]/g, '');
        if (window.showToast) window.showToast(`正在載入「${productName}」編輯頁面...`, 'info');
        setTimeout(() => {
            if (window.showSection) window.showSection('product-create');
            $('input[name="productName"]').val(productName);
            $('input[name="productPrice"]').val(price);
        }, 500);
    });
	
	
	//依商品類別分類
	$(document).on("change", "#sort-select", function(){
		let sortName = $(this).val();
		console.log(sortName);
		if(sortName === "all"){
			$(".prodContent").show();
			$(".prodContent").addClass("show");
		}else{
			$(".prodContent").each(function(i , prod){
				if($(prod).data("sortname") === sortName){
					$(prod).show();
					$(prod).addClass("show");
				}else{
					$(prod).hide();
					$(prod).removeClass("show");
				}
			})	
		}
		$("#reviewstatus-select").val("all");
		$("#status-select").val("all");
	})
	
	//依商品審核狀態分類
	$(document).on("change", "#reviewstatus-select", function(){
		let reviewStatus = $(this).val();
		if(reviewStatus === "all"){
			$(".prodContent.show").show();
			
		}else{
			$(".prodContent.show").each(function(i , prod){
				if($(prod).data("reviewstatus") === reviewStatus){
					$(prod).show();
				}else{
					$(prod).hide();
				}
			})	
		}
	})
	
	//依商品上下架狀態分類
	$(document).on("change", "#status-select", function(){
		let status = $(this).val();
		console.log(status);
		if(status === "all"){
			$(".prodContent.show").show();
			
		}else{
			$(".prodContent.show").each(function(i , prod){
				if($(prod).data("status") === status){
					$(prod).show();
				}else{
					$(prod).hide();
				}
			})	
		}
	})
}
