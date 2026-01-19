package com.momento.event.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EVENT_IMAGE")
public class EventImageVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_IMAGE_ID")
    private Integer eventImageId;

    @ManyToOne
    @JoinColumn(name = "EVENT_ID", nullable = false)
    private EventVO event;

    @Column(name = "IMAGE_URL", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "UPLOADED_AT", updatable = false)
    private LocalDateTime uploadedAt;

    // ========== Lifecycle ==========

    @PrePersist
    protected void onCreate() {
        if (this.uploadedAt == null) {
            this.uploadedAt = LocalDateTime.now();
        }
    }

    // ========== Constructors ==========

    public EventImageVO() {
    }

    public EventImageVO(EventVO event, String imageUrl) {
        this.event = event;
        this.imageUrl = imageUrl;
    }

    // ========== Getters & Setters ==========

    public Integer getEventImageId() {
        return eventImageId;
    }

    public void setEventImageId(Integer eventImageId) {
        this.eventImageId = eventImageId;
    }

    public EventVO getEvent() {
        return event;
    }

    public void setEvent(EventVO event) {
        this.event = event;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}