package com.momento.organizer.controller;

import com.momento.organizer.model.OrganizerService;
import com.momento.organizer.model.OrganizerVO;
import com.momento.organizer.model.OrganizerEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/organizer/review/api")
public class AdminOrganizerReviewController {

    @Autowired
    private OrganizerService organizerService;
    @Autowired
    private OrganizerEmailService orgEmailSvc;

    /**
     * 取得主辦單位列表 (從 status 篩選)
     * status: 0-待審核, 1-使用中, 2-已停權
     */
    @GetMapping("/list")
    public ResponseEntity<?> getOrganizersByStatus(@RequestParam(defaultValue = "0") Byte status, HttpSession session) {
        if (session.getAttribute("loginEmp") == null) {
            return ResponseEntity.status(401).body("未登入");
        }

        List<OrganizerVO> list;
        if (status == 1) {
            list = organizerService.getActiveOrganizers();
        } else if (status == 2) {
            list = organizerService.getSuspendedOrganizers();
        } else {
            list = organizerService.getPendingOrganizers();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 取得單一主辦單位詳情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrganizerDetail(@PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("loginEmp") == null) {
            return ResponseEntity.status(401).body("未登入");
        }
        OrganizerVO organizer = organizerService.getOrganizer(id);
        if (organizer != null) {
            return ResponseEntity.ok(organizer);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 核准申請
     */
    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveOrganizer(@RequestBody Map<String, Integer> request,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (session.getAttribute("loginEmp") == null) {
            response.put("success", false);
            response.put("message", "未登入");
            return ResponseEntity.status(401).body(response);
        }

        try {
            Integer id = request.get("id");

            // 執行核准
            organizerService.approve(id);

            // 撈出主辦方資訊並且寄過審信
            OrganizerVO org= organizerService.getOrganizer(id);
            if(org != null){
                orgEmailSvc.sendAuditResultEmail(org.getEmail(), org.getName(), true, null);
            }

            response.put("success", true);
            response.put("message", "已核准，並且已發送過審通知給主辦單位");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 駁回申請
     */
    @PostMapping("/reject")
    public ResponseEntity<Map<String, Object>> rejectOrganizer(@RequestBody Map<String, Object> request,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (session.getAttribute("loginEmp") == null) {
            response.put("success", false);
            response.put("message", "未登入");
            return ResponseEntity.status(401).body(response);
        }

        try {
            //因應駁回原因轉型Object
            Integer id = (Integer) request.get("id");
            //嘗試抓原因
            String reason = (String) request.get("reason");

            //撈資料寄信
            OrganizerVO org= organizerService.getOrganizer(id);
            if (org != null) {
                String finalReason = (reason != null && !reason.isEmpty()) ? reason : "審核未通過，請檢查您提交的資料是否完整。";

                orgEmailSvc.sendAuditResultEmail(org.getEmail(), org.getName(), false, finalReason);
            }

            //刪除
            organizerService.deleteOrganizer(id);

            response.put("success", true);
            response.put("message", "刪除動作已執行成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 停權主辦單位
     */
    @PostMapping("/suspend")
    public ResponseEntity<Map<String, Object>> suspendOrganizer(@RequestBody Map<String, Integer> request,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (session.getAttribute("loginEmp") == null) {
            response.put("success", false);
            response.put("message", "未登入");
            return ResponseEntity.status(401).body(response);
        }

        try {
            Integer id = request.get("id");
            organizerService.suspend(id);
            response.put("success", true);
            response.put("message", "主辦單位已停權");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 解除主辦單位停權 (恢復使用)
     */
    @PostMapping("/unsuspend")
    public ResponseEntity<Map<String, Object>> unsuspendOrganizer(@RequestBody Map<String, Integer> request,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (session.getAttribute("loginEmp") == null) {
            response.put("success", false);
            response.put("message", "未登入");
            return ResponseEntity.status(401).body(response);
        }

        try {
            Integer id = request.get("id");
            organizerService.unsuspend(id);
            response.put("success", true);
            response.put("message", "主辦單位已恢復使用");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
