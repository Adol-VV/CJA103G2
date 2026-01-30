package com.momento.notify.controller;

import com.momento.member.model.MemberVO;
import com.momento.notify.model.MemberNotifyService;
import com.momento.notify.model.MemberNotifyVO;
import com.momento.notify.model.SystemNotifyService;
import com.momento.notify.model.SystemNotifyVO;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MemberNotifyController {

    @Autowired
    private MemberNotifyService memNotifySvc;

    @Autowired
    private SystemNotifyService sysNotifySvc;

    @GetMapping("/member/dashboard/my-notifications")
    public String getMyNotifications(HttpSession session, Model model) {
        // Session抓會員物件
        MemberVO member = (MemberVO) session.getAttribute("loginMember");

        if (member == null) {
            return "redirect:/member/login";
        }

        Integer memberId = member.getMemberId();

        // 抓取主辦方和會員的通知
        List<MemberNotifyVO> orgNotifies = memNotifySvc.getNotificationsByMemberId(memberId);
        List<SystemNotifyVO> sysNotifies = sysNotifySvc.getByMemId(memberId);

        if (orgNotifies == null) orgNotifies = new ArrayList<>();
        if (sysNotifies == null) sysNotifies = new ArrayList<>();

        // 放入model
        model.addAttribute("notifyList", orgNotifies);
        model.addAttribute("notifyListData", sysNotifies);

        long unreadCount = orgNotifies.stream().filter(n -> n.getIsRead() == 0).count()
                + sysNotifies.stream().filter(n -> n.getIsRead() == 0).count();
        model.addAttribute("unreadNotifyCount", unreadCount);

        return "pages/user/partials/panel-notifications";
    }

    private static @NonNull Model getNotifyListData(Model model, List<SystemNotifyVO> sysNotifies) {
        return model.addAttribute("notifyListData", sysNotifies);
    }

//    @PostMapping("/member/notifications/read")
//    @ResponseBody
//    public ResponseEntity<?> markRead(@RequestParam Integer id) {
//        try {
//            memNotifySvc.markAsRead(id);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失敗");
//        }
//    }

    @PostMapping("/member/notifications/read")
    @ResponseBody
    public String markRead(@RequestParam(name = "notifyId") Integer notifyId) {
        // 呼叫你剛剛寫好的 Service
        memNotifySvc.markAsRead(notifyId);
        return "success"; // 回傳簡單字串即可觸發 JS 的成功回調
    }
}
