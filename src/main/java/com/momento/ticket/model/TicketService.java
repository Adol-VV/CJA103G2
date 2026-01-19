package com.momento.ticket.model;

import java.util.List;
import java.util.Map;

/**
 * 票種業務邏輯層介面
 * 提供票種查詢、庫存管理、票價計算等功能
 */
public interface TicketService {

    /**
     * 查詢活動的所有票種
     * 
     * @param eventId 活動 ID
     * @return 票種列表
     */
    List<TicketVO> getTicketsByEventId(Integer eventId);

    /**
     * 查詢有剩餘票券的票種
     * 
     * @param eventId 活動 ID
     * @return 有剩餘的票種列表
     */
    List<TicketVO> getAvailableTickets(Integer eventId);

    /**
     * 查詢活動的最低票價
     * 
     * @param eventId 活動 ID
     * @return 最低票價，若無票種則返回 null
     */
    Integer getMinPrice(Integer eventId);

    /**
     * 查詢活動的最高票價
     * 
     * @param eventId 活動 ID
     * @return 最高票價，若無票種則返回 null
     */
    Integer getMaxPrice(Integer eventId);

    /**
     * 計算票種總價
     * 
     * @param ticketQuantityMap 票種 ID 與數量的對應表 (ticketId -> quantity)
     * @return 總價
     */
    Integer calculateTotalPrice(Map<Integer, Integer> ticketQuantityMap);

    /**
     * 檢查票種庫存是否足夠
     * 
     * @param ticketId 票種 ID
     * @param quantity 需要的數量
     * @return true 表示庫存足夠，false 表示不足
     */
    boolean checkAvailability(Integer ticketId, Integer quantity);

    /**
     * 扣減票種庫存
     * 此方法供訂單模組呼叫，用於購票時扣減庫存
     * 
     * @param ticketId 票種 ID
     * @param quantity 扣減數量
     * @throws RuntimeException 當庫存不足時拋出異常
     */
    void reduceStock(Integer ticketId, Integer quantity);

    /**
     * 恢復票種庫存
     * 此方法供訂單模組呼叫，用於取消訂單時恢復庫存
     * 
     * @param ticketId 票種 ID
     * @param quantity 恢復數量
     */
    void restoreStock(Integer ticketId, Integer quantity);
}
