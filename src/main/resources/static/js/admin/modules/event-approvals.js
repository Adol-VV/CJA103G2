export function initEventApprovals() {
    loadEventApprovals();

    $(document).on('click', '.nav-link[data-section="event-approval"]', function () {
        setTimeout(loadEventApprovals, 100);
    });

    $(document).on('click', '#panel-event-approval .nav-link', function () {
        loadEventApprovals();
    });

    $(document).on('click', '.btn-approve-event', function () {
        const id = $(this).data('id');
        if (confirm('確定審核通過此活動？')) {
            if (window.MockDB) {
                MockDB.events.updateStatus(id, 'ACTIVE');
                if (window.Momento && window.Momento.Toast) {
                    window.Momento.Toast.show('活動已核准', 'success');
                } else if (window.showToast) {
                    window.showToast('活動已核准', 'success');
                }
                loadEventApprovals();
            }
        }
    });

    $(document).on('click', '.btn-review-event', () => $('#eventReviewModal').modal('show'));
}

function loadEventApprovals() {
    if (!window.MockDB) return;
    const $tbody = $('#eventApprovalList');
    if ($tbody.length === 0) return;

    const pending = MockDB.events.getPending();
    $tbody.empty();

    if (pending.length === 0) {
        $tbody.html('<tr><td colspan="6" class="text-center text-muted p-4">目前沒有待審核的活動</td></tr>');
        return;
    }

    pending.forEach(evt => {
        const html = `
            <tr>
                <td>${evt.createdAt.split('T')[0]}</td>
                <td>
                    <div class="fw-bold">${evt.name}</div>
                    <small class="text-muted">ID: ${evt.id}</small>
                </td>
                <td>${evt.organizer || '主辦方'}</td>
                <td>${evt.date}</td>
                <td><span class="badge bg-warning text-dark">待審核</span></td>
                <td>
                    <button class="btn btn-sm btn-success btn-approve-event" data-id="${evt.id}">
                        <i class="fas fa-check"></i> 通過
                    </button>
                </td>
            </tr>
        `;
        $tbody.append(html);
    });
}
