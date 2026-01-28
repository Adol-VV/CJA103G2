package com.momento.eventmanage.dto;

import java.time.LocalDateTime;

public class EventListItemDTO {
    private Integer eventId;
    private String title;
    private String place;
    private LocalDateTime eventStartAt;
    private LocalDateTime publishedAt;
    private Byte status;

    public EventListItemDTO() {
    }

    public EventListItemDTO(Integer eventId, String title, String place, LocalDateTime eventStartAt,
            LocalDateTime publishedAt, Byte status) {
        this.eventId = eventId;
        this.title = title;
        this.place = place;
        this.eventStartAt = eventStartAt;
        this.publishedAt = publishedAt;
        this.status = status;
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

    public LocalDateTime getEventStartAt() {
        return eventStartAt;
    }

    public void setEventStartAt(LocalDateTime eventStartAt) {
        this.eventStartAt = eventStartAt;
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
}
