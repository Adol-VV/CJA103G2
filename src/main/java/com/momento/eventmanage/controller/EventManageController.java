package com.momento.eventmanage.controller;

import com.momento.event.model.TypeRepository;
import com.momento.event.model.EventVO;
import com.momento.eventmanage.dto.EventCreateDTO;
import com.momento.eventmanage.dto.EventUpdateDTO;
import com.momento.eventmanage.model.EventManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Event Manage Controller - 主辦方活動管理控制器
 */
@Controller
@RequestMapping("/organizer/event")
public class EventManageController {

        @Autowired
        private EventManageService eventManageService;

        @Autowired
        private TypeRepository typeRepository;

        @Autowired
        private com.momento.event.model.EventRepository eventRepository;

        private boolean isEventOwner(Integer eventId, Integer organizerId) {
                EventVO event = eventRepository.findById(eventId).orElse(null);
                return event != null && event.getOrganizer().getOrganizerId().equals(organizerId);
        }

        @GetMapping("/create")
        public String createEventPage(Model model, HttpSession session) {
                com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                                .getAttribute("loginOrganizer");
                if (organizer == null)
                        return "redirect:/organizer/login";

                model.addAttribute("types", typeRepository.findAll());
                model.addAttribute("activePanel", "event-create");
                return "pages/organizer/dashboard";
        }

        @PostMapping("/create")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> createEvent(@RequestBody EventCreateDTO dto, HttpSession session) {
                com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                                .getAttribute("loginOrganizer");
                if (organizer == null)
                        return ResponseEntity.status(401).body(Map.of("success", false, "message", "請先登入"));

                try {
                        dto.setOrganizerId(organizer.getOrganizerId());
                        Integer eventId = eventManageService.createEvent(dto);
                        return ResponseEntity.ok(Map.of("success", true, "eventId", eventId, "message", "草稿已儲存"));
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
                }
        }

        @PostMapping("/save-draft")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> saveDraft(@RequestBody EventCreateDTO dto, HttpSession session) {
                return createEvent(dto, session);
        }

        @PostMapping("/submit/{id}")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> submitEvent(@PathVariable Integer id, HttpSession session) {
                com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                                .getAttribute("loginOrganizer");
                if (organizer == null)
                        return ResponseEntity.status(401).body(Map.of("success", false, "message", "請先登入"));

                try {
                        if (!isEventOwner(id, organizer.getOrganizerId()))
                                return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限"));
                        eventManageService.submitEvent(id);
                        return ResponseEntity.ok(Map.of("success", true, "message", "活動已送出審核"));
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
                }
        }

        @PostMapping("/{id}/set-times")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> setTimes(
                        @PathVariable Integer id,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishedAt,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime saleStartAt,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime saleEndAt,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventStartAt,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventEndAt,
                        HttpSession session) {

                com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                                .getAttribute("loginOrganizer");
                if (organizer == null)
                        return ResponseEntity.status(401).body(Map.of("success", false, "message", "請先登入"));

                try {
                        if (!isEventOwner(id, organizer.getOrganizerId()))
                                return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限"));
                        eventManageService.setTimesAndPublish(id, publishedAt, saleStartAt, saleEndAt, eventStartAt,
                                        eventEndAt);
                        return ResponseEntity.ok(Map.of("success", true, "message", "上架成功"));
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
                }
        }

