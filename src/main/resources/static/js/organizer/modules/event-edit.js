export function initEventEdit() {
    let uploadedEditBannerUrl = null; // 改用 null 表示未設定

    console.log('initEventEdit: Module Initialized');

    // ========== 核心：開啟編輯器 ==========
    window.openEventEditEditor = function (eventId) {
        if (!eventId) return;

        // 1. 切換面板
        if (window.Navigation && window.Navigation.showSection) {
            window.Navigation.showSection('event-edit');
        } else {
            // 用事件委派的方式觸發切換
            $('.content-panel').removeClass('active');
            $('#panel-event-edit').addClass('active');
            history.replaceState(null, '', '#event-edit');
        }

        // 2. 清空舊資料並顯示載入中
        $('#eventUpdateForm').addClass('opacity-50');
        $('#editTicketZones').html('<div class="text-center p-5"><i class="fas fa-spinner fa-spin fa-2x text-info"></i><p class="mt-2">正在載入活動資料...</p></div>');
        $('#editRejectReasonAlert').addClass('d-none');
        $('#editImagePreview').attr('src', '').addClass('d-none');
        $('#editImageEmpty').removeClass('d-none');

        // 3. 抓取資料
        loadEditData(eventId);
    };

    async function loadEditData(eventId) {
        try {
            console.log('🔍 loadEditData: 開始載入活動', eventId);

            // 顯示載入中 (但不清空現有資料)
            $('#eventUpdateForm').addClass('opacity-50');
            $('#editTicketZones').html('<div class="text-center p-5"><i class="fas fa-spinner fa-spin fa-2x text-info"></i><p class="mt-2">正在載入活動資料...</p></div>');

            const response = await $.ajax({
                url: `/organizer/event/api/${eventId}`,
                method: 'GET',
                dataType: 'json'
            });

            console.log('✅ API 回應:', response);

            // 驗證資料結構
            if (!response || !response.event) {
                throw new Error('API 回傳資料結構錯誤');
            }

            // 解構回應，注意：後端現在把 DTO 放在 event 鍵值中
            const { event: detail, tickets, images, rejectReason } = response;
            const event = detail.event || detail; // 獲取 DTO 內的 EventVO，並保持向下兼容

            // === 填充基本欄位 ===
            $('#eventUpdateForm')[0].reset();
            $('#updateEventId').val(event.eventId);
            $('#eventUpdateForm [name="eventName"]').val(event.title || '');
            $('#eventUpdateForm [name="eventVenue"]').val(event.place || '');
            $('#editEventContent').val(event.content || '');

            // === 處理時間欄位 ===
            $('#editEventDateTime').val(formatTimeToInput(event.eventAt));
            $('#editSaleStart').val(formatTimeToInput(event.startedAt));
            $('#editSaleEnd').val(formatTimeToInput(event.endedAt));

            // === 載入活動類型 ===
            await ensureEventTypesLoaded();
            if (event.type && event.type.typeId) {
                $('#editEventType').val(event.type.typeId);
                console.log('✅ 活動類型已設定:', event.type.typeId);
            } else {
                console.warn('⚠️ 無活動類型資料');
            }

            // === 處理圖片 (修正選擇器) ===
            if (images && images.length > 0 && images[0].imageUrl) {
                let imgUrl = images[0].imageUrl;
                if (!imgUrl.startsWith('http') && !imgUrl.startsWith('/')) {
                    imgUrl = '/' + imgUrl;
                }
                uploadedEditBannerUrl = imgUrl;

                // 更新圖片顯示
                $('#editImagePreview').attr('src', imgUrl);
                $('#editImagePreview').closest('.upload-preview').removeClass('d-none');
                $('#editImageEmpty').addClass('d-none');

                console.log('✅ 圖片已載入:', imgUrl);
            } else {
                // 保持預設狀態
                $('#editImagePreview').closest('.upload-preview').addClass('d-none');
                $('#editImageEmpty').removeClass('d-none');
                console.warn('⚠️ 無圖片資料');
            }

            // === 渲染票種 ===
            renderEditTickets(tickets);

            // === 處理駁回原因 ===
            if (event.reviewStatus === 2) {
                $('#editRejectReasonAlert').removeClass('d-none');
                $('#editRejectReasonText').text(rejectReason || '內容不符規範,請修改後重新送審。');
                $('#btnUpdateSubmit').html('<i class="fas fa-paper-plane me-2"></i> 修正並重新送審');
            } else {
                $('#editRejectReasonAlert').addClass('d-none');
                $('#btnUpdateSubmit').html('<i class="fas fa-save me-2"></i> 儲存並重新送審');
            }

            $('#eventUpdateForm').removeClass('opacity-50');
            console.log('✅ 資料載入完成');

        } catch (error) {
            console.error('❌ loadEditData 失敗:', error);
            $('#eventUpdateForm').removeClass('opacity-50');
            alert(`無法載入活動資訊\n錯誤: ${error.message || error.responseJSON?.message || '未知錯誤'}`);
        }
    }

    // 新增時間格式化輔助函數
    function formatTimeToInput(timeData) {
        if (!timeData) return '';

        // 處理陣列格式 [2024, 1, 15, 14, 30]
        if (Array.isArray(timeData) && timeData.length >= 5) {
            const [y, m, d, h, min] = timeData;
            const pad = (num) => String(num).padStart(2, '0');
            return `${y}-${pad(m)}-${pad(d)}T${pad(h)}:${pad(min)}`;
        }

        // 處理字串格式 "2024-01-15 14:30:00"
        if (typeof timeData === 'string') {
            return timeData.replace(' ', 'T').substring(0, 16);
        }

        return '';
    }

    // ========== 票種渲染 ==========
    function renderEditTickets(tickets) {
        const $container = $('#editTicketZones');
        $container.empty();

        if (!tickets || tickets.length === 0) {
            addEditTicketZone(); // 至少給一個
            return;
        }

        tickets.forEach((ticket, index) => {
            addEditTicketZone(ticket, index + 1);
        });
    }

    function addEditTicketZone(data = null, index = null) {
        const idx = index || ($('#editTicketZones .ticket-zone-card').length + 1);
        const html = `
            <div class="ticket-zone-card mb-3 p-3" style="background: #1A1A1A; border-radius: 6px;">
                <input type="hidden" class="zone-id" value="${data ? data.ticketId : ''}">
                <div class="d-flex justify-content-between mb-3">
                    <h6>票種 #${idx}</h6>
                    ${idx > 1 ? '<button type="button" class="btn btn-sm btn-link text-danger btn-remove-edit-zone"><i class="fas fa-times"></i> 移除</button>' : ''}
                </div>
                <div class="row g-3">
                    <div class="col-md-5">
                        <label class="form-label required">票種名稱</label>
                        <input type="text" class="form-control zone-name" placeholder="例如:一般票" value="${data ? data.ticketName : ''}" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label required">票價 (NT$)</label>
                        <input type="number" class="form-control zone-price" placeholder="1000" value="${data ? data.price : ''}" min="0" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label required">數量</label>
                        <input type="number" class="form-control zone-qty" placeholder="100" value="${data ? data.total : ''}" min="1" required>
                    </div>
                </div>
            </div>
        `;
        $('#editTicketZones').append(html);
    }

    // ========== 輔助功能 ==========
    async function ensureEventTypesLoaded() {
        const $select = $('#editEventType');
        if ($select.find('option').length > 1) return;

        try {
            const types = await $.get('/organizer/event/api/types');
            $select.html('<option value="">請選擇</option>');
            types.forEach(t => {
                $select.append(`<option value="${t.typeId}">${t.typeName}</option>`);
            });
        } catch (e) {
            $select.html('<option value="">載入失敗</option>');
        }
    }

    // ========== 事件綁定 ==========
    $(document).on('click', '#btnEditAddTicket', () => addEditTicketZone());

    $(document).on('click', '.btn-remove-edit-zone', function () {
        $(this).closest('.ticket-zone-card').remove();
    });

    $(document).on('click', '#btnEditBackToList', (e) => {
        e.preventDefault();
        if (window.Navigation) window.Navigation.showSection('events-list');
    });

    // 圖片更換
    $(document).on('change', '#editImageInput', function (e) {
        const file = e.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        // 顯示上傳中狀態
        $('#editImageEmpty').html('<i class="fas fa-spinner fa-spin fa-2x text-info"></i><p class="mt-2">上傳中...</p>');

        $.ajax({
            url: '/organizer/event/upload-image',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (res) {
                if (res.success && res.imageUrl) {
                    uploadedEditBannerUrl = res.imageUrl;

                    // 更新圖片顯示
                    $('#editImagePreview').attr('src', res.imageUrl);
                    $('#editImagePreview').closest('.upload-preview').removeClass('d-none');
                    $('#editImageEmpty').addClass('d-none');

                    console.log('✅ 新圖片已上傳:', res.imageUrl);
                }
            },
            error: function (xhr) {
                alert('圖片上傳失敗: ' + (xhr.responseJSON?.message || '未知錯誤'));

                // 恢復原始狀態
                $('#editImageEmpty').html('<i class="fas fa-cloud-upload-alt fa-3x mb-3 text-muted"></i><p class="text-muted">點擊或拖曳圖片至此</p>');
            }
        });
    });

    // 移除圖片預覽
    $(document).on('click', '.btn-remove-edit-preview', function (e) {
        e.preventDefault();
        e.stopPropagation();

        if (!confirm('確定要移除此圖片嗎?')) return;

        uploadedEditBannerUrl = null;
        $('#editImagePreview').attr('src', '');
        $('#editImagePreview').closest('.upload-preview').addClass('d-none');
        $('#editImageEmpty').removeClass('d-none');
        $('#editImageEmpty').html('<i class="fas fa-cloud-upload-alt fa-3x mb-3 text-muted"></i><p class="text-muted">點擊或拖曳圖片至此</p>');
        $('#editImageInput').val('');

        console.log('🗑️ 圖片已移除');
    });

    // 儲存邏輯
    async function saveUpdate(submitReview = false) {
        const eventId = $('#updateEventId').val();

        // 票種處理
        const tickets = [];
        $('#editTicketZones .ticket-zone-card').each(function () {
            const ticketIdVal = $(this).find('.zone-id').val();
            const priceVal = parseInt($(this).find('.zone-price').val());
            const totalVal = parseInt($(this).find('.zone-qty').val());

            tickets.push({
                ticketId: ticketIdVal ? parseInt(ticketIdVal) : null,
                name: $(this).find('.zone-name').val(),
                price: isNaN(priceVal) ? 0 : priceVal,
                total: isNaN(totalVal) ? 0 : totalVal
            });
        });

        const typeIdVal = parseInt($('#editEventType').val());

        const data = {
            eventId: eventId,
            title: $('#eventUpdateForm [name="eventName"]').val(),
            typeId: isNaN(typeIdVal) ? null : typeIdVal,
            place: $('#eventUpdateForm [name="eventVenue"]').val(),
            content: $('#editEventContent').val(),
            bannerUrl: uploadedEditBannerUrl || '',
            tickets: tickets
        };

        // 僅當時間欄位存在於 DOM 中時才傳送 (避免 undefined:00 導致 400 錯誤)
        const $eventAt = $('#editEventDateTime');
        if ($eventAt.length && $eventAt.val()) data.eventStartAt = $eventAt.val() + ':00';

        const $saleStart = $('#editSaleStart');
        if ($saleStart.length && $saleStart.val()) data.saleStartAt = $saleStart.val() + ':00';

        const $saleEnd = $('#editSaleEnd');
        if ($saleEnd.length && $saleEnd.val()) data.saleEndAt = $saleEnd.val() + ':00';

        const btn = submitReview ? $('#btnUpdateSubmit') : $('#btnUpdateSave');
        const originalHtml = btn.html();
        btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i>處理中...');

        try {
            await $.ajax({
                url: `/organizer/event/${eventId}`,
                method: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(data)
            });

            if (submitReview) {
                await $.post(`/organizer/event/submit/${eventId}`);
                Swal.fire({
                    icon: 'success',
                    title: '成功',
                    text: '活動已送出審核',
                    background: '#1a1d20',
                    color: '#fff'
                });
                if (window.Navigation) window.Navigation.showSection('events-list');
            } else {
                Swal.fire({
                    icon: 'success',
                    title: '成功',
                    text: '修改已儲存',
                    timer: 1500,
                    showConfirmButton: false,
                    background: '#1a1d20',
                    color: '#fff'
                });
            }
        } catch (e) {
            Swal.fire({
                icon: 'error',
                title: '錯誤',
                text: '操作失敗: ' + (e.responseJSON?.message || '發生錯誤'),
                background: '#1a1d20',
                color: '#fff'
            });
        } finally {
            btn.prop('disabled', false).html(originalHtml);
        }
    }

    $(document).on('click', '#btnUpdateSave', () => saveUpdate(false));
    $(document).on('click', '#btnUpdateSubmit', () => saveUpdate(true));
}
