/**
 * Momento 前端配置檔
 * 
 * 資料庫對照：MySQL 8.0+ / momento database
 * 後端框架：Spring Boot (預期)
 */

export const API_CONFIG = {
    api: {
        // API 基礎路徑
        // 開發環境：使用相對路徑或本地後端
        // 生產環境：改為實際 API 網址
        baseUrl: '/api/v1',
        
        // 是否使用真實 API（false = 使用 Mock 資料）
        useRealAPI: false,
        
        // API 請求超時時間（毫秒）
        timeout: 30000
    },
    
    // LocalStorage 鍵名
    storage: {
        memberUser: 'momento_member',           // 會員資料
        memberToken: 'momento_member_token',    // 會員 JWT
        organizerUser: 'momento_organizer',     // 主辦方資料
        organizerToken: 'momento_organizer_token', // 主辦方 JWT
        adminUser: 'momento_admin',             // 管理員資料
        adminToken: 'momento_admin_token',      // 管理員 JWT
        cart: 'momento_cart'                    // 購物車
    },
    
    // 分頁預設值
    pagination: {
        defaultPageSize: 10,
        maxPageSize: 100
    }
};

/**
 * 資料庫欄位對照表
 * 前端 camelCase ↔ 資料庫 UPPER_SNAKE_CASE
 */
