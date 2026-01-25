package com.momento.prodorderitem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.momento.prodorderitem.model.ProdOrderItemService;
import com.momento.prodorderitem.model.ProdOrderItemVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/member/prod_order_item")
public class ProdOrderItemController {

	@Autowired
	ProdOrderItemService poItemSev;
	
	@PostMapping("/insertOrderItem")
	@ResponseBody // 回傳純文字或 JSON，不轉導網頁
	public String insertOrderItem(@Valid @RequestBody ProdOrderItemVO prodOrderItemVO,BindingResult result,ModelMap model) {
		if(!result.hasErrors()) {
			poItemSev.addProdOrderItem(prodOrderItemVO);
			return "新增成功";
		}else {
			return "資料驗證失敗";
		}
	}
}
