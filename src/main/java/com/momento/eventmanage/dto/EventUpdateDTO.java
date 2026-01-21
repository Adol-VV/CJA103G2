package com.momento.eventmanage.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event Update DTO - 更新活動資料傳輸物件
 * 
 * 用於接收前端傳來的活動更新資料
 */
public class EventUpdateDTO {

    private Integer eventId; // 活動 ID

    // 基本資訊 (永遠可以修改)
    private String title; // 活動名稱
    private Integer typeId; // 活動類型 ID
    private String place; // 活動地點
    private LocalDateTime eventAt; // 活動舉辦時間
    private String summary; // 活動簡介
    private LocalDateTime startedAt; // 售票開始時間
    private LocalDateTime endedAt; // 售票結束時間
    private String content; // 活動詳細內容
    private String bannerUrl; // 主圖 URL
    private List<String> imageUrls; // 活動相簿 URLs

    // 票種列表 (有訂單時不可修改價格和數量)
    private List<TicketUpdateDTO> tickets;

    // ========== 內部類別: 票種更新 DTO ==========

    public static class TicketUpdateDTO {
        private Integer ticketId; // 票種 ID (null 表示新增)
        private String name; // 票種名稱
        private Integer price; // 票價
        private Integer total; // 總票數
        // private Integer limitPerPerson; // 每人限購 (待確認)

        // Constructors
        public TicketUpdateDTO() {
        }

        // Getters and Setters
        public Integer getTicketId() {
            return ticketId;
        }

        public void setTicketId(Integer ticketId) {
            this.ticketId = ticketId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }
    }

    // ========== Constructors ==========

    public EventUpdateDTO() {
    }

    // ========== Getters and Setters ==========

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public LocalDateTime getEventAt() {
        return eventAt;
    }

    public void setEventAt(LocalDateTime eventAt) {
        this.eventAt = eventAt;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<TicketUpdateDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketUpdateDTO> tickets) {
        this.tickets = tickets;
    }
}
