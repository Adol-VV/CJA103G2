package com.momento.eventorder.dto;

import java.io.Serializable;

public class OrderItemDTO implements Serializable{

	private Integer ticketId;      // 票種編號
    private Integer quantity;      // 購買數量
    private Integer price;         // 下單時的價格 (預防價格變動，通常由後端查資料庫為準)
    
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
}
