package com.momento.prod.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.momento.prod.model.ProdService;
import com.momento.prod.model.ProdVO;

@Controller
@RequestMapping("/prod")
public class ProdController {
	
	@Autowired
	ProdService prodSvc;
	
	public String addProd(ModelMap model) {
		ProdVO prodVO = new ProdVO();
		model.addAttribute("prodVO",prodVO);
		return "pages/prod/addProd";
	}
}
