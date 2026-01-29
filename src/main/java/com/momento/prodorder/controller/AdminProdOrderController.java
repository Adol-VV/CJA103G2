package com.momento.prodorder.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.momento.prodorder.model.ProdOrderIdService;
import com.momento.prodorder.model.ProdOrderIdVO;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Admin/prod_order")
public class AdminProdOrderController {

	@Autowired
	ProdOrderIdService poIdSev;

	@GetMapping("/getAllOrder")
	public String getAllOrder(Model model, HttpSession session) {

		System.out.println("===================================");
		List<ProdOrderIdVO> orders = poIdSev.getAll();
		model.addAttribute("allProdOrders", orders);
		System.out.println("===================================");
		System.out.println("========="+ orders.size());
		System.out.println("===================================");
		return "pages/admin/partials/panel-product-orders";
	}
	
	@GetMapping("/orderStats")
	@ResponseBody
	public Map<String, Integer> getStats(Model model,HttpSession session) {
	    List<ProdOrderIdVO> orders = poIdSev.getAll();
	    Map<String, Integer> stats = new HashMap<>();
	    
	    int pending = (int) orders.stream().filter(o -> o.getStatus() == 0).count();
	    int processing = (int) orders.stream().filter(o -> o.getStatus() == 1).count();
	    int completed = (int) orders.stream().filter(o -> o.getStatus() == 2).count();
	    int refunding = (int) orders.stream().filter(o -> o.getStatus() == 3).count();
	    int cancelled = (int) orders.stream().filter(o -> o.getStatus() == 4).count();

	    stats.put("pending", (int) orders.stream().filter(o -> o.getStatus() == 0).count());
	    stats.put("processing", (int) orders.stream().filter(o -> o.getStatus() == 1).count());
	    stats.put("completed", (int) orders.stream().filter(o -> o.getStatus() == 2).count());
	    stats.put("refund", (int) orders.stream().filter(o -> o.getStatus() == 3).count());
	    stats.put("cancelled", (int) orders.stream().filter(o -> o.getStatus() == 4).count());
	    
	    return stats;
	}
}
