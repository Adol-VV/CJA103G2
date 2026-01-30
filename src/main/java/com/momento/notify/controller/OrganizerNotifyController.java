package com.momento.notify.controller;

import com.momento.eventorder.model.EventOrderRepository;
import com.momento.notify.model.OrganizerNotifyService;
import com.momento.notify.model.OrganizerNotifyVO;
import com.momento.notify.model.SystemNotifyService;
import com.momento.notify.model.SystemNotifyVO;
import com.momento.organizer.model.OrganizerVO;
import com.momento.prodorderitem.model.ProdOrderItemRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@Validated
@RequestMapping("/organizer/notify")
public class OrganizerNotifyController {
    @Autowired
    OrganizerNotifyService orgNotifySvc;

    @Autowired
    SystemNotifyService sysNotifySvc;

    @Autowired
    EventOrderRepository eventOrderRepo;

    @Autowired
    ProdOrderItemRepository prodOrderItemRepo;

    /**
     * AJAX endpoint：取得通知列表 (合併平台通知+會員動態)
     * 返回 JSON，給小鈴鐺使用
     */
    @PostMapping("/list")
    @ResponseBody
    public Map<String, Object> getNotifications(HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 從 Session 取得登入的主辦方
            OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");

            if (organizer == null) {
                result.put("success", false);
                result.put("message", "請先登入");
                return result;
            }

            Integer organizerId = organizer.getOrganizerId();

            // 查詢主辦方通知 (會員動態)
            List<OrganizerNotifyVO> orgNotifications = orgNotifySvc.getByOrgId(organizerId);
            if (orgNotifications == null) orgNotifications = new ArrayList<>();

            // 查詢系統通知 (平台公告)
            List<SystemNotifyVO> sysNotifications = sysNotifySvc.getByOrgId(organizerId);
            if (sysNotifications == null) sysNotifications = new ArrayList<>();

            // 合併成統一格式
            List<Map<String, Object>> allNotifications = new ArrayList<>();

            for (OrganizerNotifyVO n : orgNotifications) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", n.getOrganizerNotifyId());
                map.put("title", n.getTitle());
                map.put("content", n.getContent());
                map.put("createdAt", n.getCreatedAt() != null ? n.getCreatedAt().toString() : "");
                map.put("isRead", n.getIsRead());
                map.put("type", n.getEmpVO() == null ? "MEMBER" : "PLATFORM");
                map.put("notifyType", "ORG");
                allNotifications.add(map);
            }

