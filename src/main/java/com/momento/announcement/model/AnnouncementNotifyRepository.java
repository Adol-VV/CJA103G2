package com.momento.announcement.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementNotifyRepository extends JpaRepository<AnnouncementVO, Integer> {

}
