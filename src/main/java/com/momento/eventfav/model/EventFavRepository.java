package com.momento.eventfav.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * EventFav Repository - 活動收藏資料存取層
 * 
 * 提供活動收藏相關的資料庫查詢方法
 */
@Repository
public interface EventFavRepository extends JpaRepository<EventFavVO, Integer> {

        /**
         * 查詢會員的所有收藏活動
         * 
         * @param memberId 會員 ID
         * @return 收藏活動列表
         */
        @org.springframework.data.jpa.repository.Query("SELECT ef FROM EventFavVO ef WHERE ef.member.memberId = :memberId")
        List<EventFavVO> findByMember_MemberId(
                        @org.springframework.data.repository.query.Param("memberId") Integer memberId);

        /**
         * 檢查會員是否已收藏該活動
         * 
         * @param memberId 會員 ID
         * @param eventId  活動 ID
         * @return true 表示已收藏，false 表示未收藏
         */
        boolean existsByMember_MemberIdAndEvent_EventId(
                        Integer memberId,
                        Integer eventId);

        /**
         * 查詢特定會員對特定活動的收藏記錄
         * 
         * @param memberId 會員 ID
         * @param eventId  活動 ID
         * @return 收藏記錄（可能為空）
         */
        Optional<EventFavVO> findByMember_MemberIdAndEvent_EventId(
                        Integer memberId,
                        Integer eventId);

        /**
         * 計算活動的收藏數量
         * 
         * @param eventId 活動 ID
         * @return 收藏數量
         */
        long countByEvent_EventId(Integer eventId);

        /**
         * 刪除特定會員對特定活動的收藏
         * 
         * @param memberId 會員 ID
         * @param eventId  活動 ID
         */
        void deleteByMember_MemberIdAndEvent_EventId(Integer memberId, Integer eventId);

        /**
         * 查詢特定活動的所有收藏記錄
         * 
         * @param eventId 活動 ID
         * @return 收藏記錄列表
         */
        List<EventFavVO> findByEvent_EventId(Integer eventId);

        /**
         * 計算主辦方所有活動的總收藏數
         * 
         * @param organizerId 主辦方 ID
         * @return 總收藏數
         */
        @Query("SELECT COUNT(ef) FROM EventFavVO ef WHERE ef.event.organizer.organizerId = :organizerId")
        long countByOrganizerId(@Param("organizerId") Integer organizerId);
}
