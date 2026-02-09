export function initSettings() {
    // 表單提交處理
    $(document).on('submit', '#formOrganizerSettings', function (e) {
        e.preventDefault();
        if (window.showToast) window.showToast('主辦方資訊已成功更新', 'success');
    });

    // 密碼變更表單驗證
    initPasswordValidation();
}

/**
 * 初始化密碼變更表單的即時驗證
 */
function initPasswordValidation() {
    const newPasswordInput = $('#newPassword');
    const confirmPasswordInput = $('#confirmPassword');
    const matchMessage = $('#passwordMatchMessage');
    const submitBtn = $('#submitPasswordBtn');

    // 檢查密碼是否一致
    function checkPasswordMatch() {
        const newPassword = newPasswordInput.val();
        const confirmPassword = confirmPasswordInput.val();

        // 如果確認密碼欄位為空，不顯示任何訊息
        if (!confirmPassword) {
            matchMessage.hide();
            submitBtn.prop('disabled', false);
            confirmPasswordInput.removeClass('is-invalid is-valid');
            return;
        }

        // 檢查是否一致
        if (newPassword === confirmPassword) {
            matchMessage
                .removeClass('text-danger')
                .addClass('text-success d-block')
                .html('<i class="fas fa-check-circle me-1"></i>密碼一致')
                .show();
            confirmPasswordInput.removeClass('is-invalid').addClass('is-valid');
            submitBtn.prop('disabled', false);
        } else {
            matchMessage
                .removeClass('text-success')
                .addClass('text-danger d-block')
                .html('<i class="fas fa-times-circle me-1"></i>密碼不一致，請重新輸入')
                .show();
            confirmPasswordInput.removeClass('is-valid').addClass('is-invalid');
            submitBtn.prop('disabled', true);
        }
    }

    // 監聽輸入事件
    confirmPasswordInput.on('input', checkPasswordMatch);
    newPasswordInput.on('input', function () {
        // 如果確認密碼已經有值，重新檢查
        if (confirmPasswordInput.val()) {
            checkPasswordMatch();
        }
    });

    // 表單提交前最終驗證
    $('#changePasswordForm').on('submit', function (e) {
        const newPassword = newPasswordInput.val();
        const confirmPassword = confirmPasswordInput.val();

        if (newPassword !== confirmPassword) {
            e.preventDefault();
            matchMessage
                .removeClass('text-success')
                .addClass('text-danger d-block')
                .html('<i class="fas fa-times-circle me-1"></i>密碼不一致，請重新輸入')
                .show();
            confirmPasswordInput.focus();
            return false;
        }

        // 密碼長度檢查
        if (newPassword.length < 8) {
            e.preventDefault();
            alert('新密碼至少需要 8 個字元');
            newPasswordInput.focus();
            return false;
        }
    });
}
