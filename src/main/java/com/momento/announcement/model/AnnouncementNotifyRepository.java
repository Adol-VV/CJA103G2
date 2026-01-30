package com.momento.announcement.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementNotifyRepository extends JpaRepository<AnnouncementVO, Integer> {

    // 依建立時間排序
    List<AnnouncementVO> findAllByOrderByCreatedAtDesc();

    // 依更新時間排序
    List<AnnouncementVO> findAllByOrderByUpdatedAtDesc();
}
