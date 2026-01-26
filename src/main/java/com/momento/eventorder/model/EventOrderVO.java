package com.momento.eventorder.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import com.momento.event.model.EventVO;
import com.momento.member.model.MemberVO;
import com.momento.organizer.model.OrganizerVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;;

@Entity
@Table(name = "event_order")
public class EventOrderVO implements Serializable {

	@Id
	@Column(name = "event_order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer eventOrderId;

	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "member_id")
	private MemberVO member;

	@ManyToOne
	@JoinColumn(name = "organizer_id", referencedColumnName = "organizer_id")
	private OrganizerVO organizer;

	@ManyToOne
	@JoinColumn(name = "event_id", referencedColumnName = "event_id")
	private EventVO event;

	@Column(name = "total")
	private Integer total;

	@Column(name = "token_used")
	private Integer tokenUsed;

	@Column(name = "payable")
	private Integer payable;

	@Column(name = "pay_status")
	private Integer payStatus;

	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "eventOrder", cascade = CascadeType.ALL)
	@OrderBy("event_order_item_id asc")
	private Set<EventOrderItemVO> eventOrderItems;

	public EventOrderVO() {
		super();
	}

	public EventOrderVO(Integer eventOrderId, MemberVO member, OrganizerVO organizer, EventVO event, Integer total,
			Integer tokenUsed, Integer payable, Integer payStatus, LocalDateTime createdAt,
			Set<EventOrderItemVO> eventOrderItems) {
		super();
		this.eventOrderId = eventOrderId;
		this.member = member;
		this.organizer = organizer;
		this.event = event;
		this.total = total;
		this.tokenUsed = tokenUsed;
		this.payable = payable;
		this.payStatus = payStatus;
		this.createdAt = createdAt;
		this.eventOrderItems = eventOrderItems;
	}

	public Integer getEventOrderId() {
		return eventOrderId;
	}

	public void setEventOrderId(Integer eventOrderId) {
		this.eventOrderId = eventOrderId;
	}

	public MemberVO getMember() {
		return member;
	}

	public void setMember(MemberVO member) {
		this.member = member;
	}

	public OrganizerVO getOrganizer() {
		return organizer;
	}

	public void setOrganizer(OrganizerVO organizer) {
		this.organizer = organizer;
	}

	public EventVO getEvent() {
		return event;
	}

	public void setEvent(EventVO event) {
		this.event = event;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getTokenUsed() {
		return tokenUsed;
	}

	public void setTokenUsed(Integer tokenUsed) {
		this.tokenUsed = tokenUsed;
	}

	public Integer getPayable() {
		return payable;
	}

	public void setPayable(Integer payable) {
		this.payable = payable;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Set<EventOrderItemVO> getEventOrderItems() {
		return eventOrderItems;
	}

	public void setEventOrderItems(Set<EventOrderItemVO> eventOrderItems) {
		this.eventOrderItems = eventOrderItems;
	}

	@Override
	public String toString() {
		return "EventOrderVO [eventOrderId=" + eventOrderId + ", member=" + member + ", organizer=" + organizer
				+ ", event=" + event + ", total=" + total + ", tokenUsed=" + tokenUsed + ", payable=" + payable
				+ ", payStatus=" + payStatus + ", createdAt=" + createdAt + ", eventOrderItems=" + eventOrderItems
				+ "]";
	}
}
