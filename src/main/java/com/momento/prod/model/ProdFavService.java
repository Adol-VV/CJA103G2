package com.momento.prod.model;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.momento.prod.dto.ProdDTO;

@Service
public class ProdFavService {
	
	@Autowired
	ProdFavRepository prodFavRepository;
	
	@Autowired
	ProdRepository prodRepository;
	
	public List<ProdDTO> favProdsByMember(Integer memberId){
		System.out.println(memberId);
		List<ProdFavVO> prodFavs = prodFavRepository.findByMemberId(memberId);
		
		return prodFavs.stream().map(prodFavVO -> {
            ProdVO prod = prodFavVO.getProdVO();
			ProdDTO dto = new ProdDTO();
            dto.setProdId(prod.getProdId());
            dto.setProdName(prod.getProdName());
            dto.setProdPrice(prod.getProdPrice());
            dto.setProdStock(prod.getProdStock());
            dto.setSortId(prodFavVO.getProdVO().getProdSortVO().getSortId());
            dto.setSortName(prodFavVO.getProdVO().getProdSortVO().getSortName());
            
            // 取出第一張圖片作為主圖
            if (prodFavVO.getProdVO().getProdImages() == null || prodFavVO.getProdVO().getProdImages().isEmpty()) {
            	dto.setMainImageUrl("/images/default.png"); // 預設圖片
            } else {
                dto.setMainImageUrl(prodFavVO.getProdVO().getProdImages().get(0).getImageUrl());
            }
            return dto;
        }).collect(Collectors.toList());
	}
}
