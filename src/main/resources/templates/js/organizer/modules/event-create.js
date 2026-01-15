export function initEventCreate() {
    let currentStep = 1;

    window.showStep = function (step) {
        currentStep = step;
        $('.step-content').removeClass('active');
        $(`.step-content[data-step="${step}"]`).addClass('active');

        $('.stepper-wrapper').attr('data-progress', step);
        $('.stepper-item').removeClass('active completed');
        for (let i = 1; i < step; i++) {
            $(`.stepper-item[data-step="${i}"]`).addClass('completed');
        }
        $(`.stepper-item[data-step="${step}"]`).addClass('active');
        window.scrollTo(0, 0);
    };

    $(document).on('click', '.btn-next-step', function () {
        const next = $(this).data('next');
        if (!validateStep(currentStep)) return;
        if (next === 4) populatePreview();
        showStep(next);
    });

    $(document).on('click', '.btn-prev-step', function () {
        showStep($(this).data('prev'));
    });

    $(document).on('change', 'select[name="refundPolicy"]', function () {
        if ($(this).val() === 'custom') {
            $('.custom-refund-policy').removeClass('d-none');
        } else {
            $('.custom-refund-policy').addClass('d-none');
        }
    });

    $(document).on('change', '#enablePresale', function () {
        if ($(this).is(':checked')) {
            $('.presale-settings').removeClass('d-none');
        } else {
            $('.presale-settings').addClass('d-none');
        }
    });

    $(document).on('input', 'textarea[maxlength]', function () {
        const max = $(this).attr('maxlength');
        const current = $(this).val().length;
        $(this).closest('.mb-4').find('.char-count').text(current);
    });

    $(document).on('click', '#btnSaveDraft', function () {
        if (window.Momento && window.Momento.Toast) {
            window.Momento.Toast.show('草稿已儲存', 'success');
        } else if (window.showToast) {
            window.showToast('草稿已儲存', 'success');
        }
    });

    $(document).on('submit', '#eventCreateForm', function (e) {
        e.preventDefault();
        if (validateStep(currentStep)) {
            const eventData = {
                name: $('input[name="eventName"]').val().trim(),
                category: $('select[name="eventCategory"]').val(),
                venue: $('input[name="eventVenue"]').val().trim(),
                date: $('input[name="sessionDate[]"]').first().val(),
                startTime: $('input[name="sessionStartTime[]"]').first().val(),
                tickets: []
            };

            $('.ticket-type-item').each(function () {
                eventData.tickets.push({
                    name: $(this).find('input[name="ticketTypeName[]"]').val(),
                    price: $(this).find('input[name="ticketPrice[]"]').val(),
                    qty: $(this).find('input[name="ticketQuantity[]"]').val()
                });
            });

            if (window.MockDB) {
                MockDB.events.create(eventData);
            }

            if (window.showToast) window.showToast('活動已送出審核，請等待審核結果', 'success');
            setTimeout(() => {
                if (window.showSection) window.showSection('events-list');
            }, 2000);
        }
    });

    $(document).on('click', '#mainImageUpload', function () { $('#mainImageInput').click(); });
    $(document).on('change', '#mainImageInput', function (e) {
        if (e.target.files[0]) {
            const reader = new FileReader();
            reader.onload = function (e) {
                $('#mainImagePreview').attr('src', e.target.result);
                $('#mainImageUpload .upload-placeholder').addClass('d-none');
                $('#mainImageUpload .upload-preview').removeClass('d-none');
            };
            reader.readAsDataURL(e.target.files[0]);
        }
    });
}

