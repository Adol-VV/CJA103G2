package com.momento.prod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.momento.prod.model.ProdService;
import com.momento.prod.model.ProdVO;

@Controller
@RequestMapping("/prod")
public class ProdController {
	
	@Autowired
	ProdService prodSvc;
	
	@GetMapping("addProd")
	public String addProd(ModelMap model) {
		ProdVO prodVO = new ProdVO();
		model.addAttribute("prodVO",prodVO);
		return "pages/users/addProd";
	}
	
	
	@GetMapping("listAllProd")
	public String listAllProd(ModelMap model) {
		return "pages/user/prod-list";
	}
	
	@GetMapping("listOneProd")
	public String listOneProd(ModelMap model) {
		return "pages/user/prod-detail";
	}
	
	@ModelAttribute("prodListData")
	protected List<ProdVO> referenceListData() {
		// DeptService deptSvc = new DeptService();
		List<ProdVO> list = prodSvc.getAll();
		return list;
	}
	
	
	//單一查詢
	@GetMapping("getOne_For_Display")
	public String getOne_For_Display(
		/***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
//		@NotEmpty(message="員工編號: 請勿空白")
//		@Digits(integer = 4, fraction = 0, message = "員工編號: 請填數字-請勿超過{integer}位數")
//		@Min(value = 7001, message = "員工編號: 不能小於{value}")
//		@Max(value = 7777, message = "員工編號: 不能超過{value}")
		@RequestParam("prodId") String prodId,
		ModelMap model) {
		
		/***************************2.開始查詢資料*********************************************/
//		EmpService empSvc = new EmpService();
		ProdVO prodVO = prodSvc.getOneProd(Integer.valueOf(prodId));
		
		/***************************3.查詢完成,準備轉交(Send the Success view)*****************/
		model.addAttribute("prodVO", prodVO); // for1 --> listOneEmp.html 的第37~44行用
                                            // for2 --> select_page.html的第156用
		return "pages/user/prod-detail";  // 查詢完成後轉交select_page.html由其第158行insert listOneEmp.html內的th:fragment="listOneEmp-div
	}
	
	
	
}
