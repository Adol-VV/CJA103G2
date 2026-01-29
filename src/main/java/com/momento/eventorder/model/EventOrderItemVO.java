package com.momento.eventorder.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.momento.ticket.model.TicketVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "event_order_item")
public class EventOrderItemVO implements Serializable {

	@Id
	@Column(name = "event_order_item_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer eventOrderItemId;

	@ManyToOne
	@JoinColumn(name = "event_order_id", referencedColumnName = "EVENT_ORDER_ID")
	private EventOrderVO eventOrder;

	@ManyToOne
	@JoinColumn(name = "ticket_id", referencedColumnName = "ticket_id")
	private TicketVO ticket;

	@Column(name = "qrcode")
	private String qrcode;

	@Column(name = "price")
	private Integer price;
	
	@Column(name = "status")
	private Integer status;
	
	@Column(name = "verified_at")
	private LocalDateTime verifiedAt;

	public EventOrderItemVO() {

	}

	public EventOrderItemVO(Integer eventOrderItemId, EventOrderVO eventOrder, TicketVO ticket, String qrcode,
			Integer price, Integer status, LocalDateTime verifiedAt) {
		super();
		this.eventOrderItemId = eventOrderItemId;
		this.eventOrder = eventOrder;
		this.ticket = ticket;
		this.qrcode = qrcode;
		this.price = price;
		this.status = status;
		this.verifiedAt = verifiedAt;
	}





	public Integer getEventOrderItemId() {
		return eventOrderItemId;
	}

	public void setEventOrderItemId(Integer eventOrderItemId) {
		this.eventOrderItemId = eventOrderItemId;
	}

	public EventOrderVO getEventOrder() {
		return eventOrder;
	}

	public void setEventOrder(EventOrderVO eventOrder) {
		this.eventOrder = eventOrder;
	}

	public TicketVO getTicket() {
		return ticket;
	}

	public void setTicket(TicketVO ticket) {
		this.ticket = ticket;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}





	public LocalDateTime getVerifiedAt() {
		return verifiedAt;
	}





	public void setVerifiedAt(LocalDateTime verifiedAt) {
		this.verifiedAt = verifiedAt;
	}
}
