package com.momento.eventorder.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.momento.event.model.EventVO;

@Service
public class EventOrderService {
	
	@Autowired
	EventOrderRepository eventOrderRepo;
	
	@Autowired
	EventOrderItemRepository eventOrderItemRepo;
	

	public List<EventOrderVO> getEventOrderByMemberId(Integer memberId){
		return eventOrderRepo.findByMember_MemberId(memberId);
	}
	
	public List<EventOrderVO> getEventOrderByCompositeQuery(Integer organizerId, Integer activeEvent, Integer finishedEvent, String buyer){
		return eventOrderRepo.filterOrders(organizerId, activeEvent, finishedEvent, buyer);
	}
	
	public List<EventOrderVO> getEventOrdersByEventId(Integer eventId){
		return eventOrderRepo.findByEvent_EventId(eventId);
	};
}
