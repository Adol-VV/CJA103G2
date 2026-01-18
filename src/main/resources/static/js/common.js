/**
 * Momento 共用 JavaScript 模組
 * 處理登入狀態、購物車、API 請求、安全工具等共用功能
 */

const Momento = (function () {
    'use strict';

    // ========================================
    // 設定
    // ========================================
    const CONFIG = {
        API_BASE_URL: '/api/v1',
        STORAGE_KEYS: {
            USER: 'momento_user',
            TOKEN: 'momento_token',
            CART: 'momento_cart',
            ADMIN_USER: 'momento_admin_user'
        }
    };

    // ========================================
    // 安全工具模組（XSS 防護）
    // ========================================
    const Security = {
        /**
         * 轉義 HTML 特殊字元，防止 XSS
         * @param {string} text - 原始文字
         * @returns {string} 轉義後的安全文字
         */
        escapeHtml: function (text) {
            if (text === null || text === undefined) return '';
            const div = document.createElement('div');
            div.textContent = String(text);
            return div.innerHTML;
        },

        /**
         * 安全地設定元素的文字內容
         * @param {Element|string} element - DOM 元素或選擇器
         * @param {string} text - 要設定的文字
         */
        setText: function (element, text) {
            if (typeof element === 'string') {
                element = document.querySelector(element);
            }
            if (element) {
                element.textContent = text;
            }
        },

        /**
         * 建立帶有安全文字的 HTML 元素
         * @param {string} tag - 標籤名稱
         * @param {object} attrs - 屬性物件
         * @param {string} text - 文字內容
         * @returns {Element}
         */
        createElement: function (tag, attrs = {}, text = '') {
            const el = document.createElement(tag);
            Object.keys(attrs).forEach(key => {
                if (key === 'className') {
                    el.className = attrs[key];
                } else if (key === 'dataset') {
                    Object.assign(el.dataset, attrs[key]);
                } else {
                    el.setAttribute(key, attrs[key]);
                }
            });
            if (text) {
                el.textContent = text;
            }
            return el;
        },

        /**
         * 安全的模板渲染（僅允許白名單標籤）
         * @param {string} template - HTML 模板
         * @param {object} data - 資料物件（值會被轉義）
         * @returns {string}
         */
        renderTemplate: function (template, data) {
            return template.replace(/\{\{(\w+)\}\}/g, (match, key) => {
                return data.hasOwnProperty(key) ? this.escapeHtml(data[key]) : match;
            });
        }
    };

    // ========================================
    // 工具函數模組
    // ========================================
    const Utils = {
        /**
         * 防抖函數
         * @param {Function} func - 要執行的函數
         * @param {number} wait - 等待時間（毫秒）
         * @returns {Function}
         */
        debounce: function (func, wait = 300) {
            let timeout;
            return function executedFunction(...args) {
                const later = () => {
                    clearTimeout(timeout);
                    func.apply(this, args);
                };
                clearTimeout(timeout);
                timeout = setTimeout(later, wait);
            };
        },

        /**
         * 節流函數
         * @param {Function} func - 要執行的函數
         * @param {number} limit - 限制時間（毫秒）
         * @returns {Function}
         */
        throttle: function (func, limit = 300) {
            let inThrottle;
            return function executedFunction(...args) {
                if (!inThrottle) {
                    func.apply(this, args);
                    inThrottle = true;
                    setTimeout(() => inThrottle = false, limit);
                }
            };
        },

        /**
         * 格式化金額
         * @param {number} amount - 金額
         * @returns {string}
         */
        formatCurrency: function (amount) {
            return 'NT$ ' + Number(amount).toLocaleString();
        },

        /**
         * 格式化日期
         * @param {string|Date} date - 日期
         * @param {string} format - 格式 ('date', 'datetime', 'time')
         * @returns {string}
         */
        formatDate: function (date, format = 'datetime') {
            const d = new Date(date);
            const pad = n => String(n).padStart(2, '0');
            
            const dateStr = `${d.getFullYear()}/${pad(d.getMonth() + 1)}/${pad(d.getDate())}`;
            const timeStr = `${pad(d.getHours())}:${pad(d.getMinutes())}`;
            
            if (format === 'date') return dateStr;
            if (format === 'time') return timeStr;
            return `${dateStr} ${timeStr}`;
        }
    };

    // ========================================
    // 認證模組
    // ========================================
    const Auth = {
        /**
         * 取得當前用戶
         */
        getUser: function () {
            const userData = localStorage.getItem(CONFIG.STORAGE_KEYS.USER);
            return userData ? JSON.parse(userData) : null;
        },

        /**
         * 取得 JWT Token
         */
        getToken: function () {
            return localStorage.getItem(CONFIG.STORAGE_KEYS.TOKEN);
        },

        /**
         * 檢查是否已登入
         */
        isLoggedIn: function () {
            return !!this.getToken();
        },

        /**
         * 儲存登入資訊
         */
        login: function (user, token) {
            localStorage.setItem(CONFIG.STORAGE_KEYS.USER, JSON.stringify(user));
            localStorage.setItem(CONFIG.STORAGE_KEYS.TOKEN, token);
        },

        /**
         * 登出
         * @param {boolean} redirect - 是否自動跳轉到登入頁
         */
        logout: function (redirect = true) {
            localStorage.removeItem(CONFIG.STORAGE_KEYS.USER);
            localStorage.removeItem(CONFIG.STORAGE_KEYS.TOKEN);
            if (redirect) {
                window.location.href = this.getLoginUrl();
            }
        },

        /**
         * 取得登入頁面 URL（根據當前頁面位置）
         */
        getLoginUrl: function () {
            const path = window.location.pathname;
            if (path.includes('/pages/')) {
                const depth = (path.match(/\//g) || []).length - 1;
                return '../'.repeat(depth - 1) + 'user/login.html';
            }
            return 'pages/user/login.html';
        },

        /**
         * 需要登入才能訪問（跳轉到登入頁）
         */
        requireLogin: function () {
            if (!this.isLoggedIn()) {
                window.location.href = this.getLoginUrl();
                return false;
            }
            return true;
        }
    };

    // ========================================
    // 購物車模組
    // ========================================
    const Cart = {
        items: [],

        /**
         * 初始化購物車
         */
        init: function () {
            const saved = localStorage.getItem(CONFIG.STORAGE_KEYS.CART);
            if (saved) {
                try {
                    this.items = JSON.parse(saved);
                } catch (e) {
                    console.error('Error loading cart:', e);
                    this.items = [];
                }
            }
            this.updateBadge();
        },

        /**
         * 儲存購物車
         */
        save: function () {
            localStorage.setItem(CONFIG.STORAGE_KEYS.CART, JSON.stringify(this.items));
            this.updateBadge();
        },

        /**
         * 新增商品到購物車
         */
        add: function (item) {
            const existing = this.items.find(i => i.id === item.id && i.type === item.type);
            if (existing) {
                existing.quantity += item.quantity || 1;
            } else {
                this.items.push({
                    id: item.id,
                    type: item.type, // 'ticket' or 'product'
                    name: item.name,
                    price: item.price,
                    quantity: item.quantity || 1,
                    image: item.image,
                    options: item.options || {}
                });
            }
            this.save();
            return true;
        },

        /**
         * 移除商品
         */
        remove: function (index) {
            this.items.splice(index, 1);
            this.save();
        },

        /**
         * 更新數量
         */
        updateQuantity: function (index, quantity) {
            if (quantity > 0 && quantity <= 99) {
                this.items[index].quantity = quantity;
                this.save();
            }
        },

        /**
         * 清空購物車
         */
        clear: function () {
            this.items = [];
            this.save();
        },

        /**
         * 取得商品數量
         */
        getCount: function () {
            return this.items.reduce((sum, item) => sum + item.quantity, 0);
        },

        /**
         * 取得小計
         */
        getSubtotal: function () {
            return this.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        },

        /**
         * 更新購物車徽章
         */
        updateBadge: function () {
            const count = this.getCount();
            const badges = document.querySelectorAll('.cart-count, .cart-badge');
            badges.forEach(badge => {
                if (count > 0) {
                    badge.textContent = count > 99 ? '99+' : count;
                    badge.style.display = 'flex';
                } else {
                    badge.style.display = 'none';
                }
            });
        }
    };

    // ========================================
    // API 請求模組
    // ========================================
    const API = {
        /**
         * 錯誤處理器
         */
        errorHandler: function (error, response = null) {
            // 網路錯誤
            if (!navigator.onLine) {
                UI.toast('網路連線中斷，請檢查您的網路設定', 'error');
                return;
            }

            // HTTP 狀態碼錯誤
            if (response) {
                switch (response.status) {
                    case 400:
                        UI.toast(error.message || '請求參數錯誤', 'error');
                        break;
                    case 401:
                        UI.toast('登入已過期，請重新登入', 'error');
                        Auth.logout(false); // 清除登入資料，但不自動跳轉
                        setTimeout(() => {
                            window.location.href = Auth.getLoginUrl();
                        }, 1500);
                        break;
                    case 403:
                        UI.toast('您沒有權限執行此操作', 'error');
                        break;
                    case 404:
                        UI.toast('請求的資源不存在', 'error');
                        break;
                    case 429:
                        UI.toast('請求過於頻繁，請稍後再試', 'error');
                        break;
                    case 500:
                        UI.toast('伺服器發生錯誤，請稍後再試', 'error');
                        // 如果是重要操作，可以跳轉到 500 頁面
                        if (error.critical) {
                            setTimeout(() => {
                                window.location.href = '/pages/public/500.html';
                            }, 2000);
                        }
                        break;
                    case 503:
                        UI.toast('服務暫時無法使用，請稍後再試', 'error');
                        break;
                    default:
                        UI.toast(error.message || '請求失敗，請稍後再試', 'error');
                }
            } else {
                // 其他錯誤（如超時、解析錯誤等）
                if (error.name === 'AbortError') {
                    UI.toast('請求超時，請檢查網路連線', 'error');
                } else {
                    UI.toast(error.message || '發生未知錯誤', 'error');
                }
            }

            // 記錄錯誤（生產環境可以發送到錯誤追蹤服務）
            console.error('API Error:', {
                message: error.message,
                status: response?.status,
                endpoint: error.endpoint,
                timestamp: new Date().toISOString()
            });
        },

        /**
         * 發送 API 請求
         */
        request: async function (endpoint, options = {}) {
            const url = CONFIG.API_BASE_URL + endpoint;
            const token = Auth.getToken();

            // 請求超時設定（預設 30 秒）
            const timeout = options.timeout || 30000;
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), timeout);

            const config = {
                method: options.method || 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    ...(token && { 'Authorization': `Bearer ${token}` }),
                    ...options.headers
                },
                signal: controller.signal
            };

            if (options.body) {
                config.body = JSON.stringify(options.body);
            }

            try {
                const response = await fetch(url, config);
                clearTimeout(timeoutId);

                let data;
                const contentType = response.headers.get('content-type');
                
                // 處理不同的回應格式
                if (contentType && contentType.includes('application/json')) {
                    data = await response.json();
                } else {
                    data = await response.text();
                }

                if (!response.ok) {
                    const error = new Error(data.message || data || 'Request failed');
                    error.endpoint = endpoint;
                    error.status = response.status;
                    this.errorHandler(error, response);
                    throw error;
                }

                return data;
            } catch (error) {
                clearTimeout(timeoutId);
                
                // 如果已經在 errorHandler 處理過，就不再重複處理
                if (!error.status) {
                    error.endpoint = endpoint;
                    this.errorHandler(error, null);
                }
                
                throw error;
            }
        },

        get: function (endpoint) {
            return this.request(endpoint, { method: 'GET' });
        },

        post: function (endpoint, body) {
            return this.request(endpoint, { method: 'POST', body });
        },

        put: function (endpoint, body) {
            return this.request(endpoint, { method: 'PUT', body });
        },

        delete: function (endpoint) {
            return this.request(endpoint, { method: 'DELETE' });
        }
    };

    // ========================================
    // UI 工具模組
    // ========================================
    const UI = {
        /**
         * 顯示 Toast 通知
         */
        toast: function (message, type = 'success') {
            const toastContainer = document.getElementById('toastContainer') || this.createToastContainer();

            const toast = document.createElement('div');
            toast.className = `toast align-items-center text-white bg-${type === 'error' ? 'danger' : type} border-0`;
            toast.setAttribute('role', 'alert');
            toast.innerHTML = `
                <div class="d-flex">
                    <div class="toast-body">
                        <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'} me-2"></i>
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            `;

            toastContainer.appendChild(toast);
            const bsToast = new bootstrap.Toast(toast, { delay: 3000 });
            bsToast.show();

            toast.addEventListener('hidden.bs.toast', () => toast.remove());
        },

        createToastContainer: function () {
            const container = document.createElement('div');
            container.id = 'toastContainer';
            container.className = 'toast-container position-fixed top-0 end-0 p-3';
            container.style.zIndex = '9999';
            document.body.appendChild(container);
            return container;
        },

        /**
         * 顯示載入中
         */
        showLoading: function (element) {
            if (typeof element === 'string') {
                element = document.querySelector(element);
            }
            if (element) {
                element.dataset.originalContent = element.innerHTML;
                element.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>載入中...';
                element.disabled = true;
            }
        },

        /**
         * 隱藏載入中
         */
        hideLoading: function (element) {
            if (typeof element === 'string') {
                element = document.querySelector(element);
            }
            if (element && element.dataset.originalContent) {
                element.innerHTML = element.dataset.originalContent;
                element.disabled = false;
            }
        },

        /**
         * 格式化價格
         */
        formatPrice: function (price) {
            return 'NT$ ' + price.toLocaleString();
        },

        /**
         * 格式化日期
         */
        formatDate: function (dateString, format = 'YYYY/MM/DD') {
            const date = new Date(dateString);
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');

            return format
                .replace('YYYY', year)
                .replace('MM', month)
                .replace('DD', day)
                .replace('HH', hours)
                .replace('mm', minutes);
        }
    };

    // ========================================
    // 導航模組
    // ========================================
    const Nav = {
        /**
         * 更新導航狀態（登入/未登入）
         */
        updateAuthState: function () {
            const user = Auth.getUser();
            const loginBtn = document.querySelector('.nav-login-btn');
            const userMenu = document.querySelector('.nav-user-menu');

            if (user && loginBtn && userMenu) {
                loginBtn.style.display = 'none';
                userMenu.style.display = 'block';
                const userName = userMenu.querySelector('.user-name');
                if (userName) userName.textContent = user.name;
            }
        },

        /**
         * 設置當前頁面的導航高亮
         */
        highlightCurrent: function () {
            const path = window.location.pathname;
            const navLinks = document.querySelectorAll('.nav-link');

            navLinks.forEach(link => {
                link.classList.remove('active');
                if (link.getAttribute('href') && path.includes(link.getAttribute('href'))) {
                    link.classList.add('active');
                }
            });
        },

        /**
         * 顯示空白狀態提示
         * @param {string|Element} container - 容器元素或選擇器
         * @param {object} options - 配置選項
         */
        showEmptyState: function(container, options = {}) {
            const defaults = {
                icon: 'fas fa-inbox',
                title: '暫無資料',
                message: '目前沒有符合條件的資料',
                actionText: null,
                actionHref: null,
                actionCallback: null
            };
            
            const config = { ...defaults, ...options };
            
            if (typeof container === 'string') {
                container = document.querySelector(container);
            }
            
            if (!container) return;
            
            const emptyStateHTML = `
                <div class="empty-state text-center py-5">
                    <div class="empty-icon mb-3">
                        <i class="${config.icon} fa-4x text-muted"></i>
                    </div>
                    <h4 class="text-white mb-2">${Security.escapeHtml(config.title)}</h4>
                    <p class="text-muted mb-4">${Security.escapeHtml(config.message)}</p>
                    ${config.actionText ? `
                        <button class="btn btn-primary empty-state-action">
                            ${Security.escapeHtml(config.actionText)}
                        </button>
                    ` : ''}
                </div>
            `;
            
            container.innerHTML = emptyStateHTML;
            
            // 綁定動作按鈕事件
            if (config.actionText) {
                const actionBtn = container.querySelector('.empty-state-action');
                if (actionBtn) {
                    actionBtn.addEventListener('click', function(e) {
                        e.preventDefault();
                        if (config.actionCallback) {
                            config.actionCallback();
                        } else if (config.actionHref) {
                            window.location.href = config.actionHref;
                        }
                    });
                }
            }
        }
    };

    // ========================================
    // 表單驗證模組
    // ========================================
    const Validation = {
        /**
         * 驗證 Email
         */
        isValidEmail: function (email) {
            return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
        },

        /**
         * 驗證手機號碼（台灣）
         */
        isValidPhone: function (phone) {
            return /^09\d{8}$/.test(phone.replace(/-/g, ''));
        },

        /**
         * 驗證密碼強度
         */
        getPasswordStrength: function (password) {
            if (password.length < 6) return 'weak';
            if (password.length < 10 || !/[A-Z]/.test(password) || !/[0-9]/.test(password)) return 'medium';
            return 'strong';
        }
    };

    // ========================================
    // 初始化
    // ========================================
    function init() {
        // 初始化購物車
        Cart.init();

        // 更新導航狀態
        Nav.updateAuthState();
        Nav.highlightCurrent();

        // 綁定全域購物車按鈕
        document.querySelectorAll('.btn-add-to-cart').forEach(btn => {
            btn.addEventListener('click', function () {
                const item = {
                    id: this.dataset.id,
                    type: this.dataset.type || 'product',
                    name: this.dataset.name,
                    price: parseInt(this.dataset.price),
                    image: this.dataset.image
                };

                if (Cart.add(item)) {
                    UI.toast('已加入購物車', 'success');
                    
                    // 按鈕動畫
                    const originalText = this.innerHTML;
                    this.innerHTML = '<i class="fas fa-check me-2"></i>已加入';
                    this.classList.remove('btn-primary');
                    this.classList.add('btn-success');
                    
                    setTimeout(() => {
                        this.innerHTML = originalText;
                        this.classList.remove('btn-success');
                        this.classList.add('btn-primary');
                    }, 1500);
                }
            });
        });
    }

    // DOM Ready 時初始化
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    // ========================================
    // 網路狀態監聽
    // ========================================
    window.addEventListener('online', function() {
        UI.toast('網路連線已恢復', 'success');
    });

    window.addEventListener('offline', function() {
        UI.toast('網路連線中斷', 'error');
    });

    // ========================================
    // 公開 API
    // ========================================
    return {
        Auth,
        Cart,
        API,
        UI,
        Nav,
        Validation,
        Security,
        Utils,
        CONFIG
    };
})();

// 為了方便使用，也導出到全域
window.Momento = Momento;

// 便捷函數（全域可用）
window.escapeHtml = Momento.Security.escapeHtml;
window.debounce = Momento.Utils.debounce;
window.showToast = function(message, type) {
    Momento.UI.toast(message, type);
};

