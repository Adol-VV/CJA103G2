package com.momento.article.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.momento.articleimage.model.ArticleImageVO;
import com.momento.organizer.model.OrganizerVO;
import com.momento.message.model.MessageVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "ARTICLE")
public class ArticleVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer articleId;
	private OrganizerVO organizerVO;
	private String title;
	private String content;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private List<ArticleImageVO> articleImages = new ArrayList<>();
	private List<MessageVO> messages = new ArrayList<>();

	public ArticleVO() {
	}

	@Id
	@Column(name = "ARTICLE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getArticleId() {
		return this.articleId;
	}

	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}

	@ManyToOne
	@JoinColumn(name = "ORGANIZER_ID")
	public OrganizerVO getOrganizerVO() {
		return this.organizerVO;
	}

	public void setOrganizerVO(OrganizerVO organizerVO) {
		this.organizerVO = organizerVO;
	}

	@Column(name = "TITLE")
	@NotEmpty(message = "標題: 請勿空白")
	@Size(max = 100, message = "標題: 長度不能超過{max}")
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "CONTENT", columnDefinition = "LONGTEXT")
	@NotEmpty(message = "內容: 請勿空白")
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "CREATED_AT", insertable = false, updatable = false)
	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@Column(name = "UPDATED_AT", insertable = false, updatable = false)
	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@OneToMany(mappedBy = "articleVO", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("articleImageId ASC") // 讓取出的圖片順序固定
	public List<ArticleImageVO> getArticleImages() {
		return this.articleImages;
	}

	public void setArticleImages(List<ArticleImageVO> articleImages) {
		this.articleImages = articleImages;
	}

	@OneToMany(mappedBy = "articleVO", cascade = CascadeType.ALL)
	public List<MessageVO> getMessages() {
		return messages;
	}

	public void setMessages(List<MessageVO> messages) {
		this.messages = messages;
	}
}
