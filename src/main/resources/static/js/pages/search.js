/**
 * Search Page Module (Upgraded)
 * Handles data fetching and UI rendering for the search results page.
 */

const SearchPage = {
    init() {
        this.query = this.getInitialQuery();
        this.bindEvents();

        if (this.query) {
            this.performSearch(this.query);
        } else {
            this.showNoResults();
        }
        console.log('Premium Search Page Initialized');
    },

    getInitialQuery() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('q') || window.initialKeyword || '';
    },

    bindEvents() {
        const self = this;

        // Search Button Click
        $('#btnSearch').click(() => {
            const query = $('#searchInput').val().trim();
            if (query) this.performSearch(query);
        });

        // Enter Key
        $('#searchInput').on('keypress', (e) => {
            if (e.which === 13) {
                const query = $('#searchInput').val().trim();
                if (query) this.performSearch(query);
            }
        });

        // Filter Chips
        $('.filter-chip').on('click', function () {
            $('.filter-chip').removeClass('active');
            $(this).addClass('active');
            const type = $(this).data('type');
            self.applyFilter(type);
        });

        // Suggestion Tags
        $('.suggestion-tag').on('click', function () {
            const keyword = $(this).text().trim();
            $('#searchInput').val(keyword);
            self.performSearch(keyword);
        });
    },

    async performSearch(query) {
        if (!query) return;

        // Update UI & URL
        $('#searchKeyword').text(query);
        $('#searchInput').val(query);
        const newUrl = `${window.location.pathname}?q=${encodeURIComponent(query)}`;
        window.history.pushState({ path: newUrl }, '', newUrl);

        this.showLoading();

        try {
            // Parallel fetching
            const [eventRes, prodRes, orgRes] = await Promise.all([
                fetch(`/event/api/search?keyword=${encodeURIComponent(query)}&size=20`),
                fetch(`/prod/api/search?keyword=${encodeURIComponent(query)}`),
                fetch(`/orginformation/api/search?keyword=${encodeURIComponent(query)}`)
            ]);

            const eventData = eventRes.ok ? await eventRes.json() : { content: [] };
            const prodData = prodRes.ok ? await prodRes.json() : [];
            const orgData = orgRes.ok ? await orgRes.json() : [];

            this.results = {
                events: Array.isArray(eventData.content) ? eventData.content : [],
                products: Array.isArray(prodData) ? prodData : [],
                organizers: Array.isArray(orgData) ? orgData : []
            };

            this.renderResults();

        } catch (err) {
            console.error('Search Fetch Failed:', err);
            $('#resultsGrid').html('<div class="col-12 text-center py-5 text-danger">搜尋過程發生錯誤，請稍後再試。</div>');
        }
    },

    renderResults() {
        const grid = $('#resultsGrid');
        grid.empty();

        const total = this.results.events.length + this.results.products.length + this.results.organizers.length;
        $('#resultCount').text(total);

        if (total === 0) {
            this.showNoResults();
            return;
        }

        $('#noResults').addClass('d-none');
        grid.removeClass('d-none');

        // Render Events
        this.results.events.forEach(item => grid.append(this.createEventCard(item)));

        // Render Products
        this.results.products.forEach(item => grid.append(this.createProdCard(item)));

        // Render Organizers
        this.results.organizers.forEach(item => grid.append(this.createOrgCard(item)));

        // Reveal animation
        grid.children().each((i, el) => {
            $(el).css({ opacity: 0, transform: 'translateY(20px)' });
            setTimeout(() => {
                $(el).animate({ opacity: 1, transform: 'translateY(0)' }, 400);
            }, i * 50);
        });
    },

    createEventCard(e) {
        const date = e.eventStartAt ? new Date(e.eventStartAt).toLocaleDateString('zh-TW') : '時間待定';
        return `
            <div class="col-md-6 col-lg-4 result-item" data-type="event">
                <div class="card result-card h-100 border-0">
                    <div class="position-relative">
                        <span class="badge bg-primary result-type-badge">活動 · ${e.typeName}</span>
                        <img src="${e.coverImageUrl || '/api/placeholder/400/200'}" class="card-img-top" style="height: 200px; object-fit: cover;">
                    </div>
                    <div class="card-body p-4">
                        <h5 class="card-title text-white mb-3 text-truncate-2">${e.title}</h5>
                        <p class="text-muted small mb-3">
                            <i class="far fa-calendar-alt me-2"></i>${date}
                            <i class="fas fa-map-marker-alt ms-3 me-2"></i>${e.place}
                        </p>
                        <div class="d-flex justify-content-between align-items-center mt-auto">
                            <span class="text-success fw-bold fs-5">NT$ ${e.minPrice || 0} 起</span>
                            <a href="/event/${e.eventId}" class="btn btn-sm btn-outline-success rounded-pill px-3">查看詳情</a>
                        </div>
                    </div>
                </div>
            </div>
        `;
    },

    createProdCard(p) {
        return `
            <div class="col-md-6 col-lg-4 result-item" data-type="product">
                <div class="card result-card h-100 border-0">
                    <div class="position-relative">
                        <span class="badge bg-warning text-dark result-type-badge">商品 · ${p.sortName || '熱銷'}</span>
                        <img src="${p.mainImageUrl || '/api/placeholder/400/200'}" class="card-img-top" style="height: 200px; object-fit: cover;">
                    </div>
                    <div class="card-body p-4">
                        <h5 class="card-title text-white mb-3 text-truncate-2">${p.prodName}</h5>
                        <p class="text-muted small mb-3">
                            <i class="fas fa-store me-2"></i>品牌週邊
                        </p>
                        <div class="d-flex justify-content-between align-items-center mt-auto">
                            <span class="text-warning fw-bold fs-5">NT$ ${p.prodPrice || 0}</span>
                            <a href="/prod/getOne_For_Display?prodId=${p.prodId}" class="btn btn-sm btn-outline-warning rounded-pill px-3">查看商品</a>
                        </div>
                    </div>
                </div>
            </div>
        `;
    },

    createOrgCard(o) {
        return `
            <div class="col-md-6 col-lg-4 result-item" data-type="organizer">
                <div class="card result-card h-100 border-0 text-center py-5 px-4">
                    <div class="d-flex justify-content-center mb-4">
                        <div class="rounded-circle p-1" style="border: 2px solid var(--momento-green);">
                            <img src="https://ui-avatars.com/api/?name=${encodeURIComponent(o.name)}&background=10B981&color=fff&size=128" 
                                class="rounded-circle" style="width: 100px; height: 100px; object-fit: cover;">
                        </div>
                    </div>
                    <span class="badge bg-success bg-opacity-20 text-success rounded-pill mb-3" style="width: fit-content; margin: 0 auto;">主辦官方</span>
                    <h5 class="card-title text-white mb-3">${o.name}</h5>
                    <p class="text-muted small mb-4 text-truncate-3">${o.introduction || '致力於提供高品質的藝文體驗。'}</p>
                    <a href="/orginformation/${o.organizerId}" class="btn btn-success rounded-pill px-5 mt-auto shadow-lg">進入主辦專頁</a>
                </div>
            </div>
        `;
    },

    applyFilter(type) {
        if (type === 'all') {
            $('.result-item').fadeIn(300);
        } else {
            $('.result-item').hide();
            $(`.result-item[data-type="${type}"]`).fadeIn(300);
        }

        const count = type === 'all'
            ? $('#resultsGrid').children().length
            : $(`.result-item[data-type="${type}"]`).length;

        $('#resultCount').text(count);

        if (count === 0) $('#noResults').removeClass('d-none');
        else $('#noResults').addClass('d-none');
    },

    showLoading() {
        $('#noResults').addClass('d-none');
        $('#resultsGrid').html(`
            <div class="col-12 text-center py-5">
                <div class="spinner-border text-success" style="width: 3rem; height: 3rem;" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-4 text-muted fs-5">正在為您搜尋精彩內容...</p>
            </div>
        `);
    },

    showNoResults() {
        $('#resultsGrid').addClass('d-none');
        $('#noResults').removeClass('d-none');
        $('#resultCount').text('0');
    }
};

$(document).ready(() => SearchPage.init());
