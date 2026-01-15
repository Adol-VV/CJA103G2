package com.momento.prodsettle.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="prod_settle")
public class ProdSettleVO {

	@Id
	@Column(name="PROD_SETTLE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer prodSettleId; 
	
	@ManyToOne
	@JoinColumn(name="ORGANIZER_ID")
	private Integer organizerId;
	
	@Column(name="SALES")
	@NotNull(message = "銷售金額請勿空白")
	@DecimalMin(value="1",message="銷售金額請勿小於1")
	private Integer sales;
	
	@Column(name="PAYABLE")
	@NotNull(message = "付款金額請勿空白")
	@DecimalMin(value="1",message="付款金額請勿小於1")
	private Integer payable;

	@Column(name="STATUS")
	private Byte  status;
	
	public Integer getProdSettleId() {
		return prodSettleId;
	}

	public void setProdSettleId(Integer prodSettleId) {
		this.prodSettleId = prodSettleId;
	}

	public Integer getOrganizerId() {
		return organizerId;
	}

	public void setOrganizerId(Integer organizerId) {
		this.organizerId = organizerId;
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

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	@Column(name="CREATED_AT",updatable = false)
	private Date createdDate;
	
	@Column(name="PAID_AT")
	private Date paidDate;
}
