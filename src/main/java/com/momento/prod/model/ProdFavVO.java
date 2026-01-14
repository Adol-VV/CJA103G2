package com.momento.prod.model;

import java.io.Serializable;

import com.momento.member.model.MemberVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "prod_fav")
public class ProdFavVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PROD_FAV_ID", updatable = false)
	private Integer prodFavId;
	
	@ManyToOne
	@JoinColumn(name = "MEMBER_ID")
	private MemberVO memberVO;
	
	@ManyToOne
	@JoinColumn(name = "PROD_ID")
	private ProdVO prodVO;

	public ProdFavVO() {

	}

	public Integer getProdFavId() {
		return prodFavId;
	}

	public void setProdFavId(Integer prodFavId) {
		this.prodFavId = prodFavId;
	}

	public MemberVO getMemberVO() {
		return memberVO;
	}

	public void setMemberVO(MemberVO memberVO) {
		this.memberVO = memberVO;
	}

	public ProdVO getProdVO() {
		return prodVO;
	}

	public void setProdVO(ProdVO prodVO) {
		this.prodVO = prodVO;
	}


	
	
}
