export function checkAuth() {
    // 後端 Session 驗證
    // 如果沒有登入,後端會自動導向登入頁面
    // 這裡只需要設定前端的使用者名稱

    // 從 Thymeleaf 傳入的資料設定使用者名稱
    // 這部分應該在 HTML 中由 Thymeleaf 設定

    return true;
}

// 初始化管理員資訊 (由 Thymeleaf 設定)
export function initAdminInfo(empName, isSuperAdmin) {
    if (empName) {
        $('#adminName').text(empName);
    }

    // 如果不是超級管理員,隱藏某些功能
    if (!isSuperAdmin) {
        // 可以根據權限隱藏某些選單項目
        // 例如: $('.nav-link[data-super-admin-only]').parent().hide();
    }
}
