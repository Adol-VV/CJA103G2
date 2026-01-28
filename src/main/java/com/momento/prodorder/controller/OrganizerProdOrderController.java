package com.momento.prodorder.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.momento.prodorder.model.ProdOrderIdService;
import com.momento.prodorder.model.ProdOrderIdVO;
import com.momento.organizer.model.OrganizerVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/organizer/prod_order")
public class OrganizerProdOrderController {

	@Autowired
	ProdOrderIdService poIdSev;

	@GetMapping("/getAllOrderByOrganizerId")
	public String getAllOrderByOrganizerId(Model model, HttpSession session) {
		OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");

		if (organizer != null) {
			List<ProdOrderIdVO> orders = poIdSev.getByOrganizerId(organizer.getOrganizerId());
			model.addAttribute("allOrganizerProdOrder", orders);
			System.out.println("===========================================");
			System.out.println("主辦方ID: " + organizer.getOrganizerId());
			System.out.println("訂單數量: " + orders.size());
		}
		return "pages/organizer/partials/panel-product-orders";
	}
}
