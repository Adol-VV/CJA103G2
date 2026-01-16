export function initSettlement() {
    $(document).on('click', '.btn-view-settlement', function () {
        const id = $(this).data('id');
        $('#settlementDetailModal').modal('show');
    });

    window.exportReport = exportReport;
}

function exportReport(format) {
    const formatNames = { 'csv': 'CSV', 'excel': 'Excel', 'pdf': 'PDF' };
    if (window.Momento && window.Momento.Toast) {
        window.Momento.Toast.show(`正在產生 ${formatNames[format]} 報表...`, 'info');
    }

    setTimeout(() => {
        const data = [
            { event: '五月天 2024 巡迴', organizer: '相信音樂', revenue: 5000000, payable: 4500000, status: '待審核' },
            { event: '2024 動漫節', organizer: '動漫協進會', revenue: 2000000, payable: 1800000, status: '計算中' },
            { event: '2025新年音樂會', organizer: 'Momento 官方', revenue: 1500000, payable: 1350000, status: '已撥款' }
        ];

        if (format === 'csv') downloadCSV(data, 'settlement-report.csv');
        else if (format === 'excel') downloadExcel(data, 'settlement-report.xlsx');
        else if (format === 'pdf') downloadPDF(data, 'settlement-report.pdf');

        if (window.Momento && window.Momento.Toast) {
            window.Momento.Toast.show(`${formatNames[format]} 報表已下載！`, 'success');
        }
    }, 1500);
}

function downloadCSV(data, filename) {
    const headers = ['活動名稱', '主辦方', '總營收', '應付金額', '狀態'];
    const csvContent = [
        headers.join(','),
        ...data.map(row => [`"${row.event}"`, `"${row.organizer}"`, row.revenue, row.payable, `"${row.status}"`].join(','))
    ].join('\n');
    const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' });
    triggerDownload(blob, filename);
}

function downloadExcel(data, filename) {
    const headers = ['活動名稱', '主辦方', '總營營收', '應付金額', '狀態'];
    const csvContent = [
        headers.join('\t'),
        ...data.map(row => [row.event, row.organizer, row.revenue, row.payable, row.status].join('\t'))
    ].join('\n');
    const blob = new Blob(['\uFEFF' + csvContent], { type: 'application/vnd.ms-excel;charset=utf-8;' });
    triggerDownload(blob, filename);
}

function downloadPDF(data, filename) {
    alert('PDF 報表產生完成！\n\n（完整功能需要整合 jsPDF 或後端 API）');
}

function triggerDownload(blob, filename) {
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(link.href);
}
