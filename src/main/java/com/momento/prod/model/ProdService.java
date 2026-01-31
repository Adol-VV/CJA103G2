package com.momento.prod.model;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.momento.emp.model.EmpVO;
import com.momento.organizer.model.OrganizerVO;
import com.momento.prod.dto.ProdDTO;

@Service
public class ProdService {
	
	@Autowired
	ProdRepository repository;
	
	private final String uploadDir = "C:/momento-uploads/product/";
	private final String baseUrl = "/product/";
	
	@Transactional
	public void addProd(ProdDTO prodDTO, MultipartFile[] files) {
		ProdVO prodVO = new ProdVO();
		OrganizerVO organizerVO = new OrganizerVO();
		organizerVO.setOrganizerId(prodDTO.getOrganizerId());
		ProdSortVO prodSortVO = new ProdSortVO();
		prodSortVO.setSortId(prodDTO.getSortId());
		EmpVO empVO = new EmpVO();
		empVO.setEmpId(8);
		prodVO.setOrganizerVO(organizerVO);
		prodVO.setProdSortVO(prodSortVO);
        prodVO.setEmpVO(empVO);
        prodVO.setProdName(prodDTO.getProdName());
        prodVO.setProdContent(prodDTO.getProdContent());
        prodVO.setProdPrice(prodDTO.getProdPrice());
        prodVO.setProdStock(prodDTO.getProdStock());
        prodVO.setCreatedAt(LocalDateTime.now());
        prodVO.setUpdatedAt(LocalDateTime.now());
        prodVO.setProdStatus((byte) 0);
        prodVO.setReviewStatus((byte) 0);
        
        //建目標路徑資料夾
        try {
            Files.createDirectories(Path.of(uploadDir));
        } catch (IOException e) {
            throw new UncheckedIOException("無法建立上傳目錄", e);
        }
        //圖片
		List<ProdImageVO> prodImages = Arrays.stream(files).filter(file -> !file.isEmpty()).map(file -> {
	        try {

	        	// 建立隨機檔名防止衝突
		        String originalFilename = file.getOriginalFilename();
		        String extension = "";
		        if (originalFilename != null && originalFilename.contains(".")) {
		            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		        }
		        String fileName = UUID.randomUUID().toString() + extension;
		        // 使用 Path 組合完整的儲存路徑
		        Path targetPath = Path.of(uploadDir).resolve(fileName);
		        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
				ProdImageVO image = new ProdImageVO();
				image.setImageUrl(baseUrl+fileName);
				image.setProdVO(prodVO);
				image.setCreatedAt(LocalDateTime.now());
		        return image;
	        } catch (IOException e) {
	        	throw new UncheckedIOException("圖片儲存失敗: " + file.getOriginalFilename(), e);
	        }
		}).collect(Collectors.toList());
		
		prodVO.setProdImages(prodImages);
		repository.save(prodVO);
	}
	
