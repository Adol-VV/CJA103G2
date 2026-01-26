package com.momento.eventorder.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface EventOrderRepository extends JpaRepository<EventOrderVO, Integer>{
	
	public List<EventOrderVO> findByOrganizer_OrganizerId(Integer organizerId);
	
	public List<EventOrderVO> findByEvent_EventId(int eventId);
	
	public Optional<EventOrderVO> findByEventOrderId(Integer eventOrderId);
	
	public List<EventOrderVO> findByMember_MemberId(int memberId);
}
