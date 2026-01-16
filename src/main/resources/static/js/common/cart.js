/**
 
 */

const CartUI = {
    updateBadge() {
        // Just read from storage for visual count
        // No price calculation or sync logic
        const cart = JSON.parse(localStorage.getItem('cart') || '[]');
        const count = cart.length;

        document.querySelectorAll('.cart_count, .cart_badge').forEach(el => {
            if (count > 0) {
                el.textContent = count > 99 ? '99+' : count;
                el.classList.remove('d-none');
            } else {
                el.classList.add('d-none');
            }
        });
    }
};

export default CartUI;
