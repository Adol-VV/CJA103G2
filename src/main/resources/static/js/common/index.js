/**
 * 前端共用 UI 模組的「總入口初始化檔」
 */
import AuthUI from './auth.js';
// import CartUI from './cart.js';  // 後台不需要購物車功能，且 cart.js 使用舊式寫法
import NavbarUI from './navigation.js';
import ToastUI from './toast.js';

document.addEventListener('DOMContentLoaded', () => {
    AuthUI.updateState();
    // CartUI.updateBadge();  // 後台不需要購物車
    NavbarUI.highlightCurrent();
});

// Export for usage if needed
export { AuthUI, NavbarUI, ToastUI };

// Expose globals for legacy scripts
window.showToast = (msg, type) => ToastUI.show(msg, type);
window.escapeHtml = (text) => {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
};
