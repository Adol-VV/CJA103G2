export function initEventCreate() {
    console.log('initEventCreate: Start');

    if (window.EVENT_CREATE_INITIALIZED) {
        console.log('initEventCreate: Already initialized, skipping binding');
        if (window.loadActivityTypes) window.loadActivityTypes();
        return;
    }
    window.EVENT_CREATE_INITIALIZED = true;

    let uploadedBannerUrl = '';
    let uploadedGalleryUrls = [];

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

            // 處理圖片 (主視覺 + 相簿)
            console.log('loadEventData: Handling images...');
            if (images && images.length > 0) {
                // 主視覺 (Order 0)
                const banner = images.find(img => img.imageOrder === 0) || images[0];
                let bannerUrl = banner.imageUrl;
                if (bannerUrl && !bannerUrl.startsWith('/') && !bannerUrl.startsWith('http')) {
                    bannerUrl = '/' + bannerUrl;
                }
                uploadedBannerUrl = bannerUrl;
                $('#mainImagePreview').attr('src', bannerUrl);
                $('#mainImageUpload').addClass('has-preview');
                $('#panel-event-create .upload-placeholder').addClass('d-none');
                $('#panel-event-create .upload-preview').removeClass('d-none');

                // 其他圖片 (Order > 0)
                uploadedGalleryUrls = images
                    .filter(img => img.imageOrder > 0)
                    .map(img => img.imageUrl);
                renderGallery();
            } else {
                uploadedBannerUrl = '';
                uploadedGalleryUrls = [];
                $('#mainImageUpload').removeClass('has-preview');
                $('#panel-event-create .upload-preview').addClass('d-none');
                $('#panel-event-create .upload-placeholder').removeClass('d-none');
                renderGallery();
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
        $('#mainImageUpload').removeClass('has-preview');
        uploadedBannerUrl = '';
        uploadedGalleryUrls = [];
        renderGallery();
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
            $('#mainImageUpload').addClass('has-preview');
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
        $('#mainImageUpload').removeClass('has-preview');
        $('#panel-event-create .upload-preview').addClass('d-none');
        $('#panel-event-create .upload-placeholder').removeClass('d-none');
        $('#mainImageInput').val('');
    });

    // ========== 相簿圖片處理 ==========
    $(document).on('change', '#galleryImageInput', function (e) {
        const files = e.target.files;
        if (!files.length) return;

        const limit = 5;
        const remaining = limit - uploadedGalleryUrls.length;

        if (remaining <= 0) {
            alert('最多只能上傳 5 張相簿圖片');
            this.value = '';
            return;
        }

        const filesToUpload = Array.from(files).slice(0, remaining);

        filesToUpload.forEach(file => {
            if (!file.type.startsWith('image/')) return;

            const formData = new FormData();
            formData.append('file', file);

            // Add a temporary loading placeholder
            const tempId = 'gallery-loading-' + Date.now() + Math.random().toString(36).substr(2, 9);
            const loadingHtml = `
                <div id="${tempId}" class="gallery-item-loading" style="width: 120px; height: 120px; border: 1px solid #444; border-radius: 8px; display: flex; align-items: center; justify-content: center; background: rgba(0,0,0,0.3);">
                    <i class="fas fa-spinner fa-spin text-primary"></i>
                </div>
            `;
            $('.gallery-add-zone').before(loadingHtml);

            $.ajax({
                url: '/organizer/event/upload-image',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (response) {
                    $('#' + tempId).remove();
                    if (response.success && response.imageUrl) {
                        uploadedGalleryUrls.push(response.imageUrl);
                        renderGallery();
                    } else {
                        showToast('相簿圖片上傳失敗', 'danger');
                    }
                },
                error: function () {
                    $('#' + tempId).remove();
                    showToast('系統錯誤，圖片上傳失敗', 'danger');
                }
            });
        });

        this.value = ''; // Reset input
    });

    function renderGallery() {
        const $container = $('#galleryContainer');
        $container.find('.gallery-item-preview').remove();

        uploadedGalleryUrls.forEach((url, index) => {
            let fullUrl = url;
            if (fullUrl && !fullUrl.startsWith('/') && !fullUrl.startsWith('http')) {
                fullUrl = '/' + fullUrl;
            }

            const itemHtml = `
                <div class="gallery-item-preview position-relative" style="width: 120px; height: 120px;">
                    <img src="${fullUrl}" class="img-fluid rounded border border-secondary h-100 w-100" style="object-fit: cover;">
                    <button type="button" class="btn btn-sm btn-danger p-0 position-absolute top-0 end-0 m-1 btn-remove-gallery" 
                        data-index="${index}" style="width: 22px; height: 22px; border-radius: 50%; display: flex; align-items: center; justify-content: center; z-index: 15;">
                        <i class="fas fa-times" style="font-size: 10px;"></i>
                    </button>
                </div>
            `;
            $('.gallery-add-zone').before(itemHtml);
        });

        // Toggle add button visibility
        if (uploadedGalleryUrls.length >= 5) {
            $('.gallery-add-zone').addClass('d-none');
        } else {
            $('.gallery-add-zone').removeClass('d-none');
        }
    }

    $(document).on('click', '.btn-remove-gallery', function (e) {
        e.preventDefault();
        const index = $(this).data('index');
        uploadedGalleryUrls.splice(index, 1);
        renderGallery();
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

    // 強制限制票價與數量只能輸入數字 (擋掉 e, ., -, + 等)
    $(document).on('keydown', '.zone-price, .zone-qty', function (e) {
        // 允許: Backspace, Tab, Enter, Escape, Delete, 左右箭頭
        const allowedKeys = [46, 8, 9, 27, 13, 37, 39];
        if (allowedKeys.indexOf(e.keyCode) !== -1 ||
            // 允許: Ctrl+A, Ctrl+C, Ctrl+V, Ctrl+X
            ((e.keyCode === 65 || e.keyCode === 67 || e.keyCode === 86 || e.keyCode === 88) && (e.ctrlKey === true || e.metaKey === true)) ||
            // 允許: Home, End
            (e.keyCode >= 35 && e.keyCode <= 36)) {
            return;
        }
        // 確保它是數字，否則防止預設行為 (鍵盤上方的數字鍵與右側小鍵盤)
        if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
            e.preventDefault();
        }
    });

    $(document).on('paste', '.zone-price, .zone-qty', function (e) {
        const pasteData = e.originalEvent.clipboardData.getData('text');
        if (!/^\d+$/.test(pasteData)) {
            e.preventDefault();
            alert('只能貼上數字內容');
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
            imageUrls: uploadedGalleryUrls,
            tickets: []
        };

        let ticketsValid = true;
        $('#ticketZones .ticket-zone-card').each(function () {
            const priceRaw = $(this).find('.zone-price').val();
            const qtyRaw = $(this).find('.zone-qty').val();
            const name = $(this).find('.zone-name').val()?.trim();

            if (!isDraft) {
                if (!name) {
                    alert('請填寫所有票種名稱');
                    ticketsValid = false;
                    return false;
                }
                // 送審模式：強制檢查價格與數量
                if (priceRaw === "" || !/^\d+$/.test(priceRaw)) {
                    alert('請為票種「' + (name || '未命名') + '」填入有效的票價');
                    $(this).find('.zone-price').focus();
                    ticketsValid = false;
                    return false;
                }
                if (qtyRaw === "" || !/^\d+$/.test(qtyRaw)) {
                    alert('請為票種「' + (name || '未命名') + '」填入有效的數量');
                    $(this).find('.zone-qty').focus();
                    ticketsValid = false;
                    return false;
                }
            }

            const ticketId = $(this).find('.zone-id').val();
            eventData.tickets.push({
                ticketId: ticketId ? parseInt(ticketId) : null,
                name: name || '',
                price: priceRaw !== "" ? parseInt(priceRaw) : 0,
                total: qtyRaw !== "" ? parseInt(qtyRaw) : 0
            });
        });

        if (!ticketsValid) return;

        const btn = isDraft ? $('#btnSaveDraft') : $('#btnSubmitReview');
        const originalHtml = isDraft ?
            `<i data-lucide="save" class="me-1" style="width: 18px; height: 18px; vertical-align: text-bottom;"></i>儲存草稿` :
            `<i data-lucide="send" class="me-1" style="width: 20px; height: 20px; vertical-align: text-bottom;"></i>送出審核`;

        btn.html('<span class="spinner-border spinner-border-sm me-2"></span>處理中...').prop('disabled', true);

        const url = eventId ? `/organizer/event/${eventId}` : '/organizer/event/create';
        const method = eventId ? 'PUT' : 'POST';
        let skipFinallyReset = false;

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
                    Swal.fire({
                        icon: 'success',
                        title: '成功',
                        text: '活動已送出審核！',
                        background: '#1a1d20',
                        color: '#fff',
                        timer: 2000,
                        showConfirmButton: false
                    });
                    if (window.Navigation) window.Navigation.showSection('events-list');
                } else {
                    // 按鈕回饋
                    skipFinallyReset = true;
                    btn.html('<i data-lucide="check" class="me-1" style="width:18px;height:18px;vertical-align:text-bottom;"></i>儲存成功')
                        .addClass('btn-success').removeClass('btn-outline-light');
                    if (window.lucide) window.lucide.createIcons();

                    if (!eventId && response.eventId) {
                        $('#editEventId').val(response.eventId);
                        $('#editorTitle').text('編輯活動');
                        $('#btnCancelEdit').removeClass('d-none');
                    }

                    setTimeout(() => {
                        btn.html(originalHtml).removeClass('btn-success').addClass('btn-outline-light').prop('disabled', false);
                        if (window.lucide) window.lucide.createIcons();
                    }, 2000);
                }
            } else {
                Swal.fire({
                    icon: 'error',
                    title: '失敗',
                    text: '處理失敗: ' + (response.message || '未知錯誤'),
                    background: '#1a1d20',
                    color: '#fff'
                });
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: '失敗',
                text: '處理失敗: ' + (error.responseJSON?.message || '未知錯誤'),
                background: '#1a1d20',
                color: '#fff'
            });
        } finally {
            if (!skipFinallyReset) {
                btn.html(originalHtml).prop('disabled', false);
                if (window.lucide) window.lucide.createIcons();
            }
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
