export function initProductApprovals() {
	$(document).on('click', '#productReviewTabs a', function (e) {
		e.preventDefault();
		$('#productReviewTabs a').removeClass('active text-white').addClass('text-muted');
		$(this).addClass('active text-white').removeClass('text-muted');

		//依商品狀態分類
		let status = $(this).data("status");

		if (status === "all") {
			$("tr.prodContent.show").show();
		} else {
			$("tr.prodContent.show").each(function (i, prod) {
				if ($(prod).data("status") === status) {
					$(prod).show();
				} else {
					$(prod).hide();
				};
			})
		}
	});


	//商品詳細頁面
	$(document).on('click', '.btn-review-product', function () {
		$('#productReviewModal').modal('show');

		let prodId = this.getAttribute("data-id");
		$("#reviewBtn").val(prodId);
		fetch(`/admin/api/getOneProd?prodId=${prodId}`).then(res => res.json()
			.then(prod => {
				$("#reviewProductName").text(prod.prodName);
				$("#reviewProductOrg").text(prod.organizerName);
				$("#reviewProductSort").text(prod.sortName);
				$("#reviewProductPrice").text(`$ ${prod.prodPrice}`);
				$("#reviewProductStock").text(prod.prodStock);
				$("#reviewProductDate").text(prod.createdAt);
				$("#reviewProductDesc").text(prod.prodContent);
				$("#reviewProductImage").attr("src", prod.mainImageUrl);
				$("#prodImages").empty();
				for (let i = 0; i < prod.prodImages.length; i++) {
					$("#prodImages").append(`<img loading="lazy" src="${prod.prodImages[i]}" class="prodImage"
					    style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px">`)
				}

			}))

	});
	//變更預覽圖片
	$(document).on("click", ".prodImage", function () {
		$("#reviewProductImage").attr("src", $(this).attr("src"));
	})

	//依商品類別分類
	$(document).on('change', '.form-select', function (e) {
		let sortName = $(this).val();
		let count_all = 0;
		let count_pending = 0;
		let count_rejected = 0;
		let count_approved = 0;

		if (sortName === "所有類別") {
			$("tr.prodContent").show();
			$("tr.prodContent").addClass("show");
		} else {
			$("tr.prodContent").each(function (i, prod) {
				if ($(prod).data("sort") === sortName) {
					$(prod).show();
					$(prod).addClass("show");
				} else {
					$(prod).hide();
					$(prod).removeClass("show");
				};

			})
		}

		//更新各狀態的商品數量
		count_all = $("tr.prodContent.show").length;
		$("tr.prodContent.show").each(function (i, prod) {
			if ($(prod).data("status") === "待審核") {
				count_pending += 1;
			}
			if ($(prod).data("status") === "未通過") {
				count_rejected += 1;
			}
			if ($(prod).data("status") === "通過") {
				count_approved += 1;
			}
		});

		const $tabs = $('#productReviewTabs');
		$tabs.find('.nav-link[data-status="all"] .ms-1').text(count_all);
		$tabs.find('.nav-link[data-status="待審核"] .ms-1').text(count_pending);
		$tabs.find('.nav-link[data-status="未通過"] .ms-1').text(count_rejected);
		$tabs.find('.nav-link[data-status="通過"] .ms-1').text(count_approved);
	});

	//顯示各狀態的商品數量
	$(document).ready(function () {
		let count_all = $("tr.prodContent").length;
		let count_pending = 0;
		let count_rejected = 0;
		let count_approved = 0;

		$("tr.prodContent").each(function (i, prod) {
			if ($(prod).data("status") === "待審核") {
				count_pending += 1;
			}
			if ($(prod).data("status") === "未通過") {
				count_rejected += 1;
			}
			if ($(prod).data("status") === "通過") {
				count_approved += 1;
			}
		})

		const $tabs = $('#productReviewTabs');
		$tabs.find('.nav-link[data-status="all"] .ms-1').text(count_all);
		$tabs.find('.nav-link[data-status="待審核"] .ms-1').text(count_pending);
		$tabs.find('.nav-link[data-status="未通過"] .ms-1').text(count_rejected);
		$tabs.find('.nav-link[data-status="通過"] .ms-1').text(count_approved);
		$("span#prodApprovalCount").text(count_pending);
	})
	

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
			$(reviewStatus).addClass("bg-danger");
		}else{
			$(reviewStatus).addClass("bg-warning");				
		}
	})
}
