package com.momento.eventSettle.model;

import java.io.Serializable;
import java.sql.Timestamp;

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
@Table(name = "event_settle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventSettleVO implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_settle_id")
    private Integer eventSettleId;

    // 等organizerVO整合
    // @ManyToOne
    // @JoinColumn(name = "organizer_id", referencedColumnName = "organizer_id")
    // private OrganizerVO organizer;

    // 等eventVO整合
    // @ManyToOne
    // @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    // private EventVO event;

    @Column(name = "sales")
    private Integer sales;

    @Column(name = "payable")
    private Integer payable;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "paid_at", insertable = false)
    private Timestamp paidAt;

}
