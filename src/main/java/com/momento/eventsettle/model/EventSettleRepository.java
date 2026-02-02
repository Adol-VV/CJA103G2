package com.momento.eventsettle.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.momento.event.model.EventVO;

public interface EventSettleRepository extends JpaRepository<EventSettleVO, Integer>{
	
	
//	@Query("SELECT a FROM Event a WHERE YEAR(a.eventEndAt) = :year AND MONTH(a.activityDate) = :month")
//	List<EventVO> findByYearAndMonth(@Param("year") int year, @Param("month") int month);
}
