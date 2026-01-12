/**
 * 報表匯出模組
 */
import { showToast, downloadBlob } from './utils.js';

export function initReports() {
    // Reports specific UI initialization if any
}

export function exportOrganizerReport(format) {
    const formatNames = { 'csv': 'CSV', 'excel': 'Excel', 'pdf': 'PDF' };

    // Show loading
    showToast(`正在產生 ${formatNames[format]} 報表...`, 'info');

    setTimeout(() => {
        const data = [
            { date: '2024/12/25', event: '2025新年音樂會', type: '票券', quantity: 15, amount: 42000 },
            { date: '2024/12/24', event: '2025新年音樂會', type: '票券', quantity: 28, amount: 78400 },
            { date: '2024/12/23', event: '冬季周邊禮盒', type: '商品', quantity: 45, amount: 22500 },
            { date: '2024/12/22', event: '2025新年音樂會', type: '票券', quantity: 32, amount: 89600 },
            { date: '2024/12/21', event: '冬季周邊禮盒', type: '商品', quantity: 18, amount: 9000 }
        ];

        if (format === 'csv') {
            const headers = ['日期', '活動/商品', '類型', '數量', '金額'];
            const csvContent = [
                headers.join(','),
                ...data.map(row => [
                    row.date,
                    `"${row.event}"`,
                    row.type,
                    row.quantity,
                    row.amount
                ].join(','))
            ].join('\n');

            const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' });
            downloadBlob(blob, 'sales-report.csv');
        } else if (format === 'excel') {
            const headers = ['日期', '活動/商品', '類型', '數量', '金額'];
            const content = [
                headers.join('\t'),
                ...data.map(row => [row.date, row.event, row.type, row.quantity, row.amount].join('\t'))
            ].join('\n');

            const blob = new Blob(['\uFEFF' + content], { type: 'application/vnd.ms-excel;charset=utf-8;' });
            downloadBlob(blob, 'sales-report.xlsx');
        } else if (format === 'pdf') {
            alert('PDF 報表產生完成！\n\n（完整功能需要整合 jsPDF 或後端 API）');
        }

        showToast(`${formatNames[format]} 報表已下載！`, 'success');
    }, 1500);
}
