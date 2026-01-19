package com.momento.prodorder.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("prodOrderIdService")
public class ProdOrderIdService {
	
	@Autowired
	private ProdOrderIdRepository repository;
	
	public void addProdOrder(ProdOrderIdVO prodOrderIdVO) {
		repository.save(prodOrderIdVO);
	}
	
	public void updateProdOrder(ProdOrderIdVO prodOrderIdVO) {
		repository.save(prodOrderIdVO);
	}
	
	public void deleteProdOrder(Integer orderID) {
		if(repository.existsById(orderID))
			repository.deleteById(orderID);
	}
	
	public List<ProdOrderIdVO> getAll() {
		return repository.findAll();
	}
	
	public ProdOrderIdVO getOne(Integer orderID) {
		return repository.findById(orderID).orElse(null);
	}
}
