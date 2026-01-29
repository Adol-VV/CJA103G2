package com.momento.eventorder.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.momento.event.model.EventRepository;
import com.momento.event.model.EventVO;
import com.momento.eventorder.model.EventOrderItemService;
import com.momento.eventorder.model.EventOrderItemVO;
import com.momento.eventorder.model.EventOrderService;
import com.momento.eventorder.model.EventOrderVO;
import com.momento.organizer.model.OrganizerVO;

import jakarta.servlet.http.HttpServletRequest;
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

		List<EventOrderItemVO> checkedIn = eventOrderItemSvc.getEventOrderItemBystatus(1);
		model.addAttribute("activeEvents", activeEvents);
		model.addAttribute("checkedIn", checkedIn);
		// 驗票(比對UUID)
		if (randomUUID != null && !randomUUID.isEmpty()) {
			EventOrderItemVO item = eventOrderItemSvc.getItemsByQrcode(randomUUID);
			System.out.println(item.getEventOrder().getEvent().getEventId());
			if (item != null && item.getEventOrder().getEvent().getEventId() == eventId && item.getStatus() != 1) {
				model.addAttribute("msg", "核銷成功");
				item.setStatus(1);
				item.setVerifiedAt(LocalDateTime.now());
				eventOrderItemSvc.updateItems(item);
				model.addAttribute("information", item);

			} else if (item.getEventOrder().getEvent().getEventId() != eventId) {
				model.addAttribute("msg", "活動不符");
			} else if (item.getStatus() == 1) {
				model.addAttribute("msg", "重複驗票");
			} else {
				model.addAttribute("msg", "無效票券");
			}
			return "pages/organizer/partials/panel-ticket-scanner :: success";
		}

		return "pages/organizer/partials/panel-ticket-scanner";
	}
}
