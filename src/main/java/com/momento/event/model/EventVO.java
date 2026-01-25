package com.momento.event.model;

import java.time.LocalDateTime;

import com.momento.emp.model.EmpVO;
import com.momento.organizer.model.OrganizerVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EVENT")
public class EventVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID")
    private Integer eventId;

    @ManyToOne
    @JoinColumn(name = "ORGANIZER_ID", nullable = false)
    private OrganizerVO organizer;

    @ManyToOne
    @JoinColumn(name = "TYPE_ID", nullable = true)
    private TypeVO type;

    @ManyToOne
    @JoinColumn(name = "EMP_ID", nullable = true)
    private EmpVO emp;

    @Column(name = "STATUS")
    private Byte status = 0;

    @Column(name = "REVIEW_STATUS")
    private Byte reviewStatus = 0;

    @Column(name = "TITLE", length = 100)
    private String title;

    @Lob
    @Column(name = "CONTENT")
    private String content;

    @Column(name = "PLACE", length = 200)
    private String place;

    @Column(name = "STARTED_AT", nullable = true)
    private LocalDateTime startedAt;

    @Column(name = "ENDED_AT", nullable = true)
    private LocalDateTime endedAt;

    @Column(name = "EVENT_AT", nullable = true)
    private LocalDateTime eventAt;

    @Column(name = "PUBLISHED_AT", nullable = true)
    private LocalDateTime publishedAt;

    @org.hibernate.annotations.Formula("(SELECT MIN(t.PRICE) FROM TICKET t WHERE t.EVENT_ID = EVENT_ID)")
    private Integer minPrice;

    // ========== Constructors ==========

    public EventVO() {
    }

    // ========== Getters & Setters ==========

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public OrganizerVO getOrganizer() {
        return organizer;
    }

    public void setOrganizer(OrganizerVO organizer) {
        this.organizer = organizer;
    }

    public TypeVO getType() {
        return type;
    }

    public void setType(TypeVO type) {
        this.type = type;
    }

    public EmpVO getEmp() {
        return emp;
    }

    public void setEmp(EmpVO emp) {
        this.emp = emp;
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
}