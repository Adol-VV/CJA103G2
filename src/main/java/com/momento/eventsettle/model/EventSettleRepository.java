package com.momento.eventsettle.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSettleRepository extends JpaRepository<EventSettleVO, Integer> {
    public java.util.List<EventSettleVO> findByOrganizer_OrganizerIdOrderByCreatedAtDesc(Integer organizerId);
}
