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
                            <form class="row g-3" onsubmit="window.submitTime(event, ${event.eventId})">
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">1. 上架時間 *</label>
                                    <input type="datetime-local" name="publishedAt" class="form-control form-control-sm" required>
                                    <small class="text-muted">設定後活動立即在前台上架</small>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">2. 售票開始時間 *</label>
                                    <div class="input-group input-group-sm">
                                        <input type="datetime-local" name="saleStartAt" class="form-control" required>
                                        <button class="btn btn-outline-secondary" type="button" onclick="window.syncPublished(${event.eventId})">🔗 同步上架</button>
                                    </div>
                                    <small class="text-muted">消費者開始購票時間</small>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-bold">3. 售票結束時間 *</label>
                                    <input type="datetime-local" name="saleEndAt" class="form-control form-control-sm" required>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-bold">4. 活動開始時間 *</label>
                                    <input type="datetime-local" name="eventStartAt" class="form-control form-control-sm" required>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-bold">5. 活動結束時間 *</label>
                                    <input type="datetime-local" name="eventEndAt" class="form-control form-control-sm" required>
                                </div>
                                <div class="col-12">
                                    <div class="time-preview p-3 bg-black rounded border border-secondary mt-2">
                                        <h6 class="text-primary small fw-bold mb-2">⏰ 時間順序預覽：</h6>
                                        <ul class="preview-list list-unstyled small mb-0 ms-2"><li>請填寫時間...</li></ul>
                                    </div>
                                </div>
                                <div class="col-12 text-end mt-3">
                                    <button type="button" class="btn btn-sm btn-outline-light me-2" onclick="window.toggleTimeForm(${event.eventId})">取消</button>
                                    <button type="submit" class="btn btn-sm btn-primary px-4">✅ 確認設定並上架</button>
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

    function getStatusBadge(event) {
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
        let buttons = '<div class="btn-group">';

        // 草稿(0) 或 駁回(4)
        if (event.status === 0 || event.status === 4) {
            buttons += `
                <button type="button" class="btn btn-sm btn-outline-light" onclick="window.editDraft(${event.eventId})" title="編輯">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-outline-success" onclick="window.submitEvent(${event.eventId})" title="送審">
                    <i class="fas fa-paper-plane"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger" onclick="window.deleteEvent(${event.eventId})" title="刪除">
                    <i class="fas fa-trash-alt"></i>
                </button>
            `;
        }
        // 待審核(1)
        else if (event.status === 1) {
            buttons += `
                <button class="btn btn-sm btn-outline-warning" onclick="window.withdrawEvent(${event.eventId})">
                    <i class="fas fa-undo"></i> 撤回
                </button>
            `;
        }
        // 審核成功(2)
        else if (event.status === 2) {
            buttons += `
                <a href="/event/${event.eventId}" target="_blank" class="btn btn-sm btn-outline-info" title="查看活動詳情">
                    <i class="fas fa-eye"></i>
                </a>
                <button class="btn btn-sm btn-primary" onclick="window.toggleTimeForm(${event.eventId})">
                    <i class="fas fa-clock me-1"></i>設定時間
                </button>
            `;
        }
        // 已上架(3)
        else if (event.status === 3) {
            buttons += `
                <a href="/event/${event.eventId}" target="_blank" class="btn btn-sm btn-outline-info" title="查看">
                    <i class="fas fa-external-link-alt"></i>
                </a>
                <button class="btn btn-sm btn-danger" onclick="window.forceClose(${event.eventId})" title="強制下架">
                    <i class="fas fa-stop-circle"></i>
                </button>
            `;
        }
        // 已下架(5)
        else if (event.status === 5) {
            buttons += `
                <a href="/event/${event.eventId}" target="_blank" class="btn btn-sm btn-outline-secondary">
                    詳情
                </a>
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

    window.syncPublished = function (eventId) {
        const $form = $('#timeRow-' + eventId);
        const pub = $form.find('[name="publishedAt"]').val();
        if (pub) {
            $form.find('[name="saleStartAt"]').val(pub);
            window.updatePreview(eventId);
        } else {
            alert('請先填寫上架時間');
        }
    };

    window.updatePreview = function (eventId) {
        const $form = $('#timeRow-' + eventId);
        const format = (v) => v ? v.replace('T', ' ') : null;
        const times = {
            published: format($form.find('[name="publishedAt"]').val()),
            saleStart: format($form.find('[name="saleStartAt"]').val()),
            saleEnd: format($form.find('[name="saleEndAt"]').val()),
            eventStart: format($form.find('[name="eventStartAt"]').val()),
            eventEnd: format($form.find('[name="eventEndAt"]').val())
        };

        let html = '';
        if (times.published) html += `<li>上架: ${times.published}</li>`;
        if (times.saleStart) html += `<li>售票開始: ${times.saleStart}</li>`;
        if (times.saleEnd) html += `<li>售票結束: ${times.saleEnd}</li>`;
        if (times.eventStart) html += `<li>活動開始: ${times.eventStart}</li>`;
        if (times.eventEnd) html += `<li>活動結束: ${times.eventEnd}</li>`;

        $form.find('.preview-list').html(html || '<li>請填寫時間...</li>');
    };

    window.submitTime = function (e, eventId) {
        e.preventDefault();
        const formData = new FormData(e.target);
        const now = new Date();

        // 取得時間值並轉化為 Date 物件
        const pub = new Date(formData.get('publishedAt'));
        const saleStart = new Date(formData.get('saleStartAt'));
        const saleEnd = new Date(formData.get('saleEndAt'));
        const eventStart = new Date(formData.get('eventStartAt'));
        const eventEnd = new Date(formData.get('eventEndAt'));

        // 1. 確保所有時間都在未來 (考慮幾秒鐘的誤差，故多減一點)
        const checkNow = new Date(now.getTime() - 60000); // 容許1分鐘內誤差

        if (pub < checkNow) { alert('上架時間不允許過去的時間！'); return; }
        if (saleStart < checkNow) { alert('售票開始時間不允許過去的時間！'); return; }
        if (saleEnd < checkNow) { alert('售票結束時間不允許過去的時間！'); return; }
        if (eventStart < checkNow) { alert('活動舉辦時間不允許過去的時間！'); return; }
        if (eventEnd < checkNow) { alert('活動結束時間不允許過去的時間！'); return; }

        // 2. 順序邏輯檢查
        if (saleStart < pub) {
            alert('❌ 售票開始時間不能早於上架時間');
            return;
        }
        if (saleEnd <= saleStart) {
            alert('❌ 售票結束時間必須晚於售票開始時間');
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
        for (const [key, value] of formData) {
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
        if (!confirm('確定要送出審核嗎？')) return;
        $.post('/organizer/event/submit/' + eventId, function (res) {
            if (res.success) { showToast('活動已送出審核！', 'success'); loadOrganizerEvents(); }
            else alert(res.message);
        });
    };

    window.withdrawEvent = function (eventId) {
        if (!confirm('確定要撤回審核嗎？')) return;
        $.post('/organizer/event/withdraw/' + eventId, function (res) {
            if (res.success) { showToast('活動已撤回！', 'success'); loadOrganizerEvents(); }
            else alert(res.message);
        });
    };

    window.deleteEvent = function (eventId) {
        if (!confirm('確定要刪除嗎？')) return;
        $.ajax({
            url: '/organizer/event/' + eventId, type: 'DELETE', success: function (res) {
                if (res.success) { showToast('已刪除！', 'success'); loadOrganizerEvents(); }
                else alert(res.message);
            }
        });
    };

    window.forceClose = function (eventId) {
        const reason = prompt('請輸入下架原因：');
        if (reason === null) return;
        $.ajax({
            url: `/organizer/event/${eventId}/force-close`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ reason: reason }),
            success: function (res) {
                if (res.success) { showToast('活動已下架', 'success'); loadOrganizerEvents(); }
                else alert(res.message);
            }
        });
    };

    function showToast(msg, type) {
        if (window.showToast) window.showToast(msg, type);
        else console.log(`[${type}] ${msg}`);
    }

    // Initial Load
    if ($('#panel-events-list').hasClass('active')) loadOrganizerEvents();
    $(document).on('click', '[data-section="events-list"]', loadOrganizerEvents);
}