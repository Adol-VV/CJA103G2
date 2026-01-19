package com.momento.recommendedprod.model;

import com.momento.organizer.model.OrganizerVO;
import com.momento.prod.model.ProdVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "RECOMMENDED_PROD")
public class RecommendedProdVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer recommendedProdId;
	private ProdVO prodVO;
	private OrganizerVO organizerVO;

	public RecommendedProdVO() {
	}

	@Id
	@Column(name = "RECOMMENDED_PROD_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getRecommendedProdId() {
		return this.recommendedProdId;
	}

	public void setRecommendedProdId(Integer recommendedProdId) {
		this.recommendedProdId = recommendedProdId;
	}

	@ManyToOne
	@JoinColumn(name = "PROD_ID")
	public ProdVO getProdVO() {
		return this.prodVO;
	}

	public void setProdVO(ProdVO prodVO) {
		this.prodVO = prodVO;
	}

	@ManyToOne
	@JoinColumn(name = "ORGANIZER_ID")
	public OrganizerVO getOrganizerVO() {
		return this.organizerVO;
	}

	public void setOrganizerVO(OrganizerVO organizerVO) {
		this.organizerVO = organizerVO;
	}
}