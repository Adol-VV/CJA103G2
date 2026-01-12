/**
 * Home Page Logic
 * Handles Announcement UI and Search UI interactions.
 */

// Basic static data for presentation only
const mockAnnouncements = [
    { id: 1, title: '平台上線公告', content: 'Momento 平台正式上線！', date: '2024-01-01', type: 'system' },
    { id: 2, title: '春節營運公告', content: '春節期間客服暫停服務。', date: '2024-02-01', type: 'notice' },
    { id: 3, title: '新功能上線', content: '代幣功能已啟用。', date: '2024-09-01', type: 'feature' }
];

function getTypeBadge(type) {
    const badges = {
        system: '<span class="badge bg-secondary me-2">系統</span>',
        notice: '<span class="badge bg-info me-2">公告</span>',
        feature: '<span class="badge bg-success me-2">新功能</span>',
        event: '<span class="badge bg-primary me-2">活動</span>',
        promo: '<span class="badge bg-warning text-dark me-2">優惠</span>'
    };
    return badges[type] || badges.notice;
}

function loadAnnouncements() {
    // 跑馬燈
    const track = document.getElementById('announce_track');
    if (track) {
        let html = '';
        mockAnnouncements.forEach(a => {
            html += `<span class="announce_item" data-id="${a.id}" data-bs-toggle="modal" data-bs-target="#announce_modal">
                ${getTypeBadge(a.type)}${a.title}
            </span>`;
        });
        track.innerHTML = html + html;
    }

    // Modal List
    const list = document.getElementById('announce_list');
    if (list) {
        let listHtml = '';
        mockAnnouncements.forEach(a => {
            listHtml += `
                <div class="list-group-item bg-dark text-white border-secondary">
                    <div class="d-flex w-100 justify-content-between align-items-start">
                        <div>${getTypeBadge(a.type)} <strong>${a.title}</strong></div>
                        <small class="text-muted">${a.date}</small>
                    </div>
                    <p class="mb-0 mt-2 text-muted small">${a.content}</p>
                </div>
            `;
        });
        list.innerHTML = listHtml;
    }
}

function initSearch() {
    const searchModal = document.getElementById('search_modal');
    const searchInput = document.getElementById('search_input');
    const searchResults = document.getElementById('search_results');
    const searchInit = document.getElementById('search_init');

    if (searchModal) {
        searchModal.addEventListener('shown.bs.modal', () => {
            if (searchInput) searchInput.focus();
        });
    }

    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            const query = e.target.value.trim();
            if (query.length > 0) {
                if (searchInit) searchInit.classList.add('d-none');
                if (searchResults) {
                    searchResults.classList.remove('d-none');
                    // Mock search results for UI demo
                    searchResults.innerHTML = `
                        <div class="p-3 text-center text-muted">
                            <p>搜尋功能將由後端處理</p>
                            <p>搜尋關鍵字: ${query}</p>
                        </div>
                    `;
                }
            } else {
                if (searchInit) searchInit.classList.remove('d-none');
                if (searchResults) searchResults.classList.add('d-none');
            }
        });

        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                // Navigation only
                const val = searchInput.value.trim();
                if (val) window.location.href = 'pages/public/search.html?q=' + encodeURIComponent(val);
            }
        });
    }

    document.querySelectorAll('.search_tag').forEach(tag => {
        tag.addEventListener('click', () => {
            const val = tag.textContent.trim();
            if (val) window.location.href = 'pages/public/search.html?q=' + encodeURIComponent(val);
        });
    });
}

document.addEventListener('DOMContentLoaded', () => {
    loadAnnouncements();
    initSearch();
});
