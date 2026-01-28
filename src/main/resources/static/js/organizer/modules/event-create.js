export function initEventCreate() {
    console.log('initEventCreate: Start');

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
        if (e.originalEvent && !window.IS_EDITING_EVENT) {
            console.log('Sidebar Navigation: Resetting Editor to Create Mode');
            if (window.openDraftEditor) window.openDraftEditor(null);
        }
        window.IS_EDITING_EVENT = false;
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
            console.log('loadEventData: Sending AJAX request to /organizer/event/api/' + eventId);
            const response = await $.ajax({
                url: `/organizer/event/api/${eventId}`,
                method: 'GET'
            });

            console.log('loadEventData: Received Response:', response);

            if (!response || !response.event) {
                console.error('loadEventData: Invalid response structure', response);
                alert('系統無法解析活動資料，請聯絡管理員。');
                return;
            }

            const event = response.event;
            const tickets = response.tickets || [];
            const images = response.images || [];

            console.log('loadEventData: Populating basic fields...');
            $('#eventCreateForm [name="eventName"]').val(event.title || '');
            $('#eventCreateForm [name="eventVenue"]').val(event.place || '');
            $('#eventType').val(event.type?.typeId || '');
            $('#eventContent').val(event.content || '');

            // 處理圖片 (主視覺)
            console.log('loadEventData: Handling images...');
            if (images && images.length > 0 && images[0].imageUrl) {
                let imgUrl = images[0].imageUrl;
                if (imgUrl && !imgUrl.startsWith('/') && !imgUrl.startsWith('http')) {
                    imgUrl = '/' + imgUrl;
                }
                uploadedBannerUrl = imgUrl;
                $('#mainImagePreview').attr('src', imgUrl);
                $('#panel-event-create .upload-placeholder').addClass('d-none');
                $('#panel-event-create .upload-preview').removeClass('d-none');
            } else {
                uploadedBannerUrl = '';
                $('#panel-event-create .upload-preview').addClass('d-none');
                $('#panel-event-create .upload-placeholder').removeClass('d-none');
            }

            // 渲染票種
            console.log('loadEventData: Rendering tickets...');
            renderTicketZones(tickets);

            // 處理駁回原因 (Status 4)
            if (response.status === 4) {
                console.log('loadEventData: Rejection detect, showing reason.');
                $('#rejectReasonAlert').removeClass('d-none');
                $('#rejectReasonText').text(response.rejectReason || '內容不符規範，請修改後重新送審。');
                $('#btnSubmitReview').html('<i class="fas fa-paper-plane me-1"></i>重新送審');
            } else {
                $('#rejectReasonAlert').addClass('d-none');
                $('#btnSubmitReview').html('<i class="fas fa-paper-plane me-1"></i>送出審核');
            }

            console.log('loadEventData: ALL STEPS COMPLETED SUCCESSFULLY');

        } catch (error) {
            console.error('loadEventData: CRASHED!', error);
            alert('載入活動資料時發生程式錯誤，請查看控制台報告。');
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
    window.openDraftEditor = function (eventId) {
        $('#eventCreateForm')[0].reset();
        $('#editEventId').val('');
        $('#editorTitle').text('建立新活動');
        $('#btnCancelEdit').addClass('d-none');
        $('#rejectReasonAlert').addClass('d-none');
        $('#panel-event-create .upload-preview').addClass('d-none');
        $('#panel-event-create .upload-placeholder').removeClass('d-none');
        uploadedBannerUrl = '';
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

    // ========== 圖片上傳處理 ==========
    $(document).on('change', '#mainImageInput', function (e) {
        const file = e.target.files[0];
        if (!file) return;

        if (!file.type.startsWith('image/')) {
            alert('請選擇圖片檔案!');
            this.value = '';
            return;
        }

        const maxSize = 5 * 1024 * 1024;
        if (file.size > maxSize) {
            alert('圖片大小不能超過 5MB!');
            this.value = '';
            return;
        }

        const reader = new FileReader();
        reader.onload = function (e) {
            $('#mainImagePreview').attr('src', e.target.result);
            $('#panel-event-create .upload-placeholder').addClass('d-none');
            $('#panel-event-create .upload-preview').removeClass('d-none');
        };
        reader.readAsDataURL(file);

        uploadImageToServer(file);
    });

    function uploadImageToServer(file) {
        const formData = new FormData();
        formData.append('file', file);

        $('#panel-event-create .upload-preview').css('opacity', '0.6');
        $('#panel-event-create .upload-preview').append('<div class="upload-spinner position-absolute top-50 start-50 translate-middle"><i class="fas fa-spinner fa-spin fa-2x text-light"></i></div>');

        $.ajax({
            url: '/organizer/event/upload-image',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                if (response.success && response.imageUrl) {
                    uploadedBannerUrl = response.imageUrl;
                    $('#mainImagePreview').attr('src', response.imageUrl);
                    $('#panel-event-create .upload-spinner').remove();
                    $('#panel-event-create .upload-preview').css('opacity', '1');
                    showToast('圖片上傳成功!', 'success');
                } else {
                    alert('上傳失敗: ' + (response.message || '未知錯誤'));
                }
            },
            error: function (xhr) {
                $('#panel-event-create .upload-preview').addClass('d-none');
                $('#panel-event-create .upload-placeholder').removeClass('d-none');
                $('#mainImageInput').val('');
                alert(xhr.responseJSON?.message || '圖片上傳失敗,請重試');
            }
        });
    }

    $(document).on('click', '.btn-remove-preview', function (e) {
        e.preventDefault();
        e.stopPropagation();
        if (!confirm('確定要移除此圖片嗎?')) return;
        uploadedBannerUrl = '';
        $('#mainImagePreview').attr('src', '');
        $('#panel-event-create .upload-preview').addClass('d-none');
        $('#panel-event-create .upload-placeholder').removeClass('d-none');
        $('#mainImageInput').val('');
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

    // ========== 建立活動邏輯 (無時間設定) ==========
    async function createEvent(isDraft = true) {
        const title = $('[name="eventName"]').val();
        if (!title) {
            alert('請至少填寫活動名稱');
            return;
        }

        if (!isDraft) {
            if (!uploadedBannerUrl) {
                alert('請上傳活動主視覺圖片!');
                return;
            }
            if (!$('#eventType').val()) {
                alert('請選擇活動類型');
                return;
            }
            if (!$('#eventContent').val()) {
                alert('請填寫活動詳細說明');
                return;
            }
        }

        const eventId = $('#editEventId').val() ? parseInt($('#editEventId').val()) : null;

        const eventData = {
            eventId: eventId,
            title: title,
            typeId: parseInt($('#eventType').val()) || null,
            place: $('[name="eventVenue"]').val(),
            content: $('#eventContent').val(),
            bannerUrl: uploadedBannerUrl,
            tickets: []
        };

        $('#ticketZones .ticket-zone-card').each(function () {
            const ticketId = $(this).find('.zone-id').val();
            eventData.tickets.push({
                ticketId: ticketId ? parseInt(ticketId) : null,
                name: $(this).find('.zone-name').val(),
                price: parseInt($(this).find('.zone-price').val()) || 0,
                total: parseInt($(this).find('.zone-qty').val()) || 0
            });
        });

        const btn = isDraft ? $('#btnSaveDraft') : $('#btnSubmitReview');
        const originalHtml = btn.html();
        btn.html('<span class="spinner-border spinner-border-sm me-2"></span>處理中...').prop('disabled', true);

        const url = eventId ? `/organizer/event/${eventId}` : '/organizer/event/create';
        const method = eventId ? 'PUT' : 'POST';

        try {
            const response = await $.ajax({
                url: url,
                method: method,
                contentType: 'application/json',
                data: JSON.stringify(eventData)
            });

            if (response.success) {
                const targetEventId = eventId || response.eventId;
                if (!isDraft) {
                    await $.post('/organizer/event/submit/' + targetEventId);
                    alert('活動已送出審核！');
                    if (window.Navigation) window.Navigation.showSection('events-list');
                } else {
                    showToast('草稿儲存成功!', 'success');
                    if (!eventId && response.eventId) {
                        $('#editEventId').val(response.eventId);
                        $('#editorTitle').text('編輯活動');
                        $('#btnCancelEdit').removeClass('d-none');
                    }
                }
            } else {
                alert('處理失敗: ' + (response.message || '未知錯誤'));
            }
        } catch (error) {
            alert('處理失敗: ' + (error.responseJSON?.message || '未知錯誤'));
        } finally {
            btn.html(originalHtml).prop('disabled', false);
        }
    }

    $(document).on('click', '#btnSubmitReview', () => createEvent(false));
    $(document).on('click', '#btnSaveDraft', () => createEvent(true));

    function showToast(message, type = 'info') {
        if (window.showToast) {
            window.showToast(message, type);
        } else {
            console.log(`[${type}] ${message}`);
        }
    }

    $(document).on('click', '#btnCancelEdit', function () {
        if (confirm('確定要取消編輯嗎？未儲存的變更將會遺失。')) {
            window.Navigation.showSection('events-list');
        }
    });

    $(document).on('show.bs.modal', '#draftsModal', function () {
        const tbody = $('#draftsTable tbody');
        tbody.html('<tr><td colspan="3" class="text-center text-muted">正在載入...</td></tr>');

        $.ajax({
            url: '/organizer/event/api/list?status=0',
            type: 'GET',
            success: function (res) {
                const drafts = res.content || [];
                tbody.empty();
                if (drafts.length === 0) {
                    tbody.html('<tr><td colspan="3" class="text-center text-muted">目前沒有草稿</td></tr>');
                    return;
                }

                drafts.forEach(function (event) {
                    tbody.append(`
                        <tr>
                            <td>${event.title || '未命名活動'}</td>
                            <td>${event.publishedAt ? new Date(event.publishedAt).toLocaleDateString() : '尚未送審'}</td>
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

    window.editDraft = function (eventId) {
        $('#draftsModal').modal('hide');
        if (window.Navigation && window.Navigation.showSection) {
            window.Navigation.showSection('event-create');
        }
        setTimeout(() => {
            if (window.openDraftEditor) {
                window.openDraftEditor(eventId);
            }
        }, 100);
    };

    window.deleteDraft = function (id) {
        if (!confirm('確定要刪除此草稿嗎？')) return;
        $.ajax({
            url: '/organizer/event/' + id,
            type: 'DELETE',
            success: function (res) {
                if (res.success) {
                    $('#draftsModal').trigger('show.bs.modal');
                } else {
                    alert('刪除失敗: ' + res.message);
                }
            }
        });
    };
}
