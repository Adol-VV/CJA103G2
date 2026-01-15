const Sidebar = {
    init() {
        this.bindEvents();
    },

    bindEvents() {
        // Toggle desktop sidebar if needed, or handle offcanvas triggers
        $('.toggle-sidebar').click(function () {
            $('.sidebar').toggleClass('collapsed');
        });
    }
};

export default Sidebar;