	@Transactional
	public void updateProd(ProdDTO prodDTO, MultipartFile[] files, Integer[] imageIds) {
		Optional<ProdVO> optional = repository.findById(prodDTO.getProdId());
		ProdVO prodVO = optional.get();
        prodVO.setProdContent(prodDTO.getProdContent());
        prodVO.setProdPrice(prodDTO.getProdPrice());
        prodVO.setProdStock(prodDTO.getProdStock());
        prodVO.setUpdatedAt(LocalDateTime.now());
        prodVO.setProdStatus((byte) 0);
        prodVO.setReviewStatus((byte) 0);
        
        //建目標路徑資料夾
        try {
            Files.createDirectories(Path.of(uploadDir));
        } catch (IOException e) {
            throw new UncheckedIOException("無法建立上傳目錄", e);
        }
        
        //圖片
        List<ProdImageVO> currentImages = prodVO.getProdImages();  //先抓原本的ProdImageVO出來
		for(int i = 0 ; i < files.length; i++) {
			MultipartFile file = files[i];
			System.out.println("aaaa");
			//這個位置沒有上傳新圖的話就跳過
			if(file == null || file.isEmpty()) {
				continue;
			}else {
				System.out.println("bbbb");
				if(imageIds[i] == null) {
					System.out.println("cccc");
					//新圖
					try {				
			        	// 建立隨機檔名防止衝突
				        String originalFilename = file.getOriginalFilename();
				        String extension = "";
				        if (originalFilename != null && originalFilename.contains(".")) {
				            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
				        }
				        String fileName = UUID.randomUUID().toString() + extension;
				        // 使用 Path 組合完整的儲存路徑
				        Path targetPath = Path.of(uploadDir).resolve(fileName);
				        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
						//建新的物件塞進list
				        ProdImageVO image = new ProdImageVO();
						image.setImageUrl(baseUrl+fileName);
						image.setProdVO(prodVO);
						image.setCreatedAt(LocalDateTime.now());
						currentImages.add(image);
			        } catch (IOException e) {
			        	throw new UncheckedIOException("圖片儲存失敗: " + file.getOriginalFilename(), e);
			        }
				}else {
					//舊圖更新
					try {				
			        	// 建立隨機檔名防止衝突
				        String originalFilename = file.getOriginalFilename();
				        String extension = "";
				        if (originalFilename != null && originalFilename.contains(".")) {
				            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
				        }
				        String fileName = UUID.randomUUID().toString() + extension;
				        // 使用 Path 組合完整的儲存路徑
				        Path targetPath = Path.of(uploadDir).resolve(fileName);
				        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
						//抓對應的既存物件去更新欄位
				        ProdImageVO image = currentImages.get(i);
				        image.setImageUrl(baseUrl+fileName);
						image.setCreatedAt(LocalDateTime.now());
			        } catch (IOException e) {
			        	throw new UncheckedIOException("圖片儲存失敗: " + file.getOriginalFilename(), e);
			        }
				}
			}
		}
		repository.save(prodVO);
	}
	
	
	//變更商品審核狀態
	@Transactional
	public void updateProdReviewStatus(Integer prodId, byte reviewStatus) {
		Optional<ProdVO> optional = repository.findById(prodId);
		ProdVO prod = optional.get();
		prod.setReviewStatus(reviewStatus);
		repository.save(prod);
	}
	
	//變更商品上下架狀態
	@Transactional
	public void updateProdStatus(Integer prodId, byte prodStatus) {
		Optional<ProdVO> optional = repository.findById(prodId);
		ProdVO prod = optional.get();
		prod.setProdStatus(prodStatus);
		repository.save(prod);
	}
	
	//商品單一查詢
	public ProdDTO getOneProd(Integer prodId) {
		Optional<ProdVO> optional = repository.findById(prodId);
		ProdVO prod = optional.get();
		ProdDTO dto = new ProdDTO();
        dto.setProdId(prod.getProdId());
        dto.setProdName(prod.getProdName());
        dto.setProdPrice(prod.getProdPrice());
        dto.setProdStock(prod.getProdStock());
        dto.setProdContent(prod.getProdContent());
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
        List<String> imageUrls = prod.getProdImages().stream().map(prodImage -> 
        	prodImage.getImageUrl()).collect(Collectors.toList());
        dto.setProdImages(imageUrls);
        
        List<ProdImageVO> images = prod.getProdImages();
        dto.setImages(images);
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
	
	//商品查詢全部
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

	//最新商品
	public List<ProdDTO> findLatestProds() {
        return repository.findLatestProds().stream().map(prod -> {
            ProdDTO dto = new ProdDTO();
            dto.setProdId(prod.getProdId());
            dto.setProdName(prod.getProdName());
            
            // 取出第一張圖片作為主圖
            if (prod.getProdImages() == null || prod.getProdImages().isEmpty()) {
            	dto.setMainImageUrl("/images/default.png"); // 預設圖片
            } else {
                dto.setMainImageUrl(prod.getProdImages().get(0).getImageUrl());
            }
            return dto;
        }).collect(Collectors.toList());
    }
	
	
	//主辦方查詢商品
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
	
	
	//商品模糊名稱查詢
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
	
	//主辦方商品的模糊名稱查詢
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
