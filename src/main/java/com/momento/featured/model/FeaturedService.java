package com.momento.featured.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeaturedService {

	@Autowired
	private FeaturedRepository repository;

	public FeaturedVO addFeatured(FeaturedVO featuredVO) {
		return repository.save(featuredVO);
	}

	public FeaturedVO updateFeatured(FeaturedVO featuredVO) {
		return repository.save(featuredVO);
	}

	public void deleteFeatured(Integer featuredId) {
		if (repository.existsById(featuredId)) {
			repository.deleteById(featuredId);
		}
	}

	public FeaturedVO getOneFeatured(Integer featuredId) {
		Optional<FeaturedVO> optional = repository.findById(featuredId);
		return optional.orElse(null);
	}

	public List<FeaturedVO> getAll() {
		return repository.findAll();
	}
}
