/**
 * Ticket & QR Code UI
 * Handles QR Modal display and Download.
 * Removed backend simulation.
 */

const Tickets = {
    init() {
        this.bindEvents();
    },

    bindEvents() {
        // Show QR
        $(document).on('click', '.btn-show-qr', function () {
            const orderId = $(this).data('order');
            const qrCodeData = $(this).data('qr'); // Expecting QR data to be present in DOM or fetched via API

            $('#qrOrderId').text(orderId);

            // Assuming QRious is loaded globally or imported
            // If data is missing in "No Logic" mode, we might just show a placeholder or expect backend to have rendered it
            // For now, keeping the QR generation visual logic but removing the mock data creation.

            // If the button has no data, we cannot generate. 
            // In a real "Frontend Only" flow, the data usually comes from the backend api.
            // We will assume the backend passes the string to generate.

            const qrValue = qrCodeData || `MOMENTO-TICKET-${orderId}`; // Fallback for demo

            if (window.QRious) {
                new QRious({
                    element: document.getElementById('qrCanvas'),
                    value: qrValue,
                    size: 200,
                    foreground: '#000000',
                    background: '#ffffff'
                });
            }

            $('#qrModal').modal('show');
        });

        // Download QR
        $('#btnDownloadQR').click(function () {
            const canvas = document.getElementById('qrCanvas');
            if (canvas) {
                const image = canvas.toDataURL("image/png");
                const link = document.createElement('a');
                link.download = `ticket-${$('#qrOrderId').text()}.png`;
                link.href = image;
                link.click();
            }
        });
    }
};

export default Tickets;
