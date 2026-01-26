package com.momento.eventorder.dto;

import java.io.Serializable;

public class CheckoutDTO implements Serializable{

		private Integer total;
		private Integer tokenUsed;
		private Integer payable;
		
		public Integer getTotal() {
			return total;
		}
		public void setTotal(Integer total) {
			this.total = total;
		}
		public Integer getTokenUsed() {
			return tokenUsed;
		}
		public void setTokenUsed(Integer tokenUsed) {
			this.tokenUsed = tokenUsed;
		}
		public Integer getPayable() {
			return payable;
		}
		public void setPayable(Integer payable) {
			this.payable = payable;
		}
}
