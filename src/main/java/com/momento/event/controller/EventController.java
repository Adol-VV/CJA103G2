package com.momento.event.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.momento.event.dto.EventDetailDTO;
import com.momento.event.dto.EventFilterDTO;
import com.momento.event.dto.EventListItemDTO;
import com.momento.event.dto.TypeMenuDTO;
import com.momento.event.model.EventService;
import com.momento.event.model.TypeRepository;
import com.momento.event.model.TypeVO;
import com.momento.eventorder.dto.SelectionFormDTO;

import jakarta.servlet.http.HttpSession;

/**
 * Event Controller - 活動頁面控制器
 * 
 * 處理活動相關的 Thymeleaf 頁面請求
 */
@Controller
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private TypeRepository typeRepository;

    /**
     * AJAX 活動列表 API
     * GET /event/api/list
     */
    @GetMapping("/api/list")
    @ResponseBody
    public Page<EventListItemDTO> getEventListJson(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "eventStartAt") String sort,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) String place,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String direction) {

        System.out.println("=== API Request: /event/api/list [Sort=" + sort + ", Dir=" + direction + "] ===");

        EventFilterDTO filterDTO = new EventFilterDTO();
        filterDTO.setPage(page);
        filterDTO.setSize(size);
        filterDTO.setTypeId(typeId);
        filterDTO.setPlace(place);
        filterDTO.setMinPrice(minPrice);
        filterDTO.setMaxPrice(maxPrice);

        // 預設方向處理
        String finalDir = (direction != null && !direction.isEmpty()) ? direction : "ASC";

        // 核心排序與過濾邏輯
        if ("newest".equals(sort)) {
            filterDTO.setSort("publishedAt");
            filterDTO.setDirection("DESC");
        } else if ("minPrice".equals(sort)) {
            filterDTO.setSort("minPrice");
            filterDTO.setDirection(finalDir);
        } else if ("onSale".equals(sort)) {
            filterDTO.setSort("eventStartAt");
            filterDTO.setDirection("ASC");
            filterDTO.setOnSaleOnly(true);
        } else {
            filterDTO.setSort(sort);
            filterDTO.setDirection(finalDir);
        }

        return eventService.filterEvents(filterDTO);
    }

    @GetMapping("/test-data")
    @ResponseBody
    public String testData() {
        EventFilterDTO filterDTO = new EventFilterDTO();
        filterDTO.setPage(0);
        filterDTO.setSize(12);

        Page<EventListItemDTO> eventPage = eventService.filterEvents(filterDTO);

        StringBuilder result = new StringBuilder();
        result.append("總數：").append(eventPage.getTotalElements()).append("<br>");
        result.append("當前頁數量：").append(eventPage.getContent().size()).append("<br><br>");

        for (EventListItemDTO event : eventPage.getContent()) {
            result.append("活動：").append(event.getTitle())
                    .append(" | 價格：").append(event.getMinPrice())
                    .append(" | 地點：").append(event.getPlace())
                    .append("<br>");
        }

        return result.toString();
    }

    /**
     * 活動列表頁面
     * GET /events
     * 
     * @param page     頁碼（預設 0）
     * @param size     每頁筆數（預設 12）
     * @param sort     排序欄位（預設 eventStartAt）
     * @param typeId   活動類型 ID（可選）
     * @param place    地區關鍵字（可選）
     * @param minPrice 最低票價（可選）
     * @param maxPrice 最高票價（可選）
     * @param model    Spring MVC Model
     * @return 活動列表頁面
     */
    @GetMapping("/list")
    public String eventList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "eventStartAt") String sort,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) String place,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            Model model) {

        System.out.println("=== EventController.eventList() Executed ===");
        System.out.println("Page: " + page + ", Size: " + size);

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

        System.out.println("=== Event List Debug ===");
        System.out.println("總活動數：" + eventPage.getTotalElements());
        System.out.println("當前頁資料數：" + eventPage.getContent().size());
        System.out.println("是否為空：" + eventPage.getContent().isEmpty());

        if (!eventPage.getContent().isEmpty()) {
            EventListItemDTO first = eventPage.getContent().get(0);
            System.out.println("第一筆活動：" + first.getTitle());
            System.out.println("價格：" + first.getMinPrice());
            System.out.println("圖片URL：" + first.getCoverImageUrl());
        }

        // 查詢所有活動類型（用於篩選選單）
        List<TypeVO> typeVOs = typeRepository.findAll();
        List<TypeMenuDTO> types = typeVOs.stream().map(TypeMenuDTO::new).collect(java.util.stream.Collectors.toList());
        System.out.println("活動類型數：" + types.size());
        System.out.println("=== Debug End ===");

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
        com.momento.member.model.MemberVO loginMember = (com.momento.member.model.MemberVO) session
                .getAttribute("loginMember");
        Integer memberId = (loginMember != null) ? loginMember.getMemberId() : null;

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
        model.addAttribute("relatedProducts", eventDetail.getRelatedProducts());

        // 傳到結帳頁面
        model.addAttribute("selectionForm", new SelectionFormDTO());

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
        List<TypeVO> typeVOs = typeRepository.findAll();
        List<TypeMenuDTO> types = typeVOs.stream().map(TypeMenuDTO::new).collect(java.util.stream.Collectors.toList());

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
        List<TypeVO> typeVOs = typeRepository.findAll();
        List<TypeMenuDTO> types = typeVOs.stream().map(TypeMenuDTO::new).collect(java.util.stream.Collectors.toList());

        // 傳遞資料到頁面
        model.addAttribute("events", eventPage.getContent());
        model.addAttribute("page", eventPage);
        model.addAttribute("types", types);
        model.addAttribute("currentTypeId", typeId);

        return "pages/user/event-list";
    }

}
