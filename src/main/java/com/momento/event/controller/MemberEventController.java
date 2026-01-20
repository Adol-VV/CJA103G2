package com.momento.event.controller;

import com.momento.event.dto.*;
import com.momento.event.model.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

/**
 * Member Event Controller - 會員活動功能控制器
 * 
 * 處理需要登入才能使用的活動相關功能
 * 基底路徑: /member/event
 */
@Controller
@RequestMapping("/member/event")
public class MemberEventController {

    @Autowired
    private EventService eventService;

    /**
     * 下單頁面（需登入）
     * GET /member/event/order/{id}
     * 
     * @param id      活動 ID
     * @param session HTTP Session
     * @param model   Spring MVC Model
     * @return 下單頁面
     */
    @GetMapping("/order/{id}")
    public String orderPage(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        // 取得當前登入會員 ID（從 session）
        Integer memberId = (Integer) session.getAttribute("memberId");

        // 查詢活動詳情（包含票種資訊）
        EventDetailDTO eventDetail = eventService.getEventDetail(id, memberId);

        // 傳遞資料到頁面
        model.addAttribute("event", eventDetail.getEvent());
        model.addAttribute("tickets", eventDetail.getTickets());
        model.addAttribute("organizer", eventDetail.getOrganizer());

        return "pages/user/event-order";
    }

    /**
     * 新增/取消收藏（需登入）
     * POST /member/event/favorite/{id}
     * 
     * @param id      活動 ID
     * @param session HTTP Session
     * @return JSON 回應 {success, isFavorited, favoriteCount}
     */
    @PostMapping("/favorite/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @PathVariable Integer id,
            HttpSession session) {

        // 從 session 取得會員 ID
        Integer memberId = (Integer) session.getAttribute("memberId");

        // 檢查是否已登入
        if (memberId == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "請先登入"));
        }

        // 切換收藏狀態
        boolean isFavorited = eventService.toggleFavorite(id, memberId);

        // 查詢收藏數量
        Long favoriteCount = eventService.getFavoriteCount(id);

        // 返回結果
        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "isFavorited", isFavorited,
                        "favoriteCount", favoriteCount));
    }
}
