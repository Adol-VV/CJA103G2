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
}
