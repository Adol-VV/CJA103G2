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

        // ========== 前台公開查詢 (僅 STATUS = 3 且 已過上架時間) ==========

        @Query("SELECT e FROM EventVO e WHERE e.status = :status AND (e.publishedAt IS NULL OR e.publishedAt <= :now)")
        Page<EventVO> findAvailableEvents(@Param("status") Byte status, @Param("now") LocalDateTime now,
                        Pageable pageable);

        @Query("SELECT e FROM EventVO e WHERE e.status = :status AND (e.publishedAt IS NULL OR e.publishedAt <= :now)")
        List<EventVO> findAvailableEvents(@Param("status") Byte status, @Param("now") LocalDateTime now);

        @Query("SELECT e FROM EventVO e WHERE e.status = :status AND e.type.typeId = :typeId AND (e.publishedAt IS NULL OR e.publishedAt <= :now)")
        Page<EventVO> findAvailableEventsByType(@Param("status") Byte status, @Param("typeId") Integer typeId,
                        @Param("now") LocalDateTime now, Pageable pageable);

        @Query("SELECT e FROM EventVO e WHERE e.status = :status AND (e.title LIKE %:keyword% OR e.content LIKE %:keyword%) AND (e.publishedAt IS NULL OR e.publishedAt <= :now)")
        Page<EventVO> searchAvailableEvents(@Param("status") Byte status, @Param("keyword") String keyword,
                        @Param("now") LocalDateTime now, Pageable pageable);

        List<EventVO> findByStatusAndEventEndAtBefore(Byte status, LocalDateTime dateTime);

        List<EventVO> findByOrganizer_OrganizerId(Integer organizerId);

        // ========== 篩選查詢 ==========

        Page<EventVO> findByStatusAndType_TypeId(Byte status, Integer typeId, Pageable pageable);

        Page<EventVO> findByStatusAndPlaceContaining(Byte status, String place, Pageable pageable);

        Page<EventVO> findByStatusAndEventStartAtBetween(Byte status, LocalDateTime startDate, LocalDateTime endDate,
                        Pageable pageable);

        // ========== 搜尋查詢 ==========

        Page<EventVO> findByStatusAndTitleContainingOrContentContaining(Byte status, String titleKeyword,
                        String contentKeyword, Pageable pageable);

        // ========== 複合篩選查詢 (使用 @Query) ==========

        @Query("SELECT e FROM EventVO e " +
                        "WHERE e.status = 3 " +
                        "AND (e.publishedAt IS NULL OR e.publishedAt <= :now) " +
                        "AND (:typeId IS NULL OR e.type.typeId = :typeId) " +
                        "AND (:place IS NULL OR e.place LIKE %:place%) " +
                        "AND (:startDate IS NULL OR e.eventStartAt >= :startDate) " +
                        "AND (:endDate IS NULL OR e.eventStartAt <= :endDate) " +
                        "AND (:minPrice IS NULL OR e.minPrice >= :minPrice) " +
                        "AND (:maxPrice IS NULL OR e.minPrice <= :maxPrice) " +
                        "AND (:onSaleOnly = false OR (e.saleStartAt IS NOT NULL AND e.saleEndAt IS NOT NULL AND e.saleStartAt <= :now AND e.saleEndAt > :now))")
        Page<EventVO> filterEvents(
                        @Param("statuses") java.util.List<Byte> statuses,
                        @Param("typeId") Integer typeId,
                        @Param("place") String place,
                        @Param("startDate") java.time.LocalDateTime startDate,
                        @Param("endDate") java.time.LocalDateTime endDate,
                        @Param("minPrice") Integer minPrice,
                        @Param("maxPrice") Integer maxPrice,
                        @Param("onSaleOnly") Boolean onSaleOnly,
                        @Param("now") java.time.LocalDateTime now,
                        Pageable pageable);

        // ========== 主辦方相關 ==========

        Page<EventVO> findByOrganizer_OrganizerId(Integer organizerId, Pageable pageable);

        Page<EventVO> findByOrganizer_OrganizerIdAndStatus(Integer organizerId, Byte status, Pageable pageable);

        List<EventVO> findByOrganizer_OrganizerIdAndStatus(Integer organizerId, Byte status);

        List<EventVO> findByOrganizer_OrganizerIdAndStatusIn(Integer organizerId, java.util.Collection<Byte> statuses);

        Page<EventVO> findByOrganizer_OrganizerIdAndTitleContaining(Integer organizerId, String keyword,
                        Pageable pageable);

        Page<EventVO> findByOrganizer_OrganizerIdAndStatusAndTitleContaining(Integer organizerId, Byte status,
                        String keyword, Pageable pageable);

        List<EventVO> findByOrganizer_OrganizerIdAndStatusInAndPublishedAtIsNull(Integer organizerId,
                        List<Byte> statuses);

        // ========== 統計與狀態數查詢 ==========

        long countByStatus(Byte status);

        long countByStatusAndType_TypeId(Byte status, Integer typeId);

        long countByStatusIn(java.util.Collection<Byte> statuses);

        long countByOrganizer_OrganizerId(Integer organizerId);

        long countByOrganizer_OrganizerIdAndStatusNot(Integer organizerId, Byte status);

        long countByOrganizer_OrganizerIdAndStatus(Integer organizerId, Byte status);

        long countByOrganizer_OrganizerIdAndStatusIn(Integer organizerId, java.util.Collection<Byte> statuses);

        @Query("SELECT e FROM EventVO e WHERE e.organizer.organizerId = :organizerId " +
                        "AND ((e.status = 3 AND (e.publishedAt IS NULL OR e.publishedAt <= :now)) " +
                        "     OR e.status = 5) " +
                        "ORDER BY CASE WHEN e.status = 3 THEN 0 ELSE 1 END, e.eventStartAt DESC")
        List<EventVO> findOrganizerProfileEvents(@Param("organizerId") Integer organizerId,
                        @Param("now") LocalDateTime now);

        // ========== 複合搜尋 (主辦方) ==========

        @Query("SELECT e FROM EventVO e WHERE e.organizer.organizerId = :organizerId " +
                        "AND (:statuses IS NULL OR e.status IN :statuses) " +
                        "AND (:keyword IS NULL OR :keyword = '' OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
                        +
                        "ORDER BY e.updatedAt DESC, e.eventId DESC")
        Page<EventVO> searchOrganizerEvents(
                        @Param("organizerId") Integer organizerId,
                        @Param("statuses") java.util.Collection<Byte> statuses,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        // ========== 複合搜尋 (管理員) ==========

        @Query("SELECT e FROM EventVO e WHERE (:statuses IS NULL OR e.status IN :statuses) " +
                        "AND (:keyword IS NULL OR :keyword = '' OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
                        +
                        "ORDER BY e.updatedAt DESC, e.eventId DESC")
        List<EventVO> searchAdminEvents(
                        @Param("statuses") java.util.Collection<Byte> statuses,
                        @Param("keyword") String keyword);

        @Query(value = "SELECT TITLE FROM EVENT WHERE STATUS = 3 AND (PUBLISHED_AT IS NULL OR PUBLISHED_AT <= NOW()) ORDER BY RAND() LIMIT 5", nativeQuery = true)
        List<String> findRandomTitles();
}
