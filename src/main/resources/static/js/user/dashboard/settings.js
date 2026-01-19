export function initSettings() {
    // 1. Profile Update
    $(document).on('click', '#btnSaveProfile', function () {
        if (window.Momento && window.Momento.Toast) {
            window.Momento.Toast.show('個人資料已更新', 'success');
        } else if (window.showToast) {
            window.showToast('個人資料已更新', 'success');
        }
    });

    // 2. Avatar Preview
    $(document).on('change', '#avatarUpload', function (e) {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                $('#avatarPreview').attr('src', e.target.result);
            }
            reader.readAsDataURL(file);
        }
    });

    // 3. Password Update
    $(document).on('click', '#btnUpdatePassword', function () {
        const newPass = $('#newPassword').val();
        if (!newPass) {
            if (window.showToast) window.showToast('請輸入新密碼', 'warning');
            return;
        }
        if (window.showToast) window.showToast('密碼已成功重新設定', 'success');
    });
	
//	$(document).ready(function() {
//	    // 使用委派監聽 (Delegated Event)，防止碎片更新後按鈕失效
//	    $(document).on('click', '#btnSaveProfile', function(e) {
//	        e.preventDefault(); // 阻擋預設動作
//
//	        const $form = $('#settings-form');
//	        
//	        $.ajax({
//	            url: $form.attr('action'),
//	            type: 'POST',
//	            data: $form.serialize(), // 自動打包所有 input 內容
//	            success: function(response) {
//	                // 當後端回傳 ResponseEntity.ok("success")
//	                if (response === "success") {
//	                    alert("更新成功！");
//	                    window.location.reload(); // 成功後重整，刷新 placeholder
//	                }
//	            },
//	            error: function(xhr) {
//	                // 當後端回傳 ResponseEntity.badRequest()
//	                // xhr.responseText 就包含了帶有錯誤訊息的 HTML 碎片
//	                if (xhr.status === 400 || xhr.status === 500){
//	                    console.log("驗證失敗，更新碎片內容");
//	                    $('#settings-outer-container').html(xhr.responseText);
//	                } else {
//	                    alert("系統發生非預期錯誤");
//	                }
//	            }
//	        });
//	    });
//	});
//	success: function(response) {
//	    // 檢查回傳內容是否包含成功標記
//	    if (response.includes("success-flag")) { 
//	        alert("更新成功！");
//	        window.location.reload();
//	    } else {
//	        // 如果包含的是驗證失敗的 HTML 片段
//	        console.log("收到錯誤片段，更新 DOM");
//	        // 將渲染後的 HTML（包含紅色 <span>）填入容器
//	        $('#settings-outer-container').html(response);
//	    }
//	}
}
