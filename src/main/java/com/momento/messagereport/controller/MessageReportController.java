package com.momento.messagereport.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.momento.emp.model.EmpVO;
import com.momento.member.model.MemberVO;
import com.momento.message.model.MessageVO;
import com.momento.messagereport.model.MessageReportService;
import com.momento.messagereport.model.MessageReportVO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/report")
public class MessageReportController {

    @Autowired
    private MessageReportService messageReportService;

    @Autowired
    private com.momento.message.model.MessageService messageService;

    @PostMapping("/comment")
    public ResponseEntity<Map<String, Object>> submitReport(
            @RequestParam("messageId") Integer messageId,
            @RequestParam("reportReason") String reportReason,
            @RequestParam(value = "description", required = false) String description,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        // 1. Check login status
        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            response.put("success", false);
            response.put("message", "請先登入會員");
            return ResponseEntity.status(401).body(response);
        }

        try {
            // 2. Prepare MessageReportVO
            MessageReportVO reportVO = new MessageReportVO();

            // Set Member
            reportVO.setMemberVO(loginMember);

            // Set Message
            MessageVO messageVO = new MessageVO();
            messageVO.setMessageId(messageId);
            reportVO.setMessageVO(messageVO);

            // Set Reason
            String fullReason = "原因: " + reportReason;
            if (description != null && !description.trim().isEmpty()) {
                fullReason += " | 說明: " + description.trim();
            }
            // Truncate if too long (max 500 defined in VO)
            if (fullReason.length() > 500) {
                fullReason = fullReason.substring(0, 500);
            }
            reportVO.setReportReason(fullReason);

            // Set Status (0: Pending)
            reportVO.setStatus(0);

            // 3. Save to database
            messageReportService.addMessageReport(reportVO);

            response.put("success", true);
            response.put("message", "檢舉已提交");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "提交失敗，請稍後再試");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteReport(@RequestParam("messageReportId") Integer messageReportId,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 1. Check Admin Login
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        if (loginEmp == null) {
            response.put("success", false);
            response.put("message", "請先登入管理員帳號");
            return ResponseEntity.status(401).body(response);
        }

        try {
            // 2. Get MessageReport
            MessageReportVO reportVO = messageReportService.getOneMessageReport(messageReportId);
            if (reportVO == null) {
                response.put("success", false);
                response.put("message", "找不到該檢舉紀錄");
                return ResponseEntity.badRequest().body(response);
            }

            // 3. Update Report Status & Record Employee
            reportVO.setStatus(1);
            reportVO.setEmpVO(loginEmp); // Record who deleted it
            messageReportService.updateMessageReport(reportVO);

            // 4. Update Message Status to 1 (Hidden/Deleted)
            if (reportVO.getMessageVO() != null) {
                MessageVO messageVO = reportVO.getMessageVO();
                messageVO.setStatus(1);
                messageService.updateMessage(messageVO);
            }

            response.put("success", true);
            response.put("message", "留言已刪除且檢舉已標記為處理完成");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "刪除失敗，請稍後再試");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/keep")
    public ResponseEntity<Map<String, Object>> keepReport(@RequestParam("messageReportId") Integer messageReportId,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 1. Check Admin Login
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        if (loginEmp == null) {
            response.put("success", false);
            response.put("message", "請先登入管理員帳號");
            return ResponseEntity.status(401).body(response);
        }

        try {
            // 2. Get MessageReport
            MessageReportVO reportVO = messageReportService.getOneMessageReport(messageReportId);
            if (reportVO == null) {
                response.put("success", false);
                response.put("message", "找不到該檢舉紀錄");
                return ResponseEntity.badRequest().body(response);
            }

            // 3. Update Report Status to 2 (Keep/Reject Report) & Record Employee
            reportVO.setStatus(2);
            reportVO.setEmpVO(loginEmp);
            messageReportService.updateMessageReport(reportVO);

            // 4. Do NOT update Message Status (Keep it as is)

            response.put("success", true);
            response.put("message", "檢舉已駁回，留言予以保留");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "操作失敗，請稍後再試");
            return ResponseEntity.status(500).body(response);
        }
    }
}
