package com.momento.organizer.controller;

import com.momento.organizer.model.OrganizerService;
import com.momento.organizer.model.OrganizerVO;
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
        model.addAttribute("organizer", new com.momento.organizer.dto.OrganizerApplyDTO());

        // 如果有 success 參數，顯示成功訊息
        if ("true".equals(success)) {
            model.addAttribute("success", true);
            model.addAttribute("successMsg", "申請已送出，我們將在 3-5 個工作天內完成審核");
        }

        return "pages/public/organizer-apply";
    }

    @PostMapping("/apply")
    public String apply(
            @jakarta.validation.Valid @ModelAttribute("organizer") com.momento.organizer.dto.OrganizerApplyDTO organizer,
            BindingResult result,
            Model model) {

        System.out.println("========== 收到申請表單 ==========");
        System.out.println("帳號: " + organizer.getAccount());

        // 如果有綁定錯誤（例如資料型態不符），紀錄日誌但不在此攔截，交由 Service 處理或後續檢查
        if (result.hasErrors()) {
            System.err.println("資料綁定存在警告/錯誤: " + result.getAllErrors());
        }

        try {
            // 直接呼叫 Service 進行儲存（Service 內部會再做最後一次重複檢查）
            OrganizerVO saved = organizerService.apply(organizer);
            System.out.println("申請成功！ID: " + saved.getOrganizerId());

            // 重定向到申請頁面（帶著成功參數，觸發成功畫面）
            return "redirect:/organizer/apply?success=true";
        } catch (IllegalArgumentException e) {
            // 處理業務邏輯錯誤（如帳號/Email重複）
            System.err.println("業務邏輯錯誤: " + e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
            return "pages/public/organizer-apply";
        } catch (Exception e) {
            // 處理資料庫或其他系統錯誤
            System.err.println("系統錯誤: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMsg", "申請處理失敗，請聯繫技術人員或稍後再試。");
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
