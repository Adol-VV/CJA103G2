package com.momento.prod.model;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Transactional
	public void updateProdReviewStatus(Integer prodId, byte reviewStatus) {
		Optional<ProdVO> optional = repository.findById(prodId);
		ProdVO prod = optional.get();
		prod.setReviewStatus(reviewStatus);
		repository.save(prod);
	}
	
	@Transactional
	public void updateProdStatus(Integer prodId, byte prodStatus) {
		Optional<ProdVO> optional = repository.findById(prodId);
		ProdVO prod = optional.get();
		prod.setProdStatus(prodStatus);
		repository.save(prod);
	}
	
	public ProdDTO getOneProd(Integer prodId) {
		Optional<ProdVO> optional = repository.findById(prodId);
		ProdVO prod = optional.get();
		ProdDTO dto = new ProdDTO();
        dto.setProdId(prod.getProdId());
        dto.setProdName(prod.getProdName());
        dto.setProdPrice(prod.getProdPrice());
        dto.setProdStock(prod.getProdStock());
        dto.setProdContent(prod.getProdContent());
        dto.setOrganizerName(prod.getOrganizerVO().getName());
        dto.setSortName(prod.getProdSortVO().getSortName());
        dto.setCreatedAt(prod.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        ////將上下架狀態由數字變更為字串存進DTO
        switch(prod.getProdStatus()){
        case 0:
        	dto.setProdStatus("已下架");
        	break;
        case 1:
        	dto.setProdStatus("上架中");
        	break;
        }
        
        //將審核狀態由數字變更為字串存進DTO
        switch(prod.getReviewStatus()) {
        case 1:
        	dto.setReviewStatus("通過");
        	break;
        case 2:
        	dto.setReviewStatus("未通過");
        	break;
        default:
        	dto.setReviewStatus("待審核");
        }
        
        // 取出第一張圖片作為主圖
        if (prod.getProdImages() == null || prod.getProdImages().isEmpty()) {
        	dto.setMainImageUrl("/images/default.png"); // 預設圖片
        } else {
            dto.setMainImageUrl(prod.getProdImages().get(0).getImageUrl());
        }
        List<String> ImageUrls = prod.getProdImages().stream().map(prodImage -> 
        	prodImage.getImageUrl()).collect(Collectors.toList());
        dto.setProdImages(ImageUrls);
        
		return dto;
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
        return repository.findAll().stream().map(prod -> {
            ProdDTO dto = new ProdDTO();
            dto.setProdId(prod.getProdId());
            dto.setProdName(prod.getProdName());
            dto.setProdPrice(prod.getProdPrice());
            dto.setProdStock(prod.getProdStock());
            dto.setOrganizerId(prod.getOrganizerVO().getOrganizerId());
            dto.setOrganizerName(prod.getOrganizerVO().getName());
            dto.setSortId(prod.getProdSortVO().getSortId());
            dto.setSortName(prod.getProdSortVO().getSortName());
            dto.setCreatedAt(prod.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            ////將上下架狀態由數字變更為字串存進DTO
            switch(prod.getProdStatus()){
            case 0:
            	dto.setProdStatus("已下架");
            	break;
            case 1:
            	dto.setProdStatus("上架中");
            	break;
            }
            
            //將審核狀態由數字變更為字串存進DTO
            switch(prod.getReviewStatus()) {
            case 1:
            	dto.setReviewStatus("通過");
            	break;
            case 2:
            	dto.setReviewStatus("未通過");
            	break;
            default:
            	dto.setReviewStatus("待審核");
            }
            
            // 取出第一張圖片作為主圖
            if (prod.getProdImages() == null || prod.getProdImages().isEmpty()) {
            	dto.setMainImageUrl("/images/default.png"); // 預設圖片
            } else {
                dto.setMainImageUrl(prod.getProdImages().get(0).getImageUrl());
            }
            return dto;
        }).collect(Collectors.toList());
    }
	
	
	public List<ProdDTO> getProdsByOrg(Integer organizerId){
		return repository.findProdsByOrgId(organizerId).stream().map(prod -> {
			ProdDTO dto = new ProdDTO();
            dto.setProdId(prod.getProdId());
            dto.setProdName(prod.getProdName());
            dto.setProdPrice(prod.getProdPrice());
            dto.setProdStock(prod.getProdStock());
            dto.setSortId(prod.getProdSortVO().getSortId());
            dto.setSortName(prod.getProdSortVO().getSortName());
            
            ////將上下架狀態由數字變更為字串存進DTO
            switch(prod.getProdStatus()){
            case 0:
            	dto.setProdStatus("已下架");
            	break;
            case 1:
            	dto.setProdStatus("上架中");
            	break;
            }
            
            
            //將審核狀態由數字變更為字串存進DTO
            switch(prod.getReviewStatus()) {
            case 1:
            	dto.setReviewStatus("通過");
            	break;
            case 2:
            	dto.setReviewStatus("未通過");
            	break;
            default:
            	dto.setReviewStatus("待審核");
            }
            
            // 取出第一張圖片作為主圖
            if (prod.getProdImages() == null || prod.getProdImages().isEmpty()) {
            	dto.setMainImageUrl("/images/default.png"); // 預設圖片
            } else {
                dto.setMainImageUrl(prod.getProdImages().get(0).getImageUrl());
            }
            return dto;
		}).collect(Collectors.toList());
		
	}
	
	public List<ProdDTO> searchProds(String s){
        return repository.findByName(s).stream().map(prod -> {
            ProdDTO dto = new ProdDTO();
            dto.setProdId(prod.getProdId());
            dto.setProdName(prod.getProdName());
            dto.setProdPrice(prod.getProdPrice());
            dto.setProdStock(prod.getProdStock());
            dto.setOrganizerId(prod.getOrganizerVO().getOrganizerId());
            dto.setOrganizerName(prod.getOrganizerVO().getName());
            dto.setSortId(prod.getProdSortVO().getSortId());
            dto.setSortName(prod.getProdSortVO().getSortName());
            dto.setCreatedAt(prod.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            ////將上下架狀態由數字變更為字串存進DTO
            switch(prod.getProdStatus()){
            case 0:
            	dto.setProdStatus("已下架");
            	break;
            case 1:
            	dto.setProdStatus("上架中");
            	break;
            }
            
            //將審核狀態由數字變更為字串存進DTO
            switch(prod.getReviewStatus()) {
            case 1:
            	dto.setReviewStatus("通過");
            	break;
            case 2:
            	dto.setReviewStatus("未通過");
            	break;
            default:
            	dto.setReviewStatus("待審核");
            }
            
            // 取出第一張圖片作為主圖
            if (prod.getProdImages() == null || prod.getProdImages().isEmpty()) {
            	dto.setMainImageUrl("/images/default.png"); // 預設圖片
            } else {
                dto.setMainImageUrl(prod.getProdImages().get(0).getImageUrl());
            }
            return dto;
        }).collect(Collectors.toList());
	}
	
	
	public List<ProdDTO> orgSearchProds(Integer organizerId,String s){
        return repository.findByOrgAndName(organizerId,s).stream().map(prod -> {
			ProdDTO dto = new ProdDTO();
            dto.setProdId(prod.getProdId());
            dto.setProdName(prod.getProdName());
            dto.setProdPrice(prod.getProdPrice());
            dto.setProdStock(prod.getProdStock());
            dto.setSortId(prod.getProdSortVO().getSortId());
            dto.setSortName(prod.getProdSortVO().getSortName());
            
            ////將上下架狀態由數字變更為字串存進DTO
            switch(prod.getProdStatus()){
            case 0:
            	dto.setProdStatus("已下架");
            	break;
            case 1:
            	dto.setProdStatus("上架中");
            	break;
            }
            
            
            //將審核狀態由數字變更為字串存進DTO
            switch(prod.getReviewStatus()) {
            case 1:
            	dto.setReviewStatus("通過");
            	break;
            case 2:
            	dto.setReviewStatus("未通過");
            	break;
            default:
            	dto.setReviewStatus("待審核");
            }
            
            // 取出第一張圖片作為主圖
            if (prod.getProdImages() == null || prod.getProdImages().isEmpty()) {
            	dto.setMainImageUrl("/images/default.png"); // 預設圖片
            } else {
                dto.setMainImageUrl(prod.getProdImages().get(0).getImageUrl());
            }
            return dto;
		}).collect(Collectors.toList());
	}
	
	
}
