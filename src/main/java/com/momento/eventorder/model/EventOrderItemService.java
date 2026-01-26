package com.momento.eventorder.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.momento.ticket.model.TicketVO;

@Service
public class EventOrderItemService {
	
	@Autowired
	EventOrderItemRepository repository;
	
	public String getQrcodeById(Integer eventOrderItemId) {
		EventOrderItemVO item = repository.getById(eventOrderItemId);
		return item.getQrcode();
	}
	
	public TicketVO getTicketById(Integer eventOrderItemId) {
		EventOrderItemVO item = repository.getById(eventOrderItemId);
		
		return item.getTicket();
	}
}
