package com.momento.member.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.momento.member.model.MemberService;
import com.momento.member.model.MemberVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/member")
public class MemberRegisterController {
	
	@Autowired
	MemberService memberSvc;

	@GetMapping("/register")
	public String showRegisterPage(Model model) {
		model.addAttribute("member", new MemberVO());
		return "pages/user/register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("member") MemberVO memberVO, BindingResult result) {
		
		memberVO.setToken(0);
		memberVO.setStatus(1);
		memberVO.setCreatedAt(LocalDateTime.now());
		
		if(memberSvc.findByAccount(memberVO.getAccount()) != null) {
			result.rejectValue("account", "member.account.duplicate");
		}
		
		if(result.hasErrors()) {
			return "pages/user/register";
		}
		
		memberSvc.addMember(memberVO);
		return "redirect:/";
	}
}
