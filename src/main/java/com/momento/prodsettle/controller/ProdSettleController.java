package com.momento.prodsettle.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.momento.organizer.model.OrganizerVO;
import com.momento.prodsettle.model.ProdSettleService;
import com.momento.prodsettle.model.ProdSettleVO;
import com.momento.prodsettledetail.model.ProdSettleDetailVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/organizer/prod_settle")
public class ProdSettleController {

	@Autowired
	ProdSettleService psSev;
	
	@GetMapping("/getAllSettlementByOrganizerId")
	public String getAllSettlementByOrganizerId(Model model, HttpSession session) {
		OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");

		if (organizer != null) {
			List<ProdSettleVO> Settlements = psSev.getByOrganizerId(organizer.getOrganizerId());
			Integer totalDetailSales = Settlements.stream()
				    .flatMap(main -> main.getProdSettles().stream())
				    .mapToInt(ProdSettleDetailVO::getProdSales)  
				    .sum();
			
			model.addAttribute("trueTotalPayable", Math.round(totalDetailSales * 0.95));
			model.addAttribute("ProdSettlements", Settlements);
		}
		return "pages/organizer/partials/panel-settlement";
	}
}
