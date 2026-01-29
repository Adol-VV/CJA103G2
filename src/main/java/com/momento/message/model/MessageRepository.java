package com.momento.message.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageVO, Integer> {
	// 查詢某篇文章下的所有留言：
	List<MessageVO> findByArticleVO_ArticleId(Integer articleId);

	// 查詢某個會員的所有留言：
	List<MessageVO> findByMemberVO_MemberId(Integer memberId);

	// 根據狀態查詢留言
	List<MessageVO> findByStatus(Integer status);
}
