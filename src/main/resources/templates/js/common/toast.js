/**
 * 全站可用的提示訊息（Toast 通知）工具
 * 用來在畫面右上角跳出「成功 / 錯誤 / 提示」的小訊息框。
 */

const ToastUI = {
    show(message, type = 'info') {
        const container = document.getElementById('toast_box') || this.createContainer();

        // Simple toast creation
        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-white bg-${type === 'error' ? 'danger' : type === 'success' ? 'success' : 'primary'} border-0`;
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');

        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        `;

        container.appendChild(toast);
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();

        toast.addEventListener('hidden.bs.toast', () => toast.remove());
    },

    createContainer() {
        const div = document.createElement('div');
        div.id = 'toast_box';
        div.className = 'toast-container position-fixed top-0 end-0 p-3';
        div.style.zIndex = '9999';
        document.body.appendChild(div);
        return div;
    }
};

export default ToastUI;
