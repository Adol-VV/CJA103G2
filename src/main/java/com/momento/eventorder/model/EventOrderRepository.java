package com.momento.eventorder.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.momento.event.model.EventVO;

public interface EventOrderRepository extends JpaRepository<EventOrderVO, Integer>{
	
	public List<EventOrderVO> findByOrganizer_OrganizerId(Integer organizerId);
	
	Optional<EventVO> findEventByEventOrderId(Integer eventOrderId);
	
	public Optional<EventOrderVO> findByEventOrderId(Integer eventOrderId);
	
	public List<EventOrderVO> findByMember_MemberId(Integer memberId);
	
}
