package com.momento.message.model;

import java.sql.Timestamp;

import com.momento.article.model.ArticleVO;
import com.momento.member.model.MemberVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "MESSAGE")
public class MessageVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer messageId;
	private MemberVO memberVO;
	private ArticleVO articleVO;
	private String content;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private Integer status;

	public MessageVO() {
	}

	@Id
	@Column(name = "MESSAGE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getMessageId() {
		return this.messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	@ManyToOne
	@JoinColumn(name = "MEMBER_ID")
	public MemberVO getMemberVO() {
		return this.memberVO;
	}

	public void setMemberVO(MemberVO memberVO) {
		this.memberVO = memberVO;
	}

	@ManyToOne
	@JoinColumn(name = "ARTICLE_ID")
	public ArticleVO getArticleVO() {
		return this.articleVO;
	}

	public void setArticleVO(ArticleVO articleVO) {
		this.articleVO = articleVO;
	}

	@Column(name = "CONTENT", columnDefinition = "LONGTEXT")
	@NotEmpty(message="留言內容: 請勿空白")
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
	
	@Column(name = "STATUS")
	@NotNull(message="狀態: 請勿空白")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
