package com.momento.eventorder.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.momento.event.model.EventRepository;
import com.momento.event.model.EventVO;
import com.momento.organizer.model.OrganizerRepository;
import com.momento.organizer.model.OrganizerVO;

@Service
public class EventOrderService {
	
	@Autowired
	EventOrderRepository eventOrderRepo;
	
	@Autowired
	EventOrderItemRepository eventOrderItemRepo;
	
	@Autowired
	EventRepository eventRepo;
	
	@Autowired
	OrganizerRepository organizerRepo;
	
	public void updateEventOrder(EventOrderVO eventOrder) {
		eventOrderRepo.save(eventOrder);
	}

	public List<EventOrderVO> getEventOrderByMemberId(Integer memberId){
		return eventOrderRepo.findByMember_MemberIdOrderByCreatedAtDesc(memberId);
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
	
	public List<EventOrderVO> getTwoRecentEvents(Integer memberId, LocalDateTime currentTime){
		return eventOrderRepo.findTop2ByMember_MemberIdAndEvent_EventStartAtAfterOrderByEvent_EventStartAtAsc(memberId, currentTime);
	}
	
	public List<Map<String, Object>>getThreeRecentOrders(Integer memberId){
		List<Object[]> results = eventOrderRepo.findLatestThreeOrdersUnified(memberId);
		
		List<Map<String, Object>> orders = new ArrayList<>();

	    for (Object[] row : results) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("id", row[0]);
	        map.put("date", row[1]);
	        map.put("amount", row[2]);
	        map.put("status", row[3]);
	        String type = (String) row[5]; // 取得 SQL 中的 'EVENT' 或 'PRODUCT'
	        Integer infoId = (Integer) row[4]; // 取得 event_id 或 organizer_id
	        
	        String displayName = "未知名稱";
	        if ("EVENT".equals(type)) {
	            // 假設你有 EventRepository，用 event_id 找標題
	             displayName = eventRepo.findById(infoId).map(EventVO::getTitle).orElse("活動已失效");
	            
	        } else if ("PRODUCT".equals(type)) {
	            // 假設你有 OrganizerRepository，用 organizer_id 找名稱
	             displayName = organizerRepo.findById(infoId).map(OrganizerVO::getName).orElse("商家已失效");
	            
	        }
	        
	        map.put("name", displayName); // 將解析後的名稱存入 Map
	        map.put("type", type);
	        orders.add(map);
	    }
	    return orders;
	}
}
