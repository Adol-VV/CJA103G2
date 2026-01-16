/**
 * Search Page Module
 * Handles UI for search results, filters, and keyword updates.
 * Collaborates with filter.js for UI events.
 */

import FilterManager from '../modules/filter.js';

const SearchPage = {
    init() {
        this.initQueryFromUrl();
        this.initFilters();
        this.bindEvents();
        console.log('Search Page UI Module Initialized');
    },

    initQueryFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        const query = urlParams.get('q');
        if (query) {
            $('#searchInput').val(query);
            $('#searchKeyword').text(query);
        }
    },

    initFilters() {
        // Initialize the filter module with a callback
        FilterManager.init({
            onFilterChange: (filters) => {
                this.handleFilterChange(filters);
            }
        });

        // Handle the specific 'filter chips' on the search page
        $('.filter-chip').click(function () {
            $('.filter-chip').removeClass('active');
            $(this).addClass('active');
            const type = $(this).data('type');
            SearchPage.applyTypeFilter(type);
        });
    },

    bindEvents() {
        const self = this;

        // Search Button
        $('#btnSearch').click(() => this.performSearch());

        // Enter key
        $('#searchInput').keypress((e) => {
            if (e.which === 13) this.performSearch();
        });

        // Suggestion Tags
        $('.suggestion-tag').click(function () {
            const keyword = $(this).text();
            $('#searchInput').val(keyword);
            self.performSearch();
        });

        // Load More (UI Only)
        $('#btnLoadMore').click(function () {
            $(this).prop('disabled', true).text('已顯示全部結果');
            if (window.showToast) window.showToast('已載入所有結果', 'info');
        });

        // Sort Select
        $('#sortSelect').change(function () {
            console.log('Sort changed to:', $(this).val());
            // In a real app, this would trigger loadDataFromBackend
        });
    },

    performSearch() {
        const query = $('#searchInput').val().trim();
        if (!query) return;

        // Update URL and Header
        const newUrl = window.location.pathname + '?q=' + encodeURIComponent(query);
        window.history.pushState({ path: newUrl }, '', newUrl);
        $('#searchKeyword').text(query);

        // UI Feedback: Show loading skeleton or clear results
        this.showLoading();

        // Note: Real data fetching would happen here
        console.log('Performing backend search for:', query);
    },

    applyTypeFilter(type) {
        if (type === 'all') {
            $('.result-item').show();
        } else {
            $('.result-item').hide();
            $(`.result-item[data-type="${type}"]`).show();
        }

        const visibleCount = $('.result-item:visible').length;
        $('#resultCount').text(visibleCount);

        if (visibleCount === 0) {
            $('#resultsGrid').addClass('d-none');
            $('#noResults').removeClass('d-none');
        } else {
            $('#resultsGrid').removeClass('d-none');
            $('#noResults').addClass('d-none');
        }
    },

    handleFilterChange(filters) {
        console.log('Criteria changed:', filters);
        // This is where you would call your API
    },

    showLoading() {
        // Simple visual feedback
        $('#resultsGrid').html(`
            <div class="col-12 text-center py-5">
                <div class="spinner-border text-success" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-2 text-muted">搜尋中...</p>
            </div>
        `);
    }
};

export default SearchPage;

// Auto-init when loaded
$(document).ready(() => SearchPage.init());
