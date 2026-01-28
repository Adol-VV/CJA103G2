package com.momento.eventmanage.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event Create DTO - 建立活動資料傳輸物件
 */
public class EventCreateDTO {

    private Integer organizerId;

    // 基本資訊
    private String title;
    private Integer typeId;
    private String place;

    // 時間欄位 (在建立階段可能為空)
    private LocalDateTime saleStartAt;
    private LocalDateTime saleEndAt;
    private LocalDateTime eventStartAt;
    private LocalDateTime eventEndAt;
    private LocalDateTime publishedAt;

    private String content;

    // 圖片
    private String bannerUrl;
    private List<String> imageUrls;

    private List<TicketDTO> tickets;

    public static class TicketDTO {
        private String name;
        private Integer price;
        private Integer total;

        public TicketDTO() {
        }

        public TicketDTO(String name, Integer price, Integer total) {
            this.name = name;
            this.price = price;
            this.total = total;
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

    public EventCreateDTO() {
    }

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

    public LocalDateTime getSaleStartAt() {
        return saleStartAt;
    }

    public void setSaleStartAt(LocalDateTime saleStartAt) {
        this.saleStartAt = saleStartAt;
    }

    public LocalDateTime getSaleEndAt() {
        return saleEndAt;
    }

    public void setSaleEndAt(LocalDateTime saleEndAt) {
        this.saleEndAt = saleEndAt;
    }

    public LocalDateTime getEventStartAt() {
        return eventStartAt;
    }

    public void setEventStartAt(LocalDateTime eventStartAt) {
        this.eventStartAt = eventStartAt;
    }

    public LocalDateTime getEventEndAt() {
        return eventEndAt;
    }

    public void setEventEndAt(LocalDateTime eventEndAt) {
        this.eventEndAt = eventEndAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
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
