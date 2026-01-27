// Initialize generic homepage editor components
export function initHomepageEditor() {
    if (document.getElementById('announcementList')) {
        new Sortable(document.getElementById('announcementList'), { handle: '.drag-handle', animation: 150 });
    }

    $(document).on('click', '#btnAddAnnouncement', () => $('#announcementModal').modal('show'));
    $(document).on('click', '#btnSelectFeatured', () => $('#featuredSelectorModal').modal('show'));

    // Load events when the selector modal is shown
    const featuredModal = document.getElementById('featuredSelectorModal');
    if (featuredModal) {
        featuredModal.addEventListener('show.bs.modal', function () {
            loadEventsForSelector();
        });
    }

    // Load featured events when the tab is shown
    const featuredTabBtn = document.querySelector('button[data-bs-target="#tab-featured"]');
    if (featuredTabBtn) {
        featuredTabBtn.addEventListener('shown.bs.tab', function () {
            loadFeaturedEvents();
        });

        // Initial load if tab is active
        if (featuredTabBtn.classList.contains('active')) {
            loadFeaturedEvents();
        }
    }
}

// State for selected event in modal
let selectedEventForFeature = null;

// Fetch and display events in the selector modal
function loadEventsForSelector() {
    selectedEventForFeature = null; // Reset selection
    const container = $('#featuredEventSelectorContainer');
    container.html('<div class="text-center w-100 py-5"><div class="spinner-border text-light" role="status"></div></div>');

    $.ajax({
        url: '/event/api/list',
        method: 'GET',
        data: {
            page: 0,
            size: 100, // Fetch top 100 events
            sort: 'newest'
        },
        success: function (response) {
            container.empty();
            const events = response.content;

            if (events && events.length > 0) {
                events.forEach((event) => {
                    const eventHtml = `
                        <div class="col-md-3">
                            <div class="card bg-dark border-secondary selector-item h-100" style="cursor: pointer;" 
                                 data-id="${event.eventId}" data-organizer-id="${event.organizerId}">
                                <div class="position-relative">
                                    <img loading="lazy" src="${event.coverImageUrl || 'https://placehold.co/300x200?text=No+Image'}" 
                                         class="card-img-top" style="height: 120px; object-fit: cover;">
                                </div>
                                <div class="card-body p-2">
                                    <h6 class="small mb-0 text-white text-truncate" title="${event.title}">${event.title}</h6>
                                    <small class="text-muted">${event.place || '線上活動'}</small>
                                </div>
                            </div>
                        </div>
                    `;
                    container.append(eventHtml);
                });

                // Bind click event for selection
                $('.selector-item').on('click', function () {
                    $('.selector-item').removeClass('border-success').css('border-color', '');
                    $('.selector-item .check-mark').remove();

                    $(this).addClass('border-success').css('border-color', '#198754 !important');
                    $(this).find('.position-relative').append(`
                        <span class="check-mark position-absolute top-0 end-0 m-1 text-success">
                            <i class="fas fa-check-circle bg-dark rounded-circle"></i>
                        </span>
                    `);

                    // Store selection
                    selectedEventForFeature = {
                        eventId: $(this).data('id'),
                        organizerId: $(this).data('organizer-id')
                    };
                });

            } else {
                container.html('<div class="text-center w-100 text-muted">暫無活動</div>');
            }
        },
        error: function (err) {
            console.error('Error fetching events:', err);
            container.html('<div class="text-center w-100 text-danger">無法載入活動列表</div>');
        }
    });

    // Unbind previous handler to avoid duplicates (safeguard)
    $('#btnConfirmFeaturedSelection').off('click').on('click', function () {
        if (!selectedEventForFeature) {
            alert('請選擇一個活動');
            // Prevent modal close if needed, but data-bs-dismiss handles it. 
            // We return here, effectively letting the modal close if user clicked the button.
            // But if we want to stop it, we should stopPropagation if it wasn't already dismissed.
            // Since data-bs-dismiss is attribute-driven, we can't easily stop it in JS handler unless we remove the attribute.
            // For now, simple alert is fine.
            return;
        }

        // Prepare Payload
        const payload = {
            eventVO: {
                eventId: selectedEventForFeature.eventId
            },
            organizerVO: {
                organizerId: selectedEventForFeature.organizerId
            },
            startedAt: new Date().toISOString(), // Default: Now
            endedAt: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString() // Default: +30 Days
        };

        $.ajax({
            url: '/featured',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(payload),
            success: function (response) {
                // Refresh list
                loadFeaturedEvents();
                // Reset selection
                selectedEventForFeature = null;
                // Modal will be closed automatically by Bootstrap data-bs-dismiss
            },
            error: function (xhr) {
                console.error('Add featured failed:', xhr);
                alert('新增主打活動失敗:' + (xhr.status === 400 ? ' 資料驗證錯誤' : ' 系統錯誤'));
            }
        });
    });
}

// Fetch and display currently featured events
function loadFeaturedEvents() {
    const container = $('#featuredList');
    container.html('<div class="text-center w-100 py-5"><div class="spinner-border text-light" role="status"></div></div>');

    $.ajax({
        url: '/featured/api/list',
        method: 'GET',
        success: function (response) {
            container.empty();
            if (response && response.length > 0) {
                response.forEach((featured, index) => {
                    const html = `
                        <div class="col-md-4">
                            <div class="card featured-item bg-dark border-secondary h-100">
                                <div class="position-relative">
                                    <img loading="lazy" src="${featured.imageUrl || 'https://placehold.co/300x200?text=No+Image'}"
                                        class="card-img-top" style="height:150px; object-fit:cover;">
                                    <span class="badge bg-danger position-absolute top-0 start-0 m-2">主打 #${index + 1}</span>
                                    <button class="btn btn-sm btn-danger position-absolute top-0 end-0 m-2" onclick="deleteFeatured(${featured.featuredId})">
                                        <i class="fas fa-times"></i>
                                    </button>
                                </div>
                                <div class="card-body">
                                    <h6 class="text-white text-truncate" title="${featured.title}">${featured.title}</h6>
                                </div>
                            </div>
                        </div>
                    `;
                    container.append(html);
                });
            } else {
                container.html('<div class="text-center w-100 text-muted">目前沒有主打活動</div>');
            }
        },
        error: function (err) {
            console.error('Error fetching featured events:', err);
            container.html('<div class="text-center w-100 text-danger">無法載入主打活動</div>');
        }
    });
}

// Global function to delete featured event (exposed to window for onclick)
window.deleteFeatured = function (id) {
    if (!confirm('確定要移除此主打活動嗎？')) return;

    $.ajax({
        url: '/featured/' + id,
        method: 'DELETE',
        success: function () {
            // Show toast or alert? for now just reload
            // alert('已移除');
            loadFeaturedEvents();
        },
        error: function (err) {
            alert('移除失敗');
            console.error(err);
        }
    });
};
