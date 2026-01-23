package com.momento.eventmanage.controller;

import com.momento.event.model.TypeVO;
import com.momento.event.model.TypeRepository;
import com.momento.eventmanage.dto.EventCreateDTO;
import com.momento.eventmanage.dto.EventUpdateDTO;
import com.momento.eventmanage.service.EventManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Event Manage Controller - 主辦方活動管理控制器
 * 
 * 處理主辦方的活動建立、編輯、列表等功能
 * 基底路徑: /organizer/event
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

    /**
     * 檢查活動是否屬於該主辦方
     * 
     * @param eventId     活動 ID
     * @param organizerId 主辦方 ID
     * @return true 如果活動屬於該主辦方
     */
    private boolean isEventOwner(Integer eventId, Integer organizerId) {
        com.momento.event.model.EventVO event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return false;
        }
        return event.getOrganizer().getOrganizerId().equals(organizerId);
    }

    /**
     * 建立活動頁面 (返回 Dashboard,由前端切換到 panel-event-create)
     * GET /organizer/event/create
     * 
     * @param model   Spring MVC Model
     * @param session HTTP Session
     * @return Dashboard 頁面
     */
    @GetMapping("/create")
    public String createEventPage(Model model, HttpSession session) {
        // 檢查主辦方登入狀態
        com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                .getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }

        // 載入活動類型列表
        List<TypeVO> types = typeRepository.findAll();
        model.addAttribute("types", types);

        // 返回 Dashboard,前端會自動切換到 event-create panel
        return "pages/organizer/dashboard";
    }

    /**
     * 提交新活動 (AJAX)
     * POST /organizer/event/create
     * 
     * @param dto     活動建立 DTO
     * @param session HTTP Session
     * @return JSON 回應 {success, eventId, message}
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createEvent(
            @RequestBody EventCreateDTO dto,
            HttpSession session) {

        // 檢查主辦方登入狀態
        com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                .getAttribute("loginOrganizer");
        if (organizer == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "請先登入"));
        }

        try {
            // 從 session 取得主辦方 ID
            dto.setOrganizerId(organizer.getOrganizerId());

            // 建立活動
            Integer eventId = eventManageService.createEvent(dto);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "eventId", eventId,
                            "message", "活動已成功送出審核"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }

    /**
     * 上傳活動圖片 (AJAX)
     * POST /organizer/event/upload-image
     * 
     * @param file    圖片檔案
     * @param session HTTP Session
     * @return JSON 回應 {success, imageUrl}
     */
    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        // 檢查主辦方登入狀態
        com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                .getAttribute("loginOrganizer");
        if (organizer == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "請先登入"));
        }

        try {
            // 上傳圖片並取得 URL
            String imageUrl = eventManageService.uploadImage(file);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "imageUrl", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }

    /**
     * 我的活動列表頁面 (Thymeleaf)
     * GET /organizer/event/list
     * 
     * @param status  活動狀態篩選 (可選)
     * @param keyword 搜尋關鍵字 (可選)
     * @param page    頁碼 (預設 0)
     * @param size    每頁筆數 (預設 10)
     * @param model   Spring MVC Model
     * @param session HTTP Session
     * @return Dashboard 頁面
     */
    @GetMapping("/list")
    public String listEvents(
            @RequestParam(required = false) Byte status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            HttpSession session) {

        // 檢查主辦方登入狀態
        com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                .getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }

        // 建立分頁參數 (依活動日期降冪排序)
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("eventAt").descending());

        // 取得該主辦方的活動列表
        org.springframework.data.domain.Page<com.momento.event.model.EventVO> eventPage = eventManageService
                .getOrganizerEvents(
                        organizer.getOrganizerId(),
                        status,
                        keyword,
                        pageable);

        model.addAttribute("events", eventPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", eventPage.getTotalPages());
        model.addAttribute("totalEvents", eventPage.getTotalElements());
        model.addAttribute("currentStatus", status);
        model.addAttribute("keyword", keyword);

        // 返回 Dashboard,前端會顯示 panel-events-list
        return "pages/organizer/dashboard";
    }

    /**
     * 編輯活動頁面 (Thymeleaf)
     * GET /organizer/event/edit/{id}
     * 
     * @param id      活動 ID
     * @param model   Spring MVC Model
     * @param session HTTP Session
     * @return 編輯活動頁面
     */
    @GetMapping("/edit/{id}")
    public String editEventPage(
            @PathVariable Integer id,
            Model model,
            HttpSession session) {

        // 檢查主辦方登入狀態
        com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                .getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }

        // 檢查活動是否屬於該主辦方
        if (!isEventOwner(id, organizer.getOrganizerId())) {
            // 活動不屬於該主辦方,返回錯誤頁面或 dashboard
            return "redirect:/organizer/dashboard";
        }

        // TODO: 載入活動資料
        // TODO: 載入活動類型列表

        return "pages/organizer/event-edit";
    }

    /**
     * 更新活動 (AJAX)
     * PUT /organizer/event/{id}
     * 
     * @param id      活動 ID
     * @param dto     活動更新 DTO
     * @param session HTTP Session
     * @return JSON 回應 {success, message}
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateEvent(
            @PathVariable Integer id,
            @RequestBody EventUpdateDTO dto,
            HttpSession session) {

        // 檢查主辦方登入狀態
        com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                .getAttribute("loginOrganizer");
        if (organizer == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "請先登入"));
        }

        try {
            // 檢查活動是否屬於該主辦方
            if (!isEventOwner(id, organizer.getOrganizerId())) {
                return ResponseEntity.status(403)
                        .body(Map.of("success", false, "message", "無權限編輯此活動"));
            }

            // 設定活動 ID
            dto.setEventId(id);

            // 更新活動
            eventManageService.updateEvent(dto);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "活動已成功更新"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }

    /**
     * 變更活動狀態 (AJAX)
     * PATCH /organizer/event/{id}/status
     * 
     * @param id      活動 ID
     * @param request 狀態變更請求 {status, reason}
     * @param session HTTP Session
     * @return JSON 回應 {success, message}
     */
    @PatchMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changeStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request,
            HttpSession session) {

        // 檢查主辦方登入狀態
        com.momento.organizer.model.OrganizerVO organizer = (com.momento.organizer.model.OrganizerVO) session
                .getAttribute("loginOrganizer");
        if (organizer == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "請先登入"));
        }

        try {
            Byte status = ((Number) request.get("status")).byteValue();
            String reason = (String) request.get("reason");

            // 檢查活動是否屬於該主辦方
            if (!isEventOwner(id, organizer.getOrganizerId())) {
                return ResponseEntity.status(403)
                        .body(Map.of("success", false, "message", "無權限變更此活動狀態"));
            }

            // 變更狀態
            eventManageService.changeStatus(id, status, reason);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "狀態已成功變更"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }
}
