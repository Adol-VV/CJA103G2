/**
 * UI 控制模組
 * 處理 Sidebar 導航與畫面切換
 */

export function initSidebarNavigation() {
    // Sidebar Navigation (Desktop & Mobile)
    // Delegate event to handle dynamically loaded content or existing navs
    $(document).on('click', '.nav-link[data-section], .mobile-nav[data-section]', function (e) {
        e.preventDefault();
        const section = $(this).data('section');
        showSection(section);

        // Close mobile offcanvas if open
        // Check if bootstrap is defined (it should be loaded globally)
        if (typeof bootstrap !== 'undefined') {
            const offcanvasEl = document.getElementById('sidebarOffcanvas');
            if (offcanvasEl) {
                const offcanvas = bootstrap.Offcanvas.getInstance(offcanvasEl);
                if (offcanvas) offcanvas.hide();
            }
        }
    });
}

export function showSection(sectionId) {
    if (!sectionId) return;

    // Switch content panel
    $('.content-panel').removeClass('active');
    $('#panel-' + sectionId).addClass('active');

    // Update sidebar active state
    $('.nav-link, .mobile-nav').removeClass('active');
    $(`.nav-link[data-section="${sectionId}"], .mobile-nav[data-section="${sectionId}"]`).addClass('active');
}


/**
 * 初始化頂端小鈴鐺
 */
export function initNotificationBell() {
    // 點擊鈴鐺時載入未讀清單，解決「載入中...」卡住問題
    $('#bellDropdown').on('show.bs.dropdown', function () {
        const $list = $('#bellNotificationList');
        // $list.html('<div class="text-center p-3"><i class="fas fa-spinner fa-spin"></i> 載入中...</div>');

        // 對接 Controller: /organizer/notify/list
        $.post('/organizer/notify/list', function (res) {
            if (res.success && res.notifications && res.notifications.length > 0) {
                let html = '';
                // 篩選未讀的通知顯示在鈴鐺內
                const unreads = res.notifications.filter(n => n.isRead === 0).slice(0, 5);

                if (unreads.length === 0) {
                    $list.html('<li class="text-center py-4 text-muted small">暫無新通知</li>');
                    return;
                }

                unreads.forEach(item => {
                    html += `
                        <li class="dropdown-item d-flex justify-content-between align-items-center py-2 border-bottom" style="white-space: normal;">
                            <div class="small text-white-50 text-truncate me-2" style="max-width: 200px;">${item.title}</div>
                            <button class="btn btn-sm btn-link text-primary p-0 btn-bell-read" data-id="${item.organizerNotifyId}">
                                <i class="fas fa-check"></i>
                            </button>
                        </li>`;
                });
                $list.html(html);
            } else {
                $list.html('<li class="text-center py-4 text-muted small">暫無新通知</li>');
            }
        }).fail(function (err) {
            $list.html('<div class="p-3 text-center text-danger">連線失敗</div>');
        });
    });

    // 鈴鐺內的單則已讀功能
    $(document).on('click', '.btn-bell-read', function (e) {
        e.stopPropagation(); // 防止下拉選單關閉
        const notifyId = $(this).data('id');
        const $item = $(this).closest('li');

        $.post('/organizer/notify/markAsRead', { notifyId: notifyId }, function (res) {
            if (res.success) {
                $item.fadeOut(200, function () {
                    $(this).remove();
                    // 同步更新全域紅點數字，防止重整跳回
                    updateGlobalNotificationBadge();
                });
            }
        });
    });

    // 全部標記已讀按鈕
    $(document).on('click', '#btnMarkAllRead', function (e) {
        e.stopPropagation();
        $.post('/organizer/notify/markAllAsRead', function (res) {
            if (res.success) {
                $('#bellNotificationList').html('<li class="text-center py-4 text-muted small">所有通知已讀取</li>');
                updateGlobalNotificationBadge();
            }
        });
    });
}

/**
 * 重新計算並更新全域通知紅點 (包含側邊欄與頂端鈴鐺)
 */
export function updateGlobalNotificationBadge() {
    $.post('/organizer/notify/list', function(res) {
        if (res.success) {
            const count = res.unreadCount;
            const $badge = $('#bellBadge, .sidebar-notify-badge');

            if (count > 0) {
                $badge.text(count).show();
            } else {
                $badge.fadeOut();
            }
        }
    });
}