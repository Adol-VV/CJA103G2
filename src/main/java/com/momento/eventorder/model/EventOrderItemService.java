package com.momento.eventorder.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	public EventOrderItemVO getItemsByQrcode(String qrcode){
		return repository.findByQrcode(qrcode);
	}
	
	@Transactional
	public void updateItems(EventOrderItemVO item) {
		repository.save(item);
	}
	
	public List<EventOrderItemVO> getEventOrderItemBystatus(Integer status){
		return repository.findByStatus(status);
	}
	
	public List<Object[]> getTicketCount(Integer eventOrderId){
		return repository.countByTicketAndOrderId(eventOrderId);
	}
}
