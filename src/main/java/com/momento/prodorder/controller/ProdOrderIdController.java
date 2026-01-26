package com.momento.prodorder.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.momento.member.model.MemberService;
import com.momento.prodorder.model.ProdOrderIdService;
import com.momento.prodorder.model.ProdOrderIdVO;



import com.momento.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/member/prod_order")
public class ProdOrderIdController {
	
	@Autowired
	ProdOrderIdService poIdSev;
	
	@Autowired
	MemberService memberSvc;
	
	
	@PostMapping("/insertOrder")
	@ResponseBody // 回傳純文字或 JSON，不轉導網頁
	public String insertOrder(@Valid @RequestBody ProdOrderIdVO prodOrderIdVO, BindingResult result, ModelMap model
			, HttpSession session) {
		
		if(!result.hasErrors()) {
			MemberVO member = (MemberVO)session.getAttribute("loginMember");
			//會員ID
			prodOrderIdVO.setMemberId(member);
			
			System.out.println(member);
			//更改會員裡的Token
			int token = (int)member.getToken();
			token -= prodOrderIdVO.getToken();
			token += (prodOrderIdVO.getPayable()/300)*5;
			member.setToken(token);
			memberSvc.updateMember(member);
			
			
			poIdSev.addProdOrder(prodOrderIdVO);
			return "新增成功";
		}else {
			return "資料驗證失敗";
		}
	}
	
	@GetMapping("/getAllProdOrder")
	public String getAllOrder(Model model,HttpSession session) {
		MemberVO member = (MemberVO)session.getAttribute("loginMember");
		//會員ID
		if(member!=null) {
			model.addAttribute("allProdOrder",poIdSev.getByMemberId(member.getMemberId()));
		}
		return "pages/user/partials/panel-orders";
	}
	
	@PostMapping("/deleteOrder")
	public String deleteOrder(Integer orderId) {
		poIdSev.deleteProdOrder(orderId);
		return "redirect:/member/dashboard#orders";
	}
	
	@PostMapping("/orderDetail")
	public String getOrder(Integer orderId,Model model) {
		Optional<ProdOrderIdVO> optional = poIdSev.getOne(orderId);
		if(optional.isPresent()) {
			ProdOrderIdVO order = optional.get();
			model.addAttribute("orderDetail",order);
		}
		return "pages/user/order-detail";
	}
}
