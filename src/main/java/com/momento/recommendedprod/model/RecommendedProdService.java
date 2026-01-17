package com.momento.recommendedprod.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecommendedProdService {

	@Autowired
	private RecommendedProdRepository repository;

	public RecommendedProdVO addRecommendedProd(RecommendedProdVO recommendedProdVO) {
		return repository.save(recommendedProdVO);
	}

	public RecommendedProdVO updateRecommendedProd(RecommendedProdVO recommendedProdVO) {
		return repository.save(recommendedProdVO);
	}

	public void deleteRecommendedProd(Integer recommendedProdId) {
		if (repository.existsById(recommendedProdId)) {
			repository.deleteById(recommendedProdId);
		}
	}

	public RecommendedProdVO getOneRecommendedProd(Integer recommendedProdId) {
		Optional<RecommendedProdVO> optional = repository.findById(recommendedProdId);
		return optional.orElse(null);
	}

	public List<RecommendedProdVO> getAll() {
		return repository.findAll();
	}
}
