package com.momento.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.momento.member.model.EmailService;
import com.momento.member.model.MemberService;
import com.momento.member.model.MemberVO;
import com.momento.member.model.ResetPasswordService;

@Controller
@RequestMapping("/member")
public class ForgotPasswordController {

	@Autowired
	ResetPasswordService resetSvc;

	@Autowired
	MemberService memberSvc;

	@Autowired
	EmailService emailSvc;


	@GetMapping("/forgot-password")
	public String showForgotPasswordPage() {
		return "pages/user/forgot-password";
	}

	@PostMapping("/forgot-password")
	public String processForgot(@RequestParam String resetEmail, Model model) {

		MemberVO member = memberSvc.findByAccount(resetEmail);

		if (member == null) {
			model.addAttribute("accountMsg", "此電子信箱尚未註冊");
			return "pages/user/forgot-password";
		}

		// 透過email找到member_id
		String memberId = String.valueOf(member.getMemberId());
		String token = resetSvc.createResetToken(memberId);

		// 發送郵件
		try {

			emailSvc.sendResetPasswordEmail(resetEmail, token);
			model.addAttribute("message", "重設連結已發送至您的信箱");

		} catch (Exception e) {

			model.addAttribute("accountMsg", "郵件發送失敗，請稍後再試");
			e.printStackTrace();

		}
		return "pages/user/forgot-password";
	}

	@GetMapping("/reset-password")
	public String showResetPage(@RequestParam String token, Model model) {
		String memberId = resetSvc.verifyToken(token);

		if (memberId != null) {
			model.addAttribute("token",token);

			MemberVO member = memberSvc.findOneMember(Integer.valueOf(memberId));

			return "/pages/user/reset-password";
		}
		return "redirect:/member/forgot-password?tokenError";
	}
	
	@PostMapping("/reset-password")
	public String updatePassword(@RequestParam String token, @RequestParam String newPassword, Model model) {
		
		String memberId = resetSvc.verifyToken(token);
		
		if (memberId != null) {

			MemberVO member = memberSvc.findOneMember(Integer.valueOf(memberId));

			member.setPassword(newPassword);
			memberSvc.updateMember(member);

			// 2. 修改成功後，刪除 Redis 中的 token（防止重複使用）
			resetSvc.deleteToken(token);
			
			return "redirect:/member/login?resetSuccess";
		}
		return "redirect:/member/forgot-password?error";
	}

}
