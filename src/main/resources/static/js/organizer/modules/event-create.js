export function initEventCreate() {
    let uploadedBannerUrl = '';

    // ========== 圖片上傳 ==========
    $(document).on('click', '#mainImageUpload', function () {
        $('#mainImageInput').click();
    });

    $(document).on('change', '#mainImageInput', function (e) {
        const file = e.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        $.ajax({
            url: '/organizer/event/upload-image',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                if (response.success) {
                    uploadedBannerUrl = response.imageUrl;
                    $('#mainImagePreview').attr('src', response.imageUrl);
                    $('.upload-placeholder').addClass('d-none');
                    $('.upload-preview').removeClass('d-none');
                } else {
                    alert('上傳失敗: ' + response.message);
                }
            },
            error: function () {
                alert('圖片上傳失敗');
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
    function collectFormData() {
        const eventAtVal = $('#eventDateTime').val();
        const startedAtVal = $('#saleStart').val();
        const endedAtVal = $('#saleEnd').val();

        if (!eventAtVal || !startedAtVal || !endedAtVal) {
            alert('請完整填寫所有時間欄位');
            return null;
        }

        const eventAt = new Date(eventAtVal);
        const startedAt = new Date(startedAtVal);
        const endedAt = new Date(endedAtVal);

        if (startedAt >= endedAt) {
            alert('售票開始時間必須早於售票結束時間');
            return null;
        }

        if (endedAt > eventAt) {
            alert('售票結束時間不能晚於活動舉辦時間');
            return null;
        }

        const typeIdVal = $('#eventType').val();
        if (!typeIdVal) {
            alert('請選擇活動類型');
            return null;
        }

        const formData = {
            title: $('[name="eventName"]').val(),
            typeId: parseInt(typeIdVal),
            place: $('[name="eventVenue"]').val(),
            eventAt: eventAtVal + ':00',
            startedAt: startedAtVal + ':00',
            endedAt: endedAtVal + ':00',
            content: $('#eventContent').val(),
            bannerUrl: uploadedBannerUrl || null,
            imageUrls: [],
            tickets: []
        };

        $('.ticket-zone-card').each(function () {
            formData.tickets.push({
                name: $(this).find('.zone-name').val(),
                price: parseInt($(this).find('.zone-price').val()),
                total: parseInt($(this).find('.zone-qty').val())
            });
        });

        return formData;
    }

    // ========== 儲存草稿 ==========
    $(document).on('click', '#btnSaveDraft', function () {
        const formData = collectFormData();
        if (!formData) return;

        const btn = $(this);
        const originalText = btn.html();
        btn.html('<span class="spinner-border spinner-border-sm me-2"></span>儲存中...').prop('disabled', true);

        $.ajax({
            url: '/organizer/event/create',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response) {
                if (response.success) {
                    btn.html('<i class="fas fa-check me-2"></i>已儲存');
                    setTimeout(() => {
                        window.location.href = '/organizer/event/edit/' + response.eventId;
                    }, 1000);
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
        if (!confirm('確定要送出審核嗎？送出後將無法編輯。')) return;

        const formData = collectFormData();
        if (!formData) return;

        const btn = $(this);
        const originalText = btn.html();
        btn.html('<span class="spinner-border spinner-border-sm me-2"></span>處理中...').prop('disabled', true);

        $.ajax({
            url: '/organizer/event/create',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (createRes) {
                if (createRes.success) {
                    $.ajax({
                        url: '/organizer/event/submit/' + createRes.eventId,
                        type: 'POST',
                        success: function (submitRes) {
                            if (submitRes.success) {
                                alert('活動已送出審核！');
                                window.location.href = '/organizer/event/list';
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
                    alert('建立失敗: ' + createRes.message);
                    btn.html(originalText).prop('disabled', false);
                }
            },
            error: function (xhr) {
                alert('建立失敗: ' + (xhr.responseJSON?.message || '未知錯誤'));
                btn.html(originalText).prop('disabled', false);
            }
        });
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
                                <a href="/organizer/event/edit/${event.eventId}" class="btn btn-sm btn-outline-light me-2">
                                    <i class="fas fa-edit"></i> 編輯
                                </a>
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

    // ========== 刪除草稿 ==========
    window.deleteDraft = function (id) {
        if (!confirm('確定要刪除此草稿嗎？')) return;
        $.ajax({
            url: '/organizer/event/' + id,
            type: 'DELETE',
            success: function (res) {
                if (res.success) {
                    $('#draftsModal').modal('hide');
                    setTimeout(() => $('#draftsModal').modal('show'), 100);
                } else {
                    alert('刪除失敗: ' + res.message);
                }
            }
        });
    };
}