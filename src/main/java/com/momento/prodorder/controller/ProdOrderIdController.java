package com.momento.prodorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
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
import jakarta.websocket.Session;

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
			member.setToken(token);
			memberSvc.updateMember(member);
			
			
			poIdSev.addProdOrder(prodOrderIdVO);
			return "新增成功";
		}else {
			return "資料驗證失敗";
		}
	}
}
