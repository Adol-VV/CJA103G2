package com.momento.organizer.controller;

import com.momento.organizer.model.OrganizerService;
import com.momento.organizer.model.OrganizerVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/organizer")
public class OrganizerApplyController {

    @Autowired
    private OrganizerService organizerService;

    /**
     * 顯示申請頁面（公開）
     */
    @GetMapping("/apply")
    public String showApplyPage(Model model,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String success) {
        model.addAttribute("organizer", new OrganizerVO());

        // 如果有 success 參數，顯示成功訊息
        if ("true".equals(success)) {
            model.addAttribute("success", true);
            model.addAttribute("successMsg", "申請已送出，我們將在 3-5 個工作天內完成審核");
        }

        return "pages/public/organizer-apply";
    }

    @PostMapping("/apply")
    public String apply(@Valid @ModelAttribute("organizer") OrganizerVO organizer,
            BindingResult result,
            Model model) {

        System.out.println("========== 收到申請表單 ==========");
        System.out.println("帳號: " + organizer.getAccount());
        System.out.println("Email: " + organizer.getEmail());
        System.out.println("主辦方名稱: " + organizer.getName());

        if (organizerService.existsByAccount(organizer.getAccount())) {
            result.rejectValue("account", "organizer.account.duplicate", "此帳號已被使用");
        }

        // 檢查 Email 是否已存在
        if (organizerService.existsByEmail(organizer.getEmail())) {
            result.rejectValue("email", "organizer.email.duplicate", "此 Email 已被使用");
        }

        // 如果有驗證錯誤，返回申請頁面
        if (result.hasErrors()) {
            System.out.println("驗證錯誤: " + result.getAllErrors());
            return "pages/public/organizer-apply";
        }

        try {
            // 儲存申請
            OrganizerVO saved = organizerService.apply(organizer);
            System.out.println("申請成功！ID: " + saved.getOrganizerId());

            // 重定向到申請頁面（帶著成功參數）
            return "redirect:/organizer/apply?success=true";
        } catch (Exception e) {
            System.err.println("儲存失敗: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMsg", "申請失敗：" + e.getMessage());
            return "pages/public/organizer-apply";
        }
    }

    /**
     * 檢查帳號是否已存在（AJAX API）
     * GET /organizer/account/exists?account=xxx
     */
    @GetMapping("/account/exists")
    @org.springframework.web.bind.annotation.ResponseBody
    public Boolean accountExists(@org.springframework.web.bind.annotation.RequestParam String account) {
        return organizerService.existsByAccount(account);
    }

    /**
     * 檢查 Email 是否已存在（AJAX API）
     * GET /organizer/email/exists?email=xxx
     */
    @GetMapping("/email/exists")
    @org.springframework.web.bind.annotation.ResponseBody
    public Boolean emailExists(@org.springframework.web.bind.annotation.RequestParam String email) {
        return organizerService.existsByEmail(email);
    }
}
