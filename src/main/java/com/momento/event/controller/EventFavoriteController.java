package com.momento.event.controller;

import com.momento.event.model.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

/**
 * Event Favorite Controller - 活動收藏功能控制器
 * 
 * 處理活動收藏相關的 API
 * 基底路徑: /api/event/favorite
 */
@RestController
@RequestMapping("/api/event/favorite")
public class EventFavoriteController {

    @Autowired
    private EventService eventService;

    /**
     * 新增/取消收藏（需登入）
     * POST /api/event/favorite/{id}
     * 
     * @param id      活動 ID
     * @param session HTTP Session
     * @return JSON 回應 {success, isFavorited, favoriteCount}
     */
    @PostMapping("/{id}")
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
