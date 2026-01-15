package com.momento.eventOrderItem.model;

import java.io.Serializable;

import com.momento.eventOrder.model.EventOrderVO;
import com.momento.ticket.model.TicketVO;

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
@Table(name = "event_order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventOrderItemVO implements Serializable{

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

    @Column(name = "total")
    private Integer total;
}
