package com.momento.prodsettle.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ProdSettleService")
public class ProdSettleService {

	@Autowired
	private ProdSettleRepository repository;
	
	public void addProdSettle(ProdSettleVO prodSettleVO) {
		repository.save(prodSettleVO);
	}
	
	public void updateProdSettle(ProdSettleVO prodSettleVO) {
		repository.save(prodSettleVO);
	}
	public List<ProdSettleVO>getAll() {
		return repository.findAll();
	}
	public Optional<ProdSettleVO> getOne(Integer settleID) {
		return repository.findById(settleID);
	}
	
}
