/**
 * 登入 / 登出狀態的「畫面顯示控制」而已（純 UI 層），
*不負責真的驗證帳密，也不跟後端溝通，只管：
要不要顯示「登入按鈕」
要不要顯示「使用者選單」
顯示使用者名稱
登出時清掉本機資料並跳頁
 */

const AuthUI = {
    updateState() {
        // Check local storage or cookie purely for UI display state
        const user = localStorage.getItem('user'); // Basic check
        const loginBtn = document.querySelector('.nav_login');
        const userMenu = document.querySelector('.nav_user');

        if (user && loginBtn && userMenu) {
            loginBtn.style.display = 'none';
            userMenu.classList.remove('d-none');

            try {
                const userData = JSON.parse(user);
                const nameEl = userMenu.querySelector('.user_name');
                if (nameEl) nameEl.textContent = userData.name || 'User';
            } catch (e) {
                console.error('Auth Parse Error', e);
            }
        } else if (loginBtn && userMenu) {
            loginBtn.style.display = 'block';
            userMenu.classList.add('d-none');
        }
    },

    logout() {
        localStorage.removeItem('user');
        window.location.href = 'index.html';
    }
};

export default AuthUI;
