package com.momento.messagereport.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReportRepository extends JpaRepository<MessageReportVO, Integer> {
	// 查詢未處理的檢舉 (Status = 0)：
	 List<MessageReportVO> findByStatus(Integer status);
	
	// 查詢針對某則留言的所有檢舉：
	 List<MessageReportVO> findByMessageVO_MessageId(Integer messageId);
}
