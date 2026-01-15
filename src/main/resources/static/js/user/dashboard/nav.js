/**
 * User Dashboard Navigation
 * Handles section switching and sidebar interaction.
 */

const Nav = {
    init() {
        this.bindEvents();
        this.handleHash();

        // Listen for hash changes manually if needed, or rely on click handlers updating it
        window.addEventListener('hashchange', () => this.handleHash());
    },

    bindEvents() {
        // Sidebar click
        $(document).on('click', '.list-group-item[data-section]', function (e) {
            e.preventDefault();
            const section = $(this).data('section');
            Nav.showSection(section);
        });
    },

    showSection(section) {
        // Update Sidebar Active State
        $('.list-group-item').removeClass('active');
        $(`.list-group-item[data-section="${section}"]`).addClass('active');

        // Show Content Section
        $('.content-section').removeClass('active');
        $(`#section-${section}`).addClass('active');

        // Update Hash
        history.pushState(null, null, '#' + section);
        // window.scrollTo(0, 0);  // 點擊功能捲軸會跳到最上面
    },

    handleHash() {
        const hash = window.location.hash.substring(1);
        const section = hash || 'overview'; // Default to overview

        if ($(`#section-${section}`).length) {
            this.showSection(section);
        } else {
            // Fallback if hash is invalid
            this.showSection('overview');
        }
    }
};

export default Nav;
