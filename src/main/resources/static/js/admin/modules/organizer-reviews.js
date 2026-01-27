export function initOrganizerReviews() {
    let currentOrganizerId = null;
    let currentStatus = 0; // 預設待審核
    let currentPage = 1;
    const pageSize = 9;
    let allData = [];

    // 初始載入列表
    loadOrganizerReviews();

    // 當狀態篩選變更時
    $(document).on('change', '#organizerStatusFilter', function () {
        currentStatus = parseInt($(this).val());
        currentPage = 1; // 切換狀態重置回第一頁
        loadOrganizerReviews();
    });

    // 當點擊導覽列中的「主辦審核」時重新載入
    $(document).on('click', '.nav-link[data-section="organizer-review"]', function () {
        setTimeout(loadOrganizerReviews, 100);
    });

    // 點擊「進行審核」按鈕
    $(document).on('click', '.btn-review-application', function () {
        const id = $(this).data('id');
        openReviewModal(id);
    });

    // 分頁點擊事件
    $(document).on('click', '.organizer-page-link', function (e) {
        e.preventDefault();
        currentPage = $(this).data('page');
        renderListHeaders(); // 回到頁面頂部
        displayCurrentPage();
    });

    // 核准按鈕
    $('#btnApproveOrganizer').off('click').click(function () {
        if (!currentOrganizerId) return;
        if (!$('#checkReg').is(':checked') || !$('#checkBank').is(':checked')) {
            alert('請先勾選確認資料與銀行帳戶驗證無誤');
            return;
        }

        if (!confirm('確定要核准此主辦單位申請嗎？')) return;

        const btn = $(this);
        btn.prop('disabled', true).html('<span class="spinner-border spinner-border-sm me-1"></span>處理中...');

        $.ajax({
            url: '/admin/organizer/review/api/approve',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ id: currentOrganizerId }),
            success: function (res) {
                if (res.success) {
                    alert('申請已核准');
                    $('#applicationReviewModal').modal('hide');
                    loadOrganizerReviews();
                } else {
                    alert('操作失敗：' + res.message);
                }
                btn.prop('disabled', false).text('核准申請');
            },
            error: function () {
                alert('系統錯誤，請稍後再試');
                btn.prop('disabled', false).text('核准申請');
            }
        });
    });

    // 駁回按鈕
    $('#btnRejectOrganizer').off('click').click(function () {
        if (!currentOrganizerId) return;
        if (!confirm('確定要駁回此申請嗎？\n(確定將直接刪除本次申請紀錄）')) return;

        const btn = $(this);
        btn.prop('disabled', true);

        $.ajax({
            url: '/admin/organizer/review/api/reject',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ id: currentOrganizerId }),
            success: function (res) {
                if (res.success) {
                    alert('申請已駁回');
                    $('#applicationReviewModal').modal('hide');
                    loadOrganizerReviews();
                } else {
                    alert('操作失敗：' + res.message);
                }
                btn.prop('disabled', false);
            },
            error: function () {
                alert('系統錯誤');
                btn.prop('disabled', false);
            }
        });
    });

    function loadOrganizerReviews() {
        const $list = $('#organizerReviewList');
        if ($list.length === 0) return;

        $list.html('<div class="col-12 text-center py-5"><div class="spinner-border text-primary"></div><p class="mt-2 text-muted">載入中...</p></div>');

        $.get('/admin/organizer/review/api/list', { status: currentStatus }, function (data) {
            allData = data || [];
            displayCurrentPage();

            // 如果當前正在看待審核列表，直接用這份數據更新全站標籤
            if (currentStatus === 0) {
                updateBadgeDisplays(allData.length);
            }
        }).fail(function () {
            $list.html('<div class="col-12 text-center py-5 text-danger">載入失敗，請重新整理頁面</div>');
            $('#organizerPagination').empty();
        });

        // 如果當前不是看待審核，則另外去抓待審核的數量來更新標籤
        if (currentStatus !== 0) {
            updateOrganizerBadges();
        }
    }

    // 專門抓取待審核數量並更新標籤的函數
    function updateOrganizerBadges() {
        $.get('/admin/organizer/review/api/list', { status: 0 }, function (data) {
            updateBadgeDisplays(data ? data.length : 0);
        });
    }

    // 更新 DOM 上的標籤顯示
    function updateBadgeDisplays(count) {
        // 1. 側邊欄標籤
        $('#organizerReviewBadge').text(count);
        // 2. 頂部導覽列統計
        $('#navPendingCount').text(count);

        // 如果數量為 0，視需求可以隱藏標籤，這裡選擇維持顯示數字 0
    }

    function displayCurrentPage() {
        const $list = $('#organizerReviewList');
        const $pagination = $('#organizerPagination');

        $list.empty();

        if (allData.length === 0) {
            $list.html('<div class="col-12 text-center py-5 text-muted">目前沒有符合條件的資料</div>');
            $pagination.empty();
            return;
        }

        // 計算分頁
        const startIndex = (currentPage - 1) * pageSize;
        const endIndex = startIndex + pageSize;
        const pageData = allData.slice(startIndex, endIndex);

        pageData.forEach(org => {
            const date = new Date(org.createdAt).toLocaleDateString('zh-TW');

            // 標籤顏色處理
            let badgeHtml = '';
            if (currentStatus === 0) {
                badgeHtml = '<span class="badge bg-warning text-dark">待審核</span>';
            } else if (currentStatus === 1) {
                badgeHtml = '<span class="badge bg-primary">使用中</span>';
            } else if (currentStatus === 2) {
                badgeHtml = '<span class="badge bg-danger">已停權</span>';
            }

            const card = `
                <div class="col-md-6 col-lg-4">
                    <div class="card bg-dark border-secondary h-100">
                        <div class="card-header border-secondary d-flex justify-content-between">
                            ${badgeHtml}
                            <small class="text-muted">${date}</small>
                        </div>
                        <div class="card-body">
                            <h5 class="card-title text-white">${org.name}</h5>
                            <p class="card-text text-muted small">負責人：${org.ownerName}</p>
                            <p class="card-text text-white text-truncate">簡介：${org.introduction || '無'}</p>
                        </div>
                        <div class="card-footer border-secondary">
                            <button class="btn ${(currentStatus === 1 || currentStatus === 2) ? 'btn-success' : 'btn-primary'} w-100 btn-review-application" data-id="${org.organizerId}">
                                ${(currentStatus === 1 || currentStatus === 2) ? '狀態管理' : '進行審核'}
                            </button>
                        </div>
                    </div>
                </div>
            `;
            $list.append(card);
        });

        renderPagination();
    }

    function renderPagination() {
        const $pagination = $('#organizerPagination');
        $pagination.empty();

        const totalPages = Math.ceil(allData.length / pageSize);
        if (totalPages <= 1) return;

        let html = '<nav><ul class="pagination pagination-sm">';

        // 上一頁
        html += `<li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                    <a class="page-link organizer-page-link bg-dark border-secondary text-white" href="#" data-page="${currentPage - 1}">&laquo;</a>
                 </li>`;

        // 頁碼
        for (let i = 1; i <= totalPages; i++) {
            html += `<li class="page-item ${currentPage === i ? 'active' : ''}">
                        <a class="page-link organizer-page-link ${currentPage === i ? 'bg-primary border-primary' : 'bg-dark border-secondary text-white'}" href="#" data-page="${i}">${i}</a>
                     </li>`;
        }

        // 下一頁
        html += `<li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                    <a class="page-link organizer-page-link bg-dark border-secondary text-white" href="#" data-page="${currentPage + 1}">&raquo;</a>
                 </li>`;

        html += '</ul></nav>';
        $pagination.html(html);
    }

    function renderListHeaders() {
        // 這邊可以放置回到列表頂部或調整標題顯示的邏輯
        $('#panel-organizer-reviews')[0].scrollIntoView({ behavior: 'smooth' });
    }

    function openReviewModal(id) {
        currentOrganizerId = id;
        $('#checkReg, #checkBank').prop('checked', false).prop('disabled', false); // Reset states

        // 重置為載入中狀態
        let loadingTitle = '申請審核 - 載入中...';
        if (currentStatus === 1) loadingTitle = '管理主辦方 - 載入中...';
        if (currentStatus === 2) loadingTitle = '主辦停權中 - 載入中...';
        $('#modalOrganizerTitle').text(loadingTitle);

        $.get('/admin/organizer/review/api/' + id, function (org) {
            let titlePrefix = '申請審核';
            if (currentStatus === 1) titlePrefix = '管理主辦方';
            if (currentStatus === 2) titlePrefix = '主辦停權中';

            $('#modalOrganizerTitle').text(titlePrefix + ' - ' + org.name);
            $('#reviewOwnerName').text(org.ownerName);
            $('#reviewAccount').text(org.account);
            $('#reviewPhone').text(org.phone);
            $('#reviewEmail').text(org.email);
            $('#reviewIntroduction').text(org.introduction || '無簡介');
            $('#reviewBankCode').text(org.bankCode);
            $('#reviewAccountName').text(org.accountName);
            $('#reviewBankAccount').text(org.bankAccount);

            // 根據狀態調整 UI
            const isReadonly = (currentStatus === 1 || currentStatus === 2);
            $('#checkReg, #checkBank').prop('checked', isReadonly).prop('disabled', isReadonly);

            // 隱藏所有操作按鈕再根據狀態顯現
            $('#btnApproveOrganizer, #btnRejectOrganizer, #btnSuspendOrganizer, #btnDeleteOrganizer, #btnUnsuspendOrganizer').hide();

            if (currentStatus === 1) {
                // 使用中狀態
                $('#btnSuspendOrganizer').show();
            } else if (currentStatus === 2) {
                // 已停權狀態
                $('#btnDeleteOrganizer, #btnUnsuspendOrganizer').show();
            } else {
                // 待審核狀態
                $('#btnApproveOrganizer, #btnRejectOrganizer').show();
            }

            $('#applicationReviewModal').modal('show');
        }).fail(function () {
            alert('載入詳情失敗');
        });
    }

    // 停權按鈕事件
    $(document).off('click', '#btnSuspendOrganizer').on('click', '#btnSuspendOrganizer', function () {
        if (!currentOrganizerId) return;

        const confirmMsg = "\n您確定要將此主辦單位「停權」嗎？\n\n※停權後該帳號將無法登入並管理活動。";
        if (!confirm(confirmMsg)) return;

        performAction('/admin/organizer/review/api/suspend', '主辦單位已停權');
    });

    // 恢復使用按鈕事件
    $(document).off('click', '#btnUnsuspendOrganizer').on('click', '#btnUnsuspendOrganizer', function () {
        if (!currentOrganizerId) return;
        if (!confirm('確定要將此主辦單位「恢復使用」嗎？')) return;
        performAction('/admin/organizer/review/api/unsuspend', '主辦單位已恢復使用');
    });

    // 註銷帳號按鈕事件
    $(document).off('click', '#btnDeleteOrganizer').on('click', '#btnDeleteOrganizer', function () {
        if (!currentOrganizerId) return;
        if (!confirm('【注意】\n確認要「永久刪除」這個主辦單位帳號嗎？\n\n※若移除，主辦單位資料將無法復原')) return;
        performAction('/admin/organizer/review/api/reject', '帳號已成功註銷並刪除');
    });

    function performAction(url, successMsg) {
        $.ajax({
            url: url,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ id: currentOrganizerId }),
            success: function (res) {
                if (res.success) {
                    alert(successMsg);
                    $('#applicationReviewModal').modal('hide');
                    loadOrganizerReviews();
                } else {
                    alert('操作失敗：' + res.message);
                }
            },
            error: function (xhr) {
                const msg = (xhr.responseJSON && xhr.responseJSON.message) ? xhr.responseJSON.message : '系統錯誤';
                alert('操作失敗：' + msg);
            }
        });
    }
}
