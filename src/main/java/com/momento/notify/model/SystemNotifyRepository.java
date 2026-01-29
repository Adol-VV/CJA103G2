package com.momento.notify.model;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SystemNotifyRepository extends JpaRepository<SystemNotifyVO, Integer> {
    List<SystemNotifyVO> findByMemberVO_MemberIdOrderByCreatedAtDesc(Integer memberId);
    List<SystemNotifyVO> findByOrganizerVO_OrganizerIdOrderByCreatedAtDesc(Integer organizerId);

    @Query(value =
            "SELECT TITLE, CREATED_AT, " +
            "SUBSTRING_INDEX(SUBSTRING_INDEX(MIN(CONTENT), '|', 2), '|', -1) as target, " +
            "COUNT(*) as total, " +
            "SUM(IS_READ) as read_count, " +
            "SUBSTRING_INDEX(SUBSTRING_INDEX(MIN(CONTENT), ']|', 1), '[', -1) as type " +
            "FROM SYS_NOTIFY " +
            "GROUP BY TITLE, CREATED_AT " +
            "ORDER BY CREATED_AT DESC", nativeQuery = true)
    List<Object[]> findGroupedNotifyRecords();

    @Modifying
    @Transactional
    @Query("UPDATE SystemNotifyVO s SET s.isRead = 1 WHERE s.organizerVO.organizerId = :orgId AND s.isRead = 0")
    void markAllAsReadByOrgId(@Param("orgId") Integer orgId);
}
