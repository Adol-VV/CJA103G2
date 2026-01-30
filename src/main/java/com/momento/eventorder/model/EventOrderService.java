package com.momento.eventorder.model;

import com.momento.notify.model.NotificationBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventOrderService {
	
	@Autowired
	EventOrderRepository eventOrderRepo;
	
	@Autowired
	EventOrderItemRepository eventOrderItemRepo;

	@Autowired // pei
	private NotificationBridgeService bridgeService;
	

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
	
	public Page<EventOrderVO> getEventOrdersbyPages(Integer eventOrderId, String memberName, String eventTitle, Pageable pageable){
		
		if (eventOrderId != null || memberName != null || eventTitle != null) {
		    return eventOrderRepo.searchOrders(eventOrderId, memberName, eventTitle, pageable);
		}
		
		return eventOrderRepo.findAll(pageable);
	}
	
	public EventOrderVO getOneEventOrder(Integer eventOrderId){
		return eventOrderRepo.getByEventOrderId(eventOrderId);
	}
}
