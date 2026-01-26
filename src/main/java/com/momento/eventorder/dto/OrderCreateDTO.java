package com.momento.eventorder.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderCreateDTO implements Serializable{
    // 訂單主檔相關
    private Integer memberId;      // 會員編號 (從 Session 或 Token 取得則可省略)
    private Integer organizerId;   // 主辦方編號
    private Integer eventId;       // 活動編號
    private Integer tokenUsed;
    @JsonProperty("isUseToken")
    private boolean useToken;
    
    // 訂單明細 (一對多)
    private List<OrderItemDTO> items;

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getOrganizerId() {
		return organizerId;
	}

	public void setOrganizerId(Integer organizerId) {
		this.organizerId = organizerId;
	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public Integer getTokenUsed() {
		return tokenUsed;
	}

	public void setTokenUsed(Integer tokenUsed) {
		this.tokenUsed = tokenUsed;
	}

	public List<OrderItemDTO> getItems() {
		return items;
	}

	public void setItems(List<OrderItemDTO> items) {
		this.items = items;
	}

	public boolean isUseToken() {
		return useToken;
	}

	public void setUseToken(boolean useToken) {
		this.useToken = useToken;
	}
    
	
}
