$(document).ready(function () {
    let current_step = 1;
    let total_price = 3250;
    let token_used = 0;





    // Step Navigation
    function goToStep(step) {
        current_step = step;

        // Update content
        $('.step-content').removeClass('active');
        $('#step' + step).addClass('active');

        if (step === 4) {
            $('#orderSummarySidebar').addClass('d-none');
            $('.col-lg-8').removeClass('col-lg-8').addClass('col-12');
        } else {
            $('#orderSummarySidebar').removeClass('d-none');
        }

        // Update stepper
        $('.step-item').each(function () {
            const s = $(this).data('step');
            $(this).removeClass('active completed');
            if (s < step) $(this).addClass('completed');
            if (s === step) $(this).addClass('active');
        });

        // Scroll to top
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    // Next Step
    $('.btn-next').click(function () {
        const next = $(this).data('next');
        goToStep(next);
    });

    // Previous Step
    $('.btn-prev').click(function () {
        const prev = $(this).data('prev');
        goToStep(prev);
    });

    // Payment Option Selection
    $('.payment-option').click(function () {
        $('.payment-option').removeClass('selected');
        $(this).addClass('selected');
        $(this).find('input[type="radio"]').prop('checked', true);

        const method = $(this).find('input').val();
        if (method === 'credit') {
            $('#creditCardForm').removeClass('d-none');
        } else {
            $('#creditCardForm').addClass('d-none');
        }
    });

    // Invoice Type Toggle
    $('input[name="invoiceType"]').change(function () {
        $('#carrierInput, #companyInput').addClass('d-none');
        if ($('#invoiceCarrier').is(':checked')) {
            $('#carrierInput').removeClass('d-none');
        } else if ($('#invoiceCompany').is(':checked')) {
            $('#companyInput').removeClass('d-none');
        }
    });

    // Token Usage
    $('#useTokensCheckout').change(function () {
        if ($(this).is(':checked')) {
            $('#tokenSlider').removeClass('d-none');
            $('#tokenDiscountRow').removeClass('d-none');
            updateTotal(true);
        } else {
            $('#tokenSlider').addClass('d-none');
            $('#tokenDiscountRow').addClass('d-none');
            updateTotal();
            //$('tokenAmount').val(0);
            //updateTotal();
        }
    });

    /*$('#tokenAmount').on('input', function () {
        const amount = parseInt($(this).val());
        $('#tokenAmountDisplay').text('使用 ' + amount + ' 代幣');
        updateTotal();
    });*/



    // Submit Order
    $('#btnSubmitOrder').click(async function () {
        const btn = $(this);
        const paymentMethod = $('input[name="paymentMethod"]:checked').val();

        if (paymentMethod === 'credit') {
            const ccNum = $('#cc-number').val().replace(/\s/g, '');
            const ccExpiry = $('#cc-expiry').val();
            const ccCvv = $('#cc-cvv').val();
            const ccHolder = $('#cc-holder').val().trim();

            if (ccNum.length !== 16 || !/^\d+$/.test(ccNum)) {
                showToast('請輸入有效的信用卡號 (16碼)', 'error');
                return;
            }
            if (!/^\d{2}\/\d{2}$/.test(ccExpiry)) {
                showToast('請輸入有效的有效期限 (MM/YY)', 'error');
                return;
            }

            // Advanced Expiry Validation
            const [expMonth, expYear] = ccExpiry.split('/').map(num => parseInt(num, 10));
            const now = new Date();
            const currentYear = parseInt(now.getFullYear().toString().slice(-2)); // Get last 2 digits
            const currentMonth = now.getMonth() + 1; // 1-12

            if (expMonth < 1 || expMonth > 12) {
                showToast('無效的月份 (01-12)', 'error');
                return;
            }

            // Check if expired
            if (expYear < currentYear || (expYear === currentYear && expMonth < currentMonth)) {
                showToast('信用卡已過期', 'error');
                return;
            }

            // Check for unrealistic future date (e.g. > 20 years from now)
            if (expYear > currentYear + 20) {
                showToast('有效期限年份不合理', 'error');
                return;
            }
            if (ccCvv.length < 3 || ccCvv.length > 4 || !/^\d+$/.test(ccCvv)) {
                showToast('請輸入有效的安全碼 (3-4碼)', 'error');
                return;
            }
            if (ccHolder === '') {
                showToast('請輸入持卡人姓名', 'error');
                return;
            }
        }

        btn.prop('disabled', true)
            .empty()
            .append($('<span>').addClass('spinner-border spinner-border-sm me-2'))
            .append('處理中...');


        //ajax
        let shopData = JSON.parse(localStorage.getItem('momento_cart') || '[]');

        // 根據 organizerId 分組
        let groupedData = shopData.reduce((acc, item) => {
            let orgId = item.organizerId;
            if (!acc[orgId]) acc[orgId] = [];
            acc[orgId].push(item);
            return acc;
        }, {});

        let createdOrderIds = [];
        let remainingToken = token_used; // Initialize remaining tokens

        try {
            for (let orgId of Object.keys(groupedData)) {
                let items = groupedData[orgId];
                let subTotal = items.reduce((sum, i) => sum + (i.price * i.quantity), 0);

                // Calculate token usage for this order
                let tokenForThisOrder = 0;
                if (remainingToken > 0) {
                    // Use up to subTotal, or whatever is left
                    tokenForThisOrder = Math.min(remainingToken, subTotal);
                    remainingToken -= tokenForThisOrder;
                }

                // 轉換為 ProdOrderItemVO格式
                let subOrderItems = items.map(i => ({
                    prodId: { prodId: i.id },
                    quantity: i.quantity,
                    price: i.price,
                    total: i.quantity * i.price
                }));


                // 3. 發送打包 Ajax 
                const response = await $.ajax({
                    url: '/member/prod_order/insertOrder',
                    method: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        memberId: { memberId: sessionStorage.getItem('memberId') },
                        organizerId: { organizerId: parseInt(orgId) },
                        total: subTotal,
                        token: tokenForThisOrder, // Use calculated token
                        payable: subTotal - tokenForThisOrder, // Calculate payable
                        status: 1,
                        orderItems: subOrderItems
                    })
                });
                // $("#orderNumber").text(response);
                // $("#fetch_orderId").val(response);
                createdOrderIds.push(response);
            }

            if (paymentMethod === 'linepay') {
                let finalAmount = total_price;
                if ($('#useTokensCheckout').is(':checked')) {
                    let currentToken = parseInt($('#tokenAmount').text()) || 0;
                    finalAmount = (total_price - currentToken) >= 0 ? (total_price - currentToken) : 0;
                }

                if (finalAmount <= 0) {
                    showToast('恭喜！所有訂單已建立成功', 'success');
                    localStorage.removeItem('momento_cart');
                    goToStep(4);
                    return;
                }

                // Save UI state to session for return
                sessionStorage.setItem('linepay_orderNumber', createdOrderIds.join(', '));
                sessionStorage.setItem('linepay_finalPrice', $('#finalPrice').text());
                sessionStorage.setItem('linepay_itemCount', $('#finalCouhnt').text());
                if (createdOrderIds.length > 0) {
                    sessionStorage.setItem('linepay_orderId', createdOrderIds[createdOrderIds.length - 1]);
                }

                const linePayRes = await $.ajax({
                    url: '/api/linepay/request',
                    method: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        amount: finalAmount,
                        databaseOrderIds: createdOrderIds
                    })
                });
                window.location.href = linePayRes;

            } else {
                // 所有商家的訂單都跑完後
                renderOrderButtons(createdOrderIds);
                showToast('恭喜！所有訂單已建立成功', 'success');
                localStorage.removeItem('momento_cart');
                goToStep(4);
            }

        } catch (err) {
            console.error(err);
            showToast('訂單送出失敗或付款請求失敗，請檢查資料', 'error');
            btn.prop('disabled', false).empty().append('<i class="fas fa-lock me-2"></i>確認付款 NT$ ' + total_price.toLocaleString());
        }
    });

    // Credit Card Number Formatting
    $('input[placeholder="1234 5678 9012 3456"]').on('input', function () {
        let value = $(this).val().replace(/\s/g, '').replace(/\D/g, '');
        let formatted = value.match(/.{1,4}/g)?.join(' ') || value;
        $(this).val(formatted);
    });

    // Expiry Date Formatting
    $('input[placeholder="MM/YY"]').on('input', function () {
        let value = $(this).val().replace(/\D/g, '');
        if (value.length >= 2) {
            value = value.slice(0, 2) + '/' + value.slice(2);
        }
        $(this).val(value);
    });

    //更新購物車
    updateCart();
    function updateCart() {
        const cart = JSON.parse(localStorage.getItem('momento_cart') || '[]');
        let item_list = $(".card-body.p-0");
        item_list.empty();

        cart.forEach(item => {
            let item_el = `
                <div class="order-item cart-item" data-id="${item.id}" data-price="${item.price}" data-qty="${item.quantity}">
                <img loading="lazy" src="${item.image}" class="order-item-img" alt="Product">
                <div class="flex-grow-1">
                    <h6 class="mb-1">${item.name}</h6>
                </div>
                <div class="text-end">
                    <div class="text-success mb-1">NT$ ${item.price}</div>
                    <small class="text-muted">${item.quantity}</small>
                </div>
            </div>
            `;

            item_list.append(item_el);
        });
        $('#finalCouhnt').text(cart.length + "件");
    }
    //計算總額
    updateTotal();
    function updateTotal(useToken = false) {
        let total = 0;
        if (useToken) {
            const token = parseInt($('#tokenAmount').text()) || 0;
            const total = (total_price - token) >= 0 ? (total_price - token) : 0;

            token_used = (total_price - token) >= 0 ? token : total_price;

            $('#finalPrice').text("NT$ " + total);
            $('#tokenDiscountAmount').text('-NT$ ' + token_used.toLocaleString());

            $('#finalTotal').text('NT$ ' + total.toLocaleString());
            // 使用安全方式更新按鈕
            $('#btnSubmitOrder')
                .empty()
                .append($('<i>').addClass('fas fa-lock me-2'))
                .append('確認付款 NT$ ' + total.toLocaleString());
        } else {
            token_used = 0;
            $(".cart-item").each(function (item) {
                let price = parseInt($(this).data("price"));
                let quantity = parseInt($(this).data("qty"));

                total += price * quantity;
            });

            total_price = total;
            $("#finalTotal").text("NT$ " + total);
            $(".prod_price_T").text("NT$ " + total);
            $('#finalPrice').text("NT$ " + total);

            $('#btnSubmitOrder')
                .empty()
                .append($('<i>').addClass('fas fa-lock me-2'))
                .append('確認付款 NT$ ' + total.toLocaleString());
        }
        $(".momentoCoins").text((Math.floor(total / 300)) * 5);

    }

    // Check for LINE Pay callback (Run after init)
    const urlParams = new URLSearchParams(window.location.search);
    const paymentStatus = urlParams.get('payment');
    if (paymentStatus === 'success') {
        // Restore UI state from session
        if (sessionStorage.getItem('linepay_orderId')) {
            // $("#orderNumber").text(sessionStorage.getItem('linepay_orderNumber'));
            $("#finalPrice").text(sessionStorage.getItem('linepay_finalPrice'));
            $("#finalCouhnt").text(sessionStorage.getItem('linepay_itemCount'));
            $("#fetch_orderId").val(sessionStorage.getItem('linepay_orderId'));

            // Render buttons for LINE Pay return
            const storedOrderIds = sessionStorage.getItem('linepay_orderNumber'); // This is "id1, id2"
            if (storedOrderIds) {
                const ids = storedOrderIds.split(',').map(s => s.trim());
                renderOrderButtons(ids);
            }

            // Clean up
            sessionStorage.removeItem('linepay_orderNumber');
            sessionStorage.removeItem('linepay_finalPrice');
            sessionStorage.removeItem('linepay_itemCount');
            sessionStorage.removeItem('linepay_orderId');
        }

        showToast('恭喜！所有訂單已建立成功 (LINE Pay)', 'success');
        localStorage.removeItem('momento_cart');

        setTimeout(() => goToStep(4), 500);
        window.history.replaceState({}, document.title, window.location.pathname);
    } else if (paymentStatus === 'cancel') {
        showToast('您已取消 LINE Pay 付款', 'warning');
    } else if (paymentStatus === 'fail' || paymentStatus === 'error') {
        showToast('LINE Pay 付款失敗', 'error');
    }
});

