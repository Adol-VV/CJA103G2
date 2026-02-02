export function initEventList() {
    let currentTab = 'all';
    let currentStatus = null;
    let currentKeyword = '';
    let currentPage = 0;

    // --- Tab Switching ---
    $('#organizer-event-tabs .nav-link').on('click', function (e) {
        e.preventDefault();
        const $link = $(this);

        $('#organizer-event-tabs .nav-link').removeClass('active');
        $link.addClass('active');

        currentTab = $link.data('tab');
        const statusVal = $link.data('status');

        if (statusVal !== undefined && statusVal !== null && statusVal !== '') {
            currentStatus = String(statusVal).includes(',')
                ? String(statusVal).split(',')
                : [statusVal];
        } else {
            currentStatus = null;
        }

        currentPage = 0;
        loadOrganizerEvents();
    });

    // --- Search ---
    $('#btn-event-search').on('click', function () {
        currentKeyword = $('#event-search-input').val().trim();
        currentPage = 0;
        loadOrganizerEvents();
    });

    $('#event-search-input').on('keypress', function (e) {
        if (e.which === 13) {
            $('#btn-event-search').trigger('click');
        }
    });

    // --- Pagination ---
    $(document).on('click', '#organizer-event-pagination .page-link', function (e) {
        e.preventDefault();
        const page = $(this).data('page');
        if (page !== undefined && page !== null) {
            currentPage = page;
            loadOrganizerEvents();
        }
    });

    function loadOrganizerEvents() {
        const $tbody = $('#organizer-event-tbody');
        $tbody.html(`
            <tr>
                <td colspan="5" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </td>
            </tr>
        `);

        const params = {
            status: currentStatus,
            keyword: currentKeyword,
            page: currentPage,
            size: 10
        };

        $.ajax({
            url: '/organizer/event/api/list',
            method: 'GET',
            data: params,
            success: function (pageData) {
                renderEventTable(pageData.content);
                renderPagination(pageData);
                updateStats();
            },
            error: function (xhr) {
                $tbody.html(`<tr><td colspan="5" class="text-center text-danger">載入失敗: ${xhr.responseJSON?.message || '未知錯誤'}</td></tr>`);
            }
        });
    }

    function updateStats() {
        $.ajax({
            url: '/organizer/event/api/stats',
            method: 'GET',
            success: function (stats) {
                $('#count-all').text(stats.allCount || 0);
                $('#count-draft').text(stats.draftCount || 0);
                $('#count-pending').text(stats.pendingCount || 0);
                $('#count-rejected').text(stats.rejectedCount || 0);
                $('#count-approved').text(stats.approvedCount || 0);
                $('#count-published').text(stats.activeCount || 0);
                $('#count-ended').text(stats.endedCount || 0);
            }
        });
    }

    function renderEventTable(events) {
        const $tbody = $('#organizer-event-tbody');
        $tbody.empty();

        if (!events || events.length === 0) {
            $tbody.html(`
                <tr>
                    <td colspan="5" class="text-center text-muted py-4">
                        <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                        尚無活動資料
                    </td>
                </tr>
            `);
            return;
        }

        events.forEach(event => {
            const eventDate = event.eventStartAt ? formatDate(event.eventStartAt) : '未設定時間';
            const statusBadge = getStatusBadge(event);
            const actionButtons = getActionButtons(event);
            const bannerUrl = event.bannerUrl || 'https://picsum.photos/seed/event/200/120';

            const row = `
                <tr data-event-id="${event.eventId}">
                    <td>
                        <div class="rounded border border-secondary overflow-hidden" style="width: 80px; aspect-ratio: 16/9;">
                            <img src="${bannerUrl}" style="width: 100%; height: 100%; object-fit: cover;">
                        </div>
                    </td>
                    <td>
                        <div class="fw-bold">${event.title}</div>
                        <small class="text-muted">${event.place || '未設定地點'}</small>
                    </td>
                    <td>${eventDate}</td>
                    <td>${statusBadge}</td>
                    <td class="text-end">${actionButtons}</td>
                </tr>
                <tr id="timeRow-${event.eventId}" class="d-none bg-darker">
                    <td colspan="5" class="p-0">
                        <div class="time-form-container p-4 border-top border-secondary">
                            <div class="alert alert-info py-2">
                                <i class="fas fa-info-circle me-2"></i>審核已通過！請設定活動時間以完成上架。
                            </div>
                            <form class="row g-2" onsubmit="window.submitTime(event, ${event.eventId})">
                                <div class="col-md-3">
                                    <div class="d-flex justify-content-between align-items-center mb-1">
                                        <label class="form-label fw-bold mb-0 small">1. 售票開始 *</label>
                                        <div class="form-check form-switch p-0" style="min-height: auto;">
                                            <input class="form-check-input" type="checkbox" name="syncSaleStart" id="sync-${event.eventId}" onchange="window.toggleSaleStart(${event.eventId})" style="margin-left: 0; float: right; transform: scale(0.8);">
                                            <label class="form-check-label small text-info pe-1" for="sync-${event.eventId}" style="float: right; font-size: 0.7rem;">立即開賣</label>
                                        </div>
                                    </div>
                                    <div id="saleStartContainer-${event.eventId}">
                                        <input type="datetime-local" name="saleStartAt" class="form-control form-control-sm" required>
                                    </div>
                                    <div id="saleStartMsg-${event.eventId}" class="text-success small d-none p-1 bg-dark rounded border border-success border-opacity-25" style="font-size: 0.7rem;">
                                        <i class="fas fa-bolt me-1"></i> 與上架同步
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label fw-bold mb-1 small">2. 售票結束 *</label>
                                    <input type="datetime-local" name="saleEndAt" class="form-control form-control-sm" required>
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label fw-bold mb-1 small">3. 活動開始 *</label>
                                    <input type="datetime-local" name="eventStartAt" class="form-control form-control-sm" required>
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label fw-bold mb-1 small">4. 活動結束 *</label>
                                    <input type="datetime-local" name="eventEndAt" class="form-control form-control-sm" required>
                                </div>
                                <div class="col-12">
                                    <div class="time-preview p-3 bg-black rounded border border-secondary mt-2">
                                        <h6 class="text-primary small fw-bold mb-2">⏰ 時間順序預覽 (確認後立即上架)：</h6>
                                        <ul class="preview-list list-unstyled small mb-0 ms-2"><li>請填寫時間...</li></ul>
                                    </div>
                                </div>
                                <div class="col-12 text-end mt-3">
                                    <button type="button" class="btn btn-sm btn-outline-light me-2" onclick="window.toggleTimeForm(${event.eventId})">取消</button>
                                    <button type="submit" class="btn btn-sm btn-success px-4 fw-bold">✅ 確認設定並立即上架</button>
                                </div>
                            </form>
                        </div>
                    </td>
                </tr>
            `;
            $tbody.append(row);
        });

        // Bind input listeners for preview
        $('.time-form-container input').on('change', function () {
            const eventId = $(this).closest('tr').attr('id').split('-')[1];
            window.updatePreview(eventId);
        });
    }

    function renderPagination(pageData) {
        const $pager = $('#organizer-event-pagination');
        $pager.empty();
        if (pageData.totalPages <= 1) return;

        let html = '<ul class="pagination pagination-sm justify-content-center mb-0">';
        html += `<li class="page-item ${pageData.first ? 'disabled' : ''}">
            <a class="page-link bg-dark border-secondary text-light" href="#" data-page="${pageData.number - 1}">
                <i class="fas fa-chevron-left"></i>
            </a></li>`;

        for (let i = 0; i < pageData.totalPages; i++) {
            html += `<li class="page-item ${pageData.number === i ? 'active' : ''}">
                <a class="page-link ${pageData.number === i ? 'bg-primary border-primary' : 'bg-dark border-secondary'} text-light" href="#" data-page="${i}">${i + 1}</a>
            </li>`;
        }

        html += `<li class="page-item ${pageData.last ? 'disabled' : ''}">
            <a class="page-link bg-dark border-secondary text-light" href="#" data-page="${pageData.number + 1}">
                <i class="fas fa-chevron-right"></i>
            </a></li></ul>`;
        $pager.html(html);
    }

    window.viewEventDetail = viewEventDetail;

    function getStatusBadge(event) {
        const now = new Date();
        const eventEnd = event.eventEndAt ? new Date(event.eventEndAt) : null;

        // 如果資料庫狀態是 3 (已上架)，但時間已經超過結束時間，前端顯示為已結束
        if (event.status === 3 && eventEnd && now > eventEnd) {
            return '<span class="badge bg-secondary">已結束</span>';
        }

        switch (event.status) {
            case 0: return '<span class="badge bg-secondary">草稿</span>';
            case 1: return '<span class="badge bg-warning text-dark">待審核</span>';
            case 2: return '<span class="badge bg-info">審核成功 (待設定)</span>';
            case 3: return '<span class="badge bg-success">已上架</span>';
            case 4: return '<span class="badge bg-danger">審核駁回</span>';
            case 5: return '<span class="badge bg-secondary">已結束</span>';
            default: return '<span class="badge bg-dark">未知</span>';
        }
    }

    function getActionButtons(event) {
        let buttons = '<div class="btn-group gap-2">';

        // 1. 待審核 (Pending)
        if (event.status === 1) {
            buttons += `
                <button type="button" class="btn btn-sm btn-outline-info" onclick="window.viewEventDetail(${event.eventId})">
                    <i class="fas fa-search-plus me-1"></i>詳情
                </button>
                <button type="button" class="btn btn-sm btn-outline-warning" onclick="window.withdrawEvent(${event.eventId})">
                    <i class="fas fa-undo me-1"></i>撤回
                </button>
            `;
        }
        // 2. 草稿 (0) 或 已駁回 (4)
        else if (event.status === 0 || event.status === 4) {
            buttons += `
                <button type="button" class="btn btn-sm btn-warning text-dark fw-bold" onclick="window.editDraft(${event.eventId})">
                    <i class="fas fa-edit me-1"></i>編輯
                </button>
                <button type="button" class="btn btn-sm btn-outline-success" onclick="window.submitEvent(${event.eventId})">
                    <i class="fas fa-paper-plane me-1"></i>送審
                </button>
                <button type="button" class="btn btn-sm btn-outline-danger" onclick="window.deleteEvent(${event.eventId})">
                    <i class="fas fa-trash-alt me-1"></i>刪除
                </button>
            `;
        }
        // 3. 審核成功 (Approved - Status 2)
        else if (event.status === 2) {
            buttons += `
                <a href="/event/${event.eventId}" target="_blank" class="btn btn-sm btn-outline-info">
                    <i class="fas fa-external-link-alt me-1"></i>詳情
                </a>
                <button type="button" class="btn btn-sm btn-info text-dark fw-bold" onclick="window.toggleTimeForm(${event.eventId})">
                    <i class="fas fa-clock me-1"></i>設定時間
                </button>
            `;
        }
        // 4. 上架中 (Published - Status 3)
        else if (event.status === 3) {
            buttons += `
                <a href="/event/${event.eventId}" target="_blank" class="btn btn-sm btn-outline-info">
                    <i class="fas fa-external-link-alt me-1"></i>詳情
                </a>
                <button type="button" class="btn btn-sm btn-outline-danger" onclick="window.cancelEvent(${event.eventId})">
                    <i class="fas fa-stop-circle me-1"></i>取消活動
                </button>
            `;
        }
        // 5. 已下架 (Ended - Status 5)
        else if (event.status === 5) {
            buttons += `
                <a href="/event/${event.eventId}" target="_blank" class="btn btn-sm btn-outline-info">
                    <i class="fas fa-external-link-alt me-1"></i>詳情
                </a>
                <button type="button" class="btn btn-sm btn-outline-danger" onclick="window.deleteEvent(${event.eventId})">
                    <i class="fas fa-trash-alt me-1"></i>刪除
                </button>
            `;
        }

        buttons += '</div>';
        return buttons;
    }

    function formatDate(dateStr) {
        if (!dateStr) return '-';
        const date = new Date(dateStr);
        return date.getFullYear() + '/' +
            String(date.getMonth() + 1).padStart(2, '0') + '/' +
            String(date.getDate()).padStart(2, '0');
    }

    // --- Time Setting Actions ---
    window.toggleTimeForm = function (eventId) {
        const $row = $('#timeRow-' + eventId);
        $row.toggleClass('d-none');
    };

    window.toggleSaleStart = function (eventId) {
        const $form = $('#timeRow-' + eventId);
        const isSynced = $form.find('[name="syncSaleStart"]').is(':checked');
        const $container = $('#saleStartContainer-' + eventId);
        const $msg = $('#saleStartMsg-' + eventId);

        if (isSynced) {
            $container.addClass('d-none');
            $msg.removeClass('d-none');
            $container.find('input').removeAttr('required');
        } else {
            $container.removeClass('d-none');
            $msg.addClass('d-none');
            $container.find('input').attr('required', 'required');
        }
        window.updatePreview(eventId);
    };


    window.updatePreview = function (eventId) {
        const $form = $('#timeRow-' + eventId);
        const isSynced = $form.find('[name="syncSaleStart"]').is(':checked');
        const format = (v) => v ? v.replace('T', ' ') : null;

        let saleStartText = format($form.find('[name="saleStartAt"]').val());
        if (isSynced) {
            saleStartText = '<span class="text-info">與上架同步 (立即開賣)</span>';
        }

        const times = {
            saleEnd: format($form.find('[name="saleEndAt"]').val()),
            eventStart: format($form.find('[name="eventStartAt"]').val()),
            eventEnd: format($form.find('[name="eventEndAt"]').val())
        };

        let html = '';
        html += `<li class="text-success"><i class="fas fa-check-circle me-1"></i>上架時間: 立即上架</li>`;
        html += `<li>售票開始: ${saleStartText || '<span class="text-muted">未設定</span>'}</li>`;
        if (times.saleEnd) html += `<li>售票結束: ${times.saleEnd}</li>`;
        if (times.eventStart) html += `<li>活動開始: ${times.eventStart}</li>`;
        if (times.eventEnd) html += `<li>活動結束: ${times.eventEnd}</li>`;

        $form.find('.preview-list').html(html);
    };

    window.submitTime = function (e, eventId) {
        e.preventDefault();
        const formData = new FormData(e.target);
        const now = new Date();

        // 上架時間自動設為現在
        const pub = now;
        const isSynced = formData.get('syncSaleStart') === 'on';
        const saleStart = isSynced ? now : new Date(formData.get('saleStartAt'));
        const saleEnd = new Date(formData.get('saleEndAt'));
        const eventStart = new Date(formData.get('eventStartAt'));
        const eventEnd = new Date(formData.get('eventEndAt'));

        // 1. 確保所有時間都在未來 (考慮幾秒鐘的誤差，故多減一點)
        const checkNow = new Date(now.getTime() - 60000); // 容許1分鐘內誤差

        if (saleStart < checkNow) { alert('售票開始時間不可早於現在！'); return; }
        if (saleEnd < checkNow) { alert('售票結束時間不可早於現在！'); return; }
        if (eventStart < checkNow) { alert('活動舉辦時間不可早於現在！'); return; }
        if (eventEnd < checkNow) { alert('活動結束時間不可早於現在！'); return; }

        // 2. 順序邏輯檢查
        if (saleEnd <= saleStart) {
            if (isSynced) {
                alert('❌ 售票結束時間必須晚於現在 (立即開賣)！');
            } else {
                alert('❌ 售票結束時間必須晚於售票開始時間');
            }
            return;
        }
        if (eventStart < saleEnd) {
            alert('❌ 活動舉辦時間應晚於售票結束時間');
            return;
        }
        if (eventEnd <= eventStart) {
            alert('❌ 活動結束時間必須晚於活動舉辦時間');
            return;
        }

        if (!confirm('✅ 確認設定時間並立即上架？')) return;

        // Convert ISO format to Spring expecting format
        const data = new URLSearchParams();
        const pubISO = new Date(now.getTime() - (now.getTimezoneOffset() * 60000)).toISOString();
        // 手動加上自動產生的 publishedAt
        data.append('publishedAt', pubISO);

        // 如果是同步，也要補上 saleStartAt
        if (isSynced) {
            data.append('saleStartAt', pubISO);
        }

        for (const [key, value] of formData) {
            if (key === 'syncSaleStart') continue;
            data.append(key, value);
        }

        $.ajax({
            url: `/organizer/event/${eventId}/set-times`,
            type: 'POST',
            data: data.toString(),
            contentType: 'application/x-www-form-urlencoded',
            success: function (res) {
                if (res.success) {
                    alert('上架成功！');
                    loadOrganizerEvents();
                } else {
                    alert('上架失敗: ' + res.message);
                }
            },
            error: function (xhr) {
                alert('系統錯誤: ' + (xhr.responseJSON?.message || '未知錯誤'));
            }
        });
    };

    // --- Global Actions (Submit, Withdraw, Delete, ForceClose) ---
    window.submitEvent = function (eventId) {
        Swal.fire({
            title: '確定要送出審核嗎？',
            text: '送出後活動資訊將進入審核佇列。',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '確定送出',
            cancelButtonText: '取消',
            background: '#1a1d20',
            color: '#fff'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/organizer/event/submit/' + eventId,
                    type: 'POST',
                    success: function (res) {
                        if (res.success) {
                            showToast('活動已送出審核！', 'success');
                            loadOrganizerEvents();
                        } else {
                            Swal.fire({ icon: 'error', title: '錯誤', text: res.message, background: '#1a1d20', color: '#fff' });
                        }
                    },
                    error: function (xhr) {
                        Swal.fire({ icon: 'error', title: '錯誤', text: xhr.responseJSON?.message || '系統錯誤', background: '#1a1d20', color: '#fff' });
                    }
                });
            }
        });
    };

    window.withdrawEvent = function (eventId) {
        Swal.fire({
            title: '確定要撤回審核嗎？',
            text: '撤回後活動將回到草稿狀態，您可以重新編輯。',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: '確定撤回',
            cancelButtonText: '取消',
            background: '#1a1d20',
            color: '#fff'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/organizer/event/withdraw/' + eventId,
                    type: 'POST',
                    success: function (res) {
                        if (res.success) {
                            showToast('活動已撤回！', 'success');
                            loadOrganizerEvents();
                        } else {
                            Swal.fire({ icon: 'error', title: '失敗', text: res.message, background: '#1a1d20', color: '#fff' });
                        }
                    },
                    error: function (xhr) {
                        Swal.fire({ icon: 'error', title: '失敗', text: xhr.responseJSON?.message || '系統錯誤', background: '#1a1d20', color: '#fff' });
                    }
                });
            }
        });
    };

    window.deleteEvent = function (eventId) {
        Swal.fire({
            title: '⚠️ 確定要刪除此活動嗎？',
            text: '此操作將會永久刪除活動內容且無法復原！',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            confirmButtonText: '確定刪除',
            cancelButtonText: '取消',
            background: '#1a1d20',
            color: '#fff'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/organizer/event/' + eventId,
                    type: 'DELETE',
                    success: function (res) {
                        if (res.success) {
                            showToast('已成功刪除活動！', 'success');
                            loadOrganizerEvents();
                        } else {
                            Swal.fire({
                                icon: 'info',
                                title: '無法執行刪除',
                                html: `<div class="text-start small p-2">${res.message}</div>`,
                                background: '#1a1d20',
                                color: '#fff',
                                confirmButtonText: '我知道了'
                            });
                        }
                    },
                    error: function (xhr) {
                        Swal.fire({
                            icon: 'error',
                            title: '系統錯誤',
                            text: xhr.responseJSON?.message || '刪除時發生意外錯誤，請稍後再試。',
                            background: '#1a1d20',
                            color: '#fff'
                        });
                    }
                });
            }
        });
    };

    window.cancelEvent = function (eventId) {
        Swal.fire({
            title: '🛑 確定要「取消活動」嗎？',
            html: `
                <div class="text-start small p-2">
                    <p class="text-danger fw-bold mb-2">⚠️ 極致謹慎提醒：</p>
                    <ul class="ps-3 mb-3">
                        <li>此操作將立即停止前台所有售票。</li>
                        <li><b>活動取消後將無法重新上架。</b></li>
                        <li>若已有售出票券，主辦方需負擔後續退款與通知責任。</li>
                    </ul>
                    <div class="form-check mt-3 bg-dark p-2 rounded border border-danger">
                        <input class="form-check-input ms-0" type="checkbox" id="confirmCancelResponsibility">
                        <label class="form-check-label ms-2 text-warning fw-bold" for="confirmCancelResponsibility">
                            我已明確瞭解停辦責任，並願自行承擔後續衍生之維權與退款事宜
                        </label>
                    </div>
                </div>
            `,
            input: 'text',
            inputPlaceholder: '請輸入停辦具體理由（必填，將記錄於伺服器日誌）',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            confirmButtonText: '確定取消活動',
            cancelButtonText: '再考慮一下',
            background: '#1a1d20',
            color: '#fff',
            preConfirm: (reason) => {
                const isChecked = document.getElementById('confirmCancelResponsibility').checked;
                if (!isChecked) {
                    Swal.showValidationMessage('您必須勾選下方的責任聲明方可執行取消');
                    return false;
                }
                if (!reason || reason.trim() === '') {
                    Swal.showValidationMessage('請務必提供停辦理由以供後台存證');
                    return false;
                }
                return reason;
            }
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/organizer/event/${eventId}/cancel`,
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({ reason: result.value }),
                    success: function (res) {
                        if (res.success) {
                            Swal.fire({
                                icon: 'success',
                                title: '活動已停辦',
                                text: '系統已自動停止購票流程。',
                                background: '#1a1d20',
                                color: '#fff'
                            });
                            loadOrganizerEvents();
                        } else {
                            Swal.fire({
                                icon: 'info',
                                title: '暫時無法執行取消',
                                html: `
                                    <div class="text-start small">
                                        <p class="mb-2"><strong>可能原因：</strong></p>
                                        <ul class="mb-0">
                                            <li>活動目前可能已不是「上架中」狀態 (例如已被手動下架)。</li>
                                            <li>系統檢測到您的權限或登入狀態異常。</li>
                                            <li>或者是「${res.message || '伺服器拒絕請求'}」</li>
                                        </ul>
                                    </div>
                                `,
                                background: '#1a1d20',
                                color: '#fff'
                            });
                        }
                    },
                    error: function (xhr) {
                        const msg = xhr.responseJSON?.message || '發生未知錯誤';
                        Swal.fire({
                            icon: 'error',
                            title: '取消失敗',
                            html: `
                                <div class="text-start small">
                                    <p>伺服器回應：<span class="text-danger">${msg}</span></p>
                                    <hr class="border-secondary">
                                    <p class="mb-0 text-muted">提示：請確認您的網路連線是否正常，或嘗試重新整理頁面後再次操作。若活動已進入「已結束」狀態，則不需重複取消。</p>
                                </div>
                            `,
                            background: '#1a1d20',
                            color: '#fff'
                        });
                    }
                });
            }
        });
    };



    /**
     * 內部檢視詳情
     */
    async function viewEventDetail(eventId) {
        const $body = $('#organizerEventDetailBody');
        $body.html(`
            <div class="text-center p-5">
                <div class="spinner-border text-primary" role="status"></div>
                <p class="mt-3 text-muted">正在載入活動內容...</p>
            </div>
        `);
        $('#organizerEventDetailModal').modal('show');

        try {
            const response = await fetch(`/organizer/event/api/${eventId}`);
            const res = await response.json();

            if (res.success) {
                const event = res.event;
                const tickets = res.tickets;
                const images = res.images;

                // 排序圖片：banner(0) 在前
                images.sort((a, b) => (a.imageOrder || 0) - (b.imageOrder || 0));

                const bannerUrl = images.length > 0 ? images[0].imageUrl : 'https://picsum.photos/seed/event/800/400';

                let galleryHtml = '';
                if (images.length > 1) {
                    galleryHtml = `
                        <div class="mt-3">
                            <h6 class="text-primary fw-bold mb-2 small text-uppercase">活動相簿</h6>
                            <div class="d-flex gap-2 flex-wrap">
                    `;
                    images.forEach((img, idx) => {
                        if (idx === 0) return;
                        galleryHtml += `
                            <div class="rounded border border-secondary overflow-hidden bg-darker" style="width: 100px; height: 100px;">
                                <img src="${img.imageUrl}" style="width: 100%; height: 100%; object-fit: cover; cursor: pointer;" onclick="window.open('${img.imageUrl}', '_blank')">
                            </div>
                        `;
                    });
                    galleryHtml += '</div></div>';
                }

                let ticketsHtml = '';
                if (tickets && tickets.length > 0) {
                    ticketsHtml = `
                        <div class="mt-3">
                            <h6 class="text-secondary small fw-bold mb-2">票種清單</h6>
                            <div class="table-responsive">
                                <table class="table table-sm table-dark table-bordered border-secondary mb-0">
                                    <thead class="bg-darker">
                                        <tr class="small text-muted">
                                            <th>名稱</th>
                                            <th>價格</th>
                                            <th>總額</th>
                                            <th>剩餘</th>
                                        </tr>
                                    </thead>
                                    <tbody class="small">
                                        ${tickets.map(t => `
                                            <tr>
                                                <td>${t.ticketName}</td>
                                                <td>$${t.price}</td>
                                                <td>${t.total}</td>
                                                <td class="${t.remain < 10 ? 'text-danger fw-bold' : 'text-success'}">${t.remain}</td>
                                            </tr>
                                        `).join('')}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    `;
                } else {
                    ticketsHtml = `
                        <div class="mt-3">
                            <h6 class="text-secondary small fw-bold mb-2">票種清單</h6>
                            <div class="text-center py-2 bg-darker rounded border border-secondary text-muted small">
                                目前無設定票種
                            </div>
                        </div>
                    `;
                }

                const html = `
                    <div class="row g-4">
                        <div class="col-lg-7">
                            <div class="rounded border border-secondary overflow-hidden shadow-lg" style="width: 100%; aspect-ratio: 16/9;">
                                <img src="${bannerUrl}" style="width: 100%; height: 100%; object-fit: cover;">
                            </div>
                            ${galleryHtml}
                        </div>
                        <div class="col-lg-5">
                            <div class="bg-darker p-3 rounded border border-secondary h-100">
                                <h6 class="text-primary fw-bold mb-3"><i class="fas fa-info-circle me-2"></i>基本資訊</h6>
                                <p class="mb-2"><span class="text-muted">名稱：</span><span class="text-white">${event.title}</span></p>
                                <p class="mb-2"><span class="text-muted">地點：</span><span class="text-white">${event.place || '-'}</span></p>
                                <p class="mb-2"><span class="text-muted">類型：</span><span class="badge bg-secondary">${event.type ? (event.type.typeName || event.type.type_name || '-') : '-'}</span></p>
                                <p class="mb-2"><span class="text-muted">活動日期：</span><span class="text-white">${event.eventStartAt ? formatDateTime(event.eventStartAt) : '-'}</span></p>
                                
                                <hr class="border-secondary">
                                
                                <h6 class="text-primary fw-bold mb-3"><i class="fas fa-ticket-alt me-2"></i>票務資訊</h6>
                                <p class="mb-2 small"><span class="text-muted">售票期間：</span><span class="text-info">${event.saleStartAt ? formatDateTime(event.saleStartAt) : '-'}</span> 至 <span class="text-info">${event.saleEndAt ? formatDateTime(event.saleEndAt) : '-'}</span></p>
                                ${ticketsHtml}
                            </div>
                        </div>
                        <div class="col-12">
                            <h6 class="text-primary fw-bold mb-2"><i class="fas fa-align-left me-2"></i>活動詳細說明</h6>
                            <div class="bg-black border border-secondary p-3 rounded" style="max-height: 250px; overflow-y: auto; color: #a1a1aa; font-size: 0.95rem; line-height: 1.6;">
                                ${event.content || '無說明'}
                            </div>
                        </div>
                    </div>
                `;
                $body.html(html);
            } else {
                $body.html(`<div class="alert alert-danger"><i class="fas fa-exclamation-triangle me-2"></i>${res.message}</div>`);
            }
        } catch (error) {
            console.error('Error fetching event detail:', error);
            $body.html(`<div class="alert alert-danger"><i class="fas fa-exclamation-triangle me-2"></i>載入失敗，伺服器可能無回應</div>`);
        }
    }

    function formatDateTime(dateStr) {
        if (!dateStr) return '-';
        const d = new Date(dateStr);
        return d.toLocaleString('zh-TW', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function showToast(msg, type) {
        if (window.showToast) window.showToast(msg, type);
        else console.log(`[${type}] ${msg}`);
    }

    // Initial Load
    if ($('#panel-events-list').hasClass('active')) loadOrganizerEvents();
    $(document).on('click', '[data-section="events-list"]', loadOrganizerEvents);
}