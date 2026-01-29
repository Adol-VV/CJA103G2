package com.momento.notify.controller;

import com.momento.emp.model.EmpVO;
import com.momento.notify.model.OrganizerNotifyService;
import com.momento.notify.model.OrganizerNotifyVO;
import com.momento.organizer.model.OrganizerVO;
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

    /**
     * AJAX endpoint：取得通知列表
     * 返回 JSON，不返回頁面
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

            // 查詢通知列表
            List<OrganizerNotifyVO> notifications = orgNotifySvc.getByOrgId(organizer.getOrganizerId());

            if (notifications == null) {
                notifications = new ArrayList<>();
            }

            // 計算未讀數量
            long unreadCount = notifications.stream()
                    .filter(n -> n != null && n.getIsRead() != null && n.getIsRead() == 0)
                    .count();

            session.setAttribute("unreadCount", (int) unreadCount);
            result.put("success", true);
            result.put("notifications", notifications);
            result.put("unreadCount", unreadCount);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "載入通知失敗：" + e.getMessage());
        }

        return result;
    }

    /**
     * AJAX endpoint：標記通知為已讀
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
     * AJAX endpoint：全部標記為已讀
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

            // 取得所有未讀通知並標記為已讀
            List<OrganizerNotifyVO> notifications = orgNotifySvc.getByOrgId(organizer.getOrganizerId());
            if (notifications != null) {
                for (OrganizerNotifyVO notify : notifications) {
                    if (notify.getIsRead() != null && notify.getIsRead() == 0) {
                        orgNotifySvc.updateReadStatus(notify.getOrganizerNotifyId(), 1);
                    }
                }
            }

            session.setAttribute("unreadCount", 0);

            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失敗：" + e.getMessage());
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
            EmpVO currentEmp = (EmpVO) session.getAttribute("loginEmp");
            if (organizer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請重新登入");
            }

            // 注意!可能要改掉
            if (currentEmp == null) {
                // 在專案開發階段，若尚未實作員工登入 Session，先手動建立一個 ID 為 1 的員工物件
                currentEmp = new EmpVO();
                currentEmp.setEmpId(1);
            }

            // 設定主辦方 ID
            vo.setOrganizerVO(organizer);
            vo.setCreatedAt(java.time.LocalDateTime.now());
            vo.setIsRead(0);
            vo.setEmpVO(currentEmp);


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
        if ("event-1".equals(targetId)) {
            return 856; // 假資料: 維也納之夜的人數
        } else if ("event-2".equals(targetId)) {
            return 234; // 假資料: 弦樂四重奏的人數
        } else if ("prod-1".equals(targetId)) {
            return 156; // 假資料: 官方周邊T-SHIRT
        } else if ("prod-2".equals(targetId)) {
            return 89; // 假資料: 限定版海報組
        }

        // TODO: 未來可以改為動態查詢
        // if (targetId == null || !targetId.contains("-")) return 0;
        // String[] parts = targetId.split("-");
        // String type = parts[0]; // "event" 或 "prod"
        // Integer id = Integer.valueOf(parts[1]);
        // return orgNotifySvc.countTargetMembers(type, id);

        return (int) (Math.random() * 100); // 不是以上活動, 回傳隨機數字
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
