/**
 * 登入驗證模組
 */
export function checkOrganizerAuth() {
    const user = JSON.parse(localStorage.getItem('momento_user') || '{}');
    const isLoggedIn = localStorage.getItem('momento_organizer_logged_in') === 'true';

    // 檢查是否已登入且為主辦方角色
    if (!user.role || user.role !== 'ORGANIZER' || !isLoggedIn) {
        console.warn('Organizer authentication check failed (suppressed for dev)');
        // 實際專案中取消註解以下轉導
        // alert('請先登入主辦方帳號');
        // window.location.href = 'login.html';
    }
}
