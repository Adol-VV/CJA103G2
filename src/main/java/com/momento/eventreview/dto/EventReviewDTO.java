package com.momento.eventreview.dto;

import java.time.LocalDateTime;
import java.util.List;

public class EventReviewDTO {
    private Integer eventId;
    private String title;
    private String content; // Content
    private String place;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime eventAt;
    private LocalDateTime publishedAt;

    // Nested DTOs
    private OrganizerDTO organizer;
    private TypeDTO type;

    // Status
    private Byte status;
    private Byte reviewStatus;

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
        private String ticketName; // Frontend might use name but VO uses ticketName
        private Integer price;
        private Integer total;

        public TicketDTO(String ticketName, Integer price, Integer total) {
            this.ticketName = ticketName;
            this.price = price;
            this.total = total;
        }

        public String getName() {
            return ticketName;
        } // Alias for frontend name

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

    // Getters and Setters
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

    public LocalDateTime getEventAt() {
        return eventAt;
    }

    public void setEventAt(LocalDateTime eventAt) {
        this.eventAt = eventAt;
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

    public Byte getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Byte reviewStatus) {
        this.reviewStatus = reviewStatus;
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
