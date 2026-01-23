package com.momento.prod.dto;



public class ProdDTO {
	private Integer prodId;
    private String prodName;
    private Integer prodPrice;
    private Integer prodStock;
    private Integer sortId;
    private byte prodStatus;
    
    private String imageUrl; // 存儲第一張圖片
    
    
	public Integer getProdId() {
		return prodId;
	}
	public Integer getProdStock() {
		return prodStock;
	}
	public void setProdStock(Integer prodStock) {
		this.prodStock = prodStock;
	}
	public void setProdId(Integer prodId) {
		this.prodId = prodId;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public Integer getProdPrice() {
		return prodPrice;
	}
	public byte getProdStatus() {
		return prodStatus;
	}
	public void setProdStatus(byte prodStatus) {
		this.prodStatus = prodStatus;
	}


	public Integer getSortId() {
		return sortId;
	}
	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}
	public void setProdPrice(Integer prodPrice) {
		this.prodPrice = prodPrice;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	

    
}
