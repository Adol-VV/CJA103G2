package com.momento.eventorder.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventOrderItemRepository extends JpaRepository<EventOrderItemVO, Integer>{
	
	public EventOrderItemVO findByQrcode(String qrcode);
	
	public List<EventOrderItemVO> findByStatus(Integer status);
}
