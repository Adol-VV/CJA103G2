/**
 * 圖表模組
 */
export function initDashboardCharts() {
    if (typeof Chart === 'undefined') return;

    Chart.defaults.color = '#9A9A9A';
    Chart.defaults.borderColor = '#333';

    if (document.getElementById('salesChart')) {
        const ctxSales = document.getElementById('salesChart').getContext('2d');
        // Prevent re-initialization error if canvas is reused?
        // Chart.js usually handles this if we destroy previous instance, 
        // but here we just create new one as assumed fresh load or idempotent enough for demo.

        const salesChart = new Chart(ctxSales, {
            type: 'line',
            data: {
                labels: ['12/19', '12/20', '12/21', '12/22', '12/23', '12/24', '12/25'],
                datasets: [{
                    label: '票券銷售',
                    data: [45000, 52000, 48000, 67000, 73000, 65000, 82000],
                    borderColor: '#2D5F4F',
                    backgroundColor: 'rgba(45,95,79,0.1)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: { responsive: true, maintainAspectRatio: false }
        });

        // Hover Animation Effect
        const canvas = document.getElementById('salesChart');
        const originalData = [...salesChart.data.datasets[0].data];
        let animationInterval;

        canvas.addEventListener('mouseenter', () => {
            // Start wild animation
            animationInterval = setInterval(() => {
                const newData = originalData.map(val => {
                    // Exaggerated fluctuation: +/- 30%
                    const variance = val * 0.3;
                    const offset = (Math.random() * variance * 2) - variance;
                    return Math.max(0, val + offset); // Prevent negative values
                });
                salesChart.data.datasets[0].data = newData;
                salesChart.update(); // Enable animation for smooth/slower feel
            }, 500); // 500ms - Slower pace
        });

        canvas.addEventListener('mouseleave', () => {
            // Stop animation and reset
            clearInterval(animationInterval);
            salesChart.data.datasets[0].data = [...originalData];
            salesChart.update();
        });
    }
    if (document.getElementById('distributionChart')) {
        const ctxDist = document.getElementById('distributionChart').getContext('2d');
        new Chart(ctxDist, {
            type: 'doughnut',
            data: {
                labels: ['票券', '商品'],
                datasets: [{ data: [68, 32], backgroundColor: ['#2D5F4F', '#10B981'], borderWidth: 0 }]
            },
            options: { responsive: true, maintainAspectRatio: false, cutout: '70%', plugins: { legend: { display: false } } }
        });
    }
}
