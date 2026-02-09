package com.momento.notify.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberNotifyRepository extends JpaRepository<MemberNotifyVO, Integer> {
    List<MemberNotifyVO> findByMemberVO_MemberIdOrderByCreatedAtDesc(Integer memberId);

    @Query(value =
            "SELECT o.TITLE, o.CREATED_AT, COUNT(*) as total, SUM(m.IS_READ) as read_count, o.NOTIFY_STATUS " +
            "FROM MEMBER_NOTIFY m " +
            "JOIN ORG_NOTIFY o ON m.ORG_NOTIFY_ID = o.ORG_NOTIFY_ID " +
            "WHERE o.ORGANIZER_ID = :organizerId AND o.EMP_ID IS NULL " +
            "AND (o.TITLE IS NULL OR o.TITLE NOT LIKE '%訂單%') " +
            "GROUP BY o.ORG_NOTIFY_ID, o.TITLE, o.CREATED_AT, o.NOTIFY_STATUS " +
            "ORDER BY o.CREATED_AT DESC", nativeQuery = true)
    List<Object[]> findGroupedSentRecordsByOrganizerId(@Param("organizerId") Integer organizerId);
}
