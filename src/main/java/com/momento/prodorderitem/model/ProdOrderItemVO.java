package com.momento.prodorderitem.model;

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
@Table(name="prod_order_item")
public class ProdOrderItemVO implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="PROD_ORDER_ITEM_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer prodOrderItemId;
	
	@ManyToOne
	@JoinColumn(name="PROD_ORDER_ID")
	private Integer prodOrderId;
	
	@ManyToOne
	@JoinColumn(name="PROD_ID")
	private Integer prodId;
	
	@Column(name="QUANTITY")
	@NotNull(message = "數量請勿空白")
	@DecimalMin(value="1",message="數量請勿小於1")
	private Integer quantity;
	
	@Column(name="PRICE")
	@NotNull(message="金額請勿空白")
	@DecimalMin(value="1",message="金額請勿小於1")
	private Integer price;
	
	@Column(name="TOTAL")
	@NotNull(message = "總金額請勿空白")
	@DecimalMin(value="1",message="總金額請勿小於1")
	private Integer total;
	
	
	
	public int getProdOrderItemId() {
		return prodOrderItemId;
	}
	public void setProdOrderItemId(int prodOrderItemId) {
		this.prodOrderItemId = prodOrderItemId;
	}
	public int getProdOrderId() {
		return prodOrderId;
	}
	public void setProdOrderId(int prodOrderId) {
		this.prodOrderId = prodOrderId;
	}
	public int getProdId() {
		return prodId;
	}
	public void setProdId(int prodId) {
		this.prodId = prodId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
}
