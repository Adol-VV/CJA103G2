export function initFaqManagement() {
    if (document.getElementById('faqSortableList')) {
        new Sortable(document.getElementById('faqSortableList'), { handle: '.fa-grip-vertical', animation: 150 });
    }
}