export const DB_FIELD_MAPPING = {
    // ========================================
    // MEMBER 會員
    // ========================================
    member: {
        memberId: 'MEMBER_ID',
        account: 'ACCOUNT',
        password: 'PASSWORD',
        name: 'NAME',
        address: 'ADDRESS',
        phone: 'PHONE',
        token: 'TOKEN',              // 代幣數量
        status: 'STATUS',            // 0:正常 1:停權
        createdAt: 'CREATED_AT'
    },
    
    // ========================================
    // ORGANIZER 主辦方
    // ========================================
    organizer: {
        organizerId: 'ORGANIZER_ID',
        account: 'ACCOUNT',
        password: 'PASSWORD',
        name: 'NAME',
        address: 'ADDRESS',
        phone: 'PHONE',
        introduction: 'INTRODUCTION',
        status: 'STATUS',            // 0:待審核 1:正常 2:停權
        createdAt: 'CREATED_AT'
    },
    
    // ========================================
    // EMP 後台管理員
    // ========================================
    emp: {
        empId: 'EMP_ID',
        empName: 'EMP_NAME',
        account: 'ACCOUNT',
        password: 'PASSWORD',
        createdAt: 'CREATED_AT',
        status: 'STATUS'             // 0:離職 1:在職
    },
    
    // ========================================
    // SORT 商品類別
    // ========================================
    sort: {
        sortId: 'SORT_ID',
        sortName: 'SORT_NAME',
        sortDesc: 'SORT_DESC'
    },
    
    // ========================================
    // PROD 商品
    // ========================================
    prod: {
        prodId: 'PROD_ID',
        organizerId: 'ORGANIZER_ID',
        sortId: 'SORT_ID',
        empId: 'EMP_ID',
        prodName: 'PROD_NAME',
        prodContent: 'PROD_CONTENT',
        prodPrice: 'PROD_PRICE',
        prodStock: 'PROD_STOCK',
        createdAt: 'CREATED_AT',
        updatedAt: 'UPDATED_AT',
        prodStatus: 'PROD_STATUS',   // 0:未上架 1:已上架
        reviewStatus: 'REVIEW_STATUS' // 0:待審核 1:通過 2:未通過
    },
    
    // ========================================
    // TYPE 活動類型
    // ========================================
    type: {
        typeId: 'TYPE_ID',
        typeName: 'TYPE_NAME',
        note: 'NOTE'
    },
    
    // ========================================
    // EVENT 活動
    // ========================================
    event: {
        eventId: 'EVENT_ID',
        organizerId: 'ORGANIZER_ID',
        typeId: 'TYPE_ID',
        empId: 'EMP_ID',
        status: 'STATUS',            // 0:未上架 1:已上架
        reviewStatus: 'REVIEW_STATUS', // 0:待審核 1:通過 2:未通過
        title: 'TITLE',
        content: 'CONTENT',
        place: 'PLACE',
        startedAt: 'STARTED_AT',     // 購票開始時間
        endedAt: 'ENDED_AT',         // 購票結束時間
        eventAt: 'EVENT_AT',         // 活動舉辦時間
        publishedAt: 'PUBLISHED_AT'
    },
    
    // ========================================
    // TICKET 票種
    // ========================================
    ticket: {
        ticketId: 'TICKET_ID',
        eventId: 'EVENT_ID',
        price: 'PRICE',
        total: 'TOTAL',
        remain: 'REMAIN',
        ticketName: 'TICKET_NAME'
    },
    
    // ========================================
    // PROD_ORDER 商品訂單
    // ========================================
    prodOrder: {
        prodOrderId: 'PROD_ORDER_ID',
        memberId: 'MEMBER_ID',
        organizerId: 'ORGANIZER_ID',
        createdAt: 'CREATED_AT',
        total: 'TOTAL',
        tokenUsed: 'TOKEN_USED',
        payable: 'PAYABLE',
        payStatus: 'PAY_STATUS'      // 0:未付款 1:已付款
    },
    
    // ========================================
    // EVENT_ORDER 票券訂單
    // ========================================
    eventOrder: {
        eventOrderId: 'EVENT_ORDER_ID',
        memberId: 'MEMBER_ID',
        organizerId: 'ORGANIZER_ID',
        eventId: 'EVENT_ID',
        total: 'TOTAL',
        tokenUsed: 'TOKEN_USED',
        payable: 'PAYABLE',
        payStatus: 'PAY_STATUS',     // 0:未付款 1:已付款 2:申請退款 3:已退款
        createdAt: 'CREATED_AT'
    },
    
    // ========================================
    // EVENT_ORDER_ITEM 票券訂單明細
    // ========================================
    eventOrderItem: {
        eventOrderItemId: 'EVENT_ORDER_ITEM_ID',
        eventOrderId: 'EVENT_ORDER_ID',
        ticketId: 'TICKET_ID',
        qrcode: 'QRCODE',
        price: 'PRICE',
        total: 'TOTAL'
    },
    
    // ========================================
    // ARTICLE 文章
    // ========================================
    article: {
        articleId: 'ARTICLE_ID',
        organizerId: 'ORGANIZER_ID',
        title: 'TITLE',
        content: 'CONTENT',
        createdAt: 'CREATED_AT',
        updatedAt: 'UPDATED_AT'
    },
    
    // ========================================
    // MESSAGE 留言
    // ========================================
    message: {
        messageId: 'MESSAGE_ID',
        memberId: 'MEMBER_ID',
        articleId: 'ARTICLE_ID',
        content: 'CONTENT',
        createdAt: 'CREATED_AT',
        updatedAt: 'UPDATED_AT',
        status: 'STATUS'             // 0:正常顯示 1:隱藏
    },
    
    // ========================================
    // MESSAGE_REPORT 留言檢舉
    // ========================================
    messageReport: {
        messageReportId: 'MESSAGE_REPORT_ID',
        messageId: 'MESSAGE_ID',
        memberId: 'MEMBER_ID',
        empId: 'EMP_ID',
        reportedAt: 'REPORTED_AT',
        reportReason: 'REPORT_REASON',
        status: 'STATUS'             // 0:未處理 1:成功 2:失敗
    },
    
    // ========================================
    // SYS_NOTIFY 系統通知
    // ========================================
    sysNotify: {
        sysNotifyId: 'SYS_NOTIFY_ID',
        memberId: 'MEMBER_ID',
        empId: 'EMP_ID',
        title: 'TITLE',
        content: 'CONTENT',
        isRead: 'IS_READ',           // 0:未讀 1:已讀
        createdAt: 'CREATED_AT'
    },
    
    // ========================================
    // ORG_NOTIFY 主辦方通知
    // ========================================
    orgNotify: {
        orgNotifyId: 'ORG_NOTIFY_ID',
        organizerId: 'ORGANIZER_ID',
        empId: 'EMP_ID',
        title: 'TITLE',
        content: 'CONTENT',
        isRead: 'IS_READ',
        createdAt: 'CREATED_AT'
    },
    
    // ========================================
    // ANNOUNCEMENT 公告欄
    // ========================================
    announcement: {
        announcementId: 'ANNOUNCEMENT_ID',
        empId: 'EMP_ID',
        title: 'TITLE',
        content: 'CONTENT',
        createdAt: 'CREATED_AT',
        updatedAt: 'UPDATED_AT'
    },
    
    // ========================================
    // EVENT_SETTLE 活動結算
    // ========================================
    eventSettle: {
        eventSettleId: 'EVENT_SETTLE_ID',
        organizerId: 'ORGANIZER_ID',
        eventId: 'EVENT_ID',
        sales: 'SALES',
        payable: 'PAYABLE',
        status: 'STATUS',            // 0:未付款 1:已付款
        createdAt: 'CREATED_AT',
        paidAt: 'PAID_AT'
    },
    
    // ========================================
    // PROD_SETTLE 商品結算
    // ========================================
    prodSettle: {
        prodSettleId: 'PROD_SETTLE_ID',
        organizerId: 'ORGANIZER_ID',
        sales: 'SALES',
        payable: 'PAYABLE',
        status: 'STATUS',
        createdAt: 'CREATED_AT',
        paidAt: 'PAID_AT'
    }
};

