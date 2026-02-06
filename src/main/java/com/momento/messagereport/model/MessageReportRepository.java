package com.momento.messagereport.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReportRepository extends JpaRepository<MessageReportVO, Integer> {
	// 查詢未處理的檢舉 (Status = 0)：
	List<MessageReportVO> findByStatus(Integer status);

	// 查詢針對某則留言的所有檢舉：
	List<MessageReportVO> findByMessageVO_MessageId(Integer messageId);

	// 搜尋特定狀態的檢舉 (支援內容、原因、會員名稱搜尋)
	@org.springframework.data.jpa.repository.Query("SELECT m FROM MessageReportVO m WHERE m.status = ?1 AND (m.messageVO.content LIKE %?2% OR m.reportReason LIKE %?2% OR m.memberVO.name LIKE %?2%)")
	List<MessageReportVO> findByStatusAndKeyword(Integer status, String keyword,
			org.springframework.data.domain.Sort sort);

	// 搜尋所有檢舉 (支援內容、原因、會員名稱搜尋)
	@org.springframework.data.jpa.repository.Query("SELECT m FROM MessageReportVO m WHERE m.messageVO.content LIKE %?1% OR m.reportReason LIKE %?1% OR m.memberVO.name LIKE %?1%")
	List<MessageReportVO> findByKeyword(String keyword, org.springframework.data.domain.Sort sort);
}
