package com.momento.prodsettledetail.model;

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
@Table(name="prod_settle_detail")
public class ProdSettleDetail {

	@Id
	@Column(name="PROD_SETTLE_DETAIL_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer prodSettleDetailId;
	
	@ManyToOne
	@JoinColumn(name="PROD_SETTLE_ID")
	private Integer prodSettleId;
	
	@ManyToOne
	@JoinColumn(name="PROD_ID")
	private Integer prodId;
	
	@Column(name="PROD_SALES")
	@NotNull(message = "銷售金額請勿空白")
	@DecimalMin(value="1",message="銷售金額請勿小於1")
	private Integer prodSales;
	
	
	public Integer getProdSettleDetailId() {
		return prodSettleDetailId;
	}
	public void setProdSettleDetailId(Integer prodSettleDetailId) {
		this.prodSettleDetailId = prodSettleDetailId;
	}
	public Integer getProdSettleId() {
		return prodSettleId;
	}
	public void setProdSettleId(Integer prodSettleId) {
		this.prodSettleId = prodSettleId;
	}
	public Integer getProdId() {
		return prodId;
	}
	public void setProdId(Integer prodId) {
		this.prodId = prodId;
	}
	public Integer getProdSales() {
		return prodSales;
	}
	public void setProdSales(Integer prodSales) {
		this.prodSales = prodSales;
	}
}
