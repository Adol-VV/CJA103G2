package com.momento.prod.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdService {
	
	@Autowired
	ProdRepository repository;
	
	public void addProd(ProdVO prodVO) {
		repository.save(prodVO);
	}
	
	public void updateProd(ProdVO prodVO) {
		repository.save(prodVO);
	}
	
	public ProdVO getOneProd(Integer prodId) {
		Optional<ProdVO> optional = repository.findById(prodId);
		return optional.orElse(null);
	}
	
	public List<ProdVO> getAll(){
		return repository.findAll();
	}
	
}
