package com.momento.prodorder.model;

import com.momento.notify.model.NotificationBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("prodOrderIdService")
@Transactional
public class ProdOrderIdService {
	
	@Autowired
	private ProdOrderIdRepository repository;

	@Autowired //pei
	private NotificationBridgeService bridgeService;
	
	public void addProdOrder(ProdOrderIdVO prodOrderIdVO) {
		if(prodOrderIdVO.getOrderItems() !=null) {
			prodOrderIdVO.getOrderItems().forEach(item->item.setProdOrderId(prodOrderIdVO));
		}
		repository.save(prodOrderIdVO);

		// pei
		bridgeService.processProdOrderNotify(prodOrderIdVO);
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
	public List<ProdOrderIdVO> getByOrganizerId(Integer organizerId) {
		return repository.findByOrganizerId_OrganizerId(organizerId);
	}
}
