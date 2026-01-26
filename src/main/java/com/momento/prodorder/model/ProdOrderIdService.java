package com.momento.prodorder.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("prodOrderIdService")
@Transactional
public class ProdOrderIdService {
	
	@Autowired
	private ProdOrderIdRepository repository;
	
	public void addProdOrder(ProdOrderIdVO prodOrderIdVO) {
		if(prodOrderIdVO.getOrderItems() !=null) {
			prodOrderIdVO.getOrderItems().forEach(item->item.setProdOrderId(prodOrderIdVO));
		}
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
	
	public Optional<ProdOrderIdVO> getOne(Integer orderID) {
		return repository.findById(orderID);
	}
	
	public List<ProdOrderIdVO> getByMemberId(Integer memberId) {
		return repository.findByMemberId_MemberId(memberId);
	}
}
