package com.momento.prodsettledetail.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("prodSettleDetailService")
public class ProdSettleDetailService {

	@Autowired
	private ProdSettleDetailRepository repository;

	public void addProdSettleDetail(ProdSettleDetailVO prodSettleDetailVO) {
		repository.save(prodSettleDetailVO);
	}

	public void updateProdSettleDetail(ProdSettleDetailVO prodSettleDetailVO) {
		repository.save(prodSettleDetailVO);
	}
	
	public List<ProdSettleDetailVO> getAll() {
		return repository.findAll();
	}
	public ProdSettleDetailVO  getOne(Integer SettleID) {
		return repository.findById(SettleID).orElse(null);
	}
}
