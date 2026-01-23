package com.momento.event.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event Repository - 活動資料存取層
 * 
 * 提供活動相關的資料庫查詢方法
 */
@Repository
public interface EventRepository extends JpaRepository<EventVO, Integer> {

        // ========== 基本查詢 ==========

        /**
         * 查詢已上架且審核通過的活動（分頁）
         * 
         * @param status       活動狀態 (1:已上架)
         * @param reviewStatus 審核狀態 (1:通過)
         * @param pageable     分頁參數
         * @return 活動分頁列表
         */
        Page<EventVO> findByStatusAndReviewStatus(
                        Byte status,
                        Byte reviewStatus,
                        Pageable pageable);

        /**
         * 查詢已上架且審核通過的活動（列表）
         * 
         * @param status       活動狀態 (1:已上架)
         * @param reviewStatus 審核狀態 (1:通過)
         * @return 活動列表
         */
        List<EventVO> findByStatusAndReviewStatus(
                        Byte status,
                        Byte reviewStatus);

        // ========== 篩選查詢 ==========

        /**
         * 依活動類型篩選（分頁）
         * 
         * @param status       活動狀態
         * @param reviewStatus 審核狀態
         * @param typeId       活動類型 ID
         * @param pageable     分頁參數
         * @return 活動分頁列表
         */
        Page<EventVO> findByStatusAndReviewStatusAndType_TypeId(
                        Byte status,
                        Byte reviewStatus,
                        Integer typeId,
                        Pageable pageable);

        /**
         * 依地區篩選（模糊搜尋 PLACE 欄位）
         * 
         * @param status       活動狀態
         * @param reviewStatus 審核狀態
         * @param place        地區關鍵字
         * @param pageable     分頁參數
         * @return 活動分頁列表
         */
        Page<EventVO> findByStatusAndReviewStatusAndPlaceContaining(
                        Byte status,
                        Byte reviewStatus,
                        String place,
                        Pageable pageable);

        /**
         * 依活動舉辦日期範圍篩選
         * 
         * @param status       活動狀態
         * @param reviewStatus 審核狀態
         * @param startDate    開始日期
         * @param endDate      結束日期
         * @param pageable     分頁參數
         * @return 活動分頁列表
         */
        Page<EventVO> findByStatusAndReviewStatusAndEventAtBetween(
                        Byte status,
                        Byte reviewStatus,
                        LocalDateTime startDate,
                        LocalDateTime endDate,
                        Pageable pageable);

        // ========== 搜尋查詢 ==========

        /**
         * 關鍵字搜尋（標題或內容）
         * 
         * @param status         活動狀態
         * @param reviewStatus   審核狀態
         * @param titleKeyword   標題關鍵字
         * @param contentKeyword 內容關鍵字
         * @param pageable       分頁參數
         * @return 活動分頁列表
         */
        Page<EventVO> findByStatusAndReviewStatusAndTitleContainingOrContentContaining(
                        Byte status,
                        Byte reviewStatus,
                        String titleKeyword,
                        String contentKeyword,
                        Pageable pageable);

        // ========== 複合篩選查詢（使用 @Query） ==========

        /**
         * 複合篩選：類型 + 地區 + 日期範圍 + 價格範圍
         * 使用 JOIN TICKET 表來篩選價格
         * 
         * @param status       活動狀態
         * @param reviewStatus 審核狀態
         * @param typeId       活動類型 ID (可為 null)
         * @param place        地區關鍵字 (可為 null)
         * @param startDate    活動開始日期 (可為 null)
         * @param endDate      活動結束日期 (可為 null)
         * @param minPrice     最低票價 (可為 null)
         * @param maxPrice     最高票價 (可為 null)
         * @param pageable     分頁參數
         * @return 活動分頁列表
         */
        @Query("SELECT DISTINCT e FROM EventVO e " +
                        "LEFT JOIN TicketVO t ON t.event.eventId = e.eventId " +
                        "WHERE e.status = :status " +
                        "AND e.reviewStatus = :reviewStatus " +
                        "AND (:typeId IS NULL OR e.type.typeId = :typeId) " +
                        "AND (:place IS NULL OR e.place LIKE %:place%) " +
                        "AND (:startDate IS NULL OR e.eventAt >= :startDate) " +
                        "AND (:endDate IS NULL OR e.eventAt <= :endDate) " +
                        "AND (:minPrice IS NULL OR t.price >= :minPrice) " +
                        "AND (:maxPrice IS NULL OR t.price <= :maxPrice)")
        Page<EventVO> filterEvents(
                        @Param("status") Byte status,
                        @Param("reviewStatus") Byte reviewStatus,
                        @Param("typeId") Integer typeId,
                        @Param("place") String place,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("minPrice") Integer minPrice,
                        @Param("maxPrice") Integer maxPrice,
                        Pageable pageable);

        // ========== 主辦方相關 ==========

        /**
         * 查詢特定主辦方的所有活動
         * 
         * @param organizerId 主辦方 ID
         * @param pageable    分頁參數
         * @return 活動分頁列表
         */
        Page<EventVO> findByOrganizer_OrganizerId(
                        Integer organizerId,
                        Pageable pageable);

        /**
         * 查詢特定主辦方的已上架活動
         * 
         * @param organizerId  主辦方 ID
         * @param status       活動狀態
         * @param reviewStatus 審核狀態
         * @return 活動列表
         */
        List<EventVO> findByOrganizer_OrganizerIdAndStatusAndReviewStatus(
                        Integer organizerId,
                        Byte status,
                        Byte reviewStatus);

        /**
         * 查詢特定主辦方的活動 (依狀態篩選)
         * 
         * @param organizerId 主辦方 ID
         * @param status      活動狀態
         * @param pageable    分頁參數
         * @return 活動分頁列表
         */
        Page<EventVO> findByOrganizer_OrganizerIdAndStatus(
                        Integer organizerId,
                        Byte status,
                        Pageable pageable);

        /**
         * 查詢特定主辦方的活動 (依標題關鍵字搜尋)
         * 
         * @param organizerId 主辦方 ID
         * @param keyword     標題關鍵字
         * @param pageable    分頁參數
         * @return 活動分頁列表
         */
        Page<EventVO> findByOrganizer_OrganizerIdAndTitleContaining(
                        Integer organizerId,
                        String keyword,
                        Pageable pageable);

        /**
         * 查詢特定主辦方的活動 (依狀態 + 標題關鍵字)
         * 
         * @param organizerId 主辦方 ID
         * @param status      活動狀態
         * @param keyword     標題關鍵字
         * @param pageable    分頁參數
         * @return 活動分頁列表
         */
        Page<EventVO> findByOrganizer_OrganizerIdAndStatusAndTitleContaining(
                        Integer organizerId,
                        Byte status,
                        String keyword,
                        Pageable pageable);

        // ========== 統計查詢 ==========

        /**
         * 計算已上架活動總數
         * 
         * @param status       活動狀態
         * @param reviewStatus 審核狀態
         * @return 活動數量
         */
        long countByStatusAndReviewStatus(Byte status, Byte reviewStatus);

        /**
         * 計算特定類型的活動數量
         * 
         * @param status       活動狀態
         * @param reviewStatus 審核狀態
         * @param typeId       活動類型 ID
         * @return 活動數量
         */
        long countByStatusAndReviewStatusAndType_TypeId(
                        Byte status,
                        Byte reviewStatus,
                        Integer typeId);
}
