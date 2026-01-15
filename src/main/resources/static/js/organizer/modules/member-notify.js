/**
 * 會員通知發送模組
 */
import { showToast } from './utils.js';

export function initMemberNotify() {
    // Template buttons
    $(document).on('click', '.btn-template', function () {
        const title = $(this).data('title');
        const content = $(this).data('content');
        $('#memberNotifyTitle').val(title);
        $('#memberNotifyContent').val(content.replace(/\\n/g, '\n'));
        updateNotifyPreview();
    });

    // Update preview on input
    $(document).on('input', '#memberNotifyTitle, #memberNotifyContent', function () {
        updateNotifyPreview();
    });

    // Update target count when selecting event/product
    $(document).on('change', '#memberNotifyTarget', function () {
        const selected = $(this).find('option:selected').text();
        const match = selected.match(/\(.*?(\d+).*?\)/);
        const count = match ? match[1] : 0;
        $('#notifyTargetCount').text(count);
    });

    // Send member notification
    $(document).on('click', '#btnSendMemberNotify', function () {
        sendMemberNotify();
    });
}

function updateNotifyPreview() {
    const title = $('#memberNotifyTitle').val() || '通知標題';
    const content = $('#memberNotifyContent').val() || '通知內容預覽...';
    $('#previewNotifyTitle').text(title);
    $('#previewNotifyContent').text(content.substring(0, 100) + (content.length > 100 ? '...' : ''));
}

function sendMemberNotify() {
    const target = $('#memberNotifyTarget').val();
    const title = $('#memberNotifyTitle').val().trim();
    const content = $('#memberNotifyContent').val().trim();

    if (!target) {
        showToast('請選擇通知對象（活動或商品）', 'warning');
        return;
    }
    if (!title) {
        showToast('請輸入通知標題', 'warning');
        return;
    }
    if (!content) {
        showToast('請輸入通知內容', 'warning');
        return;
    }

    if (confirm(`確定要發送通知給 ${$('#notifyTargetCount').text()} 位會員？`)) {
        showToast('通知已發送！', 'success');
        // Clear form
        $('#memberNotifyTarget').val('');
        $('#memberNotifyTitle').val('');
        $('#memberNotifyContent').val('');
        $('#notifyTargetCount').text('0');
        updateNotifyPreview();
    }
}
