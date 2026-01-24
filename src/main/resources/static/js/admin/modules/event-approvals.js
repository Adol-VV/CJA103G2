export function initEventApprovals() {
    loadEventApprovals('pending'); // Default load

    // Reload when tab is clicked
    $(document).on('click', '.nav-link[data-section="event-approval"]', function () {
        setTimeout(() => loadEventApprovals('pending'), 100);
    });

    // Tab Switching
    $(document).on('click', '#eventReviewTabs .nav-link', function (e) {
        e.preventDefault();
        $('#eventReviewTabs .nav-link').removeClass('active text-white').addClass('text-muted');
        $(this).addClass('active text-white').removeClass('text-muted');

        const status = $(this).data('status'); // pending, rejected, approved
        loadEventApprovals(status);
    });

    // Approve Button Click (Only in Pending tab usually, but good to have)
    $(document).on('click', '.btn-approve-event', function () {
        const id = $(this).data('id');
        if (confirm('確定審核通過此活動？')) {
            approveEvent(id);
        }
    });

    // Review/Reject Button Click (Opens Modal)
    $(document).on('click', '.btn-review-event-detail', function () {
        const id = $(this).data('id');
        fetchEventDetail(id);
    });

    // Confirm Reject in Modal
    $('#btnConfirmReject').click(function () {
        const id = $(this).data('id');
        const reason = $('#rejectReason').val();
        if (!reason) {
            alert('請填寫駁回原因');
            return;
        }
        rejectEvent(id, reason);
    });

    // Confirm Approve in Modal
    $('#btnConfirmApprove').click(function () {
        const id = $(this).data('id');
        approveEvent(id);
        $('#eventReviewModal').modal('hide');
    });
}

// Fetch stats and update badges
async function updateStats() {
    try {
        const response = await fetch('/admin/event/review/api/stats');
        if (response.ok) {
            const stats = await response.json();
            updateBadge('pending', stats.pending);
            updateBadge('rejected', stats.rejected);
            updateBadge('approved', stats.approved);
        }
    } catch (e) {
        console.error('Failed to load stats', e);
    }
}

function updateBadge(status, count) {
    const $link = $(`.nav-link[data-status="${status}"]`);
    // Remove existing badge
    $link.find('.badge').remove();

    // Add new badge if count > 0 (or always show depending on design)
    // User screenshot shows badges always present even if number is likely small
    let badgeClass = 'bg-secondary';
    if (status === 'pending') badgeClass = 'bg-warning text-dark';
    if (status === 'rejected') badgeClass = 'bg-danger';
    if (status === 'approved') badgeClass = 'bg-success';

    $link.append(` <span class="badge ${badgeClass} ms-1">${count}</span>`);
}

async function loadEventApprovals(tab = 'pending') {
    // Update stats whenever we reload list
    updateStats();

    const $tbody = $('#eventApprovalList');
    if ($tbody.length === 0) return;

    try {
        const response = await fetch(`/admin/event/review/api/list?tab=${tab}`);
        if (!response.ok) throw new Error('Query failed');
        const events = await response.json();

        $tbody.empty();

        if (events.length === 0) {
            $tbody.html('<tr><td colspan="6" class="text-center text-muted p-4">此分類目前無活動</td></tr>');
            return;
        }

        events.forEach(evt => {
            // Application Date fallback
            const appDateStr = evt.publishedAt || evt.eventAt || evt.startedAt || '';
            const expectedDate = appDateStr ? String(appDateStr).split('T')[0] : '-';

            const eventTitle = evt.title || evt.name || `未命名活動 (ID: ${evt.eventId})`;
            const organizerName = evt.organizer ? (evt.organizer.name || evt.organizer.accountName || '未知主辦方') : '系統管理';

            // Status Badge Logic
            let badgeHtml = '<span class="badge bg-warning text-dark">待審核</span>';
            if (tab === 'rejected') badgeHtml = '<span class="badge bg-danger">已駁回</span>';
            if (tab === 'approved') badgeHtml = '<span class="badge bg-success">已上架</span>';

            // Action Buttons Logic
            let actionsHtml = '';
            if (tab === 'pending') {
                actionsHtml = `
                    <button class="btn btn-sm btn-info btn-review-event-detail me-1" data-id="${evt.eventId}">
                        <i class="fas fa-eye"></i> 審核
                    </button>
                    <button class="btn btn-sm btn-success btn-approve-event" data-id="${evt.eventId}">
                        <i class="fas fa-check"></i> 通過
                    </button>
                `;
            } else {
                // View only for processed events
                actionsHtml = `
                    <button class="btn btn-sm btn-outline-info btn-review-event-detail" data-id="${evt.eventId}">
                        <i class="fas fa-eye"></i> 詳情
                    </button>
                `;
            }

            const html = `
                <tr>
                    <td>-</td>
                    <td>
                        <div class="fw-bold">${eventTitle}</div>
                        <small class="text-muted">ID: ${evt.eventId}</small>
                    </td>
                    <td>${organizerName}</td>
                    <td>${expectedDate}</td>
                    <td>${badgeHtml}</td>
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

async function fetchEventDetail(id) {
    try {
        const response = await fetch(`/admin/event/review/api/${id}`);
        if (!response.ok) throw new Error('Fetch failed');
        const evt = await response.json();

        // Populate Modal
        let content = `
            <h4>${evt.title}</h4>
            <p><i class="fas fa-map-marker-alt me-2"></i>${evt.place}</p>
            <div class="row mb-3">
                <div class="col-6"><strong>開始時間:</strong> ${evt.startedAt.replace('T', ' ')}</div>
                <div class="col-6"><strong>結束時間:</strong> ${evt.endedAt.replace('T', ' ')}</div>
            </div>
            <div class="bg-secondary bg-opacity-10 p-3 rounded mb-3">
                <strong>活動內容:</strong>
                <p class="mb-0 mt-2">${evt.content || '無內容'}</p>
            </div>
        `;
        $('#eventReviewDetails').html(content);

        // Store ID on buttons for action
        $('#btnConfirmReject').data('id', id);
        $('#btnConfirmApprove').data('id', id);

        $('#rejectReason').val(''); // Clear reason
        $('#eventReviewModal').modal('show');

    } catch (error) {
        alert('無法載入活動詳情');
    }
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
            loadEventApprovals();
        } else {
            const data = await response.json();
            alert('操作失敗: ' + (data.error || 'Unknown error'));
        }
    } catch (error) {
        alert('系統錯誤');
    }
}

async function rejectEvent(id, reason) {
    try {
        const response = await fetch('/admin/event/review/api/reject', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ eventId: id, reason: reason })
        });

        if (response.ok) {
            alert('活動已駁回');
            $('#eventReviewModal').modal('hide');
            loadEventApprovals();
        } else {
            const data = await response.json();
            alert('操作失敗: ' + (data.error || 'Unknown error'));
        }
    } catch (error) {
        alert('系統錯誤');
    }
}
