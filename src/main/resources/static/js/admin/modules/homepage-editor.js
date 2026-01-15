export function initHomepageEditor() {
    if (document.getElementById('announcementList')) {
        new Sortable(document.getElementById('announcementList'), { handle: '.drag-handle', animation: 150 });
    }
    $(document).on('click', '#btnAddAnnouncement', () => $('#announcementModal').modal('show'));
    $(document).on('click', '#btnSelectFeatured', () => $('#featuredSelectorModal').modal('show'));
}
