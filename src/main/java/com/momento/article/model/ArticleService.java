package com.momento.article.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

	@Autowired
	private ArticleRepository repository;

	public ArticleVO addArticle(ArticleVO articleVO) {
		return repository.save(articleVO);
	}

	public ArticleVO updateArticle(ArticleVO articleVO) {
		return repository.save(articleVO);
	}

	public void deleteArticle(Integer articleId) {
		if (repository.existsById(articleId)) {
			repository.deleteById(articleId);
		}
	}

	public ArticleVO getOneArticle(Integer articleId) {
		Optional<ArticleVO> optional = repository.findById(articleId);
		return optional.orElse(null);
	}

	public List<ArticleVO> getAll() {
		return repository.findAll();
	}
}