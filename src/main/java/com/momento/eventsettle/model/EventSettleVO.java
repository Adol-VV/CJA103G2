package com.momento.eventsettle.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.momento.event.model.EventVO;
import com.momento.organizer.model.OrganizerVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "event_settle")
public class EventSettleVO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_settle_id")
	private Integer eventSettleId;

	@ManyToOne
	@JoinColumn(name = "organizer_id", referencedColumnName = "organizer_id")
	private OrganizerVO organizer;

	@ManyToOne
	@JoinColumn(name = "event_id", referencedColumnName = "event_id")
	private EventVO event;

	@Column(name = "sales")
	private Integer sales;

	@Column(name = "payable")
	private Integer payable;

	@Column(name = "status")
	private Integer status;

	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "paid_at", insertable = false)
	private LocalDateTime paidAt;

	public EventSettleVO() {
		super();
	}

	public EventSettleVO(Integer eventSettleId, OrganizerVO organizer, EventVO event, Integer sales, Integer payable,
			Integer status, LocalDateTime createdAt, LocalDateTime paidAt) {
		super();
		this.eventSettleId = eventSettleId;
		this.organizer = organizer;
		this.event = event;
		this.sales = sales;
		this.payable = payable;
		this.status = status;
		this.createdAt = createdAt;
		this.paidAt = paidAt;
	}

	public Integer getEventSettleId() {
		return eventSettleId;
	}

	public void setEventSettleId(Integer eventSettleId) {
		this.eventSettleId = eventSettleId;
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

	public Integer getSales() {
		return sales;
	}

	public void setSales(Integer sales) {
		this.sales = sales;
	}

	public Integer getPayable() {
		return payable;
	}

	public void setPayable(Integer payable) {
		this.payable = payable;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}

}
