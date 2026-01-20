$(function(){

    
    //localstroge
    localStorage.setItem('cart', JSON.stringify([{"id":1,"name":"商品A","price":100,"quantity":2},{"id":2,"name":"商品B","price":200,"quantity":1}]));

    //更新購物車
    updateCart();
    function updateCart() {
        const cart = JSON.parse(localStorage.getItem('cart') || '[]');
        let item_list = $(".card.bg-dark.border-secondary.mb-3");
        item_list.empty();
        
        cart.forEach(item => {
            let item_el=`
            <div class="card-body cart-item" data-id="${item.id}" data-price="${item.price}">
                <div class="Pic">
                        <img loading="lazy" src="https://via.placeholder.com/100x100" class="rounded" alt="Product">
                </div>
                    <div class="item">
                        <span class="badge bg-warning text-dark mb-1">商品</span>
                        <h5 class="item">${item.name}</h5>
                        <p class="itemType">米白色 x 1</p>
                </div>
                <div class="count">
                    <div class="input-group input-group-sm" style="width: 120px;">
                        <button class="btn btn-sm cart_reduce">-</button>
                        <input type="text"
                                class="form-control bg-dark text-white text-center border-secondary" 
                                value="${item.quantity}">
                        <button class="btn btn-sm cart_add">+</button>
                    </div>
                </div>
                <div class="col-auto text-end">
                        <h5 class="text-success mb-1">NT$ ${item.price * item.quantity}</h5>
                        <button class="btn btn-sm btn-outline-danger"><i class="fas fa-trash"></i></button>
                </div>
            </div>
            `;

            item_list.append(item_el);
        });}

        //減少數量
    $(document).on("click",".cart_reduce", function(){
        let count_el = $(this).closest("div").find("input");
        let count = count_el.val();
        if(count_el.val() >1){
            count--;
            $(count_el).val(count);
            
            let item_price = parseInt($(this).closest(".cart-item").data("price")) * count;
            $(this).closest(".cart-item").find(".text-success").html("NT$"+item_price);
            calculateTotal();
        }
    });
    
    //增加數量
    $(document).on("click",".cart_add", function(){
        let count_el = $(this).closest("div").find("input");
        let count = count_el.val();
        if(count_el.val() <99){
            count++;
            $(count_el).val(count);

            let item_price = parseInt($(this).closest(".cart-item").data("price")) * count;
            $(this).closest(".cart-item").find(".text-success").html("NT$"+item_price);
            calculateTotal();
        }
    });
    //刪除商品
    $(document).on("click",".btn-outline-danger", function(){
        let r = confirm("確定要刪除該商品嗎？");
        if(r){
            this.closest(".cart-item").remove();
            calculateTotal();
            checkCartCount();
        }
    });

    //計算總額
    function calculateTotal() {
        let total = 0;
        $(".cart-item").each(function(item){
            let price = parseInt($(this).data("price"));
            let quantity = parseInt($(this).find("input").val());
            total += price * quantity;
        });
        $(".fw-bold.text-success.fs-4").text("NT$ " + total);
        $(".prod_price").text("NT$ " + total);
        
    }
    calculateTotal();
    //檢查購物車數量
    function checkCartCount() {
        let count = $(".cart-item").length;
        let el = $("#emptyCart");
        if (count > 0) {
                el.textContent = count > 99 ? '99+' : count;
                el.addClass("d-none");
            } else {
                el.removeClass('d-none');
        }
    }
    checkCartCount();
    }
);

