package com.momento.prodorderitem.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("prodOrderItemService")
public class ProdOrderItemService {
	
	@Autowired
	private ProdOrderItemRepository repository;
	
	public void addProdOrderItem(ProdOrderItemVO prodOrderItemVO) {
		repository.save(prodOrderItemVO);
	}
	
	public void updateProdOrderItem(ProdOrderItemVO prodOrderItemVO) {
		repository.save(prodOrderItemVO);
	}
	
	public List<ProdOrderItemVO> getAll(){
		return repository.findAll();
	}
	
	public ProdOrderItemVO getOne(Integer orderID) {
		return repository.findById(orderID).orElse(null);
	}
}
