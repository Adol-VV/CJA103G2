/**
 * 自動幫導覽列標記目前所在頁面（加上 active 樣式）
 */

const NavbarUI = {
    highlightCurrent() {
        const path = window.location.pathname;
        document.querySelectorAll('.nav-link').forEach(link => {
            const href = link.getAttribute('href');
            if (href && path.includes(href)) {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        });
    }
};

export default NavbarUI;
