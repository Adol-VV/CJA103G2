export function initEventApprovals() {
    let currentTab = 'pending';
    let currentEventId = null;

    // 初始載入
    loadEventApprovals('pending');

    // Reload when tab is clicked
    $(document).on('click', '.nav-link[data-section="event-approval"]', function () {
        setTimeout(() => loadEventApprovals('pending'), 100);
    });

    // Tab Switching
    $(document).on('click', '#eventReviewTabs .nav-link', function (e) {
        e.preventDefault();
        $('#eventReviewTabs .nav-link').removeClass('active text-white').addClass('text-muted');
        $(this).addClass('active text-white').removeClass('text-muted');

        currentTab = $(this).data('status'); // all, pending, rejected, approved, ended
        loadEventApprovals(currentTab, $('#adminEventSearch').val());
    });

    // Search Input Event
    let searchTimer;
    $(document).on('input', '#adminEventSearch', function () {
        clearTimeout(searchTimer);
        const keyword = $(this).val();
        searchTimer = setTimeout(() => {
            loadEventApprovals(currentTab, keyword);
        }, 500);
    });

    // Approve Button Click
    $(document).on('click', '.btn-approve-event', function () {
        const id = $(this).data('id');
        if (confirm('確定審核通過此活動？')) {
            approveEvent(id);
        }
    });

    // Review/Reject Button Click (Opens Modal)
    $(document).on('click', '.btn-review-event-detail', function () {
        const id = $(this).data('id');
        openReviewModal(id);
    });

    // Confirm Reject in Modal
    $('#btnConfirmReject').click(function () {
        if (!currentEventId) return;

        const reason = $('#rejectReason').val().trim();
        if (!reason) {
            alert('請填寫駁回原因');
            return;
        }

        if (!confirm('確定要駁回此活動嗎？')) return;

        const btn = $(this);
        const originalText = btn.html();
        btn.html('<span class="spinner-border spinner-border-sm me-1"></span>處理中...').prop('disabled', true);

        $.ajax({
            url: '/admin/event/review/api/reject',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                eventId: currentEventId,
                reason: reason
            }),
            success: function (response) {
                if (response.success) {
                    alert('活動已駁回');
                    $('#eventReviewModal').modal('hide');
                    loadEventApprovals(currentTab);
                } else {
                    alert('操作失敗: ' + response.message);
                }
                btn.html(originalText).prop('disabled', false);
            },
            error: function (xhr) {
                alert('操作失敗: ' + (xhr.responseJSON?.message || '未知錯誤'));
                btn.html(originalText).prop('disabled', false);
            }
        });
    });

    // Confirm Approve in Modal
    $('#btnConfirmApprove').click(function () {
        if (!currentEventId) return;
        if (!confirm('確定要批准此活動上架嗎？')) return;

        const btn = $(this);
        const originalText = btn.html();
        btn.html('<span class="spinner-border spinner-border-sm me-1"></span>處理中...').prop('disabled', true);

        $.ajax({
            url: '/admin/event/review/api/approve',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ eventId: currentEventId }),
            success: function (response) {
                if (response.success) {
                    alert('活動已批准上架');
                    $('#eventReviewModal').modal('hide');
                    loadEventApprovals(currentTab);
                } else {
                    alert('操作失敗: ' + response.message);
                }
                btn.html(originalText).prop('disabled', false);
            },
            error: function (xhr) {
                alert('操作失敗: ' + (xhr.responseJSON?.message || '未知錯誤'));
                btn.html(originalText).prop('disabled', false);
            }
        });
    });

    // Fetch stats and update badges
    async function updateStats() {
        try {
            const response = await fetch('/admin/event/review/api/stats');
            if (response.ok) {
                const stats = await response.json();
                updateBadge('all', stats.all);
                updateBadge('pending', stats.pending);
                updateBadge('rejected', stats.rejected);
                updateBadge('approved', stats.approved);
                updateBadge('ended', stats.ended);
            }
        } catch (e) {
            console.error('Failed to load stats', e);
        }
    }

    function updateBadge(status, count) {
        const $link = $(`.nav-link[data-status="${status}"]`);
        $link.find('.badge').remove();

        let badgeClass = 'bg-secondary';
        if (status === 'pending') badgeClass = 'bg-warning text-dark';
        if (status === 'rejected') badgeClass = 'bg-danger';
        if (status === 'approved') badgeClass = 'bg-success';
        if (status === 'all') badgeClass = 'bg-info';

        $link.append(` <span class="badge ${badgeClass} ms-1">${count}</span>`);
    }

    async function loadEventApprovals(tab = 'all', keyword = '') {
        updateStats();

        const $tbody = $('#eventApprovalList');
        if ($tbody.length === 0) return;

        try {
            const response = await fetch(`/admin/event/review/api/list?tab=${tab}&keyword=${encodeURIComponent(keyword)}`);
            if (!response.ok) throw new Error('Query failed');
            const events = await response.json();

            $tbody.empty();

            if (events.length === 0) {
                $tbody.html('<tr><td colspan="6" class="text-center text-muted p-4">此分類目前無活動</td></tr>');
                return;
            }

            events.forEach(evt => {
                const publishedDate = evt.publishedAt ? formatDate(evt.publishedAt) : '-';
                const eventDate = evt.eventAt ? formatDate(evt.eventAt) : '-';
                const organizerName = evt.organizer ? (evt.organizer.name || evt.organizer.accountName || '未知主辦方') : '系統管理';

                let statusBadge = '';
                // 根據活動真實狀態顯示標籤
                if (evt.status === 0 && evt.reviewStatus === 0 && evt.publishedAt) {
                    statusBadge = '<span class="badge bg-warning text-dark">待審核</span>';
                } else if (evt.status === 0 && evt.reviewStatus === 2) {
                    statusBadge = '<span class="badge bg-danger">已駁回</span>';
                } else if (evt.status === 1) {
                    statusBadge = '<span class="badge bg-success">上架中</span>';
                } else if (evt.status === 2 || evt.status === 3) {
                    statusBadge = '<span class="badge bg-secondary">已結束/取消</span>';
                }

                let actionsHtml = '';
                // 只有「待審核」狀態需要審核按鈕
                if (evt.status === 0 && evt.reviewStatus === 0 && evt.publishedAt) {
                    actionsHtml = `
                        <button class="btn btn-sm btn-info btn-review-event-detail me-1" data-id="${evt.eventId}">
                            <i class="fas fa-eye"></i> 審核
                        </button>
                        <button class="btn btn-sm btn-success btn-approve-event" data-id="${evt.eventId}">
                            <i class="fas fa-check"></i> 通過
                        </button>
                    `;
                } else {
                    actionsHtml = `
                        <button class="btn btn-sm btn-outline-info btn-review-event-detail" data-id="${evt.eventId}">
                            <i class="fas fa-eye"></i> 詳情
                        </button>
                    `;
                }

                const html = `
                    <tr>
                        <td>${publishedDate}</td>
                        <td>
                            <div class="fw-bold">${evt.title}</div>
                            <small class="text-muted">${evt.place || ''}</small>
                        </td>
                        <td>${organizerName}</td>
                        <td>${eventDate}</td>
                        <td>${statusBadge}</td>
                        <td>${actionsHtml}</td>
                    </tr>
                `;
                $tbody.append(html);
            });

        } catch (error) {
            console.error('Error loading events:', error);
            $tbody.html('<tr><td colspan="6" class="text-center text-danger p-4">載入失敗，請稍後再試</td></tr>');
        }
    }

    function openReviewModal(eventId) {
        currentEventId = eventId;
        $('#rejectReason').val('');

        $.ajax({
            url: '/admin/event/review/api/' + eventId,
            type: 'GET',
            success: function (event) {
                const detailsHtml = `
                    <div class="row">
                        <div class="col-md-6">
                            <h6 class="text-muted mb-2">活動資訊</h6>
                            <p><strong>活動名稱：</strong>${event.title}</p>
                            <p><strong>活動類型：</strong>${event.type?.typeName || '-'}</p>
                            <p><strong>活動地點：</strong>${event.place}</p>
                            <p><strong>活動時間：</strong>${formatDateTime(event.eventAt)}</p>
                            <p><strong>主辦單位：</strong>${event.organizer?.name || '-'}</p>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-muted mb-2">售票資訊</h6>
                            <p><strong>售票開始：</strong>${formatDateTime(event.startedAt)}</p>
                            <p><strong>售票結束：</strong>${formatDateTime(event.endedAt)}</p>
                            <p><strong>票種數量：</strong>${event.tickets?.length || 0} 種</p>
                        </div>
                        <div class="col-12 mt-3">
                            <h6 class="text-muted mb-2">活動說明</h6>
                            <p class="border border-secondary p-3 rounded">${event.content || '無'}</p>
                        </div>
                    </div>
                `;
                $('#eventReviewDetails').html(detailsHtml);
                $('#eventReviewModal').modal('show');
            },
            error: function () {
                alert('載入活動詳情失敗');
            }
        });
    }

    async function approveEvent(id) {
        try {
            const response = await fetch('/admin/event/review/api/approve', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ eventId: id })
            });

            if (response.ok) {
                alert('活動已核准上架');
                loadEventApprovals(currentTab);
            } else {
                const data = await response.json();
                alert('操作失敗: ' + (data.error || 'Unknown error'));
            }
        } catch (error) {
            alert('系統錯誤');
        }
    }

    function formatDate(dateStr) {
        if (!dateStr) return '-';
        const d = new Date(dateStr);
        return d.toLocaleDateString('zh-TW');
    }

    function formatDateTime(dateStr) {
        if (!dateStr) return '-';
        const d = new Date(dateStr);
        return d.toLocaleString('zh-TW');
    }
}