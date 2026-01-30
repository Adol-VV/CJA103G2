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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<Map<String, Object>> allNotifiesNormalized = new ArrayList<>();

        if (orgNotifies != null) {
            for (MemberNotifyVO n : orgNotifies) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", n.getMemberNotifyId()); // 統一叫 id
                map.put("title", n.getTitle());
                map.put("content", n.getContent());
                map.put("createdAt", n.getCreatedAt());
                map.put("isRead", n.getIsRead());
                map.put("type", "ORGANIZER"); // 標記來源
                map.put("sourceName", (n.getOrganizerNotifyVO() != null && n.getOrganizerNotifyVO().getOrganizerVO() != null)
                        ? n.getOrganizerNotifyVO().getOrganizerVO().getOrganizerId() : "Momento 官方");
                allNotifiesNormalized.add(map);
            }
        }

        if (sysNotifies != null) {
            for (SystemNotifyVO s : sysNotifies) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getSystemNotifyId()); // 統一叫 id
                map.put("title", s.getTitle());
                map.put("content", s.getContent());
                map.put("createdAt", s.getCreatedAt());
                map.put("isRead", s.getIsRead());
                map.put("type", "SYSTEM"); // 標記來源
                map.put("sourceName", "Momento 官方");
                allNotifiesNormalized.add(map);
            }
        }

        // 依照時間序由新到舊排序
        allNotifiesNormalized.sort((a, b) -> {
            java.time.LocalDateTime t1 = (java.time.LocalDateTime) a.get("createdAt");
            java.time.LocalDateTime t2 = (java.time.LocalDateTime) b.get("createdAt");
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return t2.compareTo(t1); // DESC 排序
        });


//        if (orgNotifies == null) orgNotifies = new ArrayList<>();
//        if (sysNotifies == null) sysNotifies = new ArrayList<>();
//        List<Object> allNotifies = new ArrayList<>();
//        allNotifies.addAll(orgNotifies);
//        allNotifies.addAll(sysNotifies);
//
//        allNotifies.sort((a, b) -> {
//            java.time.LocalDateTime timeA = (a instanceof MemberNotifyVO) ?
//                    ((MemberNotifyVO) a).getCreatedAt() : ((SystemNotifyVO) a).getCreatedAt();
//            java.time.LocalDateTime timeB = (b instanceof MemberNotifyVO) ?
//                    ((MemberNotifyVO) b).getCreatedAt() : ((SystemNotifyVO) b).getCreatedAt();
//            return timeB.compareTo(timeA); // DESC 排序
//        });

        // 計算各分類的數量
        long totalCount = allNotifiesNormalized.size();
        long systemCount = sysNotifies != null ? sysNotifies.size() : 0;
        long organizerCount = orgNotifies != null ? orgNotifies.size() : 0;

        // 計算各分類的未讀數量
        long totalUnread = allNotifiesNormalized.stream()
                .filter(n -> Integer.valueOf(0).equals(n.get("isRead")))
                .count();
        long systemUnread = sysNotifies != null ?
                sysNotifies.stream().filter(n -> n.getIsRead() == 0).count() : 0;
        long organizerUnread = orgNotifies != null ?
                orgNotifies.stream().filter(n -> n.getIsRead() == 0).count() : 0;

        // 放入model
        model.addAttribute("allNotifies", allNotifiesNormalized);
        model.addAttribute("notifyList", orgNotifies);
        model.addAttribute("notifyListData", sysNotifies);

        // 總數量
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("systemCount", systemCount);
        model.addAttribute("organizerCount", organizerCount);

        // 未讀數量
        model.addAttribute("unreadNotifyCount", totalUnread);
        model.addAttribute("systemUnreadCount", systemUnread);
        model.addAttribute("organizerUnreadCount", organizerUnread);

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

    /**
     * 標記單則通知為已讀 (支援 ORGANIZER 和 SYSTEM 兩種類型)
     */
    @PostMapping("/member/notifications/read")
    @ResponseBody
    public Map<String, Object> markRead(
            @RequestParam(name = "notifyId") Integer notifyId,
            @RequestParam(name = "type", defaultValue = "ORGANIZER") String type) {
        Map<String, Object> result = new HashMap<>();
        try {
            if ("SYSTEM".equals(type)) {
                sysNotifySvc.updateReadStatus(notifyId, 1);
            } else {
                memNotifySvc.markAsRead(notifyId);
            }
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 標記所有通知為已讀
     */
    @PostMapping("/member/notifications/read-all")
    @ResponseBody
    public Map<String, Object> markAllRead(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberVO member = (MemberVO) session.getAttribute("loginMember");

        if (member == null) {
            result.put("success", false);
            result.put("message", "請先登入");
            return result;
        }

        try {
            Integer memberId = member.getMemberId();
            // 標記所有主辦方通知為已讀
            memNotifySvc.markAllAsReadByMemberId(memberId);
            // 標記所有系統通知為已讀
            sysNotifySvc.markAllAsReadByMemberId(memberId);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 全部刪除通知
     */
    @PostMapping("/member/notifications/delete-all")
    @ResponseBody
    public Map<String, Object> deleteAllNotifications(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberVO member = (MemberVO) session.getAttribute("loginMember");

        if (member == null) {
            result.put("success", false);
            result.put("message", "請先登入");
            return result;
        }

        try {
            Integer memberId = member.getMemberId();
            // 刪除所有主辦方通知
            memNotifySvc.deleteAllByMemberId(memberId);
            // 刪除所有系統通知
            sysNotifySvc.deleteAllByMemberId(memberId);
            result.put("success", true);
            result.put("message", "已刪除所有通知");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * AJAX: 取得會員通知列表 (給小鈴鐺用)
     */
    @PostMapping("/member/notifications/list")
    @ResponseBody
    public Map<String, Object> getNotificationList(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberVO member = (MemberVO) session.getAttribute("loginMember");

        if (member == null) {
            result.put("success", false);
            result.put("message", "請先登入");
            return result;
        }

        try {
            Integer memberId = member.getMemberId();
            List<MemberNotifyVO> orgNotifies = memNotifySvc.getNotificationsByMemberId(memberId);
            List<SystemNotifyVO> sysNotifies = sysNotifySvc.getByMemId(memberId);

            List<Map<String, Object>> notifications = new ArrayList<>();

            if (orgNotifies != null) {
                for (MemberNotifyVO n : orgNotifies) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", n.getMemberNotifyId());
                    map.put("title", n.getTitle());
                    map.put("content", n.getContent());
                    map.put("createdAt", n.getCreatedAt() != null ? n.getCreatedAt().toString() : "");
                    map.put("isRead", n.getIsRead());
                    map.put("type", "ORGANIZER");
                    notifications.add(map);
                }
            }

            if (sysNotifies != null) {
                for (SystemNotifyVO s : sysNotifies) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", s.getSystemNotifyId());
                    map.put("title", s.getTitle());
                    map.put("content", s.getContent());
                    map.put("createdAt", s.getCreatedAt() != null ? s.getCreatedAt().toString() : "");
                    map.put("isRead", s.getIsRead());
                    map.put("type", "SYSTEM");
                    notifications.add(map);
                }
            }

            // 按時間排序 (新的在前)
            notifications.sort((a, b) -> {
                String t1 = (String) a.get("createdAt");
                String t2 = (String) b.get("createdAt");
                return t2.compareTo(t1);
            });

            long unreadCount = notifications.stream()
                    .filter(n -> Integer.valueOf(0).equals(n.get("isRead")))
                    .count();

            result.put("success", true);
            result.put("notifications", notifications);
            result.put("unreadCount", unreadCount);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
