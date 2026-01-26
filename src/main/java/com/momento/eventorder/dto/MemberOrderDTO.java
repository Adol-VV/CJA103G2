package com.momento.eventorder.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MemberOrderDTO implements Serializable{
	
	private Integer eventOrderId;
	private String organizerName;
	private String eventTitle;
	private LocalDateTime startedAt;
	private LocalDateTime endedAt;
	private String place;
	private Integer total;
	private Integer tokenUsed;
	private Integer payable;
	private Integer status;
	private LocalDateTime createdAt;
	
	
	public Integer getEventOrderId() {
		return eventOrderId;
	}
	public void setEventOrderId(Integer eventOrderId) {
		this.eventOrderId = eventOrderId;
	}
	public String getTitle() {
		return eventTitle;
	}
	public void setTitle(String title) {
		this.eventTitle = title;
	}
	public LocalDateTime getStartedAt() {
		return startedAt;
	}
	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}
	public LocalDateTime getEndedAt() {
		return endedAt;
	}
	public void setEndedAt(LocalDateTime endedAt) {
		this.endedAt = endedAt;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getTokenUsed() {
		return tokenUsed;
	}
	public void setTokenUsed(Integer tokenUsed) {
		this.tokenUsed = tokenUsed;
	}
	public Integer getPayable() {
		return payable;
	}
	public void setPayable(Integer payable) {
		this.payable = payable;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public MemberOrderDTO() {
		super();
	}
	public MemberOrderDTO(Integer eventOrderId, String organizerName, String eventTitle, LocalDateTime startedAt,
			LocalDateTime endedAt, String place, Integer total, Integer tokenUsed, Integer payable, Integer status,
			LocalDateTime createdAt) {
		super();
		this.eventOrderId = eventOrderId;
		this.organizerName = organizerName;
		this.eventTitle = eventTitle;
		this.startedAt = startedAt;
		this.endedAt = endedAt;
		this.place = place;
		this.total = total;
		this.tokenUsed = tokenUsed;
		this.payable = payable;
		this.status = status;
		this.createdAt = createdAt;
	}
}
