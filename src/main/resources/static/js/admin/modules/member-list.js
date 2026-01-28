export function initMemberList() {
	$(document).on('click', '.btn-view-member', function() {
		const id = $(this).data('id');
		$('#memberDetailModal').modal('show');
	});

	$('#name').on("keyup", function(e) {
		if (e.which == 13)
			$('#search').click();
	})

	$("#search").on("click", function() {
		const status = document.querySelector('#memberStatus').value; // 假設你有篩選下拉框
		const keyword = document.querySelector('#name').value; // 假設你有搜尋框


		// 呼叫後端 API
		fetch(`/admin/dashboard/member-list?status=${status}&keyword=${keyword}`, {
			method: 'GET', // 指定請求方法
			headers: {
				// 加入你想要的標頭
				'X-Requested-With': 'XMLHttpRequest' // 標記為非同步請求，參考圖二做法
			}
		})
			.then(response => {
				if (!response.ok) throw new Error('網路回應不正常');
				return response.text(); // 取得 Thymeleaf 渲染後的 HTML 字串
			})
			.then(htmlSnippet => {
				// 將取得的 HTML 片段直接覆蓋掉原本的 tbody
				document.querySelector('#memberTableBody').innerHTML = htmlSnippet;
			})
			.catch(error => console.error('發生錯誤:', error));
	})

	$(document).on("click", ".accountRecovery, .accountSuspend", function() {
		let confirm_recovery = $(this).hasClass("accountRecovery");
		let message = confirm_recovery ? "確定要復原此會員帳戶？" : "確定要停權此會員帳戶？";
		let memberId = $(this).closest("td").find(".btn-view-member").attr("id");

		if (confirm(message)) {
			fetch(`/admin/dashboard/update-member-status?memberId=${memberId}`, {
				method: "POST",
				headers: { 'X-Requested-With': 'XMLHttpRequest' }

			}).then(response => {
				if (response.ok) {
					return response.text();
				}
				throw new Error("網路回應不正常");
			}).then(data => {
				alert("更新成功！");
				location.reload();
			}).catch(error => {
				alert("發生錯誤：" + error);
			})


		}
	})

	$(document).on("click", ".btn-view-member", function() {
		let memberId = $(this).attr("id");

		fetch(`/admin/dashboard/member-detail?memberId=${memberId}`, {
			method: "GET",
			headers: { 'X-Requested-With': 'XMLHttpRequest' }

		}).then(res => res.text())
			.then(htmlFragment => {
				// 把抓回來的 HTML 塞進 Modal 的內容區
				$("#memberDetailModal .modal-body").html(htmlFragment);
				// 顯示 Modal
				$("#memberDetailModal").modal('show');
			})
	})

	$(document).on("click", "#btn-updateMember", function() {
		$("#accountData").text("修改會員資料");
		$("#information").addClass("-none");
		$("#updateMember").removeClass("-none");
		$("#btn-updateMember").prop("disabled", true).text("修改中...");
	})

	$(document).on("click", "#checkUpdate", function() {
		let memberId = $("#memberId").val();
		let phone = $("input[name='phone']").val();
		let password = $("input[name='password']").val();
		let account = $("input[name='account']").val();
		let address = $("input[name='address']").val();

		const params = new URLSearchParams();
		params.append("memberId", memberId);
		params.append("phone", phone);
		params.append("password", password);
		params.append("account", account);
		params.append("address", address);

		fetch(`/admin/dashboard/update-member-information`, {
			method: "POST",
			body: params,
			headers: { 'X-Requested-With': 'XMLHttpRequest' }
		}).then(res => res.text())
		.then(msg => {
				alert(msg);
				location.reload(); // 更新成功後重新整理頁面看到新資料
		}).catch(error => {
				alert("發生錯誤：" + error);
		})


	})
	$(document).on("click", "#cancelUpdate", function(){
		$("#memberDetailModal").modal('hide');
	})
}