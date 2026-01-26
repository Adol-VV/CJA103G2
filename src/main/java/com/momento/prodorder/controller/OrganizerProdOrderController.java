package com.momento.prodorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.momento.prodorder.model.ProdOrderIdService;
import com.momento.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/organizer/prod_order")
public class OrganizerProdOrderController {
	
	@Autowired
	ProdOrderIdService poIdSev;
	
	@PostMapping("/getAllProdOrder")
	public String getAllOrderByOrganizer(Model model,HttpSession session) {
		MemberVO member = (MemberVO)session.getAttribute("loginMember");
		//會員ID
		if(member!=null) {
			model.addAttribute("allProdOrder",poIdSev.getByMemberId(member.getMemberId()));
		}
		return "pages/organizer/partials/panel-product-orders";
	}
}
