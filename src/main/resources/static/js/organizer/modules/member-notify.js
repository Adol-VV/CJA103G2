/**
 * 會員通知發送模組
 */
import { showToast } from './utils.js';

export function initMemberNotify() {
    // Template buttons
    $(document).on('click', '.btn-template', function () {
        const title = $(this).data('title');
        const content = $(this).data('content');
        $('#title').val(title);
        $('#content').val(content.replace(/\\n/g, '\n'));
        updateNotifyPreview();
    });

    // Update preview on input
    $(document).on('input', '#title, #content', function () {
        updateNotifyPreview();
    });

    // Update target count when selecting event/product
    $(document).on('change', '#targetId', function () {
        const selected = $(this).find('option:selected').text();
        const match = selected.match(/\(.*?(\d+).*?\)/);
        const count = match ? match[1] : 0;
        $('#notifyTargetCount').text(count);
    });

    // Send member notification AJAX
    $(document).on('click', '#btnSendMemberNotify', function (e) {
        e.preventDefault();
        sendMemberNotify();
    });
}

function updateNotifyPreview() {
    const title = $('#title').val() || '通知標題';
    const content = $('#content').val() || '通知內容預覽...';
    $('#previewNotifyTitle').text(title);
    $('#previewNotifyContent').text(content.substring(0, 100) + (content.length > 100 ? '...' : ''));
}

function sendMemberNotify() {
    const target = $('#targetId').val();
    const title = $('#title').val().trim();
    const content = $('#content').val().trim();
    const status = $('#notifyStatus').val(); // 取得通知類型

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

    // if (confirm(`確定要發送通知給 ${$('#notifyTargetCount').text()} 位會員？`)) {
    //     showToast('通知已發送！', 'success');
    //     // Clear form
    //     $('#memberNotifyTarget').val('');
    //     $('#memberNotifyTitle').val('');
    //     $('#memberNotifyContent').val('');
    //     $('#notifyTargetCount').text('0');
    //     updateNotifyPreview();
    // }
    if (confirm(`確定要發送通知給 ${$('#notifyTargetCount').text()} 位會員？`)) {

        //  AJAX
        $.ajax({
            url: '/organizer/notify/addNotify', // 對應Controller的@PostMapping
            type: 'POST',
            data: {
                targetId: target,
                title: title,
                content: content,
                notifyStatus: status
            },
            success: function (updatedList) {
                showToast('通知已成功發送並同步至資料庫！', 'success');

                // 清空表單
                $('#targetId').val('');
                $('#title').val('');
                $('#content').val('');
                $('#notifyTargetCount').text('0');
                updateNotifyPreview();

                // 同步更新下方的已發送通知紀錄
                renderNotifyTable(updatedList);
            },
            error: function (xhr) {
                if (xhr.status === 401) {
                    showToast('登入超時，請重新登入', 'danger');
                    window.location.href = '/organizer/login';
                } else {
                    showToast('發送失敗：' + xhr.responseText, 'danger');
                }
            }
        });
    }
}

/**
 * 重新渲染紀錄表格
 */
/**
 * 重新渲染紀錄表格 (優化顯示)
 */
function renderNotifyTable(list) {
    const $tbody = $('table tbody');
    $tbody.empty();

    if (!list || list.length === 0) return;

    list.forEach(item => {
        // 優化時間顯示
        let dateStr = "時間未知";
        if (item.createdAt) {
            if (Array.isArray(item.createdAt)) {
                const [y, m, d, hh, mm] = item.createdAt;
                dateStr = `${y}/${m.toString().padStart(2, '0')}/${d.toString().padStart(2, '0')} ${hh.toString().padStart(2, '0')}:${mm.toString().padStart(2, '0')}`;
            } else {
                dateStr = item.createdAt.replace('T', ' ').substring(0, 16);
            }
        }

        const statusBadge = item.isRead === 1
            ? '<span class="badge bg-success">已發送</span>'
            : '<span class="badge bg-warning">處理中</span>';

        const typeText = item.notifyStatus == 1 ? '活動提醒' : '一般通知';
        // 確保標題不為 undefined
        const displayTitle = item.title || "無標題";

        const row = `
            <tr>
                <td>${dateStr}</td>
                <td><span class="badge bg-info">${typeText}</span></td>
                <td>${displayTitle}</td>
                <td>所有人</td>
                <td>${statusBadge}</td>
            </tr>
        `;
        $tbody.append(row);
    });
}