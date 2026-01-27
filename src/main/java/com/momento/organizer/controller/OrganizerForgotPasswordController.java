package com.momento.organizer.controller;

import com.momento.organizer.model.OrganizerEmailService;
import com.momento.organizer.model.OrganizerResetPasswordService;
import com.momento.organizer.model.OrganizerService;
import com.momento.organizer.model.OrganizerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/organizer")
public class OrganizerForgotPasswordController {

    @Autowired
    OrganizerResetPasswordService resetSvc;

    @Autowired
    OrganizerService organizerSvc;

    @Autowired
    OrganizerEmailService emailSvc;

    @GetMapping("/forgot-password")
    public String ShowForgotPasswordPage(){
        return "pages/organizer/forgot-password";    }

    @PostMapping("/forgot-password")
    public String processForgot(@RequestParam String resetEmail, Model model){

        OrganizerVO organizer = organizerSvc.findByEmail(resetEmail);

        if (organizer == null) {
            model.addAttribute("accountMsg", "您提供的電子信箱並非主辦單位Email，請留意審核流程是否完成");
            return "pages/organizer/forgot-password";
        }

        // 透過email找到organizer_id
        String organizerId = String.valueOf(organizer.getOrganizerId());
        String token = resetSvc.createResetToken(organizerId);

        try {
            emailSvc.sendResetPasswordEmail(resetEmail, token);
            model.addAttribute("message", "密碼重設連結已發送至您的信箱");

        } catch (Exception e) {

            model.addAttribute("accountMsg", "郵件發送失敗，請稍後再試");
            e.printStackTrace();

        }
        return "pages/organizer/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPage(@RequestParam String token, Model model) {
        String organizerId = resetSvc.verifyToken(token);

        if (organizerId != null) {
            model.addAttribute("token", token);

            OrganizerVO organizer = organizerSvc.getOrganizer(Integer.valueOf(organizerId));

            return "/pages/organizer/reset-password";
        }

        return "redirect:/organizer/forgot-password?tokenError";
    }

    @PostMapping("/reset-password")
    public String updatePassword(@RequestParam String token, @RequestParam String newPassword, Model model) {

        String organizerId = resetSvc.verifyToken(token);

        if (organizerId != null) {

            OrganizerVO organizer = organizerSvc.getOrganizer(Integer.valueOf(organizerId));

            organizer.setPassword(newPassword);
            organizerSvc.updateOrganizer(organizer);

            // 2. 修改成功後，刪除 Redis 中的 token（防止重複使用）
            resetSvc.deleteToken(token);

            return "redirect:/organizer/login?resetSuccess";
        }
        return "redirect:/organizer/forgot-password?error";
    }

}
