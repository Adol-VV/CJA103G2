package com.momento.prod.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
	
	public void deleteProdImageById(Integer prodImageId) {
		Optional<ProdImageVO> optional = repository.findById(prodImageId);
		ProdImageVO prodImage = optional.get();
		Path targetPath = Path.of("C:/momento-uploads/" + prodImage.getImageUrl());
		try {
			Files.deleteIfExists(targetPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		repository.deleteByImageId(prodImageId);
	}
	
	public ProdImageVO getOneProdImage(Integer prodImageId) {
		Optional<ProdImageVO> optional = repository.findById(prodImageId);
		return optional.orElse(null);
	}
		
}
