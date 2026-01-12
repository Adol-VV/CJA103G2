/**
 * 通用工具模組
 */

// 通用 Toast 通知
export function showToast(message, type = 'info') {
    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }

    const bgClass = {
        'success': 'bg-success',
        'error': 'bg-danger',
        'warning': 'bg-warning',
        'info': 'bg-info'
    }[type] || 'bg-info';

    const iconClass = {
        'success': 'fa-check-circle',
        'error': 'fa-times-circle',
        'warning': 'fa-exclamation-triangle',
        'info': 'fa-info-circle'
    }[type] || 'fa-info-circle';

    const toast = document.createElement('div');
    toast.className = `toast show align-items-center text-white ${bgClass} border-0`;
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body"><i class="fas ${iconClass} me-2"></i>${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" onclick="this.closest('.toast').remove()"></button>
        </div>
    `;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// 主辦方專用 Toast (保持相容性，或可統一使用 showToast)
export function showOrganizerToast(message, type) {
    // 這裡為了保持原有風格，可以重用 showToast 或維持獨立樣式，
    // 但原代碼中 showOrganizerToast 和 showToast 邏輯幾乎一樣，
    // 只是默認樣式和位置可能微調。這裡統一使用 showToast。
    showToast(message, type);
}

export function downloadBlob(blob, filename) {
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(link.href);
}
