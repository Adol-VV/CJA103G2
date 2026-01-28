package com.momento.member.controller;

import java.util.List;

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

import com.momento.eventorder.model.EventOrderService;
import com.momento.eventorder.model.EventOrderVO;
import com.momento.member.model.MemberService;
import com.momento.member.model.MemberVO;
import com.momento.prodorder.model.ProdOrderIdService;
import com.momento.prodorder.model.ProdOrderIdVO;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/dashboard")
public class MemberController {

	@Autowired
	MemberService memberSvc;

	@Autowired
	ProdOrderIdService prodOrderSvc;

	@Autowired
	EventOrderService eventOrderSvc;

	@GetMapping("/member-list")
	public String showMembers(@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String keyword, Model model, HttpServletRequest req) {

		String requestedWith = req.getHeader("X-Requested-With");

		List<MemberVO> searchedMember = memberSvc.searchMembers(status, keyword);
		model.addAttribute("memberList", searchedMember);

		if ("XMLHttpRequest".equals(requestedWith)) {
			// 如果是 Ajax 請求，只回傳表格片段
			return "pages/admin/partials/panel-member-list :: memberTable";
		}

		return "pages/admin/partials/panel-member-list";
	}

	@PostMapping("/update-member-status")
	@ResponseBody
	public String updateMemberStatus(@RequestParam Integer memberId) {

		MemberVO member = memberSvc.findOneMember(memberId);

		if (member == null) {
			return "查無會員";
		}

		try {
			if (member.getStatus() == 0)
				member.setStatus(1);

			else if (member.getStatus() == 1)
				member.setStatus(0);

			memberSvc.updateMember(member);
			return "更新成功";

		} catch (Exception e) {
			return e.getMessage();
		}

	}

	@GetMapping("/member-detail")
	public String showMemberDetail(@RequestParam Integer memberId, Model model) {
		MemberVO member = memberSvc.findOneMember(memberId);

		List<ProdOrderIdVO> memberProdOrder = prodOrderSvc.getByMemberId(memberId);
		List<EventOrderVO> memberEventOrder = eventOrderSvc.getEventOrderByMemberId(memberId);

		Integer orderCount = memberProdOrder.size() + memberEventOrder.size();

		int expense = 0;
		for (ProdOrderIdVO prodOrders : memberProdOrder)
			expense += prodOrders.getPayable();

		for (EventOrderVO eventOrders : memberEventOrder)
			expense += eventOrders.getPayable();

		model.addAttribute("detail", member);
		model.addAttribute("orders", orderCount);
		model.addAttribute("expense", expense);
		return "pages/admin/partials/panel-member-list :: detailBody";
	}

	@PostMapping("update-member-information")
	@ResponseBody
	public ResponseEntity<String> updateMemberInformation(
			@RequestParam Integer memberId,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String phone,
			@RequestParam(required = false) String account,
			@RequestParam(required = false) String address
			) {
		MemberVO member = memberSvc.findOneMember(memberId);
		try {
			if(member != null) {
				
				if(password != null && !password.trim().isEmpty())
					member.setPassword(password);
				
				if(phone != null && !phone.trim().isEmpty())
					member.setPhone(phone);
				
				if(account != null && !account.trim().isEmpty())
					member.setAccount(account);
				
				if(address != null && !address.trim().isEmpty())
					member.setAddress(address);
				
				memberSvc.updateMember(member);
				return ResponseEntity.ok("更新成功");
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到該會員");
			}
		}catch(Exception e){
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("系統錯誤：" + e.getMessage());
		}
	}

}
