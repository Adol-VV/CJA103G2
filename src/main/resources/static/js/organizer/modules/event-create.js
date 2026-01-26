export function initEventCreate() {
    console.log('initEventCreate: Start');
    // alert('Debug: 正在載入活動建立模組...');

    if (window.EVENT_CREATE_INITIALIZED) {
        console.log('initEventCreate: Already initialized, skipping binding');
        if (window.loadActivityTypes) window.loadActivityTypes();
        return;
    }
    window.EVENT_CREATE_INITIALIZED = true;

    let uploadedBannerUrl = '';

    console.log('initEventCreate: Module Fully Initialized');

    // ========== 載入活動類型 ==========
    function loadActivityTypes() {
        console.log('loadActivityTypes: Fetching from API...');
        const $select = $('#eventType');
        if (!$select.length) {
            console.error('loadActivityTypes: #eventType element NOT found!');
            return;
        }

        $select.html('<option value="">讀取中...</option>');

        $.ajax({
            url: '/organizer/event/api/types?_t=' + Date.now(),
            type: 'GET',
            dataType: 'json',
            success: function (types) {
                console.log('loadActivityTypes: Received', types);
                if (!types || types.length === 0) {
                    $select.html('<option value="">無類別資料 (請檢查資料庫)</option>');
                    return;
                }
                $select.html('<option value="">請選擇</option>');
                types.forEach(type => {
                    $select.append(`<option value="${type.typeId}">${type.typeName}</option>`);
                });
            },
            error: function (xhr, status, err) {
                console.error('loadActivityTypes: AJAX Error', { status, err, response: xhr.responseText });
                $select.html('<option value="">載入失敗 (錯誤: ' + status + ')</option>');
            }
        });
    }

    // Expose globally for the refresh button
    window.loadActivityTypes = loadActivityTypes;

    // Initial Load
    loadActivityTypes();

    // Listen for sidebar navigation to reset to "Create" mode
    $(document).on('click', '[data-section="event-create"]', function (e) {
        // If the click didn't come from the "Edit" button logic, reset to empty
        if (!e.originalEvent || !$(e.target).closest('[data-action="edit-event"]').length) {
            console.log('Sidebar Navigation: Resetting Editor to Create Mode');
            if (window.openEventEditor) window.openEventEditor(null);
        }
    });

    /**
     * 載入編輯資料
     */
    async function loadEventData(eventId) {
        if (!eventId) return;

        console.log('loadEventData: Loading event', eventId);
        $('#editEventId').val(eventId);
        $('#editorTitle').text('編輯活動');
        $('#btnCancelEdit').removeClass('d-none');

        try {
            const response = await $.ajax({
                url: `/organizer/event/api/${eventId}`,
                method: 'GET'
            });

            console.log('loadEventData: Success', response);
            const { event, tickets, images } = response;

            // 填寫基本資訊
            $('[name="eventName"]').val(event.title);
            $('[name="eventVenue"]').val(event.place);
            $('#eventType').val(event.type?.typeId || '');
            $('#eventContent').val(event.content);

            // 填寫時間 (格式化為 datetime-local 要求的 YYYY-MM-DDTHH:mm)
            if (event.eventAt) $('#eventDateTime').val(event.eventAt.substring(0, 16));
            if (event.startedAt) $('#saleStart').val(event.startedAt.substring(0, 16));
            if (event.endedAt) $('#saleEnd').val(event.endedAt.substring(0, 16));

            // 處理圖片
            if (images && images.length > 0) {
                uploadedBannerUrl = images[0].imageUrl;
                $('#mainImagePreview').attr('src', uploadedBannerUrl);
                $('.upload-placeholder').addClass('d-none');
                $('.upload-preview').removeClass('d-none');
            }

            // 渲染票種
            renderTicketZones(tickets);

            // 處理駁回原因
            if (event.reviewStatus === 2) {
                $('#rejectReasonAlert').removeClass('d-none');
                $('#rejectReasonText').text(event.note || '內容不符規範，請修改後重新送審。');
                $('#btnSubmitReview').html('<i class="fas fa-paper-plane me-1"></i>重新送審');
            } else {
                $('#rejectReasonAlert').addClass('d-none');
            }

        } catch (error) {
            console.error('loadEventData: Failed', error);
            alert('載入活動資料失敗');
        }
    }

    function renderTicketZones(tickets) {
        if (!tickets || tickets.length === 0) return;

        const $container = $('#ticketZones');
        $container.empty();

        tickets.forEach((ticket, index) => {
            const ticketHtml = `
                <div class="ticket-zone-card mb-3 p-3" style="background: #1A1A1A; border-radius: 6px;">
                    <input type="hidden" class="zone-id" value="${ticket.ticketId}">
                    <div class="d-flex justify-content-between mb-3">
                        <h6>票種 #${index + 1}</h6>
                        ${index > 0 ? `
                        <button type="button" class="btn btn-sm btn-outline-danger btn-remove-zone">
                            <i class="fas fa-trash"></i> 刪除
                        </button>
                        ` : ''}
                    </div>
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label class="form-label required">票種名稱</label>
                            <input type="text" class="form-control zone-name" value="${ticket.ticketName}" required>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label required">票價 (NT$)</label>
                            <input type="number" class="form-control zone-price" value="${ticket.price}" min="0" required>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label required">數量</label>
                            <input type="number" class="form-control zone-qty" value="${ticket.total}" min="1" required>
                        </div>
                    </div>
                </div>
            `;
            $container.append(ticketHtml);
        });
    }

    // Expose for external use (from List)
    window.openEventEditor = function (eventId) {
        // Reset form first
        $('#eventCreateForm')[0].reset();
        $('#editEventId').val('');
        $('#editorTitle').text('建立新活動');
        $('#btnCancelEdit').addClass('d-none');
        $('#rejectReasonAlert').addClass('d-none');
        $('.upload-preview').addClass('d-none');
        $('.upload-placeholder').removeClass('d-none');
        $('#ticketZones').html(`
            <div class="ticket-zone-card mb-3 p-3" style="background: #1A1A1A; border-radius: 6px;">
                <div class="d-flex justify-content-between mb-3">
                    <h6>票種 #1</h6>
                </div>
                <div class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label required">票種名稱</label>
                        <input type="text" class="form-control zone-name" placeholder="例如:全票" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label required">票價 (NT$)</label>
                        <input type="number" class="form-control zone-price" placeholder="1800" min="0" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label required">數量</label>
                        <input type="number" class="form-control zone-qty" placeholder="200" min="1" required>
                    </div>
                </div>
            </div>
        `);

        if (eventId) {
            loadEventData(eventId);
        }
    };

    // ========== 時間序驗證 ==========
    function validateTimeSequence() {
        const eventAtVal = $('#eventDateTime').val();
        const startedAtVal = $('#saleStart').val();
        const endedAtVal = $('#saleEnd').val();

        let isValid = true;

        // Reset states
        $('.form-control').removeClass('is-invalid');
        $('#btnSaveDraft, #btnSubmitReview').prop('disabled', false);

        if (!eventAtVal || !startedAtVal || !endedAtVal) return true; // Wait for all fields

        const eventAt = new Date(eventAtVal);
        const startedAt = new Date(startedAtVal);
        const endedAt = new Date(endedAtVal);

        // 1. 開始必須早於結束
        if (startedAt >= endedAt) {
            $('#saleStart, #saleEnd').addClass('is-invalid');
            isValid = false;
        }

        // 2. 結束必須早於活動舉辦
        if (endedAt >= eventAt) {
            $('#saleEnd, #eventDateTime').addClass('is-invalid');
            isValid = false;
        }

        if (!isValid) {
            $('#btnSaveDraft, #btnSubmitReview').prop('disabled', true);
        }

        return isValid;
    }

    $(document).on('change', '#saleStart, #saleEnd, #eventDateTime', function () {
        validateTimeSequence();
    });

    // ========== 圖片上傳 (優化) ==========
    // Note: Now using <label for="mainImageInput">, so manual .click() is not needed
    $(document).on('click', '#mainImageUpload', function (e) {
        console.log('mainImageUpload: Box clicked');
        // If clicking remove button, stop propagation
        if ($(e.target).closest('.btn-remove-preview').length) {
            e.preventDefault();
            return;
        }
    });

    $(document).on('click', '.btn-remove-preview', function (e) {
        e.stopPropagation();
        $('#mainImagePreview').attr('src', '');
        $('.upload-preview').addClass('d-none');
        $('.upload-placeholder').removeClass('d-none');
        uploadedBannerUrl = '';
        $('#mainImageInput').val(''); // Clear file input
    });

    $(document).on('change', '#mainImageInput', function (e) {
        const file = e.target.files[0];
        console.log('mainImageInput: File selected', file);
        if (!file) return;

        // Show Loading
        const $uploadZone = $('#mainImageUpload');
        const $placeholder = $uploadZone.find('.upload-placeholder');
        const originalPlaceholderHtml = $placeholder.html();

        $placeholder.html('<div class="spinner-border text-primary mb-2"></div><p>上傳中...</p>');

        const formData = new FormData();
        formData.append('file', file);

        $.ajax({
            url: '/organizer/event/upload-image',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                console.log('Image upload response', response);
                if (response.success) {
                    uploadedBannerUrl = response.imageUrl;
                    $('#mainImagePreview').attr('src', response.imageUrl);
                    $placeholder.addClass('d-none').html(originalPlaceholderHtml);
                    $('.upload-preview').removeClass('d-none');
                    if (window.showToast) window.showToast('圖片上傳成功', 'success');
                } else {
                    alert('上傳失敗: ' + response.message);
                    $placeholder.html(originalPlaceholderHtml);
                }
            },
            error: function (xhr) {
                console.error('Image upload error', xhr);
                alert('圖片上傳失敗');
                $placeholder.html(originalPlaceholderHtml);
            }
        });
    });

    // ========== 新增票種 ==========
    $(document).on('click', '#btnAddTicketZone', function () {
        const count = $('#ticketZones .ticket-zone-card').length + 1;
        const newZone = `
            <div class="ticket-zone-card mb-3 p-3" style="background: #1A1A1A; border-radius: 6px;">
                <div class="d-flex justify-content-between mb-3">
                    <h6>票種 #${count}</h6>
                    <button type="button" class="btn btn-sm btn-outline-danger btn-remove-zone">
                        <i class="fas fa-trash"></i> 刪除
                    </button>
                </div>
                <div class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label required">票種名稱</label>
                        <input type="text" class="form-control zone-name" placeholder="例如:全票" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label required">票價 (NT$)</label>
                        <input type="number" class="form-control zone-price" placeholder="1800" min="0" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label required">數量</label>
                        <input type="number" class="form-control zone-qty" placeholder="200" min="1" required>
                    </div>
                </div>
            </div>
        `;
        $('#ticketZones').append(newZone);
    });

    $(document).on('click', '.btn-remove-zone', function () {
        if ($('#ticketZones .ticket-zone-card').length > 1) {
            $(this).closest('.ticket-zone-card').remove();
        } else {
            alert('至少需要一個票種');
        }
    });

    // ========== 資料收集 ==========
    function collectFormData(isDraft = false) {
        const eventAtVal = $('#eventDateTime').val();
        const startedAtVal = $('#saleStart').val();
        const endedAtVal = $('#saleEnd').val();

        if (!isDraft) {
            // Strict Validation for Submit
            if (!validateTimeSequence()) {
                alert('時間順序不正確，請修正後再試');
                return null;
            }

            if (!eventAtVal || !startedAtVal || !endedAtVal) {
                alert('請完整填寫所有時間欄位');
                return null;
            }
        }

        const formData = {
            eventId: $('#editEventId').val() ? parseInt($('#editEventId').val()) : null,
            title: $('[name="eventName"]').val(),
            typeId: parseInt($('#eventType').val()) || null, // Allow null if not selected
            place: $('[name="eventVenue"]').val(),
            eventAt: (eventAtVal && eventAtVal.includes(':') && eventAtVal.split(':').length === 2) ? eventAtVal + ':00' : (eventAtVal || null),
            startedAt: (startedAtVal && startedAtVal.includes(':') && startedAtVal.split(':').length === 2) ? startedAtVal + ':00' : (startedAtVal || null),
            endedAt: (endedAtVal && endedAtVal.includes(':') && endedAtVal.split(':').length === 2) ? endedAtVal + ':00' : (endedAtVal || null),
            content: $('#eventContent').val(),
            bannerUrl: uploadedBannerUrl || null,
            imageUrls: [],
            tickets: []
        };

        // Draft: Ensure at least Title is present (or some minimal field) - optional requirement from user "any field"
        // But backend creates entity. Let's just pass what is there.
        // If everything is empty, backend might create an empty event? 
        // User said "Have filled in ANY field".
        // Let's rely on user not clicking save if empty.
        // Or check if at least title is there? No, user said ANY field.

        $('.ticket-zone-card').each(function () {
            const price = $(this).find('.zone-price').val();
            const total = $(this).find('.zone-qty').val();

            formData.tickets.push({
                ticketId: $(this).find('.zone-id').val() ? parseInt($(this).find('.zone-id').val()) : null,
                name: $(this).find('.zone-name').val(),
                price: price ? parseInt(price) : 0,
                total: total ? parseInt(total) : 0
            });
        });

        return formData;
    }

    // ========== 儲存草稿 ==========
    $(document).on('click', '#btnSaveDraft', function () {
        const formData = collectFormData(true);
        if (!formData) return;

        const btn = $(this);
        const originalText = btn.html();
        btn.html('<span class="spinner-border spinner-border-sm me-2"></span>儲存中...').prop('disabled', true);

        const isEdit = !!formData.eventId;
        const url = isEdit ? `/organizer/event/${formData.eventId}` : '/organizer/event/create';
        const method = isEdit ? 'PUT' : 'POST';

        $.ajax({
            url: url,
            type: method,
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                if (response.success) {
                    btn.html('<i class="fas fa-check me-2"></i>已儲存');
                    if (!isEdit && response.eventId) {
                        $('#editEventId').val(response.eventId);
                    }
                    if (window.showToast) window.showToast('草稿儲存成功', 'success');
                    setTimeout(() => {
                        btn.html(originalText).prop('disabled', false);
                    }, 2000);
                } else {
                    alert('儲存失敗: ' + response.message);
                    btn.html(originalText).prop('disabled', false);
                }
            },
            error: function (xhr) {
                alert('儲存失敗: ' + (xhr.responseJSON?.message || '未知錯誤'));
                btn.html(originalText).prop('disabled', false);
            }
        });
    });

    // ========== 送出審核 ==========
    $(document).on('click', '#btnSubmitReview', function () {
        const isEdit = !!$('#editEventId').val();
        const confirmMsg = isEdit ? '確定要更新並重新送出審核嗎？' : '確定要送出審核嗎？送出後將無法編輯。';
        if (!confirm(confirmMsg)) return;

        const formData = collectFormData(false);
        if (!formData) return;

        const btn = $(this);
        const originalText = btn.html();
        btn.html('<span class="spinner-border spinner-border-sm me-2"></span>處理中...').prop('disabled', true);

        const saveUrl = isEdit ? `/organizer/event/${formData.eventId}` : '/organizer/event/create';
        const saveMethod = isEdit ? 'PUT' : 'POST';

        $.ajax({
            url: saveUrl,
            type: saveMethod,
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (res) {
                const eventId = isEdit ? formData.eventId : res.eventId;
                if (res.success || isEdit) {
                    $.ajax({
                        url: '/organizer/event/submit/' + eventId,
                        type: 'POST',
                        success: function (submitRes) {
                            if (submitRes.success) {
                                alert('活動已送出審核！');
                                Navigation.showSection('events-list');
                            } else {
                                alert('送審失敗: ' + submitRes.message);
                                btn.html(originalText).prop('disabled', false);
                            }
                        },
                        error: function (xhr) {
                            alert('送審失敗: ' + (xhr.responseJSON?.message || '未知錯誤'));
                            btn.html(originalText).prop('disabled', false);
                        }
                    });
                } else {
                    alert('儲存失敗: ' + res.message);
                    btn.html(originalText).prop('disabled', false);
                }
            },
            error: function (xhr) {
                alert('處理失敗: ' + (xhr.responseJSON?.message || '未知錯誤'));
                btn.html(originalText).prop('disabled', false);
            }
        });
    });

    $(document).on('click', '#btnCancelEdit', function () {
        if (confirm('確定要取消編輯嗎？未儲存的變更將會遺失。')) {
            Navigation.showSection('events-list');
        }
    });

    // ========== 草稿 Modal ==========
    $(document).on('show.bs.modal', '#draftsModal', function () {
        const tbody = $('#draftsTable tbody');
        tbody.html('<tr><td colspan="3" class="text-center text-muted">正在載入...</td></tr>');

        $.ajax({
            url: '/organizer/event/drafts',
            type: 'GET',
            success: function (drafts) {
                tbody.empty();
                if (!drafts || drafts.length === 0) {
                    tbody.html('<tr><td colspan="3" class="text-center text-muted">目前沒有草稿</td></tr>');
                    return;
                }

                drafts.forEach(function (event) {
                    const dateStr = event.eventAt ? new Date(event.eventAt).toLocaleDateString() : '未設定';
                    tbody.append(`
                        <tr>
                            <td>${event.title || '未命名活動'}</td>
                            <td>${dateStr}</td>
                            <td class="text-end">
                                <button class="btn btn-sm btn-outline-light me-2" onclick="editDraft(${event.eventId})">
                                    <i class="fas fa-edit"></i> 編輯
                                </button>
                                <button class="btn btn-sm btn-outline-danger" onclick="deleteDraft(${event.eventId})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                        </tr>
                    `);
                });
            },
            error: function () {
                tbody.html('<tr><td colspan="3" class="text-center text-danger">載入失敗</td></tr>');
            }
        });
    });

    // ========== 編輯草稿 (SPA 跳轉) ==========
    window.editDraft = function (eventId) {
        // 1. 關閉 Modal
        $('#draftsModal').modal('hide');

        // 2. 切換到建立活動區塊
        // 假設 Navigation.showSection 存在，若不存在則嘗試模擬點擊
        if (window.Navigation && window.Navigation.showSection) {
            window.Navigation.showSection('event-create');
        } else {
            // Fallback: trigger click on sidebar or manually show
            $('[data-section="event-create"]').trigger('click');
        }

        // 3. 載入資料 (延遲確保畫面切換完成)
        setTimeout(() => {
            if (window.openEventEditor) {
                window.openEventEditor(eventId);
            } else {
                console.error('editDraft: window.openEventEditor not found');
            }
        }, 100);
    };

    // ========== 刪除草稿 ==========
    window.deleteDraft = function (id) {
        if (!confirm('確定要刪除此草稿嗎？')) return;
        $.ajax({
            url: '/organizer/event/' + id,
            type: 'DELETE',
            success: function (res) {
                if (res.success) {
                    // Refresh table inside modal without closing
                    $('#draftsModal').trigger('show.bs.modal');
                } else {
                    alert('刪除失敗: ' + res.message);
                }
            }
        });
    };
}
