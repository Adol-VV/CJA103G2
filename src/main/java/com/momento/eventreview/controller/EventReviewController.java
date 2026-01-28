package com.momento.eventreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.momento.event.model.EventVO;
import com.momento.eventreview.model.EventReviewService;
import com.momento.ticket.model.TicketRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/event/review/api")
public class EventReviewController {

    @Autowired
    private EventReviewService eventReviewService;

    @Autowired
    private TicketRepository ticketRepository;

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

        return dto;
    }

    @PostMapping("/approve")
    public ResponseEntity<Map<String, String>> approveEvent(@RequestBody Map<String, Integer> request,
            jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null)
            return ResponseEntity.status(401).body(Map.of("error", "未登入"));

        Integer eventId = ((Number) request.get("eventId")).intValue();
        try {
            eventReviewService.approveEvent(eventId);
            return ResponseEntity.ok(Map.of("message", "活動已核准"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<Map<String, String>> rejectEvent(@RequestBody Map<String, Object> request,
            jakarta.servlet.http.HttpSession session) {
        if (session.getAttribute("loginEmp") == null)
            return ResponseEntity.status(401).body(Map.of("error", "未登入"));

        Integer eventId = ((Number) request.get("eventId")).intValue();
        String reason = (String) request.get("reason");
        com.momento.emp.model.EmpVO emp = (com.momento.emp.model.EmpVO) session.getAttribute("loginEmp");

        try {
            eventReviewService.rejectEvent(eventId, reason, emp);
            return ResponseEntity.ok(Map.of("message", "活動已駁回"));
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
}
