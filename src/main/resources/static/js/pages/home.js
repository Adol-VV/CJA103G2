/**
 * Home Page Logic
 * Handles Announcement UI and Search UI interactions.
 */

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

async function loadAnnouncements() {
    const track = document.getElementById('announce_track');
    const list = document.getElementById('announce_list');

    try {
        // 從後端 API 取得公告資料
        const response = await fetch('/announcement/api/list');
        const announcements = await response.json();

        // 跑馬燈
        if (track) {
            if (announcements.length === 0) {
                track.innerHTML = '<span class="announce_item text-muted">目前暫無公告</span>';
            } else {
                let html = '';
                announcements.forEach(a => {
                    html += '<span class="announce_item" data-id="' + a.id + '" data-bs-toggle="modal" data-bs-target="#announce_modal">' + getTypeBadge(a.type) + escapeHtml(a.title) + '</span>';
                });
                track.innerHTML = html + html; // 複製一份讓跑馬燈連續滾動
            }
        }

        // Modal 公告列表
        if (list) {
            if (announcements.length === 0) {
                list.innerHTML = '<div class="list-group-item bg-dark text-white border-secondary text-center text-muted py-4">目前暫無公告</div>';
            } else {
                let listHtml = '';
                announcements.forEach(a => {
                    listHtml += `
                        <div class="list-group-item bg-dark text-white border-secondary">
                            <div class="d-flex w-100 justify-content-between align-items-start">
                                <div>${getTypeBadge(a.type)} <strong>${escapeHtml(a.title)}</strong></div>
                                <small class="text-muted">${a.date}</small>
                            </div>
                            <p class="mb-0 mt-2 text-muted small">${escapeHtml(a.content)}</p>
                        </div>
                    `;
                });
                list.innerHTML = listHtml;
            }
        }
    } catch (err) {
        console.error('Failed to load announcements:', err);
        // 載入失敗時顯示提示
        if (track) {
            track.innerHTML = '<span class="announce_item text-muted">公告載入中...</span>';
        }
        if (list) {
            list.innerHTML = '<div class="list-group-item bg-dark text-white border-secondary text-center text-danger py-4">公告載入失敗</div>';
        }
    }
}

