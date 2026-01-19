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


    @GetMapping("/apply")
    public String showApplyPage(Model model) {
        model.addAttribute("organizer", new OrganizerVO());
        return "pages/public/organizer-apply";
    }


    @PostMapping("/apply")
    public String apply(@Valid @ModelAttribute("organizer") OrganizerVO organizer,
            BindingResult result,
            Model model) {


        if (organizerService.existsByAccount(organizer.getAccount())) {
            result.rejectValue("account", "organizer.account.duplicate", "此帳號已被使用");
        }

        // 檢查 Email 是否已存在
        if (organizerService.existsByEmail(organizer.getEmail())) {
            result.rejectValue("email", "organizer.email.duplicate", "此 Email 已被使用");
        }

        // 如果有驗證錯誤，返回申請頁面
        if (result.hasErrors()) {
            return "pages/public/organizer-apply";
        }

        // 儲存申請
        organizerService.apply(organizer);

        // 顯示成功訊息
        model.addAttribute("successMsg", "申請已送出，我們將在 3-5 個工作天內完成審核");
        return "pages/public/organizer-apply";
    }
}
