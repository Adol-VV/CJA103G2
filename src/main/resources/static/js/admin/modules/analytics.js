export function initAnalytics() {
    initAnalyticsCharts();
    window.refreshAnalytics = refreshAnalytics;
}

function initAnalyticsCharts() {
    const revenueCtx = document.getElementById('revenueChart');
    if (revenueCtx) {
        new Chart(revenueCtx, {
            type: 'line',
            data: {
                labels: ['12/1', '12/5', '12/10', '12/15', '12/20', '12/25', '12/26'],
                datasets: [{
                    label: '票券收入',
                    data: [850000, 920000, 780000, 1100000, 1350000, 1580000, 1200000],
                    borderColor: '#3B82F6',
                    backgroundColor: 'rgba(59, 130, 246, 0.1)',
                    fill: true,
                    tension: 0.4
                }, {
                    label: '商品收入',
                    data: [280000, 350000, 290000, 420000, 510000, 680000, 450000],
                    borderColor: '#10B981',
                    backgroundColor: 'rgba(16, 185, 129, 0.1)',
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { labels: { color: '#9CA3AF' } } },
                scales: {
                    x: { ticks: { color: '#9CA3AF' }, grid: { color: 'rgba(255,255,255,0.1)' } },
                    y: {
                        ticks: { color: '#9CA3AF', callback: val => 'NT$ ' + (val / 10000) + '萬' },
                        grid: { color: 'rgba(255,255,255,0.1)' }
                    }
                }
            }
        });
    }

    const distCtx = document.getElementById('revenueDistChart');
    if (distCtx) {
        new Chart(distCtx, {
            type: 'doughnut',
            data: {
                labels: ['票券收入', '商品收入', '平台抽成'],
                datasets: [{
                    data: [68, 25, 7],
                    backgroundColor: ['#3B82F6', '#10B981', '#06B6D4'],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '70%',
                plugins: { legend: { display: false } }
            }
        });
    }
}

function refreshAnalytics() {
    if (window.showToast) window.showToast('數據刷新中...', 'info');
    setTimeout(() => {
        if (window.showToast) window.showToast('數據已更新', 'success');
    }, 1000);
}
