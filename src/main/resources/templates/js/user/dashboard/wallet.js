/**
 * Wallet & Token UI
 * Handles Token Balance display and Transaction loading UI.
 * Removed backend simulation/calculation.
 */

const Wallet = {
    init() {
        // In real app, this would fetch from API
        // Here we just ensure UI elements are ready or bind events if any

        // Example: Bind 'Copy Referral' button
        $('#btnCopyReferral').click(function () {
            const link = $('#referralLink').val();
            if (link) {
                navigator.clipboard.writeText(link).then(() => {
                    if (window.showToast) window.showToast('已複製連結', 'success');
                });
            }
        });
    },

    updateBalance(balance) {
        // Public method to update UI from external data source
        $('#tokenBalanceDisplay').text(balance);
    }
};

export default Wallet;
