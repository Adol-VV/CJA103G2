package com.momento.eventfav.model;

import jakarta.persistence.*;
import com.momento.event.model.EventVO;
import com.momento.member.model.MemberVO;

@Entity
@Table(name = "EVENT_FAV", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "MEMBER_ID", "EVENT_ID" })
})
public class EventFavVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_FAV_ID")
    private Integer eventFavId;

    @ManyToOne
    @JoinColumn(name = "EVENT_ID", nullable = false)
    private EventVO event;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private MemberVO member;

    // ========== Constructors ==========

    public EventFavVO() {
    }

    public EventFavVO(EventVO event, MemberVO member) {
        this.event = event;
        this.member = member;
    }

    // ========== Getters & Setters ==========

    public Integer getEventFavId() {
        return eventFavId;
    }

    public void setEventFavId(Integer eventFavId) {
        this.eventFavId = eventFavId;
    }

    public EventVO getEvent() {
        return event;
    }

    public void setEvent(EventVO event) {
        this.event = event;
    }

    public MemberVO getMember() {
        return member;
    }

    public void setMember(MemberVO member) {
        this.member = member;
    }
}