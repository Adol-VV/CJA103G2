package com.momento.prodsettledetail.model;

import com.momento.prod.model.ProdVO;
import com.momento.prodsettle.model.ProdSettleVO;

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
public class ProdSettleDetailVO {

	@Id
	@Column(name="PROD_SETTLE_DETAIL_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer prodSettleDetailId;
	
	@ManyToOne
	@JoinColumn(name="PROD_SETTLE_ID")
	private ProdSettleVO prodSettleId;
	
	@ManyToOne
	@JoinColumn(name="PROD_ID")
	private ProdVO prodId;
	
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
	public ProdSettleVO getProdSettleId() {
		return prodSettleId;
	}
	public void setProdSettleId(ProdSettleVO prodSettleId) {
		this.prodSettleId = prodSettleId;
	}
	public ProdVO getProdId() {
		return prodId;
	}
	public void setProdId(ProdVO prodId) {
		this.prodId = prodId;
	}
	public Integer getProdSales() {
		return prodSales;
	}
	public void setProdSales(Integer prodSales) {
		this.prodSales = prodSales;
	}
}