            for (SystemNotifyVO s : sysNotifications) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getSystemNotifyId());
                map.put("title", s.getTitle());
                map.put("content", s.getContent());
                map.put("createdAt", s.getCreatedAt() != null ? s.getCreatedAt().toString() : "");
                map.put("isRead", s.getIsRead());
                map.put("type", "PLATFORM");
                map.put("notifyType", "SYS");
                allNotifications.add(map);
            }

            // 按時間排序 (新的在前)
            allNotifications.sort((a, b) -> {
                String t1 = (String) a.get("createdAt");
                String t2 = (String) b.get("createdAt");
                if (t1 == null || t1.isEmpty()) return 1;
                if (t2 == null || t2.isEmpty()) return -1;
                return t2.compareTo(t1);
            });

            // 計算未讀數量 (平台 + 會員)
            long unreadCount = allNotifications.stream()
                    .filter(n -> Integer.valueOf(0).equals(n.get("isRead")))
                    .count();

            session.setAttribute("unreadCount", (int) unreadCount);
            result.put("success", true);
            result.put("notifications", allNotifications);
            result.put("unreadCount", unreadCount);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "載入通知失敗：" + e.getMessage());
        }

        return result;
    }

    /**
     * AJAX endpoint：標記主辦方通知為已讀
     */
    @PostMapping("/markAsRead")
    @ResponseBody
    public Map<String, Object> markAsRead(@RequestParam Integer notifyId) {
        Map<String, Object> result = new HashMap<>();

        try {
            orgNotifySvc.updateReadStatus(notifyId, 1);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "標記失敗：" + e.getMessage());
        }

        return result;
    }

    /**
     * AJAX endpoint：標記系統通知為已讀
     */
    @PostMapping("/markSysAsRead")
    @ResponseBody
    public Map<String, Object> markSysAsRead(@RequestParam Integer notifyId) {
        Map<String, Object> result = new HashMap<>();

        try {
            sysNotifySvc.updateReadStatus(notifyId, 1);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "標記失敗：" + e.getMessage());
        }

        return result;
    }

    /**
     * AJAX endpoint：全部標記為已讀 (主辦方通知 + 系統通知)
     */
    @PostMapping("/markAllAsRead")
    @ResponseBody
    public Map<String, Object> markAllAsRead(HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        try {
            OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
            if (organizer == null) {
                result.put("success", false);
                result.put("message", "請先登入");
                return result;
            }

            Integer organizerId = organizer.getOrganizerId();

            // 標記所有主辦方通知為已讀
            orgNotifySvc.markAllAsReadByOrgId(organizerId);
            // 標記所有系統通知為已讀
            sysNotifySvc.markAllAsReadForOrg(organizerId);

            session.setAttribute("unreadCount", 0);

            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失敗：" + e.getMessage());
        }

        return result;
    }

    /**
     * AJAX endpoint：全部刪除通知 (主辦方通知 + 系統通知)
     */
    @PostMapping("/deleteAll")
    @ResponseBody
    public Map<String, Object> deleteAllNotifications(HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        try {
            OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
            if (organizer == null) {
                result.put("success", false);
                result.put("message", "請先登入");
                return result;
            }

            Integer organizerId = organizer.getOrganizerId();

            // 刪除所有主辦方通知
            orgNotifySvc.deleteAllByOrgId(organizerId);
            // 刪除所有系統通知
            sysNotifySvc.deleteAllByOrgId(organizerId);

            session.setAttribute("unreadCount", 0);

            result.put("success", true);
            result.put("message", "已刪除所有通知");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "刪除失敗：" + e.getMessage());
        }

        return result;
    }

    /**
     * 發送通知給會員
     */
    @PostMapping("/addNotify")
    @ResponseBody
    public ResponseEntity<?> addNotify(
            @ModelAttribute("organizerNotifyVO") OrganizerNotifyVO vo, HttpSession session) {
        try {
            // 從Session取得登入的主辦方
            OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
            if (organizer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請重新登入");
            }

            // 設定主辦方 ID
            vo.setOrganizerVO(organizer);
            vo.setCreatedAt(java.time.LocalDateTime.now());
            vo.setIsRead(1);
            vo.setEmpVO(null);


            // 執行儲存
            orgNotifySvc.addNotify(vo);

            // 抓主辦方最新通知列表給前端
            List<OrganizerNotifyVO> updatedList = orgNotifySvc.getNotifiesByOrganizer(organizer.getOrganizerId());
            return ResponseEntity.ok(updatedList);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("發送失敗：" + e.getMessage());
        }
    }

    /**
     * AJAX endpoint：計算通知對象數量
     * 根據活動或商品 ID 返回對應的會員數量
     */
    @GetMapping("/countMembers")
    @ResponseBody
    public Integer countMembers(@RequestParam String targetId) {
        if (targetId == null || !targetId.contains("-")) {
            return 0;
        }

        try {
            String[] parts = targetId.split("-");
            String type = parts[0]; // "event" 或 "prod"
            Integer id = Integer.valueOf(parts[1]);

            if ("event".equals(type)) {
                // 查詢購買此活動的會員人數
                Integer count = eventOrderRepo.countDistinctMembersByEventId(id);
                return count != null ? count : 0;
            } else if ("prod".equals(type)) {
                // 查詢購買此商品的會員人數
                Integer count = prodOrderItemRepo.countDistinctMembersByProdId(id);
                return count != null ? count : 0;
            }
        } catch (NumberFormatException e) {
            return 0;
        }

        return 0;
    }

    // 錯誤處理
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ModelAndView handleError(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder strBuilder = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            strBuilder.append(violation.getMessage()).append("<br>");
        }
        String message = strBuilder.toString();
        return new ModelAndView("pages/organizer/dashboard", "errorMessage", "請修正以下錯誤:<br>" + message);
    }

}
