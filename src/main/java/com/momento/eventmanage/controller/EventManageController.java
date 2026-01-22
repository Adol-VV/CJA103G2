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
        // TODO: 檢查主辦方登入狀態
        // Integer organizerId = (Integer) session.getAttribute("organizerId");

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

        try {
            // TODO: 從 session 取得主辦方 ID
            // Integer organizerId = (Integer) session.getAttribute("organizerId");
            // dto.setOrganizerId(organizerId);

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

        // TODO: 從 session 取得主辦方 ID
        // Integer organizerId = (Integer) session.getAttribute("organizerId");

        // TODO: 實作分頁和篩選邏輯
        // 暫時返回所有活動 (測試用)
        List<com.momento.event.model.EventVO> events = eventManageService.getAllEvents();

        model.addAttribute("events", events);
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

        // TODO: 檢查主辦方權限
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

        try {
            // TODO: 檢查主辦方權限
            // Integer organizerId = (Integer) session.getAttribute("organizerId");

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

        try {
            Byte status = ((Number) request.get("status")).byteValue();
            String reason = (String) request.get("reason");

            // TODO: 檢查主辦方權限
            // Integer organizerId = (Integer) session.getAttribute("organizerId");

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
