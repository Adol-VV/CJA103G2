package com.momento.articleimage.model;

import java.sql.Timestamp;

import com.momento.article.model.ArticleVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "ARTICLE_IMAGE")
public class ArticleImageVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer articleImageId;
	private ArticleVO articleVO;
	private String imageUrl;
	private Timestamp createdAt;

	public ArticleImageVO() {
	}

	@Id
	@Column(name = "ARTICLE_IMAGE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getArticleImageId() {
		return this.articleImageId;
	}

	public void setArticleImageId(Integer articleImageId) {
		this.articleImageId = articleImageId;
	}

	@ManyToOne
	@JoinColumn(name = "ARTICLE_ID")
	public ArticleVO getArticleVO() {
		return this.articleVO;
	}

	public void setArticleVO(ArticleVO articleVO) {
		this.articleVO = articleVO;
	}

	@Column(name = "IMAGE_URL")
	@NotEmpty(message="圖片連結: 請勿空白")
	@Size(max=500, message="圖片連結: 長度不能超過{max}")
	public String getImageUrl() {
		return this.imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Column(name = "CREATED_AT", insertable = false, updatable = false)
	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
}
