package com.momento.ticket.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 票種資料存取層
 * 提供票種查詢、庫存檢查等功能
 */
@Repository
public interface TicketRepository extends JpaRepository<TicketVO, Integer> {

    /**
     * 查詢特定活動的所有票種
     * 
     * @param eventId 活動 ID
     * @return 票種列表
     */
    List<TicketVO> findByEvent_EventId(Integer eventId);

    /**
     * 查詢有剩餘票券的票種
     * 
     * @param eventId 活動 ID
     * @param remain  剩餘數量（大於此值）
     * @return 有剩餘的票種列表
     */
    List<TicketVO> findByEvent_EventIdAndRemainGreaterThan(Integer eventId, Integer remain);

    /**
     * 查詢活動的最低票價
     * 
     * @param eventId 活動 ID
     * @return 最低票價，若無票種則返回 null
     */
    @Query("SELECT MIN(t.price) FROM TicketVO t WHERE t.event.eventId = :eventId")
    Integer findMinPriceByEventId(@Param("eventId") Integer eventId);

    /**
     * 查詢活動的最高票價
     * 
     * @param eventId 活動 ID
     * @return 最高票價，若無票種則返回 null
     */
    @Query("SELECT MAX(t.price) FROM TicketVO t WHERE t.event.eventId = :eventId")
    Integer findMaxPriceByEventId(@Param("eventId") Integer eventId);

    /**
     * 檢查票種庫存是否足夠
     * 
     * @param ticketId 票種 ID
     * @param quantity 需要的數量
     * @return true 表示庫存足夠，false 表示不足
     */
    @Query("SELECT CASE WHEN t.remain >= :quantity THEN true ELSE false END " +
            "FROM TicketVO t WHERE t.ticketId = :ticketId")
    boolean checkAvailability(@Param("ticketId") Integer ticketId, @Param("quantity") Integer quantity);
}
