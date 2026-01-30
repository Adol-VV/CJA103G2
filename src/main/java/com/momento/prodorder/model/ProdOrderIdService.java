package com.momento.prodorder.model;

import com.momento.notify.model.NotificationBridgeService;
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

	@Autowired //pei
	private NotificationBridgeService bridgeService;
	
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
		// 確保 createdDate 有值
		if (prodOrderIdVO.getCreatedDate() == null) {
			prodOrderIdVO.setCreatedDate(new Date());
		}
		// 儲存訂單
		ProdOrderIdVO savedOrder = repository.save(prodOrderIdVO);

		// 重新載入訂單以確保所有關聯資料（包含商品名稱）都已載入
		ProdOrderIdVO reloadedOrder = repository.findById(savedOrder.getOrderId()).orElse(savedOrder);

		// pei - 發送通知
		bridgeService.processProdOrderNotify(reloadedOrder);
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
