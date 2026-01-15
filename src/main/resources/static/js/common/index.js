/**
 * 前端共用 UI 模組的「總入口初始化檔」
 */
import AuthUI from './auth.js';
import CartUI from './cart.js';
import NavbarUI from './navigation.js';
import ToastUI from './toast.js';

document.addEventListener('DOMContentLoaded', () => {
    AuthUI.updateState();
    CartUI.updateBadge();
    NavbarUI.highlightCurrent();
});

// Export for usage if needed
export { AuthUI, CartUI, NavbarUI, ToastUI };

// Expose globals for legacy scripts
window.showToast = (msg, type) => ToastUI.show(msg, type);
window.escapeHtml = (text) => {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
};
