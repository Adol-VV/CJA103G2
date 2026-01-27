package com.momento.featured.model;

import java.sql.Timestamp;

import com.momento.event.model.EventVO;
import com.momento.organizer.model.OrganizerVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "FEATURED")
public class FeaturedVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer featuredId;
	private EventVO eventVO;
	private OrganizerVO organizerVO;
	private Timestamp startedAt;
	private Timestamp endedAt;

	public FeaturedVO() {
	}

	@Id
	@Column(name = "FEATURED_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getFeaturedId() {
		return this.featuredId;
	}

	public void setFeaturedId(Integer featuredId) {
		this.featuredId = featuredId;
	}

	@ManyToOne
	@JoinColumn(name = "EVENT_ID")
	public EventVO getEventVO() {
		return this.eventVO;
	}

	public void setEventVO(EventVO eventVO) {
		this.eventVO = eventVO;
	}

	@ManyToOne
	@JoinColumn(name = "ORGANIZER_ID")
	public OrganizerVO getOrganizerVO() {
		return this.organizerVO;
	}

	public void setOrganizerVO(OrganizerVO organizerVO) {
		this.organizerVO = organizerVO;
	}

	@Column(name = "STARTED_AT")
	@NotNull(message = "開始時間: 請勿空白")
	public Timestamp getStartedAt() {
		return this.startedAt;
	}

	public void setStartedAt(Timestamp startedAt) {
		this.startedAt = startedAt;
	}

	@Column(name = "ENDED_AT")
	@NotNull(message = "結束時間: 請勿空白")
	public Timestamp getEndedAt() {
		return this.endedAt;
	}

	public void setEndedAt(Timestamp endedAt) {
		this.endedAt = endedAt;
	}
}
