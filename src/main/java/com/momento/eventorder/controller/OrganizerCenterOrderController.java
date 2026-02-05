package com.momento.eventorder.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.momento.event.model.EventRepository;
import com.momento.event.model.EventVO;
import com.momento.eventorder.model.EventOrderItemService;
import com.momento.eventorder.model.EventOrderItemVO;
import com.momento.eventorder.model.EventOrderService;
import com.momento.eventorder.model.EventOrderVO;
import com.momento.member.model.MemberService;
import com.momento.member.model.MemberVO;
import com.momento.notify.model.NotificationBridgeService;
import com.momento.organizer.model.OrganizerVO;
import com.momento.ticket.model.TicketRepository;
import com.momento.ticket.model.TicketVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/organizer/dashboard")
public class OrganizerCenterOrderController {

	@Autowired
	EventRepository eventRepo;

	@Autowired
	EventOrderService eventOrderSvc;

	@Autowired
	EventOrderItemService eventOrderItemSvc;
	
	@Autowired
	TicketRepository ticketRepo;
	
	@Autowired
	MemberService memberSvc;

	@Autowired
	NotificationBridgeService notificationBridgeService;

	@GetMapping("/tickets")
	public String showOrders(@RequestParam(required = false) Integer activeEvent,
			@RequestParam(required = false) Integer finishedEvent, @RequestParam(required = false) String buyer,
			HttpSession session, Model model, HttpServletRequest req) {

		OrganizerVO loginOrganizer = (OrganizerVO) session.getAttribute("loginOrganizer");
		Integer organizerId = loginOrganizer.getOrganizerId();
		String requestedWith = req.getHeader("X-Requested-With");

		List<EventVO> events = eventRepo.findByOrganizer_OrganizerId(organizerId);
		LocalDateTime now = LocalDateTime.now();

		List<EventVO> activeEvents = new ArrayList();
		List<EventVO> finishedEvents = new ArrayList();

		for (EventVO event : events) {
			if (event.getEventStartAt() != null && now.isBefore(event.getEventStartAt())) {
				activeEvents.add(event);
			} else {
				finishedEvents.add(event);
			}
		}

		List<EventOrderVO> eventOrderList = eventOrderSvc.getEventOrderByOrganizer(organizerId, activeEvent,
				finishedEvent, buyer);

		model.addAttribute("activeEvents", activeEvents);
		model.addAttribute("finishedEvents", finishedEvents);
		model.addAttribute("eventOrderList", eventOrderList);

		if ("XMLHttpRequest".equals(requestedWith)) {
			// 如果是 Ajax 請求，只回傳表格片段
			return "pages/organizer/partials/panel-orders :: orderTableBody";
		}
		return "pages/organizer/partials/panel-orders";
	}

	@GetMapping("ticketScanner")
	public String ticketScanner(@RequestParam(required = false) String randomUUID,
			@RequestParam(required = false) Integer eventId, HttpSession session, Model model) {
		OrganizerVO loginOrganizer = (OrganizerVO) session.getAttribute("loginOrganizer");
		Integer organizerId = loginOrganizer.getOrganizerId();

		List<EventVO> events = eventRepo.findByOrganizer_OrganizerId(organizerId);
		List<EventVO> activeEvents = new ArrayList();

		for (EventVO event : events) {
			if (event.getEventStartAt() != null && LocalDateTime.now().isBefore(event.getEventStartAt()))
				activeEvents.add(event);
		}

		model.addAttribute("activeEvents", activeEvents);
		model.addAttribute("checkedIn", checkedIn);
		
		
		List<EventOrderVO> checkedInOrders = eventOrderSvc.getEventOrdersByEventId(eventId);
		List<EventOrderItemVO> checkedIn = null;
		for(EventOrderVO order: checkedInOrders) {
			List<EventOrderItemVO> checkedInItems = order.getEventOrderItems();
			for(EventOrderItemVO items: checkedInItems) {
				if(items.getStatus() == 1)
					checkedIn.add(items);
			}
		}
		// 驗票(比對UUID)
		if (randomUUID != null && !randomUUID.isEmpty()) {
			EventOrderItemVO item = eventOrderItemSvc.getItemsByQrcode(randomUUID);

			if (item != null) {
			    // 進入這裡，代表 item 絕對不是 null，可以安全使用 get
			    Integer currentItemEventId = item.getEventOrder().getEvent().getEventId();
			    
			    if (currentItemEventId == eventId && item.getStatus() != 1) {
			        model.addAttribute("msg", "核銷成功");
					item.setStatus(1);

					item.setVerifiedAt(LocalDateTime.now());

					eventOrderItemSvc.updateItems(item);

					model.addAttribute("information", item);
			    } else if (currentItemEventId != eventId) {
			        model.addAttribute("msg", "活動不符");
			    } else if (item.getStatus() == 1) {
			        model.addAttribute("msg", "重複驗票");
			    }
			} else {
			   
			    model.addAttribute("msg", "無效票券");
			}
			return "pages/organizer/partials/panel-ticket-scanner :: success";
		}

		return "pages/organizer/partials/panel-ticket-scanner";
	}
	
	@GetMapping("/order-detail")
	public String showOrderInformation(@RequestParam Integer eventOrderId, Model model, HttpServletResponse response) {

		EventOrderVO eventOrder = eventOrderSvc.getOneEventOrder(eventOrderId);

		Integer tokenReward = (eventOrder.getPayable() / 300) * 5;

		List<Object[]> eventOrderItems = eventOrderItemSvc.getTicketCount(eventOrderId);

		Map<String, Integer> itemCount = new HashMap();

		for (Object[] count : eventOrderItems) {
			Integer ticketId = (Integer) count[0];
			Integer quantity = ((Number) count[1]).intValue();

			TicketVO ticket = ticketRepo.getById(ticketId);
			String ticketName = ticket.getTicketName();

			itemCount.put(ticketName, quantity);
		}
		if (eventOrder.getReason() == null)
			eventOrder.setReason("");

		model.addAttribute("eventOrder", eventOrder);
		model.addAttribute("tokenReward", tokenReward);
		model.addAttribute("itemCount", itemCount);

		if (eventOrder.getPayStatus() == 2) {
			response.setHeader("X-Fragment-Type", "refund");
			return "pages/organizer/partials/panel-orders :: refund-body";
		}
		response.setHeader("X-Fragment-Type", "order");
		return "pages/organizer/partials/panel-orders :: order-body";
	}
	
	@GetMapping("/refund")
	@ResponseBody
	public ResponseEntity<String> refundResult(@RequestParam Integer eventOrderId, @RequestParam boolean refundResult) {
		try {
			EventOrderVO eventOrder = eventOrderSvc.getOneEventOrder(eventOrderId);
			MemberVO member = eventOrder.getMember();
			
			Integer token = member.getToken();
			
			Integer tokenReward = (eventOrder.getPayable()/300) * 5;
			
			if (refundResult == true) {
				eventOrder.setPayStatus(3);
				
				Integer tokenRefund = eventOrder.getTokenUsed() - tokenReward;
				
				member.setToken(token + tokenRefund);
				
			} else {
				eventOrder.setPayStatus(4);
			}
			
			memberSvc.updateMember(member);
			eventOrderSvc.updateEventOrder(eventOrder);
			notificationBridgeService.processRefundResultNotify(eventOrder, refundResult);

			return ResponseEntity.ok("申請結果已送出");

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("處理失敗");
		}

	}
}
