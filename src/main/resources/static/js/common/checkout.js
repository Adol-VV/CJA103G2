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
    $('#btnSubmitOrder').click( async function () {
        const btn = $(this);
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

            for (let orgId of Object.keys(groupedData)) {
            let items = groupedData[orgId];
            let subTotal = items.reduce((sum, i) => sum + (i.price * i.quantity), 0);

            // 轉換為 ProdOrderItemVO格式
            let subOrderItems = items.map(i => ({
                prodId: { prodId: i.id },
                quantity: i.quantity,
                price: i.price,
                total: i.quantity * i.price
            }));
            

            // 3. 發送打包 Ajax 
            try {
                await $.ajax({
                    url: '/member/prod_order/insertOrder',
                    method: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        memberId: { memberId: sessionStorage.getItem('memberId') }, 
                        organizerId: { organizerId: parseInt(orgId) }, 
                        total: subTotal,
                        token: token_used, 
                        payable: subTotal-token_used,
                        status: 1,
                        orderItems: subOrderItems 
                    })
                });
            } catch (err) {
                showToast('訂單送出失敗，請檢查資料', 'error');
                return; 
            }
        }

        // 所有商家的訂單都跑完後
        showToast('恭喜！所有訂單已建立成功', 'success');
        localStorage.removeItem('momento_cart');
        goToStep(4);
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
            let item_el=`
                <div class="order-item cart-item" data-id="${item.id}" data-price="${item.price}" data-qty="${item.quantity}">
                <img loading="lazy" src="${item.image}" class="order-item-img" alt="Product">
                <div class="flex-grow-1">
                    <h6 class="mb-1">${item.name}</h6>
                    <p class="text-muted small mb-0">規格：米白色</p>
                </div>
                <div class="text-end">
                    <div class="text-success mb-1">NT$ ${item.price}</div>
                    <small class="text-muted">${item.quantity}</small>
                </div>
            </div>
            `;

            item_list.append(item_el);
        });
        $('#finalCouhnt').text(cart.length+"件");
    }
//計算總額
    updateTotal();
    function updateTotal(useToken=false) {
        if(useToken){
            const token = parseInt($('#tokenAmount').text()) || 0;
            const final = (total_price - token)>=0?(total_price - token):0;

            token_used = (total_price - token)>=0?token:total_price;
            
            $('#finalPrice').text("NT$ " + final);
            $('#tokenDiscountAmount').text('-NT$ ' + token_used.toLocaleString());
            
            $('#finalTotal').text('NT$ ' + final.toLocaleString());
            // 使用安全方式更新按鈕
            $('#btnSubmitOrder')
                .empty()
                .append($('<i>').addClass('fas fa-lock me-2'))
                .append('確認付款 NT$ ' + final.toLocaleString());
        }else{
            let total = 0;
            token_used=0;
            $(".cart-item").each(function(item){
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
        
    }
});

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



