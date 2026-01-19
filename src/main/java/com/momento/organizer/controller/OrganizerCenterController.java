package com.momento.organizer.controller;

import com.momento.organizer.model.OrganizerService;
import com.momento.organizer.model.OrganizerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/organizer")
public class OrganizerCenterController {

    @Autowired
    private OrganizerService organizerService;

    /*顯示登入頁面（公開） */

    @GetMapping("/login")
    public String showLoginPage() {
        return "pages/organizer/login";
    }

    /*處理登入（公開）*/

    @PostMapping("/login")
    public String login(@RequestParam String account,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        // 查詢主辦方
        OrganizerVO organizer = organizerService.findByAccount(account);

        if (organizer != null && organizer.getPassword().equals(password)) {
            // 檢查狀態
            if (organizer.getStatus() == 0) {
                model.addAttribute("errorMsg", "您的申請尚在審核中，請耐心等候");
                model.addAttribute("savedAccount", account);
                return "pages/organizer/login";
            } else if (organizer.getStatus() == 2) {
                model.addAttribute("errorMsg", "您的帳號已被停權，請聯繫客服");
                model.addAttribute("savedAccount", account);
                return "pages/organizer/login";
            }

            // 登入成功（狀態 = 1）
            session.setAttribute("loginOrganizer", organizer);
            return "redirect:/organizer/dashboard";

        } else if (organizer == null) {
            model.addAttribute("accountMsg", "此帳號不存在");
            return "pages/organizer/login";
        } else {
            model.addAttribute("passwordMsg", "密碼錯誤");
            model.addAttribute("savedAccount", account);
            return "pages/organizer/login";
        }
    }

    /*登出 */

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    /* 主辦方後台首頁（需要登入）*/

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }
        model.addAttribute("organizer", organizer);
        return "pages/organizer/dashboard";
    }

    /* 活動管理頁面（需要登入）*/

    @GetMapping("/events")
    public String events(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/events";
    }

    /*商品管理頁面（需要登入）*/

    @GetMapping("/products")
    public String products(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/products";
    }

    /*訂單管理頁面（需要登入）*/

    @GetMapping("/orders")
    public String orders(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/orders";
    }

    /** 結算管理頁面

    @GetMapping("/settlements")
    public String settlements(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/settlements";
    }

    /* 數據分析頁面（需要登入）*/

    @GetMapping("/analytics")
    public String analytics(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/analytics";
    }

    /* 帳戶設定頁面（需要登入）*/

    @GetMapping("/settings")
    public String settings(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/settings";
    }
}
