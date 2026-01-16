package com.momento.event.controller;

import com.momento.event.dto.*;
import com.momento.event.model.EventService;
import com.momento.event.model.TypeVO;
import com.momento.event.model.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * Event Controller - 活動頁面控制器
 * 
 * 處理活動相關的 Thymeleaf 頁面請求
 */
@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private TypeRepository typeRepository;

    /**
     * 活動列表頁面
     * GET /events
     * 
     * @param page     頁碼（預設 0）
     * @param size     每頁筆數（預設 12）
     * @param sort     排序欄位（預設 eventAt）
     * @param typeId   活動類型 ID（可選）
     * @param place    地區關鍵字（可選）
     * @param minPrice 最低票價（可選）
     * @param maxPrice 最高票價（可選）
     * @param model    Spring MVC Model
     * @return 活動列表頁面
     */
    @GetMapping
    public String eventList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "eventAt") String sort,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) String place,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            Model model) {
        // 建立篩選條件
        EventFilterDTO filterDTO = new EventFilterDTO();
        filterDTO.setPage(page);
        filterDTO.setSize(size);
        filterDTO.setSort(sort);
        filterDTO.setTypeId(typeId);
        filterDTO.setPlace(place);
        filterDTO.setMinPrice(minPrice);
        filterDTO.setMaxPrice(maxPrice);

        // 查詢活動列表
        Page<EventListItemDTO> eventPage = eventService.filterEvents(filterDTO);

        // 查詢所有活動類型（用於篩選選單）
        List<TypeVO> types = typeRepository.findAll();

        // 傳遞資料到頁面
        model.addAttribute("events", eventPage.getContent());
        model.addAttribute("page", eventPage);
        model.addAttribute("types", types);
        model.addAttribute("currentTypeId", typeId);
        model.addAttribute("currentPlace", place);

        return "pages/user/event-list";
    }

    /**
     * 活動詳情頁面
     * GET /events/{id}
     * 
     * @param id      活動 ID
     * @param session HTTP Session
     * @param model   Spring MVC Model
     * @return 活動詳情頁面
     */
    @GetMapping("/{id}")
    public String eventDetail(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {
        // 取得當前登入會員 ID（從 session）
        Integer memberId = (Integer) session.getAttribute("memberId");

        // 查詢活動詳情
        EventDetailDTO eventDetail = eventService.getEventDetail(id, memberId);

        // 傳遞資料到頁面
        model.addAttribute("event", eventDetail.getEvent());
        model.addAttribute("images", eventDetail.getImages());
        model.addAttribute("tickets", eventDetail.getTickets());
        model.addAttribute("organizer", eventDetail.getOrganizer());
        model.addAttribute("minPrice", eventDetail.getMinPrice());
        model.addAttribute("maxPrice", eventDetail.getMaxPrice());
        model.addAttribute("favoriteCount", eventDetail.getFavoriteCount());
        model.addAttribute("isFavorited", eventDetail.getIsFavorited());
        model.addAttribute("relatedEvents", eventDetail.getRelatedEvents());

        return "pages/user/event-detail";
    }

    /**
     * 搜尋結果頁面
     * GET /events/search
     * 
     * @param keyword 搜尋關鍵字
     * @param page    頁碼（預設 0）
     * @param size    每頁筆數（預設 12）
     * @param model   Spring MVC Model
     * @return 活動列表頁面（顯示搜尋結果）
     */
    @GetMapping("/search")
    public String searchEvents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        // 搜尋活動
        Page<EventListItemDTO> eventPage = eventService.searchEvents(keyword, page, size);

        // 查詢所有活動類型（用於篩選選單）
        List<TypeVO> types = typeRepository.findAll();

        // 傳遞資料到頁面
        model.addAttribute("events", eventPage.getContent());
        model.addAttribute("page", eventPage);
        model.addAttribute("types", types);
        model.addAttribute("keyword", keyword);

        return "pages/user/event-list";
    }

    /**
     * 依類型查看活動列表
     * GET /events/type/{typeId}
     * 
     * @param typeId 活動類型 ID
     * @param page   頁碼（預設 0）
     * @param size   每頁筆數（預設 12）
     * @param model  Spring MVC Model
     * @return 活動列表頁面
     */
    @GetMapping("/type/{typeId}")
    public String eventsByType(
            @PathVariable Integer typeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        // 建立篩選條件
        EventFilterDTO filterDTO = new EventFilterDTO();
        filterDTO.setPage(page);
        filterDTO.setSize(size);
        filterDTO.setTypeId(typeId);

        // 查詢活動列表
        Page<EventListItemDTO> eventPage = eventService.filterEvents(filterDTO);

        // 查詢所有活動類型
        List<TypeVO> types = typeRepository.findAll();

        // 傳遞資料到頁面
        model.addAttribute("events", eventPage.getContent());
        model.addAttribute("page", eventPage);
        model.addAttribute("types", types);
        model.addAttribute("currentTypeId", typeId);

        return "pages/user/event-list";
    }
}