// Helper to render order buttons
function renderOrderButtons(orderIds) {
    const container = $('#orderButtonsContainer');
    container.empty();

    orderIds.forEach(id => {
        const formHtml = `
            <form action="/member/prod_order/orderDetail" method="post" style="display:inline-block;" class="m-1">
                <input type="hidden" name="orderId" value="${id}" />
                <button class="btn text-start p-3 position-relative overflow-hidden group" 
                        style="min-width: 180px; background: #000; border: 1px solid #333; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);"
                        onmouseover="this.style.borderColor='#198754'; this.style.boxShadow='0 0 15px rgba(25,135,84,0.3)'; this.querySelector('.view-text').style.opacity='1'; this.querySelector('.arrow-icon').style.transform='translateX(0)';"
                        onmouseout="this.style.borderColor='#333'; this.style.boxShadow='none'; this.querySelector('.view-text').style.opacity='0'; this.querySelector('.arrow-icon').style.transform='translateX(-10px)';"
                        onclick="$('#productOrderModal').modal('show')">
                    
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <span class="text-muted small" style="font-size: 0.75rem;">訂單編號</span>
                        <div class="view-text text-success small fw-bold" style="opacity: 0; transition: opacity 0.3s ease;">
                            查看 <i class="fas fa-arrow-right arrow-icon" style="transition: transform 0.3s ease; transform: translateX(-10px);"></i>
                        </div>
                    </div>
                    <div class="h5 text-success mb-0 fw-bold font-monospace">${id}</div>
                </button>
            </form>
        `;
        container.append(formHtml);
    });
}

// Toast helper function
function showToast(message, type) {
    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }
    const bgClass = type === 'success' ? 'bg-success' : type === 'warning' ? 'bg-warning' : 'bg-danger';
    const toast = document.createElement('div');
    toast.className = `toast show align-items-center text-white ${bgClass} border-0`;
    toast.innerHTML = `<div class="d-flex"><div class="toast-body"><i class="fas fa-check-circle me-2"></i>${message}</div><button type="button" class="btn-close btn-close-white me-2 m-auto" onclick="this.closest('.toast').remove()"></button></div>`;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}



