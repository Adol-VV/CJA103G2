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
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "EVENT")
public class EventVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID")
    private Integer eventId;

    @ManyToOne
    @JoinColumn(name = "ORGANIZER_ID", nullable = false)
    @JsonIgnoreProperties({ "events", "articles", "products" })
    private OrganizerVO organizer;

    @ManyToOne
    @JoinColumn(name = "TYPE_ID", nullable = true)
    @JsonIgnoreProperties("events")
    private TypeVO type;

    @ManyToOne
    @JoinColumn(name = "EMP_ID", nullable = true)
    @JsonIgnoreProperties("events")
    private EmpVO emp;

    @Column(name = "STATUS")
    private Byte status = 0;

    @Column(name = "TITLE", length = 100)
    private String title;

    @Lob
    @Column(name = "CONTENT")
    private String content;

    @Column(name = "PLACE", length = 200)
    private String place;

    @Column(name = "SALE_START_AT", nullable = true)
    private LocalDateTime saleStartAt;

    @Column(name = "SALE_END_AT", nullable = true)
    private LocalDateTime saleEndAt;

    @Column(name = "EVENT_START_AT", nullable = true)
    private LocalDateTime eventStartAt;

    @Column(name = "EVENT_END_AT", nullable = true)
    private LocalDateTime eventEndAt;

    @Column(name = "PUBLISHED_AT", nullable = true)
    private LocalDateTime publishedAt;

    @org.hibernate.annotations.Formula("(SELECT MIN(t.PRICE) FROM TICKET t WHERE t.EVENT_ID = EVENT_ID)")
    private Integer minPrice;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("event")
    private java.util.List<com.momento.ticket.model.TicketVO> tickets;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("event")
    private java.util.List<EventImageVO> images;

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

    public java.util.List<com.momento.ticket.model.TicketVO> getTickets() {
        return tickets;
    }

    public void setTickets(java.util.List<com.momento.ticket.model.TicketVO> tickets) {
        this.tickets = tickets;
    }

    public java.util.List<EventImageVO> getImages() {
        return images;
    }

    public void setImages(java.util.List<EventImageVO> images) {
        this.images = images;
    }

    @jakarta.persistence.Transient
    private String rejectReason;

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    // ========== Status Constants & Helpers ==========
    public static final byte STATUS_DRAFT = 0;
    public static final byte STATUS_PENDING = 1;
    public static final byte STATUS_APPROVED = 2;
    public static final byte STATUS_PUBLISHED = 3;
    public static final byte STATUS_REJECTED = 4;
    public static final byte STATUS_CLOSED = 5;

    public boolean isDraft() {
        return status == STATUS_DRAFT;
    }

    public boolean isPending() {
        return status == STATUS_PENDING;
    }

    public boolean isApproved() {
        return status == STATUS_APPROVED;
    }

    public boolean isPublished() {
        return status == STATUS_PUBLISHED;
    }

    public boolean isRejected() {
        return status == STATUS_REJECTED;
    }

    public boolean isClosed() {
        return status == STATUS_CLOSED;
    }

    // ========== Frontend Display Helpers ==========
    public enum FrontendDisplayStatus {
        NOT_AVAILABLE, COMING_SOON, ON_SALE, SALE_ENDED, IN_PROGRESS, FINISHED
    }

    public FrontendDisplayStatus getFrontendStatus() {
        if (status != STATUS_PUBLISHED && status != STATUS_CLOSED) {
            return FrontendDisplayStatus.NOT_AVAILABLE;
        }

        LocalDateTime now = LocalDateTime.now();

        if (status == STATUS_CLOSED) {
            return FrontendDisplayStatus.FINISHED;
        }

        if (saleStartAt != null && now.isBefore(saleStartAt)) {
            return FrontendDisplayStatus.COMING_SOON;
        }
        if (saleEndAt != null && now.isBefore(saleEndAt)) {
            return FrontendDisplayStatus.ON_SALE;
        }
        if (eventStartAt != null && now.isBefore(eventStartAt)) {
            return FrontendDisplayStatus.SALE_ENDED;
        }
        if (eventEndAt != null && now.isBefore(eventEndAt)) {
            return FrontendDisplayStatus.IN_PROGRESS;
        }
        return FrontendDisplayStatus.FINISHED;
    }

    public boolean canPurchase() {
        return getFrontendStatus() == FrontendDisplayStatus.ON_SALE;
    }

    public boolean isManuallyClosed() {
        if (status != STATUS_CLOSED)
            return false;
        return eventEndAt != null && LocalDateTime.now().isBefore(eventEndAt);
    }
}