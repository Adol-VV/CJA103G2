export function initEventApprovals() {
    let currentTab = 'all';
    let currentEventId = null;

    // 初始載入
    loadEventApprovals('all');

    // Reload when tab is clicked
    $(document).on('click', '.nav-link[data-section="event-approval"]', function () {
        setTimeout(() => loadEventApprovals('all'), 100);
    });

    // Tab Switching
    $(document).on('click', '#eventReviewTabs .nav-link', function (e) {
        e.preventDefault();
        $('#eventReviewTabs .nav-link').removeClass('active text-white').addClass('text-muted');
        $(this).addClass('active text-white').removeClass('text-muted');

        currentTab = $(this).data('status'); // all, 1, 4, 2, 3, 5
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
        if (confirm('確定審核通過此活動？通過後主辦方將需要設定上架時間。')) {
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
                    alert(response.message || '活動已駁回');
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
        if (!confirm('確定要批准此活動通過嗎？')) return;

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
                    alert(response.message || '活動已核准通過');
                    $('#eventReviewModal').modal('hide');
                    loadEventApprovals(currentTab);
                } else {
                    alert('操作失敗: ' + (response.message || '未知錯誤'));
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
                updateBadge('all', stats.all || 0);
                updateBadge('1', stats.pending || 0); // Pending
                updateBadge('4', stats.rejected || 0); // Rejected
                updateBadge('2', stats.approved || 0); // Approved
                updateBadge('3', stats.published || 0); // Published
                updateBadge('5', stats.ended || 0); // Closed
            }
        } catch (e) {
            console.error('Failed to load stats', e);
        }
    }

    function updateBadge(status, count) {
        const $link = $(`#eventReviewTabs .nav-link[data-status="${status}"]`);
        $link.find('.badge').remove();

        let badgeClass = 'bg-secondary';
        if (status === '1' || status === 'pending') badgeClass = 'bg-warning text-dark';
        if (status === '4' || status === 'rejected') badgeClass = 'bg-danger';
        if (status === '2' || status === 'approved') badgeClass = 'bg-info';
        if (status === '3') badgeClass = 'bg-success';
        if (status === 'all') badgeClass = 'bg-info';

        $link.append(` <span class="badge ${badgeClass} ms-1">${count}</span>`);
    }

    async function loadEventApprovals(tab = 'all', keyword = '') {
        updateStats();

        const $tbody = $('#eventApprovalList');
        if ($tbody.length === 0) return;

        try {
            const url = `/admin/event/review/api/list?tab=${tab}&keyword=${encodeURIComponent(keyword)}`;
            const response = await fetch(url);
            if (!response.ok) throw new Error('Query failed');
            const events = await response.json();

            $tbody.empty();

            if (events.length === 0) {
                $tbody.html('<tr><td colspan="7" class="text-center text-muted p-4">此分類目前無活動</td></tr>');
                return;
            }

            events.forEach(evt => {
                const publishedDate = evt.publishedAt ? formatDate(evt.publishedAt) : '-';
                const eventDate = evt.eventStartAt ? formatDate(evt.eventStartAt) : '-';
                const organizerName = evt.organizer ? (evt.organizer.name || '未知主辦方') : '系統管理';
                const bannerUrl = evt.bannerUrl || 'https://picsum.photos/seed/event/200/120';

                let statusBadge = '';
                switch (evt.status) {
                    case 1: statusBadge = '<span class="badge bg-warning text-dark">待審核</span>'; break;
                    case 2: statusBadge = '<span class="badge bg-info">審核成功 (待設定)</span>'; break;
                    case 3: statusBadge = '<span class="badge bg-success">上架中</span>'; break;
                    case 4: statusBadge = '<span class="badge bg-danger">已駁回</span>'; break;
                    case 5: statusBadge = '<span class="badge bg-secondary">已結束/下架</span>'; break;
                    default: statusBadge = '<span class="badge bg-dark">草稿</span>';
                }

                let actionsHtml = '';
                if (evt.status === 1) { // 待審核
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
                        <td>
                            <div class="rounded border border-secondary overflow-hidden" style="width: 80px; aspect-ratio: 16/9;">
                                <img src="${bannerUrl}" style="width: 100%; height: 100%; object-fit: cover;">
                            </div>
                        </td>
                        <td>${publishedDate}</td>
                        <td>
                            <div class="fw-bold">${evt.title}</div>
                            <small class="text-muted">${evt.place || '-'}</small>
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
            $tbody.html('<tr><td colspan="7" class="text-center text-danger p-4">載入失敗，請稍後再試</td></tr>');
        }
    }

    function openReviewModal(eventId) {
        currentEventId = eventId;
        $('#rejectReason').val('');

        $.ajax({
            url: '/admin/event/review/api/' + eventId,
            type: 'GET',
            success: function (event) {
                const bannerUrl = event.bannerUrl || 'https://picsum.photos/seed/event/800/400';

                let galleryHtml = '';
                if (event.imageUrls && event.imageUrls.length > 1) {
                    galleryHtml = '<div class="d-flex gap-2 flex-wrap mt-2">';
                    event.imageUrls.forEach((url, idx) => {
                        if (idx === 0) return; // Skip banner
                        galleryHtml += `
                            <div class="rounded border border-secondary overflow-hidden" style="width: 100px; height: 100px;">
                                <img src="${url}" style="width: 100%; height: 100%; object-fit: cover; cursor: pointer;" onclick="window.open('${url}', '_blank')">
                            </div>
                        `;
                    });
                    galleryHtml += '</div>';
                }

                let rejectReasonHtml = '';
                if (event.status === 4 && event.rejectReason) {
                    rejectReasonHtml = `
                        <div class="col-12 mt-3">
                            <div class="alert alert-danger mb-0 border-0 bg-danger bg-opacity-10">
                                <h6 class="text-danger fw-bold mb-2"><i class="fas fa-times-circle me-2"></i>審核駁回原因</h6>
                                <p class="mb-0 small">${event.rejectReason}</p>
                            </div>
                        </div>
                    `;
                }

                const detailsHtml = `
                    <div class="row">
                        <div class="col-12 mb-4">
                            <div class="rounded border border-secondary overflow-hidden" style="width: 100%; aspect-ratio: 16/9;">
                                <img src="${bannerUrl}" style="width: 100%; height: 100%; object-fit: cover;">
                            </div>
                            ${galleryHtml}
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-primary fw-bold mb-3"><i class="fas fa-info-circle me-2"></i>活動資訊</h6>
                            <p class="mb-2"><strong>活動名稱：</strong>${event.title}</p>
                            <p class="mb-2"><strong>活動類型：</strong>${event.type?.typeName || '-'}</p>
                            <p class="mb-2"><strong>活動地點：</strong>${event.place}</p>
                            <p class="mb-2"><strong>活動時間：</strong>${event.eventStartAt ? formatDateTime(event.eventStartAt) : '主辦方尚未設定'}</p>
                            <p class="mb-2"><strong>主辦單位：</strong>${event.organizer?.name || '-'}</p>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-primary fw-bold mb-3"><i class="fas fa-ticket-alt me-2"></i>售票資訊 (上架後有效)</h6>
                            <p class="mb-2"><strong>售票開始：</strong>${event.saleStartAt ? formatDateTime(event.saleStartAt) : '-'}</p>
                            <p class="mb-2"><strong>售票結束：</strong>${event.saleEndAt ? formatDateTime(event.saleEndAt) : '-'}</p>
                            
                            <div class="mt-3">
                                <h6 class="text-secondary small fw-bold mb-2">票種清單</h6>
                                <div class="table-responsive">
                                    <table class="table table-sm table-dark table-bordered border-secondary mb-0">
                                        <thead class="bg-darker">
                                            <tr class="small text-muted">
                                                <th>名稱</th>
                                                <th>價格</th>
                                                <th>總額</th>
                                            </tr>
                                        </thead>
                                        <tbody class="small">
                                            ${event.tickets && event.tickets.length > 0
                        ? event.tickets.map(t => `<tr><td>${t.ticketName}</td><td>$${t.price}</td><td>${t.total}</td></tr>`).join('')
                        : '<tr><td colspan="3" class="text-center py-2 text-muted">目前無設定票種</td></tr>'
                    }
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <div class="col-12 mt-3">
                            <h6 class="text-primary fw-bold mb-2"><i class="fas fa-align-left me-2"></i>活動說明</h6>
                            <div class="bg-black border border-secondary p-3 rounded" style="max-height: 300px; overflow-y: auto;">
                                ${event.content || '無說明'}
                            </div>
                        </div>
                        ${rejectReasonHtml}
                    </div>
                `;
                $('#eventReviewDetails').html(detailsHtml);

                // 控制按鈕與審核 UI 顯示 (修正 selector 以對應 panel-event-approval.html)
                const $reviewHr = $('#eventReviewModal hr.my-4');
                const $reasonInputSection = $('#rejectReason').closest('.mb-3');
                const $actionButtons = $('#btnConfirmReject, #btnConfirmApprove');

                if (event.status === 1) { // 待審核 (Pending)
                    $reviewHr.show();
                    $reasonInputSection.show();
                    $actionButtons.show();
                } else {
                    $reviewHr.hide();
                    $reasonInputSection.hide();
                    $actionButtons.hide();
                }

                $('#eventReviewModal').modal('show');
            },
            error: function () {
                alert('載入活動詳情失敗');
            }
        });
    }

    async function approveEvent(id) {
        $.ajax({
            url: '/admin/event/review/api/approve',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ eventId: id }),
            success: function (res) {
                if (res.success) {
                    alert(res.message || '活動已批准通過');
                    loadEventApprovals(currentTab);
                } else {
                    alert('操作失敗: ' + res.message);
                }
            },
            error: function () {
                alert('系統錯誤');
            }
        });
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