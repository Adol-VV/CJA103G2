/**
 * QR Code 掃描與票券驗證模組
 */
import { showToast } from './utils.js';

let videoStream = null;
let scanning = false;

export function initScanner() {
	$(document).on('click', '#btnStartScanner', () => startScanner());
	$(document).on('click', '#btnStopScanner', () => stopScanner());
	$(document).on('click', '#btnManualVerify', () => verifyTicketManually());

	// 手動輸入票券驗證
	$(document).on('keypress', '#manualTicketCode', function(e) {
		if (e.which === 13) verifyTicketManually();
	});

	// Make resetVerification globally available or attached to window as original code did
	// or handle it via event delegation if possible. 
	// The original HTML calls `resetVerification()` in onclick.
	window.resetVerification = resetVerification;
	$("#scannerEventSelect").on("change", function() {
		// 取得選單元素
		const selector = document.getElementById('scannerEventSelect');

		// 取得選中的 eventId
		const selectedId = selector.value;

		const url = `/organizer/dashboard/ticketScanner?eventId=${selectedId}`;

		fetch(url, {
			method: 'GET',
			headers: {
				'X-Requested-With': 'XMLHttpRequest' // 標記為非同步請求
			}
		})
			.then(response => response.text())
			.then(html => {
				// 關鍵：將回傳的 HTML 直接替換掉原本的表格內容
				document.getElementById('orderListContainer').innerHTML = html;
				console.log("回傳的內容：", html);
			})
			.catch(error => console.error('Error:', error));
	})
}

function startScanner() {
	navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } })
		.then(function(stream) {
			videoStream = stream;
			const video = document.getElementById('scannerVideo');
			if (!video) {
				console.error('Scanner video element not found');
				return;
			}
			video.srcObject = stream;
			video.play();
			scanning = true;
			$('#scannerPlaceholder').addClass('d-none');
			$('#scannerFrame').removeClass('d-none');
			$('#btnStartScanner').prop('disabled', true);
			$('#btnStopScanner').prop('disabled', false);
			requestAnimationFrame(scanFrame);
			showToast('掃描器已啟動', 'success');
		})
		.catch(err => showToast('無法啟動攝影機: ' + err.message, 'error'));
}

function stopScanner() {
	scanning = false;
	if (videoStream) {
		videoStream.getTracks().forEach(t => t.stop());
		$('#scannerPlaceholder').removeClass('d-none');
		$('#scannerFrame').addClass('d-none');
		$('#btnStartScanner').prop('disabled', false);
		$('#btnStopScanner').prop('disabled', true);
		const video = document.getElementById('scannerVideo');
		if (video) video.srcObject = null;
		videoStream = null;
	}
}

function verifyTicketManually() {
	const uuid = document.getElementById('manualTicketCode').value;
	const eventId = document.getElementById('scannerEventSelect').value;
	const url = `/organizer/dashboard/ticketScanner?randomUUID=${uuid}&eventId=${eventId}`;
	
	console.log(uuid);
	if(uuid.trim() == ""){
		alert("請輸入票券代碼");
		return;
	}

	fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
		.then(res => res.text())
		.then(html => {
			// 只更新右側的「驗證結果」區塊
			document.getElementById('verificationResult').innerHTML = html;
			// 核銷完自動清空輸入框
			document.getElementById('manualTicketCode').value = '';
			if (html.includes("核銷成功")) {
				updateRecordTableManually();
			}
		});


}

function scanFrame() {
	if (!scanning) return;
	const video = document.getElementById('scannerVideo');
	if (!video) return;

	// 創建臨時 canvas 進行 QR 解碼
	const canvas = document.createElement('canvas');
	const ctx = canvas.getContext('2d');

	if (video.readyState === video.HAVE_ENOUGH_DATA) {
		canvas.width = video.videoWidth;
		canvas.height = video.videoHeight;
		ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
		const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);

		// 如果有 jsQR 庫則使用
		if (typeof jsQR !== 'undefined') {
			const code = jsQR(imageData.data, imageData.width, imageData.height);
			if (code) {
				verifyTicket(code.data);
				return;
			}
		}
	}
	requestAnimationFrame(scanFrame);
}


function showVerificationSuccess(ticketCode) {
	const container = $('#verificationResult');
	container.html(`
        <div class="text-center">
            <div class="bg-success bg-opacity-10 rounded-circle d-inline-flex align-items-center justify-content-center mb-3" 
                    style="width: 80px; height: 80px;">
                <i class="fas fa-check-circle fa-3x text-success"></i>
            </div>
            <h4 class="text-success mb-3">核銷成功</h4>
            <div class="text-start bg-dark rounded p-3 mb-3">
                <div class="row mb-2">
                    <div class="col-4 text-muted">票券代碼</div>
                    <div class="col-8"><code>${ticketCode}</code></div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 text-muted">票種</div>
                    <div class="col-8">貴賓席</div>
                </div>
                <div class="row mb-2">
                    <div class="col-4 text-muted">購票人</div>
                    <div class="col-8">林書涵</div>
                </div>
                <div class="row">
                    <div class="col-4 text-muted">訂單編號</div>
                    <div class="col-8">#MOM2024123101</div>
                </div>
            </div>
            <button class="btn btn-primary w-100" onclick="resetVerification()">
                <i class="fas fa-qrcode me-2"></i>繼續掃描
            </button>
        </div>
    `);
	stopScanner();
	showToast('票券核銷成功！', 'success');
}

