package com.momento.eventmanage.dto;

import java.time.LocalDateTime;

public class EventListItemDTO {
    private Integer eventId;
    private String title;
    private String place;
    private LocalDateTime eventAt;
    private LocalDateTime publishedAt;
    private Byte status;
    private Byte reviewStatus;

    public EventListItemDTO() {
    }

    public EventListItemDTO(Integer eventId, String title, String place, LocalDateTime eventAt,
            LocalDateTime publishedAt, Byte status, Byte reviewStatus) {
        this.eventId = eventId;
        this.title = title;
        this.place = place;
        this.eventAt = eventAt;
        this.publishedAt = publishedAt;
        this.status = status;
        this.reviewStatus = reviewStatus;
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
}
