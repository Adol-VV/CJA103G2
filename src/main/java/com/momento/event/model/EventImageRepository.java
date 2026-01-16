package com.momento.event.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * EventImage Repository - 活動圖片資料存取層
 * 
 * 提供活動圖片相關的資料庫查詢方法
 */
@Repository
public interface EventImageRepository extends JpaRepository<EventImageVO, Integer> {

    /**
     * 查詢特定活動的所有圖片
     * 
     * @param eventId 活動 ID
     * @return 活動圖片列表
     */
    List<EventImageVO> findByEvent_EventId(Integer eventId);

    /**
     * 查詢活動的第一張圖片（封面圖）
     * 依圖片 ID 升序排序，取第一筆
     * 
     * @param eventId 活動 ID
     * @return 封面圖片（可能為空）
     */
    Optional<EventImageVO> findFirstByEvent_EventIdOrderByEventImageIdAsc(Integer eventId);

    /**
     * 查詢活動的圖片數量
     * 
     * @param eventId 活動 ID
     * @return 圖片數量
     */
    long countByEvent_EventId(Integer eventId);

    /**
     * 刪除特定活動的所有圖片
     * 
     * @param eventId 活動 ID
     */
    void deleteByEvent_EventId(Integer eventId);
}
