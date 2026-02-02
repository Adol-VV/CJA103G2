package com.momento.eventreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.momento.event.model.EventVO;
import com.momento.eventreview.model.EventReviewService;
import com.momento.ticket.model.TicketRepository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;
import com.momento.eventsettle.model.EventSettleRepository;
import com.momento.eventsettle.model.EventSettleVO;
import com.momento.eventorder.model.EventOrderRepository;
import com.momento.eventorder.model.EventOrderVO;
import com.momento.event.model.EventRepository;

@RestController
@RequestMapping("/admin/event/review/api")
public class EventReviewController {

    @Autowired
    private EventReviewService eventReviewService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private com.momento.notify.model.OrganizerNotifyRepository organizerNotifyRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventOrderRepository eventOrderRepository;

    @Autowired
    private EventSettleRepository eventSettleRepository;

    @GetMapping("/list")
    public ResponseEntity<?> getEvents(@RequestParam(defaultValue = "all") String tab,
            @RequestParam(required = false) String keyword,
            jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null) {
            return ResponseEntity.status(401).body(null);
        }
        List<EventVO> list = eventReviewService.getEventsByTab(tab, keyword);
        return ResponseEntity.ok(list.stream().map(this::convertToDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventDetail(@PathVariable Integer id, jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null) {
            return ResponseEntity.status(401).body(null);
        }
        EventVO event = eventReviewService.getEventById(id);
        if (event != null)
            return ResponseEntity.ok(convertToDTO(event));
        return ResponseEntity.notFound().build();
    }

    private com.momento.eventreview.dto.EventReviewDTO convertToDTO(EventVO event) {
        com.momento.eventreview.dto.EventReviewDTO dto = new com.momento.eventreview.dto.EventReviewDTO();
        dto.setEventId(event.getEventId());
        dto.setTitle(event.getTitle());
        dto.setContent(event.getContent());
        dto.setPlace(event.getPlace());
        dto.setSaleStartAt(event.getSaleStartAt());
        dto.setSaleEndAt(event.getSaleEndAt());
        dto.setEventStartAt(event.getEventStartAt());
        dto.setEventEndAt(event.getEventEndAt());
        dto.setPublishedAt(event.getPublishedAt());
        dto.setStatus(event.getStatus());

        if (event.getImages() != null && !event.getImages().isEmpty()) {
            dto.setBannerUrl(event.getImages().get(0).getImageUrl());
            dto.setImageUrls(event.getImages().stream().map(img -> img.getImageUrl()).toList());
        }

        if (event.getOrganizer() != null) {
            dto.setOrganizer(new com.momento.eventreview.dto.EventReviewDTO.OrganizerDTO(
                    event.getOrganizer().getOrganizerId(),
                    event.getOrganizer().getName(),
                    event.getOrganizer().getAccountName()));
        }

        if (event.getType() != null) {
            dto.setType(new com.momento.eventreview.dto.EventReviewDTO.TypeDTO(event.getType().getTypeName()));
        }

        List<com.momento.ticket.model.TicketVO> tickets = ticketRepository.findByEvent_EventId(event.getEventId());
        if (tickets != null) {
            dto.setTickets(tickets.stream()
                    .map(t -> new com.momento.eventreview.dto.EventReviewDTO.TicketDTO(t.getTicketName(), t.getPrice(),
                            t.getTotal()))
                    .toList());
        }

        if (event.isRejected()) {
            List<com.momento.notify.model.OrganizerNotifyVO> notifies = organizerNotifyRepository
                    .findByOrganizerVO_OrganizerIdAndTitleContainingOrderByCreatedAtDesc(
                            event.getOrganizer().getOrganizerId(),
                            "活動審核未通過通知: " + event.getTitle());

            if (notifies != null && !notifies.isEmpty()) {
                String fullContent = notifies.get(0).getContent();
                if (fullContent != null && fullContent.contains("退回原因: ")) {
                    dto.setRejectReason(fullContent.split("退回原因: ")[1]);
                } else {
                    dto.setRejectReason(fullContent);
                }
            }
        }

        return dto;
    }

    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveEvent(@RequestBody Map<String, Integer> request,
            jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null)
            return ResponseEntity.status(401).body(Map.of("error", "未登入"));

        Integer eventId = ((Number) request.get("eventId")).intValue();
        com.momento.emp.model.EmpVO emp = (com.momento.emp.model.EmpVO) session.getAttribute("loginEmp");
        try {
            eventReviewService.approveEvent(eventId, emp);
            return ResponseEntity.ok(Map.of("success", true, "message", "活動已核准"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<Map<String, Object>> rejectEvent(@RequestBody Map<String, Object> request,
            jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null)
            return ResponseEntity.status(401).body(Map.of("error", "未登入"));

        Integer eventId = ((Number) request.get("eventId")).intValue();
        String reason = (String) request.get("reason");
        com.momento.emp.model.EmpVO emp = (com.momento.emp.model.EmpVO) session.getAttribute("loginEmp");

        try {
            eventReviewService.rejectEvent(eventId, reason, emp);
            return ResponseEntity.ok(Map.of("success", true, "message", "活動已駁回"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getReviewStats(jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null)
            return ResponseEntity.status(401).body(null);
        return ResponseEntity.ok(eventReviewService.getReviewStats());
    }

    /**
     * 管理員取得所有活動的結算數據 (僅限已下架活動 status=5)
     */
    @GetMapping("/settlement-data")
    public ResponseEntity<?> getSettlementData(jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "未登入"));
        }

        // 1. 找出所有「已下架 (status 5)」的活動
        List<EventVO> closedEvents = eventRepository.findAll().stream()
                .filter(e -> e.getStatus() != null && e.getStatus() == 5)
                .toList();

        System.out.println("=== Admin Settlement Debug ===");
        System.out.println("All Events count in DB: " + eventRepository.count());
        System.out.println("Status 5 events found: " + closedEvents.size());

        // 2. 找出所有的結算紀錄 (用來判斷撥款狀態)
        List<EventSettleVO> allSettles = eventSettleRepository.findAll();

        List<Map<String, Object>> result = new ArrayList<>();
        long totalSalesToSettle = 0;
        long totalPaid = 0;
        int pendingCount = 0;
        int paidCount = 0;

        for (EventVO event : closedEvents) {
            // 3. 找出該活動所有「已付款 (payStatus 1)」的訂單
            List<EventOrderVO> orders = eventOrderRepository.findByEvent_EventId(event.getEventId()).stream()
                    .filter(o -> o.getPayStatus() == 1)
                    .toList();

            System.out.println("Event ID: " + event.getEventId() + ", Orders count: " + orders.size());

            if (!orders.isEmpty()) {
                int eventSales = orders.stream().mapToInt(EventOrderVO::getTotal).sum();
                int serviceFee = (int) (eventSales * 0.10); // 平台方預設 10%
                int payable = eventSales - serviceFee;

                Map<String, Object> map = new HashMap<>();
                map.put("eventId", event.getEventId());
                map.put("title", event.getTitle());
                map.put("organizerName", event.getOrganizer() != null ? event.getOrganizer().getName() : "未知");
                map.put("eventDate", event.getEventStartAt());
                map.put("sales", eventSales);
                map.put("serviceFee", serviceFee);
                map.put("payable", payable);

                // 4. 判斷狀態
                Optional<EventSettleVO> sOpt = allSettles.stream()
                        .filter(s -> s.getEvent().getEventId().equals(event.getEventId()))
                        .findFirst();

                int status = sOpt.map(EventSettleVO::getStatus).orElse(-1); // -1 代表尚未產生成對結算單 (計算中)
                map.put("settleStatus", status);

                if (status == 1) {
                    totalPaid += payable;
                    paidCount++;
                } else {
                    totalSalesToSettle += payable;
                    pendingCount++;
                }

                result.add(map);
            }
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "settlements", result,
                "stats", Map.of(
                        "totalSalesToSettle", totalSalesToSettle,
                        "totalPaid", totalPaid,
                        "pendingCount", pendingCount,
                        "paidCount", paidCount)));
    }
}
