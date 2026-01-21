package com.momento.prod.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.momento.prod.dto.ProdDTO;
import com.momento.prod.model.ProdImageVO;
import com.momento.prod.model.ProdService;
import com.momento.prod.model.ProdSortService;
import com.momento.prod.model.ProdSortVO;
import com.momento.prod.model.ProdVO;

@Controller
@RequestMapping("/prod")
public class ProdController {
	
	@Autowired
	ProdService prodSvc;
	
	@Autowired
	ProdSortService prodSortSvc;
	
	@GetMapping("addProd")
	public String addProd(ModelMap model) {
		ProdVO prodVO = new ProdVO();
		model.addAttribute("prodVO",prodVO);
		return "pages/users/addProd";
	}
	
	
//	@GetMapping("listAllProd")
//	public String listAllProd(@PageableDefault(size = 9, sort = "prodId", direction = Sort.Direction.ASC) Pageable pageable, ModelMap model) {
//		Slice<ProdDTO> prodList = prodSvc.getAllProds(pageable);
//		
//		model.addAttribute("prodList", prodList);
//		model.addAttribute("prodSortList", prodSortSvc.getAll());
//		return "pages/user/prod-list";
//	}
	
	
	
	@GetMapping("listAllProd")
	public String listAllProd(ModelMap model) {
		model.addAttribute("prodList", prodSvc.getAllProds());
		model.addAttribute("prodSortList", prodSortSvc.getAll());
		return "pages/user/prod-list";
	}
	
	@GetMapping("listOneProd")
	public String listOneProd(ModelMap model) {
		return "pages/user/prod-detail";
	}
	
	
	//單一查詢
	@GetMapping("getOne_For_Display")
	public String getOne_For_Display(
		/***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
		@RequestParam("prodId") String prodId,
		ModelMap model) {	
		/***************************2.開始查詢資料*********************************************/
		ProdVO prodVO = prodSvc.getOneProd(Integer.valueOf(prodId));
		List<ProdImageVO> imageList = prodSvc.getProdImagesByProdId(prodVO.getProdId());
		
		/***************************3.查詢完成,準備轉交(Send the Success view)*****************/
		model.addAttribute("prodVO", prodVO); 
		model.addAttribute("prodImages", imageList);
                                           
		return "pages/user/prod-detail"; 
	}
	
	@GetMapping("searchProds")
	public String searchProds(@RequestParam("prodNameLike") String s,ModelMap model) {
		model.addAttribute("prodList",prodSvc.searchProds(s));
		model.addAttribute("prodSortList", prodSortSvc.getAll());
		return "pages/user/prod-list";
	}
	
}
