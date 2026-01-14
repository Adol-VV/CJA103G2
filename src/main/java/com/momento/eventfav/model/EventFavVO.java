package com.momento.eventfav.model;

import jakarta.persistence.*;

@Entity
@Table(name = "EVENT_FAV", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "MEMBER_ID", "EVENT_ID" })
})
public class EventFavVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_FAV_ID")
    private Integer eventFavId;

    @Column(name = "EVENT_ID", nullable = false)
    private Integer eventId;

    @Column(name = "MEMBER_ID", nullable = false)
    private Integer memberId;

    // ========== Constructors ==========

    public EventFavVO() {
    }

    public EventFavVO(Integer eventId, Integer memberId) {
        this.eventId = eventId;
        this.memberId = memberId;
    }

    // ========== Getters & Setters ==========

    public Integer getEventFavId() {
        return eventFavId;
    }

    public void setEventFavId(Integer eventFavId) {
        this.eventFavId = eventFavId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
}
