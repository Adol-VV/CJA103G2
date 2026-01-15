package com.momento.ticket.model;

import com.momento.event.model.EventVO;

import groovy.transform.ToString;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
}
