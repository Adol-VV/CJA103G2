export function initEventList() {
    let currentTab = 'all';
    let currentStatus = null;
    let currentReviewStatus = null;
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
        // Handle multi-status strings (e.g., "2,3") by splitting into array for Spring List<Byte>
        if (statusVal !== undefined && statusVal !== null) {
            currentStatus = String(statusVal).includes(',')
                ? String(statusVal).split(',')
                : [statusVal];
        } else {
            currentStatus = null;
        }
        currentReviewStatus = $link.data('review-status');

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
                <td colspan="4" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </td>
            </tr>
        `);

        const params = {
            status: currentStatus,
            reviewStatus: currentReviewStatus,
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
                // Update total counts if needed, or just specific status counts
                // For simplicity, we can fetch stats from a separate endpoint or compute from list
                // Admin dashboard has a /stats endpoint, maybe organizer needs one too
                updateStats();
            },
            error: function (xhr) {
                $tbody.html(`<tr><td colspan="4" class="text-center text-danger">載入失敗: ${xhr.responseJSON?.message || '未知錯誤'}</td></tr>`);
            }
        });
    }

    function updateStats() {
        $.ajax({
            url: '/organizer/event/api/stats',
            method: 'GET',
            success: function (stats) {
                // 更新各分類計數
                $('#count-all').text(stats.allCount || 0);
                $('#count-pending').text(stats.pendingCount || 0);
                $('#count-rejected').text(stats.rejectedCount || 0);
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
                    <td colspan="4" class="text-center text-muted py-4">
                        <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                        尚無活動資料
                    </td>
                </tr>
            `);
            return;
        }

        events.forEach(event => {
            const eventDate = formatDate(event.eventAt);
            const statusBadge = getStatusBadge(event);
            const actionButtons = getActionButtons(event);

            const row = `
                <tr>
                    <td>
                        <div class="fw-bold">${event.title}</div>
                        <small class="text-muted">${event.place || ''}</small>
                    </td>
                    <td>${eventDate}</td>
                    <td>${statusBadge}</td>
                    <td class="text-end">${actionButtons}</td>
                </tr>
            `;
            $tbody.append(row);
        });
    }

    function renderPagination(pageData) {
        const $pager = $('#organizer-event-pagination');
        $pager.empty();

        if (pageData.totalPages <= 1) return;

        let html = '<ul class="pagination pagination-sm justify-content-center mb-0">';

        // Previous
        html += `<li class="page-item ${pageData.first ? 'disabled' : ''}">
            <a class="page-link bg-dark border-secondary text-light" href="#" data-page="${pageData.number - 1}">
                <i class="fas fa-chevron-left"></i>
            </a>
        </li>`;

        // Pages
        for (let i = 0; i < pageData.totalPages; i++) {
            html += `<li class="page-item ${pageData.number === i ? 'active' : ''}">
                <a class="page-link ${pageData.number === i ? 'bg-primary border-primary' : 'bg-dark border-secondary'} text-light" href="#" data-page="${i}">${i + 1}</a>
            </li>`;
        }

        // Next
        html += `<li class="page-item ${pageData.last ? 'disabled' : ''}">
            <a class="page-link bg-dark border-secondary text-light" href="#" data-page="${pageData.number + 1}">
                <i class="fas fa-chevron-right"></i>
            </a>
        </li>`;

        html += '</ul>';
        $pager.html(html);
    }

    function getStatusBadge(event) {
        // --- 核心狀態矩陣邏輯 ---

        // 1. 已駁回 (Rejected): S=0, R=2
        if (event.status === 0 && event.reviewStatus === 2) {
            return '<span class="badge bg-danger">審核駁回</span>';
        }

        // 2. 待審核 (Pending): S=0, R=0, P!=NULL
        if (event.status === 0 && event.reviewStatus === 0 && event.publishedAt) {
            return '<span class="badge bg-warning text-dark">待審核</span>';
        }

        // 3. 活動取消 (Cancelled): S=2
        if (event.status === 2) {
            return '<span class="badge bg-danger">活動取消</span>';
        }

        // 4. 活動結束 (Closed/Archived): S=3
        if (event.status === 3) {
            return '<span class="badge bg-secondary">活動結束</span>';
        }

        // 5. 已上架 (Listed): S=1
        if (event.status === 1) {
            const now = new Date();
            const startedAt = event.startedAt ? new Date(event.startedAt) : null;
            const endedAt = event.endedAt ? new Date(event.endedAt) : null;
            const eventAt = event.eventAt ? new Date(event.eventAt) : null;

            if (startedAt && now < startedAt) {
                return '<span class="badge bg-info">即將開賣</span>';
            } else if (startedAt && endedAt && now >= startedAt && now < endedAt) {
                return '<span class="badge bg-success">立即購票</span>';
            } else if (endedAt && eventAt && now >= endedAt && now < eventAt) {
                return '<span class="badge bg-warning text-dark">銷售結束</span>';
            } else if (eventAt && now >= eventAt) {
                return '<span class="badge bg-secondary">活動結束</span>';
            }
            return '<span class="badge bg-success">已上架</span>';
        }

        // 兜底狀態 (草稿常理下不會出現在此清單)
        return '<span class="badge bg-outline-secondary">未知狀態</span>';
    }

    function getActionButtons(event) {
        let buttons = '<div class="btn-group">';

        // 草稿 (S=0, R=0, P=null) 或 駁回 (S=0, R=2)
        if (event.status === 0 && (event.reviewStatus === 2 || (event.reviewStatus === 0 && !event.publishedAt))) {
            const editClass = event.reviewStatus === 2 ? 'btn-outline-warning' : 'btn-outline-light';
            buttons += `
                <button type="button" class="btn btn-sm ${editClass}" data-action="edit-event" data-id="${event.eventId}" title="編輯">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-success" onclick="submitEvent(${event.eventId})" title="送審">
                    <i class="fas fa-paper-plane"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteEvent(${event.eventId})" title="刪除">
                    <i class="fas fa-trash-alt"></i>
                </button>
            `;
        }
        // 待審核 (S=0, R=0, P!=null)
        else if (event.status === 0 && event.reviewStatus === 0 && event.publishedAt) {
            buttons += `
                <button class="btn btn-sm btn-warning" onclick="withdrawEvent(${event.eventId})">
                    <i class="fas fa-undo"></i> 撤回
                </button>
            `;
        }
        // 已上架 (S=1, R=1)
        else if (event.status === 1 && event.reviewStatus === 1) {
            buttons += `
                <a href="/event/${event.eventId}" target="_blank" class="btn btn-sm btn-outline-info" title="查看">
                    <i class="fas fa-external-link-alt"></i>
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

    // --- Edit Event (SPA) ---
    $(document).on('click', '[data-action="edit-event"]', function () {
        const eventId = $(this).data('id');
        if (window.openEventEditor) {
            window.openEventEditor(eventId);
            // Switch to create panel
            $('[data-section="event-create"]').trigger('click');
        }
    });

    // --- Global Submit Event Function ---
    window.submitEvent = function (eventId) {
        if (!confirm('確定要送出審核嗎？')) return;

        $.ajax({
            url: '/organizer/event/submit/' + eventId,
            type: 'POST',
            success: function (response) {
                if (response.success) {
                    if (window.showToast) window.showToast('活動已送出審核！', 'success');
                    loadOrganizerEvents();
                } else {
                    alert('送審失敗: ' + response.message);
                }
            },
            error: function (xhr) {
                alert('操作失敗: ' + (xhr.responseJSON?.message || '未知錯誤'));
            }
        });
    };

    // --- Global Withdraw Event Function ---
    window.withdrawEvent = function (eventId) {
        if (!confirm('確定要撤回審核嗎？')) return;

        $.ajax({
            url: '/organizer/event/withdraw/' + eventId,
            type: 'POST',
            success: function (response) {
                if (response.success) {
                    if (window.showToast) window.showToast('活動已撤回！', 'success');
                    loadOrganizerEvents();
                } else {
                    alert('撤回失敗: ' + response.message);
                }
            },
            error: function (xhr) {
                alert('操作失敗: ' + (xhr.responseJSON?.message || '未知錯誤'));
            }
        });
    };

    // --- Global Delete Event Function ---
    window.deleteEvent = function (eventId) {
        if (!confirm('確定要刪除此活動嗎？此操作無法復原！')) return;

        $.ajax({
            url: '/organizer/event/' + eventId,
            type: 'DELETE',
            success: function (response) {
                if (response.success) {
                    if (window.showToast) window.showToast('活動已刪除！', 'success');
                    loadOrganizerEvents();
                } else {
                    alert('刪除失敗: ' + response.message);
                }
            },
            error: function (xhr) {
                alert('操作失敗: ' + (xhr.responseJSON?.message || '未知錯誤'));
            }
        });
    };

    // Initial Load
    if ($('#panel-events-list').hasClass('active')) {
        loadOrganizerEvents();
    }

    // Also load when section is shown via showSection
    $(document).on('click', '[data-section="events-list"]', function () {
        loadOrganizerEvents();
    });
}