package com.momento.eventorder.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.momento.eventorder.dto.CheckoutDTO;
import com.momento.eventorder.dto.OrderCreateDTO;
import com.momento.eventorder.dto.SelectionFormDTO;
import com.momento.eventorder.dto.TicketItemsDTO;
import com.momento.eventorder.model.CreateOrderService;
import com.momento.eventorder.model.EventOrderVO;
import com.momento.member.model.MemberService;
import com.momento.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member/event")
public class OrderController {
	
	@Autowired
	CreateOrderService eventOrderSvc;
	
	@Autowired
	MemberService memberSvc;
	
	// 從event-detail抓到資料放到event-checkout
	@PostMapping("/checkout")
	public String showCheckout(@ModelAttribute SelectionFormDTO selectionForm, HttpSession session) {
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		Integer memberId = loginMember.getMemberId();
		
		MemberVO member = memberSvc.findOneMember(memberId);
		Integer tokenRemain = member.getToken();
		
		List<TicketItemsDTO> finalSelectedItems = eventOrderSvc.processSelectedTickets(selectionForm);
		CheckoutDTO checkoutData = eventOrderSvc.calculateCheckout(selectionForm, false, memberId);
		
		session.setAttribute("originalform", selectionForm);
		session.setAttribute("finalSelectedItems", finalSelectedItems);
		session.setAttribute("checkoutData", checkoutData);
		session.setAttribute("tokenRemain", tokenRemain);
		
		return "pages/user/event-checkout";
	}
	
	// 右邊結帳框框
	@PostMapping("/checkout/refresh-summary")
	@ResponseBody
	public CheckoutDTO updateCheckout(@RequestParam boolean useToken, HttpSession session) {
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		Integer memberId = loginMember.getMemberId();
		
		SelectionFormDTO selectionForm = (SelectionFormDTO)session.getAttribute("originalform"); 
		CheckoutDTO updateData = eventOrderSvc.calculateCheckout(selectionForm, useToken, memberId);
		
		session.setAttribute("checkoutData", updateData);
		
		return updateData;
	}
	
	@PostMapping("/checkout/create")
	public ResponseEntity<?> create(@RequestBody OrderCreateDTO orderCreateDto, HttpSession session) {
        // 從 Session 取得會員 ID
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
        }
        
        try {
            // 直接呼叫寫在 Service 的方法
            EventOrderVO newOrder = eventOrderSvc.createOrder(orderCreateDto, loginMember.getMemberId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("eventOrderId", newOrder.getEventOrderId());
            response.put("payable", newOrder.getPayable());
            response.put("tokenReward", (newOrder.getPayable()/300)*5);
            response.put("totalQuantity", newOrder.getEventOrderItems().size());
            
            // 成功回傳 200
            return ResponseEntity.ok(response);
            
            
        } catch (Exception e) {
            // 如果 Service 拋出 RuntimeException (例如：購票時間已過、餘額不足)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
