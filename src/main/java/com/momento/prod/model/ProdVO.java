package com.momento.prod.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.momento.emp.model.EmpVO;
import com.momento.organizer.model.OrganizerVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "prod")
public class ProdVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PROD_ID", updatable = false)
	private Integer prodId;
	
	@ManyToOne
	@JoinColumn(name = "ORGANIZER_ID")
	private OrganizerVO organizerVO;
	
	@ManyToOne
	@JoinColumn(name = "SORT_ID")
	private ProductSortVO productSort;
	
	@ManyToOne
	@JoinColumn(name = "EMP_ID")
	private EmpVO empVO;
	
	@Column(name = "PROD_NAME")
	private String prodName;
	
	@Column(name = "PROD_CONTENT", columnDefinition = "LONGTEXT")
	private String prodContent;
	
	@Column(name = "PROD_PRICE")
	private Integer prodPrice;
	
	@Column(name = "PROD_STOCK")
	private Integer prodStock;
	
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;
	
	@Column(name = "UPDATED_AT")
	private LocalDateTime updatedAt;
	
	@Column(name = "PROD_STATUS", columnDefinition = "TINYINT")
	private byte prodStatus;
	
	@Column(name = "REVIEW_STATUS", columnDefinition = "TINYINT")
	private byte reviewStatus;
	
	public ProdVO() {
		
	}

	public Integer getProdId() {
		return prodId;
	}

	public void setProdId(Integer prodId) {
		this.prodId = prodId;
	}


	public OrganizerVO getOrganizerVO() {
		return organizerVO;
	}

	public void setOrganizerVO(OrganizerVO organizerVO) {
		this.organizerVO = organizerVO;
	}

	public ProductSortVO getProductSort() {
		return productSort;
	}

	public void setProductSort(ProductSortVO productSort) {
		this.productSort = productSort;
	}

	public EmpVO getEmpVO() {
		return empVO;
	}

	public void setEmpVO(EmpVO empVO) {
		this.empVO = empVO;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public String getProdContent() {
		return prodContent;
	}

	public void setProdContent(String prodContent) {
		this.prodContent = prodContent;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public byte getProdStatus() {
		return prodStatus;
	}

	public void setProdStatus(byte prodStatus) {
		this.prodStatus = prodStatus;
	}

	public byte getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(byte reviewStatus) {
		this.reviewStatus = reviewStatus;
	}
	
	
	
}
