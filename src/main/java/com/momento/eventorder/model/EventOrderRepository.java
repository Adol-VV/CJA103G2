package com.momento.eventorder.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventOrderRepository extends JpaRepository<EventOrderVO, Integer> {
	
	public EventOrderVO getByEventOrderId(Integer eventOrderId);

	public List<EventOrderVO> findByOrganizer_OrganizerId(Integer organizerId);

	public List<EventOrderVO> findByEvent_EventId(Integer eventId);

	public Optional<EventOrderVO> findByEventOrderId(Integer eventOrderId);

	public List<EventOrderVO> findByMember_MemberId(Integer memberId);

	public Page<EventOrderVO> findAll(Pageable pageable);

	@Query("SELECT o FROM EventOrderVO o WHERE " + "o.event.organizer.organizerId = :organizerId " + "AND ("
			+ "  (:activeEvent IS NULL AND :finishedEvent IS NULL) "
			+ "  OR (:activeEvent IS NOT NULL AND o.event.eventId = :activeEvent) "
			+ "  OR (:finishedEvent IS NOT NULL AND o.event.eventId = :finishedEvent) " + ") "
			+ "AND (:buyer IS NULL OR trim(:buyer) = '' " + "OR o.member.name LIKE %:buyer% "
			+ "OR CAST(o.eventOrderId AS string) LIKE %:buyer%)")
	public List<EventOrderVO> filterOrders(@Param("organizerId") Integer organizerId,
			@Param("activeEvent") Integer activeEvent, @Param("finishedEvent") Integer finishedEvent,
			@Param("buyer") String buyer);

	@Query("SELECT o FROM EventOrderVO o " + "JOIN o.event e " + "JOIN o.member m "
			+ "WHERE (:eventOrderId IS NULL OR o.eventOrderId = :eventOrderId) "  
		    + "AND (:memberName IS NULL OR m.name LIKE CONCAT('%', :memberName, '%')) "
			+ "AND (:eventTitle IS NULL OR e.title LIKE CONCAT('%', :eventTitle, '%'))")
	public Page<EventOrderVO> searchOrders(
			@Param("eventOrderId") Integer eventOrderId,
			@Param("memberName") String memberName, 
			@Param("eventTitle")  String eventTitle, 
			Pageable pageable);
}