        @PostMapping("/upload-image")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file,
                        HttpSession session) {
                com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                                .getAttribute("loginOrganizer");
                if (organizer == null)
                        return ResponseEntity.status(401).body(Map.of("success", false, "message", "請先登入"));
                try {
                        String imageUrl = eventManageService.uploadImage(file);
                        return ResponseEntity.ok(Map.of("success", true, "imageUrl", imageUrl));
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
                }
        }

        @GetMapping("/api/list")
        @ResponseBody
        public ResponseEntity<?> getOrganizerEventsApi(
                        @RequestParam(value = "status", required = false) List<Byte> statuses,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        HttpSession session) {

                com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                                .getAttribute("loginOrganizer");
                if (organizer == null)
                        return ResponseEntity.status(401).body(Map.of("success", false, "message", "請先登入"));

                org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page,
                                size);
                org.springframework.data.domain.Page<EventVO> eventPage = eventManageService
                                .getOrganizerEvents(organizer.getOrganizerId(), statuses, keyword, pageable);
                return ResponseEntity.ok(eventPage.map(this::convertToListItemDTO));
        }

        @GetMapping("/api/types")
        @ResponseBody
        public ResponseEntity<?> getEventTypes() {
                return ResponseEntity.ok(typeRepository.findAll());
        }

        private com.momento.eventmanage.dto.EventListItemDTO convertToListItemDTO(EventVO event) {
                String bannerUrl = (event.getImages() != null && !event.getImages().isEmpty())
                                ? event.getImages().get(0).getImageUrl()
                                : null;

                return new com.momento.eventmanage.dto.EventListItemDTO(
                                event.getEventId(), event.getTitle(), event.getPlace(),
                                event.getEventStartAt(), event.getPublishedAt(),
                                event.getEventEndAt(), event.getStatus(),
                                bannerUrl);
        }

        @GetMapping("/api/stats")
        @ResponseBody
        public ResponseEntity<?> getEventStats(HttpSession session) {
                com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                                .getAttribute("loginOrganizer");
                if (organizer == null)
                        return ResponseEntity.status(401).build();
                return ResponseEntity.ok(eventManageService.getOrganizerStats(organizer.getOrganizerId()));
        }

        @GetMapping("/api/{id}")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> getEventDetailApi(@PathVariable Integer id, HttpSession session) {
                com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                                .getAttribute("loginOrganizer");
                if (organizer == null)
                        return ResponseEntity.status(401).body(Map.of("success", false, "message", "請先登入"));

                try {
                        if (!isEventOwner(id, organizer.getOrganizerId()))
                                return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限"));

                        com.momento.event.dto.EventDetailDTO detail = eventManageService.getEventDetail(id);
                        EventVO event = detail.getEvent();

                        Map<String, Object> response = new HashMap<>();
                        Map<String, Object> eventData = new HashMap<>();
                        eventData.put("eventId", event.getEventId());
                        eventData.put("title", event.getTitle());
                        eventData.put("place", event.getPlace());
                        eventData.put("content", event.getContent());
                        eventData.put("saleStartAt", event.getSaleStartAt());
                        eventData.put("saleEndAt", event.getSaleEndAt());
                        eventData.put("eventStartAt", event.getEventStartAt());
                        eventData.put("eventEndAt", event.getEventEndAt());
                        eventData.put("type", event.getType());

                        List<Map<String, Object>> images = new ArrayList<>();
                        if (detail.getImages() != null) {
                                for (com.momento.event.model.EventImageVO img : detail.getImages()) {
                                        images.add(Map.of("eventImageId", img.getEventImageId(), "imageUrl",
                                                        img.getImageUrl(), "imageOrder",
                                                        img.getImageOrder() != null ? img.getImageOrder() : 0));
                                }
                        }

                        List<Map<String, Object>> tickets = new ArrayList<>();
                        if (detail.getTickets() != null) {
                                for (com.momento.ticket.model.TicketVO t : detail.getTickets()) {
                                        tickets.add(Map.of("ticketId", t.getTicketId(), "ticketName", t.getTicketName(),
                                                        "price", t.getPrice(), "total", t.getTotal(), "remain",
                                                        t.getRemain()));
                                }
                        }

                        response.put("event", eventData);
                        response.put("tickets", tickets);
                        response.put("images", images);
                        response.put("status", event.getStatus());
                        response.put("publishedAt", event.getPublishedAt());
                        if (event.isRejected())
                                response.put("rejectReason", detail.getRejectReason());

                        response.put("success", true);
                        return ResponseEntity.ok(response);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
                }
        }

        @PutMapping("/{id}")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> updateEvent(@PathVariable Integer id,
                        @RequestBody EventUpdateDTO dto, HttpSession session) {
                com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                                .getAttribute("loginOrganizer");
                if (organizer == null)
                        return ResponseEntity.status(401).body(Map.of("success", false, "message", "請先登入"));

                try {
                        if (!isEventOwner(id, organizer.getOrganizerId()))
                                return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限"));
                        dto.setEventId(id);
                        eventManageService.updateEvent(dto);
                        return ResponseEntity.ok(Map.of("success", true, "message", "已更新"));
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
                }
        }

        @PostMapping("/withdraw/{id}")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> withdrawEvent(@PathVariable Integer id, HttpSession session) {
                com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                                .getAttribute("loginOrganizer");
                if (organizer == null)
                        return ResponseEntity.status(401).body(Map.of("success", false, "message", "請先登入"));
                try {
                        if (!isEventOwner(id, organizer.getOrganizerId()))
                                return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限"));
                        eventManageService.withdrawEvent(id);
                        return ResponseEntity.ok(Map.of("success", true, "message", "已撤回"));
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
                }
        }

        @DeleteMapping("/{id}")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable Integer id, HttpSession session) {
                com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                                .getAttribute("loginOrganizer");
                if (organizer == null)
                        return ResponseEntity.status(401).body(Map.of("success", false, "message", "請先登入"));
                try {
                        if (!isEventOwner(id, organizer.getOrganizerId()))
                                return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限"));
                        eventManageService.deleteEvent(id);
                        return ResponseEntity.ok(Map.of("success", true, "message", "已刪除"));
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
                }
        }

        @PostMapping("/{id}/force-close")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> forceClose(@PathVariable Integer id,
                        @RequestBody Map<String, String> body, HttpSession session) {
                com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                                .getAttribute("loginOrganizer");
                if (organizer == null)
                        return ResponseEntity.status(401).body(Map.of("success", false, "message", "請先登入"));
                try {
                        if (!isEventOwner(id, organizer.getOrganizerId()))
                                return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限"));
                        eventManageService.forceClose(id, body.get("reason"));
                        return ResponseEntity.ok(Map.of("success", true, "message", "已下架"));
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
                }
        }
}
