package com.momento.notify.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemNotifyRepository extends JpaRepository<SystemNotifyVO, Integer> {
    List<SystemNotifyVO> findByMemberVO_MemberIdOrderByCreatedAtDesc(Integer memberId);

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
}
