package com.momento.prodorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.momento.prodorder.model.ProdOrderIdService;
import com.momento.prodorder.model.ProdOrderIdVO;
import com.momento.prodorderitem.model.ProdOrderItemService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/member/prod_order")
public class ProdOrderIdController {
	
	@Autowired
	ProdOrderIdService poIdSev;
	
	@Autowired
	ProdOrderItemService poItemSev;
	
	@PostMapping("/insertOrder")
	@ResponseBody // 回傳純文字或 JSON，不轉導網頁
	public String insertOrder(@Valid @RequestBody ProdOrderIdVO prodOrderIdVO, BindingResult result, ModelMap model) {
		
		if(!result.hasErrors()) {
			poIdSev.addProdOrder(prodOrderIdVO);
			//poItemSev.addProdOrderItem(prodOrderItemVO);
			return "新增成功";
		}else {
			return "資料驗證失敗";
		}
	}
}
