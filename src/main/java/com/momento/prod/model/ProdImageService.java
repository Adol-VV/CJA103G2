package com.momento.prod.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdImageService {
	
	@Autowired
	ProdImageRepository repository;
	
	public void addProdImage(ProdImageVO prodImageVO) {
		repository.save(prodImageVO);
	}
	
	public void updateProdImage(ProdImageVO prodImageVO) {
		repository.save(prodImageVO);
	}
	
	public ProdImageVO getOneProdImage(Integer prodImageId) {
		Optional<ProdImageVO> optional = repository.findById(prodImageId);
		return optional.orElse(null);
	}
		
}