function showVerificationWarning(ticketCode, message) {
	const container = $('#verificationResult');
	container.html(`
        <div class="text-center">
            <div class="bg-warning bg-opacity-10 rounded-circle d-inline-flex align-items-center justify-content-center mb-3" 
                    style="width: 80px; height: 80px;">
                <i class="fas fa-exclamation-triangle fa-3x text-warning"></i>
            </div>
            <h4 class="text-warning mb-3">注意</h4>
            <p class="text-muted mb-3">${message}</p>
            <div class="bg-dark rounded p-3 mb-3">
                <p class="mb-1"><small class="text-muted">票券代碼</small></p>
                <code>${ticketCode}</code>
                <p class="mt-2 mb-0"><small class="text-warning">此票券已於 18:30:22 核銷</small></p>
            </div>
            <button class="btn btn-outline-warning w-100" onclick="resetVerification()">
                <i class="fas fa-redo me-2"></i>重新掃描
            </button>
        </div>
    `);
	stopScanner();
	showToast(message, 'warning');
}

function showVerificationError(message) {
	const container = $('#verificationResult');
	container.html(`
        <div class="text-center">
            <div class="bg-danger bg-opacity-10 rounded-circle d-inline-flex align-items-center justify-content-center mb-3" 
                    style="width: 80px; height: 80px;">
                <i class="fas fa-times-circle fa-3x text-danger"></i>
            </div>
            <h4 class="text-danger mb-3">驗證失敗</h4>
            <p class="text-muted mb-4">${message}</p>
            <button class="btn btn-outline-danger w-100" onclick="resetVerification()">
                <i class="fas fa-redo me-2"></i>重新掃描
            </button>
        </div>
    `);
	stopScanner();
	showToast(message, 'error');
}

export function resetVerification() {
	$('#verificationResult').html(`
        <div class="text-center text-muted py-5">
            <i class="fas fa-qrcode fa-4x mb-3"></i>
            <p>等待掃描票券...</p>
        </div>
    `);
}

function updateStats() {
	const verified = parseInt($('#statVerified').text()) + 1;
	const pending = parseInt($('#statPending').text()) - 1;
	const total = parseInt($('#statTotal').text());
	const rate = Math.round((verified / total) * 100);

	$('#statVerified').text(verified);
	$('#statPending').text(pending);
	$('.progress-bar').css('width', rate + '%');
	$('.progress').next('small').text('入場率：' + rate + '%');
}

function addVerificationRecord(code, success, reason) {
	const now = new Date();
	const timeStr = now.toTimeString().slice(0, 8);
	const rowClass = success ? '' : 'table-danger';
	const statusBadge = success
		? '<span class="badge bg-success">已入場</span>'
		: `<span class="badge bg-danger">${reason || '驗證失敗'}</span>`;

	const newRow = `
        <tr class="${rowClass}">
            <td>${timeStr}</td>
            <td><code>${code}</code></td>
            <td>${success ? '貴賓席' : '-'}</td>
            <td>${success ? '林書涵' : '-'}</td>
            <td>${statusBadge}</td>
            <td>工作人員A</td>
        </tr>
    `;
	$('#recentVerifications').prepend(newRow);
}
function updateRecordTableManually() {
	// 從畫面上獲取剛剛核銷成功的資訊 (對應圖2的欄位)
	const now = new Date(); // 假設你給票券編號一個 ID
	const ticketId = document.querySelector("#info-ticket-id").innerText;
	const ticketName = document.querySelector("#info-ticket-name").innerText;
	const userName = document.querySelector("#info-user-name").innerText;
	
	const formattedTime = now.getFullYear() + '-' + 
	    String(now.getMonth() + 1).padStart(2, '0') + '-' + 
	    String(now.getDate()).padStart(2, '0') + ' ' + 
	    String(now.getHours()).padStart(2, '0') + ':' + 
	    String(now.getMinutes()).padStart(2, '0');

	// 找到紀錄表格的 tbody
	const tbody = document.querySelector("#recentVerifications");

	// 建立新的 HTML Row
	const newRow = `
        <tr>
            <td><code>${formattedTime}</code></td>
			<td><code>${ticketId}</code></td>
            <td>${ticketName}</td>
            <td>${userName}</td>
            <td><span class="badge bg-success">已核銷</span></td>
        </tr>
    `;

	// 插入到表格的最前面 (第一列)
	tbody.insertAdjacentHTML('afterbegin', newRow);
}
