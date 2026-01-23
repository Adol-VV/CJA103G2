package com.momento.prod.model;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.momento.prod.dto.ProdDTO;

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
	
	
	public List<ProdImageVO> getProdImagesByProdId(Integer prodId) {
		
		return getOneProd(prodId).getProdImages();
	}
	
	
//	public Slice<ProdDTO> getAllProds(Pageable pageable) {
//		Slice<ProdVO> prods = repository.findAll(pageable);
//		
//        return prods.map(prod -> {
//            ProdDTO dto = new ProdDTO();
//            dto.setProdId(prod.getProdId());
//            dto.setProdName(prod.getProdName());
//            dto.setProdPrice(prod.getProdPrice());
//            dto.setSortId(prod.getProdSortVO().getSortId());
//            dto.setProdStatus(prod.getProdStatus());
//            
//            // 取出第一張圖片作為主圖
//            if (prod.getProdImages() == null || prod.getProdImages().isEmpty()) {
//            	dto.setImageUrl("/images/default.png"); // 預設圖片
//            } else {
//                dto.setImageUrl(prod.getProdImages().get(0).getImageUrl());
//            }
//            return dto;
//        });
//    }
	
	
	public List<ProdDTO> getAllProds() {
        return repository.findAll().stream().filter(prod -> prod.getProdStatus() == 1).map(prod -> {
            ProdDTO dto = new ProdDTO();
            dto.setProdId(prod.getProdId());
            dto.setProdName(prod.getProdName());
            dto.setProdPrice(prod.getProdPrice());
            dto.setProdStock(prod.getProdStock());
            dto.setSortId(prod.getProdSortVO().getSortId());
            dto.setProdStatus(prod.getProdStatus());
            
            // 取出第一張圖片作為主圖
            if (prod.getProdImages() == null || prod.getProdImages().isEmpty()) {
            	dto.setImageUrl("/images/default.png"); // 預設圖片
            } else {
                dto.setImageUrl(prod.getProdImages().get(0).getImageUrl());
            }
            return dto;
        }).collect(Collectors.toList());
    }
	
	public List<ProdDTO> searchProds(String s){
        return repository.findByName(s).stream().filter(prod -> prod.getProdStatus() == 1).map(prod -> {
            ProdDTO dto = new ProdDTO();
            dto.setProdId(prod.getProdId());
            dto.setProdName(prod.getProdName());
            dto.setProdPrice(prod.getProdPrice());
            dto.setSortId(prod.getProdSortVO().getSortId());
            dto.setProdStatus(prod.getProdStatus());
            
            // 取出第一張圖片作為主圖
            if (prod.getProdImages() == null || prod.getProdImages().isEmpty()) {
            	dto.setImageUrl("/images/default.png"); // 預設圖片
            } else {
                dto.setImageUrl(prod.getProdImages().get(0).getImageUrl());
            }
            return dto;
        }).collect(Collectors.toList());
	}
}
