package com.momento.prod.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "prod_image")
public class ProdImageVO implements Serializable{
	private static final long serialVersionUID = 1L;	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PROD_IMAGE_ID", updatable = false)
	private Integer prodImageId;
	
	@ManyToOne
	@JoinColumn(name = "PROD_ID")
	private ProdVO prodVO;
	
	
	@Column(name = "IMAGE_URL")
	private String imageUrl;
	
	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;
	
	public ProdImageVO() {
		
	}

	public Integer getProdImageId() {
		return prodImageId;
	}

	public void setProdImageId(Integer prodImageId) {
		this.prodImageId = prodImageId;
	}


	public ProdVO getProdVO() {
		return prodVO;
	}

	public void setProdVO(ProdVO prodVO) {
		this.prodVO = prodVO;
	}



	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	
}
