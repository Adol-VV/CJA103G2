# Momento API 端點規格

> 本文件列出前端需要的所有 API 端點，供後端開發參考

## 基礎資訊

- **Base URL**: `/api/v1`
- **認證**: JWT Bearer Token
- **回應格式**: JSON

```json
{
  "success": true,
  "data": { ... },
  "message": "成功"
}
```

---

## 1. 會員相關 (MEMBER)

| Method | Endpoint | 說明 | 認證 |
|--------|----------|------|------|
| POST | `/member/login` | 會員登入 | ❌ |
| POST | `/member/register` | 會員註冊 | ❌ |
| GET | `/member/{memberId}` | 取得會員資料 | ✅ |
| PUT | `/member/{memberId}` | 更新會員資料 | ✅ |
| PUT | `/member/{memberId}/password` | 修改密碼 | ✅ |
| GET | `/member/{memberId}/token` | 取得代幣餘額 | ✅ |
| GET | `/member/{memberId}/event-orders` | 會員票券訂單 | ✅ |
| GET | `/member/{memberId}/product-orders` | 會員商品訂單 | ✅ |
| GET | `/member/{memberId}/event-favorites` | 收藏活動 | ✅ |
| GET | `/member/{memberId}/product-favorites` | 收藏商品 | ✅ |
| GET | `/member/{memberId}/notifications` | 系統通知 | ✅ |
| GET | `/member/{memberId}/messages` | 留言歷史 | ✅ |

---

## 2. 主辦方相關 (ORGANIZER)

| Method | Endpoint | 說明 | 認證 |
|--------|----------|------|------|
| POST | `/organizer/login` | 主辦方登入 | ❌ |
| POST | `/organizer/apply` | 申請成為主辦方 | ❌ |
| GET | `/organizer/{organizerId}` | 取得主辦方資料(公開) | ❌ |
| PUT | `/organizer/{organizerId}` | 更新主辦方資料 | ✅ |
| GET | `/organizer/{organizerId}/events` | 主辦方活動列表 | ❌ |
| GET | `/organizer/{organizerId}/products` | 主辦方商品列表 | ❌ |
| GET | `/organizer/{organizerId}/articles` | 主辦方文章列表 | ❌ |
| GET | `/organizer/{organizerId}/notifications` | 主辦方通知 | ✅ |

---

## 3. 活動相關 (EVENT)

| Method | Endpoint | 說明 | 認證 |
|--------|----------|------|------|
| GET | `/events/types` | 活動類型列表 | ❌ |
| GET | `/events` | 活動列表(分頁+篩選) | ❌ |
| GET | `/events/{eventId}` | 活動詳情(含票種) | ❌ |
| POST | `/events` | 建立活動 | ✅(主辦) |
| PUT | `/events/{eventId}` | 更新活動 | ✅(主辦) |
| GET | `/events/featured` | 主打活動列表 | ❌ |
| POST | `/events/favorite` | 收藏活動 | ✅ |
| DELETE | `/events/favorite/{memberId}/{eventId}` | 取消收藏 | ✅ |

### 活動列表查詢參數
```
GET /events?page=0&size=10&status=1&typeId=1&keyword=音樂
```

---

## 4. 票券訂單 (EVENT_ORDER)

| Method | Endpoint | 說明 | 認證 |
|--------|----------|------|------|
| POST | `/event-orders` | 建立票券訂單 | ✅ |
| GET | `/event-orders/{orderId}` | 訂單詳情 | ✅ |
| POST | `/event-orders/{orderId}/pay` | 付款 | ✅ |
| POST | `/event-orders/{orderId}/refund` | 申請退款 | ✅ |

### 建立訂單請求
```json
{
  "memberId": 1,
  "organizerId": 1,
  "eventId": 1,
  "items": [
    { "ticketId": 1, "quantity": 2 }
  ],
  "tokenUsed": 100
}
```

---

## 5. 商品相關 (PROD)

| Method | Endpoint | 說明 | 認證 |
|--------|----------|------|------|
| GET | `/products/sorts` | 商品類別列表 | ❌ |
| GET | `/products` | 商品列表(分頁+篩選) | ❌ |
| GET | `/products/{prodId}` | 商品詳情 | ❌ |
| POST | `/products` | 建立商品 | ✅(主辦) |
| PUT | `/products/{prodId}` | 更新商品 | ✅(主辦) |
| GET | `/products/recommended` | 推薦商品列表 | ❌ |
| POST | `/products/favorite` | 收藏商品 | ✅ |
| DELETE | `/products/favorite/{memberId}/{prodId}` | 取消收藏 | ✅ |

