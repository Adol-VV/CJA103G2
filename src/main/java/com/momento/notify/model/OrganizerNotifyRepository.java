package com.momento.notify.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerNotifyRepository extends JpaRepository<OrganizerNotifyVO, Integer> {
}