/**
 * 狀態碼對照
 */
export const STATUS_CODES = {
    // 會員狀態
    memberStatus: {
        0: '正常',
        1: '停權'
    },
    
    // 主辦方狀態
    organizerStatus: {
        0: '待審核',
        1: '正常',
        2: '停權'
    },
    
    // 員工狀態
    empStatus: {
        0: '離職',
        1: '在職'
    },
    
    // 商品/活動上架狀態
    itemStatus: {
        0: '未上架',
        1: '已上架'
    },
    
    // 審核狀態
    reviewStatus: {
        0: '待審核',
        1: '通過',
        2: '未通過'
    },
    
    // 商品訂單付款狀態
    prodOrderPayStatus: {
        0: '未付款',
        1: '已付款'
    },
    
    // 票券訂單付款狀態
    eventOrderPayStatus: {
        0: '未付款',
        1: '已付款',
        2: '申請退款',
        3: '已退款'
    },
    
    // 留言狀態
    messageStatus: {
        0: '正常顯示',
        1: '隱藏'
    },
    
    // 檢舉處理狀態
    reportStatus: {
        0: '未處理',
        1: '成功',
        2: '失敗'
    },
    
    // 通知已讀狀態
    readStatus: {
        0: '未讀',
        1: '已讀'
    },
    
    // 結算狀態
    settleStatus: {
        0: '未給付',
        1: '已給付'
    }
};

/**
 * 活動類型 (對應 TYPE 表)
 */
export const EVENT_TYPES = [
    { id: 1, name: '音樂演出', note: '古典音樂會、爵士演出、獨奏會、室內樂' },
    { id: 2, name: '藝術展覽', note: '繪畫展、攝影展、雕塑展、複合媒材展' },
    { id: 3, name: '戲劇表演', note: '舞台劇、音樂劇、實驗劇場、傳統戲曲' },
    { id: 4, name: '藝文講座', note: '作家講座、藝術家分享、文化論壇' },
    { id: 5, name: '手作工坊', note: '陶藝體驗、版畫製作、染織工作坊' },
    { id: 6, name: '電影放映', note: '藝術電影放映、紀錄片放映、影人座談' }
];

/**
 * 商品類別 (對應 SORT 表)
 */
export const PRODUCT_SORTS = [
    { id: 1, name: '藝術服飾', desc: '藝術家聯名服飾、展覽紀念T恤、文創配件' },
    { id: 2, name: '音樂出版品', desc: '演出實況錄音、黑膠唱片、限量版CD專輯' },
    { id: 3, name: '藝術印刷品', desc: '藝術海報、版畫複製品、明信片、藝術書籤' },
    { id: 4, name: '文創生活用品', desc: '藝術家設計馬克杯、帆布袋、手工皂、香氛蠟燭' },
    { id: 5, name: '收藏藝品', desc: '限量版畫、藝術家簽名作品、手工藝品' },
    { id: 6, name: '工藝材料包', desc: '手作DIY材料包、陶藝工具組、染織體驗包' }
];

/**
 * 後台功能權限 (對應 FUNCTION 表)
 */
export const ADMIN_FUNCTIONS = [
    { id: 1, name: '會員管理' },
    { id: 2, name: '主辦方管理' },
    { id: 3, name: '商品審核' },
    { id: 4, name: '活動審核' },
    { id: 5, name: '訂單管理' },
    { id: 6, name: '結算管理' },
    { id: 7, name: '公告管理' },
    { id: 8, name: '檢舉處理' }
];

// 為非模組環境提供全域變數
if (typeof window !== 'undefined') {
    window.API_CONFIG = API_CONFIG;
    window.DB_FIELD_MAPPING = DB_FIELD_MAPPING;
    window.STATUS_CODES = STATUS_CODES;
}
