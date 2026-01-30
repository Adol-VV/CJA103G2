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
        const targetId = $(this).val();
        console.log('選擇的目標:', targetId); // Debug log

        if (!targetId) {
            $('#notifyTargetCount').text('0');
            return;
        }

        // 透過 AJAX 取得實際購買人數
        $.ajax({
            url: '/organizer/notify/countMembers',
            type: 'GET',
            data: { targetId: targetId },
            success: function (count) {
                console.log('購買人數:', count); // Debug log
                $('#notifyTargetCount').text(count || 0);
            },
            error: function (xhr, status, error) {
                console.error('取得人數失敗:', error);
                $('#notifyTargetCount').text('0');
            }
        });
    });

    // Send member notification - 攔截表單提交 (和平台管理後台相同)
    $(document).on('submit', '#memberNotifyForm', function (e) {
        e.preventDefault(); // 阻止表單跳轉
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
    const status = $('#notifyStatus').val();
    const targetCount = $('#notifyTargetCount').text();

    // 驗證必填欄位
    if (!target) {
        Swal.fire({
            icon: 'warning',
            title: '請選擇通知對象',
            text: '請選擇要發送通知的活動或商品',
            background: '#1a1d20',
            color: '#fff'
        });
        return;
    }
    if (!title) {
        Swal.fire({
            icon: 'warning',
            title: '請輸入通知標題',
            background: '#1a1d20',
            color: '#fff'
        });
        return;
    }
    if (!content) {
        Swal.fire({
            icon: 'warning',
            title: '請輸入通知內容',
            background: '#1a1d20',
            color: '#fff'
        });
        return;
    }

    // 確認發送對話框
    Swal.fire({
        title: '確定要發送通知嗎？',
        html: `將發送通知給 <strong>${targetCount}</strong> 位會員`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '確定發送',
        cancelButtonText: '取消',
        confirmButtonColor: '#198754',
        background: '#1a1d20',
        color: '#fff'
    }).then((result) => {
        if (result.isConfirmed) {
            // 顯示 Loading 動畫
            Swal.fire({
                title: '正在發送通知...',
                allowOutsideClick: false,
                background: '#1a1d20',
                color: '#fff',
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            // AJAX 發送
            $.ajax({
                url: '/organizer/notify/addNotify',
                type: 'POST',
                data: {
                    targetId: target,
                    title: title,
                    content: content,
                    notifyStatus: status
                },
                success: function (updatedList) {
                    // 顯示成功訊息
                    Swal.fire({
                        icon: 'success',
                        title: '發送成功',
                        text: '通知已成功發送給會員！',
                        timer: 2000,
                        showConfirmButton: false,
                        background: '#1a1d20',
                        color: '#fff'
                    });

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
                    let errorMsg = '系統發生未知錯誤';
                    if (xhr.status === 401) {
                        errorMsg = '登入超時，請重新登入';
                    } else if (xhr.responseText) {
                        errorMsg = xhr.responseText;
                    }

                    Swal.fire({
                        icon: 'error',
                        title: '發送失敗',
                        text: errorMsg,
                        background: '#1a1d20',
                        color: '#fff'
                    });

                    if (xhr.status === 401) {
                        setTimeout(() => {
                            window.location.href = '/organizer/login';
                        }, 1500);
                    }
                }
            });
        }
    });
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