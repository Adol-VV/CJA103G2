package com.momento.eventsettle.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.momento.event.model.EventVO;

public interface EventSettleRepository extends JpaRepository<EventSettleVO, Integer> {
    public java.util.List<EventSettleVO> findByOrganizer_OrganizerIdOrderByCreatedAtDesc(Integer organizerId);
}
