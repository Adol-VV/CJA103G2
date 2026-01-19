package com.momento.ticket.model;

import com.momento.event.model.EventVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket")
public class TicketVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ticket_id")
	private Integer ticketId;

	@ManyToOne
	@JoinColumn(name = "event_id", referencedColumnName = "event_id")
	private EventVO event;

	@Column(name = "price")
	private Integer price;

	@Column(name = "total")
	private Integer total;

	@Column(name = "remain")
	private Integer remain;

	@Column(name = "ticket_name")
	private String ticketName;

	public TicketVO() {
		super();
	}

	public TicketVO(Integer ticketId, EventVO event, Integer price, Integer total, Integer remain, String ticketName) {
		super();
		this.ticketId = ticketId;
		this.event = event;
		this.price = price;
		this.total = total;
		this.remain = remain;
		this.ticketName = ticketName;
	}

	public Integer getTicketId() {
		return ticketId;
	}

	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}

	public EventVO getEvent() {
		return event;
	}

	public void setEvent(EventVO event) {
		this.event = event;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getRemain() {
		return remain;
	}

	public void setRemain(Integer remain) {
		this.remain = remain;
	}

	public String getTicketName() {
		return ticketName;
	}

	public void setTicketName(String ticketName) {
		this.ticketName = ticketName;
	}
}
