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
        // Only reset if it's a direct user click on the sidebar link (not triggered by and edit action)
        if (e.originalEvent && !window.IS_EDITING_EVENT) {
            console.log('Sidebar Navigation: Resetting Editor to Create Mode');
            if (window.openEventEditor) window.openEventEditor(null);
        }
        // Always clear the flag after navigation
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

            // 填寫時間 (強化格式化功能，處理陣列或字串)
            const formatTime = (timeData) => {
                if (!timeData) return '';
                console.log('formatTime processing:', timeData);

                // 如果是陣列 [2026, 1, 26, 18, 30]
                if (Array.isArray(timeData)) {
                    const y = timeData[0];
                    const m = String(timeData[1]).padStart(2, '0');
                    const d = String(timeData[2]).padStart(2, '0');
                    const hh = String(timeData[3] || 0).padStart(2, '0');
                    const mm = String(timeData[4] || 0).padStart(2, '0');
                    return `${y}-${m}-${d}T${hh}:${mm}`;
                }

                // 如果是字串
                if (typeof timeData === 'string') {
                    return timeData.replace(' ', 'T').substring(0, 16);
                }
                return '';
            };

            console.log('loadEventData: Setting date fields...');
            $('#eventDateTime').val(formatTime(event.eventAt));
            $('#saleStart').val(formatTime(event.startedAt));
            $('#saleEnd').val(formatTime(event.endedAt));

            // 處理圖片 (主視覺)
            console.log('loadEventData: Handling images...');
            if (images && images.length > 0 && images[0].imageUrl) {
                let imgUrl = images[0].imageUrl;
                if (imgUrl && !imgUrl.startsWith('/') && !imgUrl.startsWith('http')) {
                    imgUrl = '/' + imgUrl;
                }
                uploadedBannerUrl = imgUrl;
                $('#mainImagePreview').attr('src', imgUrl);
                $('.upload-placeholder').addClass('d-none');
                $('.upload-preview').removeClass('d-none');
            } else {
                uploadedBannerUrl = '';
                $('.upload-preview').addClass('d-none');
                $('.upload-placeholder').removeClass('d-none');
            }

            // 渲染票種
            console.log('loadEventData: Rendering tickets...');
            renderTicketZones(tickets);

            // 處理駁回原因
            if (event.reviewStatus === 2) {
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

    // ========== 圖片上傳處理 ==========

    // 監聽圖片選擇
    $(document).on('change', '#mainImageInput', function (e) {
        const file = e.target.files[0];
        if (!file) return;

        // 驗證檔案類型
        if (!file.type.startsWith('image/')) {
            alert('請選擇圖片檔案!');
            this.value = '';
            return;
        }

        // 驗證檔案大小 (5MB)
        const maxSize = 5 * 1024 * 1024;
        if (file.size > maxSize) {
            alert('圖片大小不能超過 5MB!');
            this.value = '';
            return;
        }

        // 顯示本地預覽 (立即反饋)
        const reader = new FileReader();
        reader.onload = function (e) {
            $('#mainImagePreview').attr('src', e.target.result);
            $('.upload-placeholder').addClass('d-none');
            $('.upload-preview').removeClass('d-none');
        };
        reader.readAsDataURL(file);

        // 上傳到後端
        uploadImageToServer(file);
    });

    // 上傳圖片到伺服器
    function uploadImageToServer(file) {
        const formData = new FormData();
        formData.append('file', file);

        // 顯示上傳中狀態
        $('.upload-preview').css('opacity', '0.6');
        $('.upload-preview').append('<div class="upload-spinner position-absolute top-50 start-50 translate-middle"><i class="fas fa-spinner fa-spin fa-2x text-light"></i></div>');

        $.ajax({
            url: '/organizer/event/upload-image',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                console.log('✅ 圖片上傳成功:', response);

                if (response.success && response.imageUrl) {
                    uploadedBannerUrl = response.imageUrl;
                    $('#mainImagePreview').attr('src', response.imageUrl);

                    // 移除上傳中狀態
                    $('.upload-spinner').remove();
                    $('.upload-preview').css('opacity', '1');

                    // 顯示成功提示
                    showToast('圖片上傳成功!', 'success');
                } else {
                    throw new Error('上傳失敗: ' + (response.message || '未知錯誤'));
                }
            },
            error: function (xhr, status, error) {
                console.error('❌ 圖片上傳失敗:', xhr.responseJSON);

                // 移除預覽
                $('.upload-preview').addClass('d-none');
                $('.upload-placeholder').removeClass('d-none');
                $('#mainImageInput').val('');

                // 顯示錯誤訊息
                const errorMsg = xhr.responseJSON?.message || '圖片上傳失敗,請重試';
                alert(errorMsg);
            }
        });
    }

    // 移除圖片預覽
    $(document).on('click', '.btn-remove-preview', function (e) {
        e.preventDefault();
        e.stopPropagation();

        if (!confirm('確定要移除此圖片嗎?')) return;

        uploadedBannerUrl = '';
        $('#mainImagePreview').attr('src', '');
        $('.upload-preview').addClass('d-none');
        $('.upload-placeholder').removeClass('d-none');
        $('#mainImageInput').val('');

        console.log('🗑️ 圖片已移除');
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

    // ========== 建立活動邏輯 (包含圖片 URL) ==========

    async function createEvent(isDraft = false) {
        // 驗證必填欄位
        if (!isDraft) {
            if (!uploadedBannerUrl) {
                alert('請上傳活動主視覺圖片!');
                return;
            }

            // 驗證時間序
            if (!validateTimeSequence()) {
                alert('時間順序不正確，請修正後再試');
                return;
            }

            if (!$('#eventDateTime').val() || !$('#saleStart').val() || !$('#saleEnd').val()) {
                alert('請完整填寫所有時間欄位');
                return;
            }
        }

        const eventId = $('#editEventId').val() ? parseInt($('#editEventId').val()) : null;

        // 組合資料
        const eventData = {
            eventId: eventId,
            title: $('[name="eventName"]').val(),
            typeId: parseInt($('#eventType').val()) || null,
            place: $('[name="eventVenue"]').val(),
            eventAt: $('#eventDateTime').val() ? $('#eventDateTime').val() + ':00' : null,
            startedAt: $('#saleStart').val() ? $('#saleStart').val() + ':00' : null,
            endedAt: $('#saleEnd').val() ? $('#saleEnd').val() + ':00' : null,
            content: $('#eventContent').val(),
            bannerUrl: uploadedBannerUrl, // ← 重要!帶上圖片 URL
            tickets: []
        };

        // 收集票種資料
        $('#ticketZones .ticket-zone-card').each(function () {
            const ticketId = $(this).find('.zone-id').val();
            eventData.tickets.push({
                ticketId: ticketId ? parseInt(ticketId) : null,
                name: $(this).find('.zone-name').val(),
                price: parseInt($(this).find('.zone-price').val()) || 0,
                total: parseInt($(this).find('.zone-qty').val()) || 0
            });
        });

        console.log('📤 送出資料:', eventData);

        const btn = isDraft ? $('#btnSaveDraft') : $('#btnSubmitReview');
        const originalHtml = btn.html();
        btn.html('<span class="spinner-border spinner-border-sm me-2"></span>處理中...').prop('disabled', true);

        const url = eventId ? `/organizer/event/${eventId}` : '/organizer/event/create';
        const method = eventId ? 'PUT' : 'POST';

        // 發送請求
        try {
            const response = await $.ajax({
                url: url,
                method: method,
                contentType: 'application/json',
                data: JSON.stringify(eventData)
            });

            console.log('✅ 處理成功:', response);

            if (response.success) {
                const targetEventId = eventId || response.eventId;

                if (!isDraft) {
                    // 如果不是草稿，還要執行送審
                    await $.post('/organizer/event/submit/' + targetEventId);
                    alert('活動已送出審核！');
                    if (window.Navigation) window.Navigation.showSection('events-list');
                } else {
                    alert('草稿儲存成功!');
                    if (!eventId && response.eventId) {
                        $('#editEventId').val(response.eventId);
                    }
                }
            } else {
                alert('處理失敗: ' + (response.message || '未知錯誤'));
            }
        } catch (error) {
            console.error('❌ 處理失敗:', error);
            alert('處理失敗: ' + (error.responseJSON?.message || '未知錯誤'));
        } finally {
            btn.html(originalHtml).prop('disabled', false);
        }
    }

    // 綁定按鈕事件
    $(document).on('click', '#btnSubmitReview', () => createEvent(false));
    $(document).on('click', '#btnSaveDraft', () => createEvent(true));

    // 輔助函數: Toast 提示
    function showToast(message, type = 'info') {
        if (window.showToast) {
            window.showToast(message, type);
        } else {
            console.log(`[${type}] ${message}`);
        }
    }

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

        // 2. 切換到「編輯活動」區塊
        if (window.Navigation && window.Navigation.showSection) {
            window.Navigation.showSection('event-edit');
        } else {
            // Fallback: trigger click or force show
            $('.content-panel').removeClass('active');
            $('#panel-event-edit').addClass('active');
            history.replaceState(null, '', '#event-edit');
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
