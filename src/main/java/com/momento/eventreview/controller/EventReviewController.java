package com.momento.eventreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.momento.event.model.EventVO;
import com.momento.eventreview.model.EventReviewService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/event/review/api")
public class EventReviewController {

    @Autowired
    private EventReviewService eventReviewService;

    /**
     * 取得待審核活動列表 (支援 tab 參數)
     * tab: pending (預設), rejected, approved
     */
    @GetMapping("/list")
    public ResponseEntity<?> getEvents(@RequestParam(defaultValue = "pending") String tab,
            jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null) {
            return ResponseEntity.status(401).body(null);
        }
        List<EventVO> list = eventReviewService.getEventsByTab(tab);
        return ResponseEntity.ok(list);
    }

    /**
     * 取得單一活動詳情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventDetail(@PathVariable Integer id, jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null) {
            return ResponseEntity.status(401).body(null);
        }
        EventVO event = eventReviewService.getEventById(id);
        if (event != null) {
            return ResponseEntity.ok(event);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 批准活動
     */
    @PostMapping("/approve")
    public ResponseEntity<Map<String, String>> approveEvent(@RequestBody Map<String, Integer> request,
            jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "未登入");
            return ResponseEntity.status(401).body(err);
        }
        Integer eventId = request.get("eventId");
        Map<String, String> response = new HashMap<>();
        try {
            eventReviewService.approveEvent(eventId);
            response.put("message", "活動已核准");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 駁回活動
     */
    @PostMapping("/reject")
    public ResponseEntity<Map<String, String>> rejectEvent(@RequestBody Map<String, Object> request,
            jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "未登入");
            return ResponseEntity.status(401).body(err);
        }
        Integer eventId = (Integer) request.get("eventId");
        String reason = (String) request.get("reason");

        Map<String, String> response = new HashMap<>();
        try {
            eventReviewService.rejectEvent(eventId, reason);
            response.put("message", "活動已駁回");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 取得審核統計 (各狀態數量)
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getReviewStats(jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null) {
            return ResponseEntity.status(401).body(null);
        }
        Map<String, Long> stats = eventReviewService.getReviewStats();
        return ResponseEntity.ok(stats);
    }
}
