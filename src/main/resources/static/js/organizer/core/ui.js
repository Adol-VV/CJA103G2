const UI = {
    init() {
        this.initTooltips();
        this.initStatsAnimation();
    },

    initTooltips() {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    },

    initStatsAnimation() {
        $('.stat-value').each(function () {
            const $this = $(this);
            const val = parseFloat($this.text().replace(/[^0-9.]/g, ''));
            if (isNaN(val)) return;

            $({ countNum: 0 }).animate({ countNum: val }, {
                duration: 1000,
                easing: 'swing',
                step: function () {
                    $this.text(Math.floor(this.countNum).toLocaleString());
                },
                complete: function () {
                    $this.text(val.toLocaleString());
                }
            });
        });
    }
};

export default UI;
