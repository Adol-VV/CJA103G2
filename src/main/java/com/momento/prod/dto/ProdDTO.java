package com.momento.prod.dto;

import java.time.LocalDateTime;

public class ProdDTO {
	private Integer prodId;
    private String prodName;
    private Integer prodPrice;
    private Integer prodStock;
    private Integer organizerId;
    private String organizerName;
    private Integer sortId;
    private String sortName;
    private LocalDateTime createdAt;
    private byte prodStatus;
    private String reviewStatus;
    
    private String imageUrl; // 存儲第一張圖片
    
    

	public Integer getOrganizerId() {
		return organizerId;
	}

	public void setOrganizerId(Integer organizerId) {
		this.organizerId = organizerId;
	}

	public Integer getProdId() {
		return prodId;
	}

	public String getOrganizerName() {
		return organizerName;
	}
	public void setOrganizerName(String organizerName) {
		this.organizerName = organizerName;
	}
	public String getSortName() {
		return sortName;
	}
	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
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
	public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
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
