package com.momento.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.momento.member.model.MemberService;
import com.momento.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberCenterController {

	@Autowired
	MemberService memberSvc;

	@GetMapping("/login")
	public String showLoginPage() {
		return "pages/user/login";
	}

	@GetMapping("/logout")
	public String logOut(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	@GetMapping("/dashboard")
	public String showMemberDashboard() {
		return "pages/user/dashboard";
	}

	@GetMapping("/dashboard-overview")
	public String showMemberCenter() {
		return "pages/user/partials/panel-overview";
	}

	@PostMapping("/login")
	public String login(@RequestParam String account, @RequestParam String password, HttpSession session, Model model) {

		MemberVO member = memberSvc.findByAccount(account);

		if (member != null && member.getPassword().equals(password)) {

			session.setAttribute("loginMember", member);

			return "redirect:/";
		} else if (member == null) {

			model.addAttribute("accountMsg", "此帳號尚未註冊");

			return "pages/user/login";
		} else if (!(member.getPassword().equals(password))) {

			model.addAttribute("passwordMsg", "密碼錯誤");
			model.addAttribute("savedAccount", account);

			return "pages/user/login";
		}

		return "pages/user/login";
	}
}
