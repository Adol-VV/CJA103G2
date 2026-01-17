package com.momento.articleimage.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleImageService {

	@Autowired
	private ArticleImageRepository repository;

	public ArticleImageVO addArticleImage(ArticleImageVO articleImageVO) {
		return repository.save(articleImageVO);
	}

	public ArticleImageVO updateArticleImage(ArticleImageVO articleImageVO) {
		return repository.save(articleImageVO);
	}

	public void deleteArticleImage(Integer articleImageId) {
		if (repository.existsById(articleImageId)) {
			repository.deleteById(articleImageId);
		}
	}

	public ArticleImageVO getOneArticleImage(Integer articleImageId) {
		Optional<ArticleImageVO> optional = repository.findById(articleImageId);
		return optional.orElse(null);
	}

	public List<ArticleImageVO> getAll() {
		return repository.findAll();
	}
}
