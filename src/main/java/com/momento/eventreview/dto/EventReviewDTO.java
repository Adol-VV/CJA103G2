package com.momento.eventreview.dto;

import java.time.LocalDateTime;
import java.util.List;

public class EventReviewDTO {
    private Integer eventId;
    private String title;
    private String content;
    private String place;
    private LocalDateTime saleStartAt;
    private LocalDateTime saleEndAt;
    private LocalDateTime eventStartAt;
    private LocalDateTime eventEndAt;
    private LocalDateTime publishedAt;
    private String bannerUrl;
    private List<String> imageUrls;

    // Nested DTOs
    private OrganizerDTO organizer;
    private TypeDTO type;

    // Status
    private Byte status;

    // Tickets
    private List<TicketDTO> tickets;

    public static class OrganizerDTO {
        private Integer organizerId;
        private String name;
        private String accountName;

        public OrganizerDTO(Integer organizerId, String name, String accountName) {
            this.organizerId = organizerId;
            this.name = name;
            this.accountName = accountName;
        }

        public Integer getOrganizerId() {
            return organizerId;
        }

        public String getName() {
            return name;
        }

        public String getAccountName() {
            return accountName;
        }
    }

    public static class TypeDTO {
        private String typeName;

        public TypeDTO(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }
    }

    public static class TicketDTO {
        private String ticketName;
        private Integer price;
        private Integer total;

        public TicketDTO(String ticketName, Integer price, Integer total) {
            this.ticketName = ticketName;
            this.price = price;
            this.total = total;
        }

        public String getTicketName() {
            return ticketName;
        }

        public Integer getPrice() {
            return price;
        }

        public Integer getTotal() {
            return total;
        }
    }

    public EventReviewDTO() {
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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

    public OrganizerDTO getOrganizer() {
        return organizer;
    }

    public void setOrganizer(OrganizerDTO organizer) {
        this.organizer = organizer;
    }

    public TypeDTO getType() {
        return type;
    }

    public void setType(TypeDTO type) {
        this.type = type;
    }
}
