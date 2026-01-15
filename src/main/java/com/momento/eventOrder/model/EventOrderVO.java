package com.momento.eventOrder.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import groovy.transform.ToString;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.momento.eventOrderItem.model.*;
import com.momento.member.model.MemberVO;;

@Entity
@Table(name = "event_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventOrderVO implements Serializable{

    @Id
    @Column(name = "event_order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventOrderId;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberVO member;

    // 等OrganizerVO整合
    // @ManyToOne
    // @JoinColumn(name = "organizer_id", referencedColumnName = "organizer_id")
    // private OrganizerVO organizer;

    // 等EventVO整合
    // @ManyToOne
    // @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    // private EventVO event;

    @Column(name = "total")
    private Integer total;

    @Column(name = "token_used")
    private Integer tokenUsed;

    @Column(name = "payable")
    private Integer payable;

    @Column(name = "pay_status")
    private Integer payStatus;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "eventOrder", cascade = CascadeType.ALL)
    @OrderBy("event_order_item_id asc")
    private Set<EventOrderItemVO> eventOrderItems;
}
