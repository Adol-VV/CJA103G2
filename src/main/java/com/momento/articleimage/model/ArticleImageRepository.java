package com.momento.articleimage.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleImageRepository extends JpaRepository<ArticleImageVO, Integer> {
	// 根據文章ID查詢該文章的所有圖片：
	 List<ArticleImageVO> findByArticleVO_ArticleId(Integer articleId);
}