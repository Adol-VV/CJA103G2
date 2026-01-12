const Sidebar = {
    init() {
        this.bindEvents();
    },

    bindEvents() {
        $('.toggle-sidebar').click(function () {
            $('.sidebar').toggleClass('collapsed');
        });
    }
};

export default Sidebar;
