package com.momento.eventorder.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.momento.eventorder.model.EventOrderItemService;
import com.momento.eventorder.model.EventOrderService;
import com.momento.eventorder.model.EventOrderVO;
import com.momento.member.model.MemberService;
import com.momento.member.model.MemberVO;
import com.momento.ticket.model.TicketVO;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/dashboard")
public class AdminOrderController {

	@Autowired
	EventOrderService eventOrderSvc;

	@Autowired
	EventOrderItemService eventOrderItemSvc;

	@Autowired
	MemberService memberSvc;

	@GetMapping("/eventOrders")
	public String showEventOrders(Model model, @RequestParam(required = false) Integer eventOrderId,
			@RequestParam(required = false) String memberName, 
			@RequestParam(required = false) String eventTitle,
			@RequestParam(required = false) Integer payStatus,
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "10") int size,
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Page<EventOrderVO> orders = eventOrderSvc.getEventOrdersbyPages(eventOrderId, memberName, eventTitle, payStatus , pageable);

		model.addAttribute("orderPage", orders);

		if ("XMLHttpRequest".equals(requestedWith)) {
			// 有點擊 a 標籤：只回傳片段，這樣樣式就不會消失
			return "pages/admin/partials/panel-event-orders :: eventOrderTable";
		}

		return "pages/admin/partials/panel-event-orders";
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

			TicketVO ticket = eventOrderItemSvc.getTicketById(ticketId);
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
			return "pages/admin/partials/panel-event-orders :: refund-body";
		}
		response.setHeader("X-Fragment-Type", "order");
		return "pages/admin/partials/panel-event-orders :: order-body";
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

			return ResponseEntity.ok("申請結果已送出");

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("處理失敗");
		}

	}
}
