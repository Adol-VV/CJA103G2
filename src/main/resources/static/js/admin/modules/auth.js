export function checkAuth() {
    const user = JSON.parse(localStorage.getItem('momento_user') || '{}');
    const isLoggedIn = localStorage.getItem('momento_admin_logged_in') === 'true';

    if (!user.role || user.role !== 'ADMIN' || !isLoggedIn) {
        alert('請先登入管理員帳號');
        window.location.href = 'login.html';
        return false;
    }

    let adminUser = JSON.parse(localStorage.getItem('momento_admin_user'));
    if (!adminUser) {
        adminUser = {
            name: 'Super Admin',
            role: 'admin',
            permissions: ['content_edit', 'content_approve', 'user_manage', 'finance_settle', 'order_refund', 'order_dispute', 'order_manage', 'system_notify']
        };
        localStorage.setItem('momento_admin_user', JSON.stringify(adminUser));
    }

    $('#adminName').text(adminUser.name);
    $('.nav-link[data-permission]').each(function () {
        if (!adminUser.permissions.includes($(this).data('permission'))) $(this).parent().hide();
    });

    return true;
}
