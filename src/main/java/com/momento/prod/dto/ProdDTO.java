package com.momento.prod.dto;

import java.util.ArrayList;
import java.util.List;


public class ProdDTO {
	private Integer prodId;
    private String prodName;
    private Integer prodPrice;
    private Integer prodStock;
    private String prodContent;
    private Integer organizerId;
    private String organizerName;
    private Integer sortId;
    private String sortName;
    private String createdAt;
    private byte prodStatus;
    private String reviewStatus;
    private String mainImageUrl; // 存儲第一張圖片  
	private List<String> prodImages = new ArrayList<String>();
	public Integer getProdId() {
		return prodId;
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
	public void setProdPrice(Integer prodPrice) {
		this.prodPrice = prodPrice;
	}
	public Integer getProdStock() {
		return prodStock;
	}
	public void setProdStock(Integer prodStock) {
		this.prodStock = prodStock;
	}
	public String getProdContent() {
		return prodContent;
	}
	public void setProdContent(String prodContent) {
		this.prodContent = prodContent;
	}
	public Integer getOrganizerId() {
		return organizerId;
	}
	public void setOrganizerId(Integer organizerId) {
		this.organizerId = organizerId;
	}
	public String getOrganizerName() {
		return organizerName;
	}
	public void setOrganizerName(String organizerName) {
		this.organizerName = organizerName;
	}
	public Integer getSortId() {
		return sortId;
	}
	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}
	public String getSortName() {
		return sortName;
	}
	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public byte getProdStatus() {
		return prodStatus;
	}
	public void setProdStatus(byte prodStatus) {
		this.prodStatus = prodStatus;
	}
	public String getReviewStatus() {
		return reviewStatus;
	}
	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}
	public String getMainImageUrl() {
		return mainImageUrl;
	}
	public void setMainImageUrl(String mainImageUrl) {
		this.mainImageUrl = mainImageUrl;
	}
	public List<String> getProdImages() {
		return prodImages;
	}
	public void setProdImages(List<String> prodImages) {
		this.prodImages = prodImages;
	}
    
    
}
