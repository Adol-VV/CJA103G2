package com.momento.eventorder.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.momento.eventorder.model.EventOrderItemService;
import com.momento.eventorder.model.EventOrderService;
import com.momento.eventorder.model.EventOrderVO;
import com.momento.member.model.MemberVO;
import com.momento.ticket.model.TicketVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member/dashboard")
public class MemberCenterOrderController {
	
	@Autowired
	EventOrderService eventOrderSvc;
	
	@Autowired
	EventOrderItemService eventOrderItemSvc;
	
	@GetMapping("/panel-tickets")
	public String MemberOrderEvent(HttpSession session, Model model) {
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		Integer memberId = loginMember.getMemberId();
		
		List<EventOrderVO> eventOrderList =eventOrderSvc.getEventOrderByMemberId(memberId);
		List<EventOrderVO> eventOrderFinished = new ArrayList();
		LocalDateTime now = LocalDateTime.now();
		Iterator<EventOrderVO> iterator = eventOrderList.iterator();
		while (iterator.hasNext()) {
		    EventOrderVO item = iterator.next();
		    if (now.isAfter(item.getEvent().getEventAt())) {
		        eventOrderFinished.add(item);
		        iterator.remove();
		    }
		}
		
		model.addAttribute("eventOrderList", eventOrderList);
		model.addAttribute("eventOrderFinished", eventOrderFinished);
		
		return "pages/user/partials/panel-tickets";
	}
	
	@GetMapping("/get-uuid-by-id")
	@ResponseBody 
	public Map<String, String> getUuid(@RequestParam String eventOrderItemId) {
	    
	    String uuid = eventOrderItemSvc.getQrcodeById(Integer.valueOf(eventOrderItemId)); 
	    TicketVO ticket = eventOrderItemSvc.getTicketById(Integer.valueOf(eventOrderItemId));
	    String eventName = ticket.getEvent().getTitle();
	    
	    Map<String, String> map = new HashMap<>();
	    map.put("uuid", uuid);
	    map.put("eventName", eventName);
	    return map;
	}
	
	@GetMapping("/tokens")
	public String showToken(HttpSession session, Model model) {
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		Integer memberId = loginMember.getMemberId();
		
		List<EventOrderVO> eventOrderList = eventOrderSvc.getEventOrderByMemberId(memberId);
		
		model.addAttribute("tokenList", eventOrderList);
		
		return "pages/user/partials/panel-tokens";
	}

}
