package com.momento.eventmanage.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event Create DTO - 建立活動資料傳輸物件
 * 
 * 用於接收前端傳來的活動建立資料
 */
public class EventCreateDTO {

    // 主辦方 ID (從 session 取得)
    private Integer organizerId;

    // 基本資訊
    private String title; // 活動名稱
    private Integer typeId; // 活動類型 ID
    private String place; // 活動地點
    private LocalDateTime eventAt; // 活動舉辦時間
    private String summary; // 活動簡介

    // 售票時間
    private LocalDateTime startedAt; // 售票開始時間
    private LocalDateTime endedAt; // 售票結束時間

    // 活動內容
    private String content; // 活動詳細內容 (HTML)

    // 圖片
    private String bannerUrl; // 主圖 URL
    private List<String> imageUrls; // 活動相簿 URLs

    // 票種列表
    private List<TicketDTO> tickets;

    // ========== 內部類別: 票種 DTO ==========

    public static class TicketDTO {
        private String name; // 票種名稱
        private Integer price; // 票價
        private Integer total; // 總票數
        // private Integer limitPerPerson; // 每人限購 (待確認)

        // Constructors
        public TicketDTO() {
        }

        public TicketDTO(String name, Integer price, Integer total) {
            this.name = name;
            this.price = price;
            this.total = total;
        }

        // Getters and Setters
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

        // public Integer getLimitPerPerson() {
        // return limitPerPerson;
        // }

        // public void setLimitPerPerson(Integer limitPerPerson) {
        // this.limitPerPerson = limitPerPerson;
        // }
    }

    // ========== Constructors ==========

    public EventCreateDTO() {
    }

    // ========== Getters and Setters ==========

    public Integer getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Integer organizerId) {
        this.organizerId = organizerId;
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

    public List<TicketDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDTO> tickets) {
        this.tickets = tickets;
    }
}
