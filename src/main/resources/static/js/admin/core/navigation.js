import { initProductApprovals } from '../modules/product-approvals.js';
const Navigation = {
    init() {
        this.bindEvents();
        this.handleHashChange();
        window.addEventListener('hashchange', () => this.handleHashChange());
    },

    bindEvents() {
        const self = this;
        $(document).on('click', '[data-section]', function (e) {
            const section = $(this).attr('data-section');
            if (section) {
                e.preventDefault();
                self.showSection(section);

                if ($(this).closest('.offcanvas').length) {
                    const offcanvasElement = $(this).closest('.offcanvas')[0];
                    const bsOffcanvas = bootstrap.Offcanvas.getInstance(offcanvasElement);
                    if (bsOffcanvas) bsOffcanvas.hide();
                }
            }
        });
    },

    showSection(section) {
        if (!section) return;
        $('.panel, .section, .content-panel').removeClass('active');
        const target = $(`#panel-${section}, #section-${section}`);
        if (target.length) target.addClass('active');

        $('[data-section]').removeClass('active');
        $(`[data-section="${section}"]`).addClass('active');

        // Scroll Top Logic - target all potential scrollable containers
        window.scrollTo(0, 0);
        $('html, body, main').scrollTop(0);
        if(section === 'product-orders'){
            getAllOrder();
        }
        history.replaceState(null, '', '#' + section);
    },

    handleHashChange() {
        const hash = window.location.hash.slice(1);
        if (hash) this.showSection(hash);
    }
};
function getAllOrder() {
    $.ajax({
        url: '/admin/prod_order/getAllOrder',
        method: 'GET',
        success: function(responseHtml) {
            $('#panel-product-orders').html(responseHtml);
            $('.panel, .section, .content-panel').removeClass('active');
            $('#panel-product-orders').addClass('active');
	        initProductApprovals();
        },
        error: function(xhr) {
            alert("載入失敗，請確認 Java 後端是否有報錯");
        }
    });
}
export default Navigation;
