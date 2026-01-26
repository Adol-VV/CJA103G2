package com.momento.eventorder.controller;

import com.momento.event.dto.*;
import com.momento.event.model.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

/**
 * Member Event Controller - 會員活動訂單控制器
 * 
 * 處理會員下單相關功能
 * 基底路徑: /member/event
 * 
 * 注意: 活動收藏功能已移至 com.momento.event.controller.EventFavoriteController
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
     * [交接說明]
     * 這個方法負責處理頁面導向 (Routing)
     * 1. 接收網址請求
     * 2. 準備需要的資料 (Model)
     * 3. 回傳視圖名稱 (View Name) -> "pages/user/event-order"
     * 
     * 注意：
     * - 這邊只負責「顯示頁面」
     * - 頁面上的按鈕邏輯 (例如：立即購票、送出訂單) 是寫在 resources/templates/pages/user/event-order.html
     * 裡面
     * 
     * @param id      活動 ID
     * @param session HTTP Session
     * @param model   Spring MVC Model
     * @return 下單頁面路徑 (對應到 resources/templates/pages/user/event-order.html)
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

        // 回傳視圖路徑 -> src/main/resources/templates/pages/user/event-order.html
        return "pages/user/event-order";
    }
}
