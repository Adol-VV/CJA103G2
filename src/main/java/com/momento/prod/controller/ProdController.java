package com.momento.prod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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
	
	
	
}
