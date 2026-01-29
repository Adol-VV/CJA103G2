package com.momento.eventorder.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventOrderItemRepository extends JpaRepository<EventOrderItemVO, Integer>{
	
	public EventOrderItemVO findByQrcode(String qrcode);
	
	public List<EventOrderItemVO> findByStatus(Integer status);
	
	@Query("SELECT i.ticket.ticketId, COUNT(i) " +
	           "FROM EventOrderItemVO i " +
	           "WHERE i.eventOrder.eventOrderId = :eventOrderId " + // 這裡加上過濾條件
	           "GROUP BY i.ticket.ticketId")
	    List<Object[]> countByTicketAndOrderId(@Param("eventOrderId") Integer orderId);
}