// HTML 跳脫函式，防止 XSS
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function initSearch() {
    const searchModal = document.getElementById('search_modal');
    const searchInput = document.getElementById('search_input');
    const searchResults = document.getElementById('search_results');
    const searchInit = document.getElementById('search_init');
    const searchIcon = document.querySelector('#search_modal .fa-search');

    if (searchModal) {
        searchModal.addEventListener('shown.bs.modal', () => {
            if (searchInput) searchInput.focus();
            loadPopularSuggestions();
            loadRecentSearches();
        });
    }

    let searchTimeout = null;

    // --- Popular Suggestions ---
    async function loadPopularSuggestions() {
        const container = document.getElementById('popular_tags');
        if (!container) return;

        try {
            const res = await fetch('/api/search-suggestion/popular');
            const data = await res.json();

            if (data.length > 0) {
                container.innerHTML = data.map(tag => {
                    const displayTag = tag.length > 12 ? tag.substring(0, 12) + '...' : tag;
                    return `<span class="badge bg-secondary bg-opacity-10 text-white border border-secondary p-2 cursor-pointer search_tag" title="${tag}">${displayTag}</span>`;
                }).join('');

                // Re-bind clicks
                container.querySelectorAll('.search_tag').forEach(tagSpan => {
                    tagSpan.addEventListener('click', () => {
                        searchInput.value = tagSpan.getAttribute('title') || tagSpan.textContent.trim();
                        performSearch();
                    });
                });
            } else {
                container.innerHTML = '<span class="text-muted small">暫無建議</span>';
            }
        } catch (err) {
            console.error('Failed to load popular suggestions:', err);
            container.innerHTML = '';
        }
    }

    // --- Recent Searches (LocalStorage) ---
    function saveRecentSearch(keyword) {
        if (!keyword) return;
        const trimmed = keyword.trim();
        if (!trimmed) return;

        let history = JSON.parse(localStorage.getItem('momento_search_history') || '[]');
        history = history.filter(h => h !== trimmed);
        history.unshift(trimmed);
        history = history.slice(0, 3); // Keep only 3
        localStorage.setItem('momento_search_history', JSON.stringify(history));
    }

    function loadRecentSearches() {
        const historyContainer = document.getElementById('recent_searches');
        if (!historyContainer) return;

        const history = JSON.parse(localStorage.getItem('momento_search_history') || '[]');
        if (history.length === 0) {
            historyContainer.innerHTML = '<div class="text-muted small px-2">暫無搜尋紀錄</div>';
            return;
        }

        historyContainer.innerHTML = history.map(h => `
            <a href="javascript:void(0)" class="list-group-item list-group-item-action bg-transparent text-white border-0 px-0 recent-search-item">
                <i class="far fa-clock text-muted me-2"></i> ${h}
            </a>
        `).join('');

        historyContainer.querySelectorAll('.recent-search-item').forEach(item => {
            item.addEventListener('click', () => {
                searchInput.value = item.textContent.trim();
                performSearch();
            });
        });
    }

    async function performSearch() {
        const query = searchInput.value.trim();
        if (!query) {
            if (searchInit) searchInit.classList.remove('d-none');
            if (searchResults) searchResults.classList.add('d-none');
            return;
        }

        if (searchInit) searchInit.classList.add('d-none');
        if (searchResults) {
            searchResults.classList.remove('d-none');
            searchResults.innerHTML = '<div class="p-4 text-center"><div class="spinner-border spinner-border-sm text-success"></div> <span class="ms-2 text-muted">搜尋中...</span></div>';
        }

        try {
            const [eventRes, prodRes, orgRes] = await Promise.all([
                fetch(`/event/api/search?keyword=${encodeURIComponent(query)}&size=5`),
                fetch(`/prod/api/search?keyword=${encodeURIComponent(query)}`),
                fetch(`/orginformation/api/search?keyword=${encodeURIComponent(query)}`)
            ]);

            const eventData = eventRes.ok ? await eventRes.json() : null;
            const prodData = prodRes.ok ? await prodRes.json() : null;
            const orgData = orgRes.ok ? await orgRes.json() : null;

            const events = (eventData && eventData.content && Array.isArray(eventData.content)) ? eventData.content : [];
            const prods = Array.isArray(prodData) ? prodData.slice(0, 5) : [];
            const organizers = Array.isArray(orgData) ? orgData.slice(0, 3) : [];

            if (events.length === 0 && prods.length === 0 && organizers.length === 0) {
                searchResults.innerHTML = `
                    <div class="p-5 text-center text-muted">
                        <i class="fas fa-search-minus mb-3 fa-2x"></i>
                        <p>找不到與「${query}」相關的內容</p>
                    </div>
                `;
                return;
            }

            let html = '<div class="list-group list-group-flush">';

            // --- 主辦方 Section ---
            if (organizers.length > 0) {
                html += '<div class="px-3 py-2 bg-secondary bg-opacity-10 small text-muted text-uppercase fw-bold">主辦方</div>';
                organizers.forEach(o => {
                    html += `
                        <a href="/orginformation/${o.organizerId}" class="list-group-item list-group-item-action bg-transparent border-0 text-white d-flex align-items-center gap-3 py-2 search-result-item">
                            <div class="rounded-circle bg-success bg-opacity-20 d-flex align-items-center justify-content-center" style="width: 40px; height: 40px;">
                                <i class="fas fa-building text-success"></i>
                            </div>
                            <div>
                                <div class="fw-bold">${o.name}</div>
                                <div class="small text-muted text-truncate" style="max-width: 250px;">${o.introduction || '主辦方介紹'}</div>
                            </div>
                        </a>
                    `;
                });
            }

            // --- 活動 Section ---
            if (events.length > 0) {
                html += '<div class="px-3 py-2 bg-secondary bg-opacity-10 small text-muted text-uppercase fw-bold mt-2">活動</div>';
                events.forEach(e => {
                    html += `
                        <a href="/event/${e.eventId}" class="list-group-item list-group-item-action bg-transparent border-0 text-white d-flex align-items-center gap-3 py-2 search-result-item">
                            <img src="${e.coverImageUrl || 'https://picsum.photos/50/50'}" class="rounded" style="width: 40px; height: 40px; object-fit: cover;">
                            <div>
                                <div class="fw-bold">${e.title}</div>
                                <div class="small text-muted">${e.place} · ${e.typeName}</div>
                            </div>
                        </a>
                    `;
                });
            }

            // --- 商品 Section ---
            if (prods.length > 0) {
                html += '<div class="px-3 py-2 bg-secondary bg-opacity-10 small text-muted text-uppercase fw-bold mt-2">商品</div>';
                prods.forEach(p => {
                    html += `
                        <a href="/prod/getOne_For_Display?prodId=${p.prodId}" class="list-group-item list-group-item-action bg-transparent border-0 text-white d-flex align-items-center gap-3 py-2 search-result-item">
                            <img src="${p.mainImageUrl || 'https://picsum.photos/50/50'}" class="rounded" style="width: 40px; height: 40px; object-fit: cover;">
                            <div>
                                <div class="fw-bold">${p.prodName}</div>
                                <div class="small text-muted">NT$ ${p.prodPrice} · ${p.sortName}</div>
                            </div>
                        </a>
                    `;
                });
            }
            html += '</div>';
            searchResults.innerHTML = html;

            // Bind click to save history
            searchResults.querySelectorAll('.search-result-item').forEach(item => {
                item.addEventListener('click', () => saveRecentSearch(query));
            });

        } catch (err) {
            console.error('Search failed:', err);
            searchResults.innerHTML = '<div class="p-4 text-center text-danger">搜尋出錯，請稍後再試</div>';
        }
    }

    function navigateToFirst() {
        const query = searchInput.value.trim();
        const first = searchResults.querySelector('.search-result-item');
        if (first) {
            saveRecentSearch(query);
            window.location.href = first.href;
        }
    }

    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(performSearch, 300);
        });

        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                navigateToFirst();
            }
        });
    }

    if (searchIcon) {
        searchIcon.parentElement.style.cursor = 'pointer';
        searchIcon.parentElement.addEventListener('click', navigateToFirst);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadAnnouncements();
    initSearch();
});
