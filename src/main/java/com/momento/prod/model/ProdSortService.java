package com.momento.prod.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdSortService {
	@Autowired
	ProdSortRepository repository;
	
	public void addProdSort(ProdSortVO prodSortVO) {
		repository.save(prodSortVO);
	}
	
	public void updateProdSort(ProdSortVO prodSortVO) {
		repository.save(prodSortVO);
	}
	
	public ProdSortVO getOneProdSort(Integer sortId) {
		Optional<ProdSortVO> optional = repository.findById(sortId);
		return optional.orElse(null);
	}
	
	public List<ProdSortVO> getAll(){
		return repository.findAll();
	}
}
