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

    @Lob
    @Column(name = "IMAGE", nullable = false)
    private byte[] image;

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

    public EventImageVO(EventVO event, byte[] image) {
        this.event = event;
        this.image = image;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}