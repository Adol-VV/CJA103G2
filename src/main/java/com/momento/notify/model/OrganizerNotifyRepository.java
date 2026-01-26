package com.momento.notify.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizerNotifyRepository extends JpaRepository<OrganizerNotifyVO, Integer> {
    List<OrganizerNotifyVO> findByOrganizerVO_OrganizerIdOrderByCreatedAtDesc(Integer organizerId);

    // 尋找特定的活動通知 (基於標題關鍵字和主辦方)
    List<OrganizerNotifyVO> findByOrganizerVO_OrganizerIdAndTitleContainingOrderByCreatedAtDesc(Integer organizerId,
            String titleKeyword);
}
