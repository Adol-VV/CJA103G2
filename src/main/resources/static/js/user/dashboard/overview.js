export async function initOverview() {
    // Member center overview logic
    // Usually for dashboard stats numbers animation or interactive highlights
    console.log('User Overview initialized');

    //order initialization
    try {
        const response = await $.get('/member/prod_order/orderStats');
        $('#order_state').text(response);
        $({ Counter: 0 }).animate({ Counter: response.pending }, {
            duration: 1000,
            step: function () {
                $('#order_state').text(Math.ceil(this.Counter));
            }
        });

    } catch (err) {
        console.error('抓取統計失敗', err);
    }
}
