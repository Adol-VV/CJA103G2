package com.momento.prodorder.model;


import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@org.hibernate.annotations.DynamicInsert
@Table(name="prod_order")
public class ProdOrderIdVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="PROD_ORDER_ID", updatable = false)
	private Integer orderId;
	
	@ManyToOne
	@JoinColumn(name="MEMBER_ID")
	private Integer memberId;
	
	@ManyToOne
	@JoinColumn(name="ORGANIZER_ID")
	private Integer organizerId;
	
	@Column(name="CREATED_AT",updatable = false)
	private Date createdDate;
	
	@Column(name="TOTAL")
	private Integer total;
	
	@Column(name="TOKEN_USED")
	private Integer token;
	
	@Column(name="PAYABLE")
	private Integer payable;
	
	@Column(name="PAY_STATUS")
	private Byte  status;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getOrganizerId() {
		return organizerId;
	}

	public void setOrganizerId(Integer organizerId) {
		this.organizerId = organizerId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getToken() {
		return token;
	}

	public void setToken(Integer token) {
		this.token = token;
	}

	public Integer getPayable() {
		return payable;
	}

	public void setPayable(Integer payable) {
		this.payable = payable;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte  status) {
		this.status = status;
	}
	
}
