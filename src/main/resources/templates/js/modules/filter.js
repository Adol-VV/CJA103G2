/**
 * Filter & Sort UI Module
 * Handles UI event bindings for filter/sort inputs.
 * Does NOT perform actual data filtering (backend responsibility).
 */

const FilterSortUI = {
    /**
     * Bind filter change events
     * @param {Function} onChangeCallback - Called with current filter state
     */
    initFilterEvents: function (onChangeCallback) {
        const ids = [
            'filterCategory', 'filterRegion',
            'filterPriceMin', 'filterPriceMax',
            'filterDateStart', 'filterDateEnd',
            'filterKeyword'
        ];

        ids.forEach(id => {
            const el = document.getElementById(id);
            if (el) {
                el.addEventListener('change', () => {
                    const filters = this.getFilterState();
                    if (onChangeCallback) onChangeCallback(filters);
                });
            }
        });

        // Keyword search debounce could be handled here or by consumer
        const keyword = document.getElementById('filterKeyword');
        if (keyword) {
            keyword.addEventListener('input', (e) => {
                // Simple debounce or just pass through
                const filters = this.getFilterState();
                if (onChangeCallback) onChangeCallback(filters);
            });
        }
    },

    getFilterState: function () {
        return {
            category: document.getElementById('filterCategory')?.value,
            region: document.getElementById('filterRegion')?.value,
            priceMin: document.getElementById('filterPriceMin')?.value,
            priceMax: document.getElementById('filterPriceMax')?.value,
            dateStart: document.getElementById('filterDateStart')?.value,
            dateEnd: document.getElementById('filterDateEnd')?.value,
            keyword: document.getElementById('filterKeyword')?.value
        };
    },

    /**
     * Bind sort change events
     */
    initSortEvents: function (onChangeCallback) {
        const sortSelect = document.getElementById('productSort');
        if (sortSelect) {
            sortSelect.addEventListener('change', () => {
                const value = sortSelect.value;
                if (onChangeCallback) onChangeCallback(value);
            });
        }
    }
};

export default FilterSortUI;
