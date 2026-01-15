/**
 * QR Code UI Module
 * Handles QR code generation for the UI only.
 * No backend verification or signature generation.
 */

const QrCodeUI = {
    /**
     * Generate QR Code in a container
     * @param {string} ticketId - The ID to encode
     * @param {string} containerId - DOM ID of the container
     */
    generate: function (ticketId, containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        container.innerHTML = '';

        try {
            // Just encode the ID or simple data for display
            // Real validation happens on backend/scanner API
            new QRCode(container, {
                text: ticketId,
                width: 200,
                height: 200,
                colorDark: "#000000",
                colorLight: "#ffffff",
                correctLevel: QRCode.CorrectLevel.H
            });
        } catch (e) {
            console.error('QR Gen Error:', e);
        }
    },

    /**
     * Batch generate for a class of containers
     */
    generateBatch: function (ticketIds, containerClass) {
        const containers = document.querySelectorAll(`.${containerClass}`);
        ticketIds.forEach((id, index) => {
            if (containers[index]) {
                this.generate(id, containers[index].id);
            }
        });
    }
};

export default QrCodeUI;
