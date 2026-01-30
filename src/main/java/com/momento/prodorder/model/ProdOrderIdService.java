package com.momento.prodorder.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.momento.prod.model.ProdRepository;
import com.momento.prod.model.ProdVO;
import com.momento.prodorderitem.model.ProdOrderItemVO;

@Service("prodOrderIdService")
@Transactional
public class ProdOrderIdService {
	
	@Autowired
	private ProdOrderIdRepository repository;
	
	@Autowired
	ProdRepository prodRepository;
	
	public void addProdOrder(ProdOrderIdVO prodOrderIdVO) {
		if(prodOrderIdVO.getOrderItems() !=null) {
			for (ProdOrderItemVO item : prodOrderIdVO.getOrderItems()) {
                item.setProdOrderId(prodOrderIdVO);
                ProdVO product = prodRepository.getById(item.getProdId().getProdId());
                if(product != null) {
                	System.out.println( " product.getProdStock()" + product.getProdStock());
                	int newStock = product.getProdStock() - item.getQuantity();
                    
                    if (newStock < 0) {
                        throw new RuntimeException("商品 " + product.getProdName() + " 庫存不足，剩餘：" + product.getProdStock());
                    }
                    product.setProdStock(newStock);
                }
            }
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
	public List<ProdOrderIdVO> getByOrganizerId(Integer organizerId) {
		return repository.findByOrganizerId_OrganizerId(organizerId);
	}
}
