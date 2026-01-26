export function initProductList() {
    $(document).on('click', '.btn-takedown-product', function (e) {
        let $row = $(this).closest('tr');
        let productName = $row.find('.fw-bold').first().text();

        if (!confirm(`確定要下架「${productName}」嗎？`)) {
			e.preventDefault();
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
        let $row = $(this).closest('tr');
        let productName = $row.find('.fw-bold').first().text();
        if (!confirm(`確定要上架「${productName}」嗎？`)) {
			e.preventDefault();
        }
    });

//    $(document).on('click', '.btn-delete-product', function () {
//        const $row = $(this).closest('tr');
//        const productName = $row.find('.fw-bold').first().text();
//        if (confirm(`確定要刪除草稿「${productName}」嗎？\n\n此操作無法復原。`)) {
//            $row.fadeOut(300, function () { $(this).remove(); });
//            if (window.showToast) window.showToast('草稿已刪除', 'success');
//        }
//    });

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
	
	$(document).ready(function(){
		$("span.showProdStatus").each(function(i, prodStatus){
			if($(prodStatus).text()  === "上架中" ){
				$(prodStatus).addClass("bg-success");
			}else{
				$(prodStatus).css("background-color","darkred");
			}
		})
		
		$("span.showReviewStatus").each(function(i, reviewStatus){
			if($(reviewStatus).text()  === "通過" ){
				$(reviewStatus).addClass("bg-success");
			}else if($(reviewStatus).text()  === "未通過"){
				$(reviewStatus).css("background-color","darkred");
			}else{
				$(reviewStatus).css("background-color","darkorange");				
			}
			
			if($(reviewStatus).text()  != "通過" ){
				$(reviewStatus).closest("tr").find(".btn-publish-product").attr("disabled",true);
				$(reviewStatus).closest("tr").find(".btn-takedown-product").attr("disabled",true);
			}
		})
		
		
		
		

	})
}
