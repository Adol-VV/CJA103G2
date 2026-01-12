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