### 商品列表查詢參數
```
GET /products?page=0&size=12&sortId=1&keyword=T恤&orderBy=price
```

---

## 6. 商品訂單 (PROD_ORDER)

| Method | Endpoint | 說明 | 認證 |
|--------|----------|------|------|
| POST | `/product-orders` | 建立商品訂單 | ✅ |
| GET | `/product-orders/{orderId}` | 訂單詳情 | ✅ |
| POST | `/product-orders/{orderId}/pay` | 付款 | ✅ |

---

## 7. 文章相關 (ARTICLE)

| Method | Endpoint | 說明 | 認證 |
|--------|----------|------|------|
| GET | `/articles` | 文章列表 | ❌ |
| GET | `/articles/{articleId}` | 文章詳情 | ❌ |
| POST | `/articles` | 建立文章 | ✅(主辦) |
| PUT | `/articles/{articleId}` | 更新文章 | ✅(主辦) |
| GET | `/articles/{articleId}/messages` | 文章留言 | ❌ |

---

## 8. 留言相關 (MESSAGE)

| Method | Endpoint | 說明 | 認證 |
|--------|----------|------|------|
| POST | `/messages` | 發表留言 | ✅ |
| PUT | `/messages/{messageId}` | 更新留言 | ✅ |
| DELETE | `/messages/{messageId}` | 刪除留言 | ✅ |
| POST | `/messages/report` | 檢舉留言 | ✅ |

---

## 9. 通知相關 (NOTIFICATION)

| Method | Endpoint | 說明 | 認證 |
|--------|----------|------|------|
| PUT | `/notifications/{notifyId}/read` | 標記已讀 | ✅ |
| GET | `/announcements` | 公告列表 | ❌ |

---

## 10. 搜尋

| Method | Endpoint | 說明 | 認證 |
|--------|----------|------|------|
| GET | `/search` | 全站搜尋 | ❌ |

### 搜尋查詢參數
```
GET /search?keyword=音樂&page=0&size=10
```

### 回應格式
```json
{
  "success": true,
  "data": {
    "events": { "content": [...], "totalElements": 5 },
    "products": { "content": [...], "totalElements": 3 },
    "articles": { "content": [...], "totalElements": 2 }
  }
}
```

---

## 11. 後台管理 (ADMIN)

| Method | Endpoint | 說明 | 認證 |
|--------|----------|------|------|
| POST | `/admin/login` | 管理員登入 | ❌ |
| GET | `/admin/events/pending` | 待審核活動 | ✅ |
| PUT | `/admin/events/{eventId}/review` | 審核活動 | ✅ |
| GET | `/admin/products/pending` | 待審核商品 | ✅ |
| PUT | `/admin/products/{prodId}/review` | 審核商品 | ✅ |
| GET | `/admin/organizers/pending` | 待審核主辦方 | ✅ |
| PUT | `/admin/organizers/{organizerId}/review` | 審核主辦方 | ✅ |
| GET | `/admin/message-reports` | 留言檢舉列表 | ✅ |
| PUT | `/admin/message-reports/{reportId}` | 處理檢舉 | ✅ |
| POST | `/admin/notifications` | 發送通知 | ✅ |
| POST | `/admin/announcements` | 建立公告 | ✅ |
| PUT | `/admin/announcements/{id}` | 更新公告 | ✅ |

---

## 12. 結算相關 (SETTLEMENT)

| Method | Endpoint | 說明 | 認證 |
|--------|----------|------|------|
| GET | `/settlements/events` | 活動結算列表 | ✅ |
| GET | `/settlements/products` | 商品結算列表 | ✅ |
| PUT | `/settlements/{type}/{settleId}/confirm` | 確認撥款 | ✅ |

---

## 分頁回應格式

所有列表 API 採用 Spring Data 分頁格式：

```json
{
  "success": true,
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 10,
    "size": 10,
    "number": 0,
    "first": true,
    "last": false
  }
}
```

---

## 錯誤回應格式

```json
{
  "success": false,
  "error": "錯誤訊息",
  "status": 400
}
```

### HTTP 狀態碼
- `200` - 成功
- `201` - 建立成功
- `400` - 請求錯誤
- `401` - 未認證
- `403` - 無權限
- `404` - 資源不存在
- `500` - 伺服器錯誤