function validateStep(step) {
    let isValid = true;
    let errorMsg = '';

    if (step === 1) {
        const eventName = $('input[name="eventName"]').val().trim();
        const eventCategory = $('select[name="eventCategory"]').val();
        const eventVenue = $('input[name="eventVenue"]').val().trim();
        const eventAddress = $('input[name="eventAddress"]').val().trim();

        if (!eventName || eventName.length < 5) {
            errorMsg = '活動名稱至少需要 5 個字';
            isValid = false;
        } else if (eventName.length > 50) {
            errorMsg = '活動名稱不能超過 50 個字';
            isValid = false;
        } else if (!eventCategory) {
            errorMsg = '請選擇活動類型';
            isValid = false;
        } else if (!eventVenue) {
            errorMsg = '請填寫活動地點';
            isValid = false;
        } else if (!eventAddress) {
            errorMsg = '請填寫詳細地址';
            isValid = false;
        }
    } else if (step === 2) {
        const sessionDate = $('input[name="sessionDate[]"]').first().val();
        const sessionStart = $('input[name="sessionStartTime[]"]').first().val();
        const sessionEnd = $('input[name="sessionEndTime[]"]').first().val();
        const ticketName = $('input[name="ticketTypeName[]"]').first().val();
        const ticketPrice = $('input[name="ticketPrice[]"]').first().val();
        const ticketQty = $('input[name="ticketQuantity[]"]').first().val();

        if (!sessionDate) {
            errorMsg = '請設定活動日期';
            isValid = false;
        } else if (new Date(sessionDate) < new Date()) {
            errorMsg = '活動日期不能早於今天';
            isValid = false;
        } else if (!sessionStart || !sessionEnd) {
            errorMsg = '請設定活動時間';
            isValid = false;
        } else if (sessionStart >= sessionEnd) {
            errorMsg = '結束時間必須晚於開始時間';
            isValid = false;
        } else if (!ticketName || !ticketPrice || !ticketQty) {
            errorMsg = '請完整填寫票種資訊';
            isValid = false;
        } else if (ticketPrice < 0) {
            errorMsg = '票價不能為負數';
            isValid = false;
        } else if (ticketQty < 1) {
            errorMsg = '票券數量至少為 1';
            isValid = false;
        }
    } else if (step === 3) {
        const summary = $('textarea[name="eventSummary"]').val().trim();
        const description = $('textarea[name="eventDescription"]').val().trim();
        const notice = $('textarea[name="eventNotice"]').val().trim();
        const saleDate = $('input[name="saleStartDate"]').val();

        if (!summary || summary.length < 20) {
            errorMsg = '活動簡介至少需要 20 個字';
            isValid = false;
        } else if (!description || description.length < 50) {
            errorMsg = '詳細說明至少需要 50 個字';
            isValid = false;
        } else if (!notice) {
            errorMsg = '請填寫購票注意事項';
            isValid = false;
        } else if (!saleDate) {
            errorMsg = '請設定開賣日期';
            isValid = false;
        }
    }

    if (!isValid && window.showToast) {
        window.showToast(errorMsg, 'error');
    }
    return isValid;
}

function populatePreview() {
    $('#previewEventName').text($('input[name="eventName"]').val() || '活動名稱');
    $('#previewSummary').text($('textarea[name="eventSummary"]').val() || '活動簡介');
    $('#previewVenue').text($('input[name="eventVenue"]').val() || '未設定');

    const categoryText = $('select[name="eventCategory"] option:selected').text();
    $('#previewCategory').text(categoryText || '未分類');

    const sessionDate = $('input[name="sessionDate[]"]').first().val();
    const sessionStart = $('input[name="sessionStartTime[]"]').first().val();
    $('#previewDate').text(sessionDate || '未設定');
    $('#previewTime').text(sessionStart || '未設定');

    const imgSrc = $('#mainImagePreview').attr('src');
    if (imgSrc) {
        $('#previewImage').attr('src', imgSrc).removeClass('d-none');
        $('#previewImagePlaceholder').addClass('d-none');
    }

    let ticketsHtml = '';
    $('.ticket-type-item').each(function (i) {
        const name = $(this).find('input[name="ticketTypeName[]"]').val() || '票種 ' + (i + 1);
        const price = $(this).find('input[name="ticketPrice[]"]').val() || 0;
        const qty = $(this).find('input[name="ticketQuantity[]"]').val() || 0;
        ticketsHtml += `<div class="d-flex justify-content-between mb-1">
            <span>${name}</span>
            <span>NT$ ${Number(price).toLocaleString()} × ${qty} 張</span>
        </div>`;
    });
    $('#previewTickets').html(ticketsHtml || '<p class="text-muted">尚未設定票種</p>');

    const saleDate = $('input[name="saleStartDate"]').val();
    const saleTime = $('input[name="saleStartTime"]').val();
    const hasPresale = $('#enablePresale').is(':checked');
    let saleHtml = `<p>開賣時間：${saleDate} ${saleTime}</p>`;
    if (hasPresale) {
        const discount = $('input[name="presaleDiscount"]').val();
        const presaleEnd = $('input[name="presaleEndDate"]').val();
        saleHtml += `<p class="text-success"><i class="fas fa-tags me-1"></i>早鳥優惠 ${discount}% OFF (至 ${presaleEnd})</p>`;
    }
    $('#previewSaleInfo').html(saleHtml);
}
