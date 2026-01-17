package com.momento.article.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleVO, Integer> {
	// 常用的查詢方法範例：
	 List<ArticleVO> findByOrganizerVO_OrganizerId(Integer organizerId);
	 List<ArticleVO> findByTitleContaining(String keyword);
}