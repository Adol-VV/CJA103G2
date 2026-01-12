export function initSettings() {
    $(document).on('submit', '#formOrganizerSettings', function (e) {
        e.preventDefault();
        if (window.showToast) window.showToast('主辦方資訊已成功更新', 'success');
    });
}
