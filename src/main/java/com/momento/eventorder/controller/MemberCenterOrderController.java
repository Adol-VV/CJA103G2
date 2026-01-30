package com.momento.eventorder.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

		List<EventOrderVO> eventOrderList = eventOrderSvc.getEventOrderByMemberId(memberId);
		List<EventOrderVO> eventOrderFinished = new ArrayList();
		LocalDateTime now = LocalDateTime.now();
		Iterator<EventOrderVO> iterator = eventOrderList.iterator();
		while (iterator.hasNext()) {
			EventOrderVO item = iterator.next();
			if (item.getEvent().getEventStartAt() != null && now.isAfter(item.getEvent().getEventEndAt())) {
				eventOrderFinished.add(item);
				iterator.remove();
			}
		}
		
		Map<Integer, Map<String, Integer>> allOrdersItemCounts = new HashMap<>();
		
		for (EventOrderVO eventOrders : eventOrderFinished) {
			Integer eventOrderId = eventOrders.getEventOrderId();
			List<Object[]> eventOrderItems = eventOrderItemSvc.getTicketCount(eventOrderId);
			
			Map<String, Integer> currentOrderCount = new HashMap<>();

			for (Object[] count : eventOrderItems) {
				Integer ticketId = (Integer) count[0];
				Integer quantity = ((Number) count[1]).intValue();

				TicketVO ticket = eventOrderItemSvc.getTicketById(ticketId);
				String ticketName = ticket.getTicketName();

				currentOrderCount.put(ticketName, quantity);
			}
			allOrdersItemCounts.put(eventOrderId, currentOrderCount);
		}

		model.addAttribute("eventOrderList", eventOrderList);
		model.addAttribute("eventOrderFinished", eventOrderFinished);
		model.addAttribute("allOrdersItemCounts", allOrdersItemCounts);

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

	@GetMapping("/panel-tokens")
	public String showToken(HttpSession session, Model model) {

		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		Integer memberId = loginMember.getMemberId();

		List<EventOrderVO> eventOrderList = eventOrderSvc.getEventOrderByMemberId(memberId);

		model.addAttribute("tokenList", eventOrderList);

		return "pages/user/partials/panel-tokens";
	}
	
	@PostMapping("/ticket-refund")
	@ResponseBody
	public ResponseEntity<String> refund(
			@RequestParam Integer eventOrderId, 
			@RequestParam String refundReason) {
		
		try {
			EventOrderVO eventOrder = eventOrderSvc.getOneEventOrder(eventOrderId);
			eventOrder.setReason(refundReason);
			eventOrder.setPayStatus(2);
			
			eventOrderSvc.updateEventOrder(eventOrder);
			return ResponseEntity.ok("退票申請已提交");
			
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("處理失敗");
		}
	}
}
