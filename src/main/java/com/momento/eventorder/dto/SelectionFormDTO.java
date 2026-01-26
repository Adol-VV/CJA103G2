package com.momento.eventorder.dto;

import java.util.ArrayList;
import java.util.List;

import com.momento.eventorder.model.EventOrderItemVO;

public class SelectionFormDTO {
	
	List<TicketItemsDTO> items;

	public List<TicketItemsDTO> getItems() {
		return items;
	}

	public void setItems(List<TicketItemsDTO> items) {
		this.items = items;
	}
	
}
