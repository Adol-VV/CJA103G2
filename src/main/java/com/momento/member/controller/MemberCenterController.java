package com.momento.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.momento.member.model.MemberService;
import com.momento.member.model.MemberVO;
import com.momento.event.model.EventService;
import com.momento.event.dto.EventListItemDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/member")
public class MemberCenterController {

	@Autowired
	MemberService memberSvc;

	@Autowired
	EventService eventService;

	@GetMapping("/login")
	public String showLoginPage(String targetUrl, HttpServletRequest request, Model model, HttpSession session) {
		String referer = request.getHeader("Referer");
		if (referer != null && !referer.contains("/login")) {
			session.setAttribute("actualBackUrl", referer);
		}
		model.addAttribute("backUrl", session.getAttribute("actualBackUrl"));
		return "pages/user/login";
	}

	@GetMapping("/logout")
	public String logOut(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	@PostMapping("/login")
	public String login(@RequestParam String account, @RequestParam String password, @RequestParam String targetUrl,
			HttpSession session, Model model) {

		MemberVO member = memberSvc.findByAccount(account);

		if (member != null && member.getPassword().equals(password)) {

			session.setAttribute("loginMember", member);
			if (targetUrl == null || targetUrl.isEmpty() || targetUrl.contains("/register")
					|| targetUrl.contains("/forgot-password")) {

				targetUrl = "/"; // 設定一個預設的跳轉頁面

			}
			return "redirect:" + targetUrl;
		} else if (member == null) {

			model.addAttribute("accountMsg", "此帳號尚未註冊");

			return "pages/user/login";
		} else if (!(member.getPassword().equals(password))) {

			model.addAttribute("passwordMsg", "密碼錯誤");
			model.addAttribute("savedAccount", account);
		}

		return "pages/user/login";
	}

	@GetMapping("/dashboard")
	public String showMemberDashboard() {
		return "pages/user/dashboard";
	}

	@GetMapping("/dashboard/sidebar")
	public String showSidebar(HttpSession session, Model model) {
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		if (loginMember != null) {
			List<EventListItemDTO> favoriteEvents = eventService.getMemberFavorites(loginMember.getMemberId());
			model.addAttribute("favoriteCount", favoriteEvents.size());
		}
		return "pages/user/partials/sidebar";
	}

	@GetMapping("/dashboard/modals")
	public String showModals() {
		return "pages/user/partials/modals";
	}

	@GetMapping("/dashboard/overview")
	public String showDashboardOverview(HttpSession session, Model model) {
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		Integer loginMemberToken = loginMember.getToken();
		model.addAttribute("loginMemberToken", loginMemberToken);
		return "pages/user/partials/panel-overview";
	}

	@GetMapping("/dashboard/panel-favorites")
	public String showFavorites(HttpSession session, Model model) {
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		if (loginMember != null) {
			List<EventListItemDTO> favoriteEvents = eventService.getMemberFavorites(loginMember.getMemberId());
			model.addAttribute("favoriteEvents", favoriteEvents);
		}
		return "pages/user/partials/panel-favorites";
	}

	@GetMapping("/dashboard/settings")
	public String showSettings() {
		return "pages/user/partials/panel-settings";
	}

	@GetMapping("/dashboard/{pageName}")
	public String showDashboardPage(@PathVariable String pageName) {
		// 排除由 MemberCenterOrderController 處理的特定面板，避免路徑衝突
		if ("panel-tickets".equals(pageName) || "panel-tokens".equals(pageName)) {
			return null;
		}
		return "pages/user/partials/" + pageName;
	}

	@PostMapping("/dashboard/settings")
	public String updateMember(@RequestParam String name, @RequestParam String phone, @RequestParam String account,
			@RequestParam String address, HttpSession session) {

		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");

		String regName = "^[\\u4e00-\\u9fa5a-zA-Z]{2,10}$";
		String regPhone = "^09\\d{8}$";
		String regAccount = "^[a-zA-Z0-9._+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		boolean hasErrors = false;

		session.removeAttribute("nameMsg");
		session.removeAttribute("phoneMsg");
		session.removeAttribute("accountMsg");
		session.removeAttribute("accountduplicate");

		if (StringUtils.hasText(name)) {
			if (!name.trim().matches(regName)) {
				session.setAttribute("nameMsg", "必須是中文或英文 , 且長度在1到10之間");
				hasErrors = true;
			} else {

				loginMember.setName(name);
			}
		}

		if (StringUtils.hasText(phone)) {
			if (!phone.trim().matches(regPhone)) {
				session.setAttribute("phoneMsg", "手機號碼格式錯誤");
				hasErrors = true;
			} else {

				loginMember.setPhone(phone);
			}

		}

		if (StringUtils.hasText(account)) {
			if (memberSvc.findByAccount(account) != null) {
				session.setAttribute("accountDuplicate", "此電子信箱已註冊");
				hasErrors = true;
			} else if (!account.trim().matches(regAccount)) {
				session.setAttribute("accountMsg", "電子信箱格式錯誤");
				hasErrors = true;
			} else {

				loginMember.setAccount(account);
			}

		}

		if (StringUtils.hasText(address))
			loginMember.setAddress(address);

		if (hasErrors) {
			return "redirect:/member/dashboard#settings";

		}

		memberSvc.updateMember(loginMember);
		session.setAttribute("loginMember", loginMember);

		return "redirect:/member/dashboard?resetInformation#settings";
	}

	@PostMapping("/dashboard/editPassword")
	public String editPassword(@RequestParam String orginalPassword, @RequestParam String newPassword,
			@RequestParam String confirmedPassword, HttpSession session) {

		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		String regPassword = "^.{8,}$";

		session.removeAttribute("passwordError");
		session.removeAttribute("newPasswordError");
		session.removeAttribute("confirmedError");
		boolean hasErrors = false;

		if (!orginalPassword.trim().equals(loginMember.getPassword())) {
			session.setAttribute("passwordError", "密碼錯誤");
			hasErrors = true;
		}

		if (!newPassword.trim().matches(regPassword)) {
			session.setAttribute("newPasswordError", "長度須為8碼以上");
			hasErrors = true;
		}

		if (!newPassword.trim().equals(confirmedPassword)) {
			session.setAttribute("confirmedError", "與新密碼不符");
			hasErrors = true;
		}

		if (hasErrors) {
			return "redirect:/member/dashboard#settings";
		} else {
			loginMember.setPassword(confirmedPassword);
		}

		memberSvc.updateMember(loginMember);
		return "redirect:/member/dashboard?resetPassword#settings";
	}
}