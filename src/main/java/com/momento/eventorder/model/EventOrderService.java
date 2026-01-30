package com.momento.eventorder.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EventOrderService {
	
	@Autowired
	EventOrderRepository eventOrderRepo;
	
	@Autowired
	EventOrderItemRepository eventOrderItemRepo;
	
	public void updateEventOrder(EventOrderVO eventOrder) {
		eventOrderRepo.save(eventOrder);
	}

	public List<EventOrderVO> getEventOrderByMemberId(Integer memberId){
		return eventOrderRepo.findByMember_MemberId(memberId);
	}
	
	public List<EventOrderVO> getEventOrderByOrganizer(Integer organizerId, Integer activeEvent, Integer finishedEvent, String buyer){
		return eventOrderRepo.filterOrders(organizerId, activeEvent, finishedEvent, buyer);
	}
	
	public List<EventOrderVO> getEventOrdersByEventId(Integer eventId){
		return eventOrderRepo.findByEvent_EventId(eventId);
	};
	
	public List<EventOrderVO> getAllEventOrders(){
		return eventOrderRepo.findAll();
	}
	
	public Page<EventOrderVO> getEventOrdersbyPages(Integer eventOrderId, String memberName, String eventTitle, Integer payStatus , Pageable pageable){
		
		if (eventOrderId != null || memberName != null || eventTitle != null || payStatus != null) {
		    return eventOrderRepo.searchOrders(eventOrderId, memberName, eventTitle, payStatus , pageable);
		}
		
		return eventOrderRepo.findAll(pageable);
	}
	
	public EventOrderVO getOneEventOrder(Integer eventOrderId){
		return eventOrderRepo.getByEventOrderId(eventOrderId);
	}
}
