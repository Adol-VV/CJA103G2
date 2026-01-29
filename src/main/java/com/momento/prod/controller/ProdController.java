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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.momento.member.model.MemberVO;
import com.momento.prod.dto.ProdDTO;
import com.momento.prod.model.ProdFavService;
import com.momento.prod.model.ProdFavVO;
import com.momento.prod.model.ProdImageVO;
import com.momento.prod.model.ProdService;
import com.momento.prod.model.ProdSortService;
import com.momento.prod.model.ProdSortVO;
import com.momento.prod.model.ProdVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/prod")
public class ProdController {
	
	@Autowired
	ProdService prodSvc;
	
	@Autowired
	ProdSortService prodSortSvc;
	
	@Autowired
	ProdFavService prodFavSvc;
	
	@GetMapping("/addProd")
	public String addProd(ModelMap model) {
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
		ModelMap model, HttpSession session) {	
		/***************************2.開始查詢資料*********************************************/
		ProdDTO prod = prodSvc.getOneProd(Integer.valueOf(prodId));
		
		/***************************3.查詢完成,準備轉交(Send the Success view)*****************/
		model.addAttribute("prod", prod);
		//檢查該會員是否有收藏這個商品
		MemberVO member = (MemberVO) session.getAttribute("loginMember");
		if(member != null) {
			boolean isFav = prodFavSvc.favProdsByMember(member.getMemberId()).stream().anyMatch(favProd -> favProd.getProdId()==prod.getProdId());
			model.addAttribute("isFav", isFav);
		}
		return "pages/user/prod-detail"; 
	}
	
	@GetMapping("searchProds")
	public String searchProds(@RequestParam("prodNameLike") String s,ModelMap model) {
		model.addAttribute("prodList",prodSvc.searchProds(s));
		model.addAttribute("prodSortList", prodSortSvc.getAll());
		return "pages/user/prod-list";
	}
	
	@GetMapping("/member/prod/getOne")
	public ProdDTO getOne(@RequestParam Integer prodId) {
	    return prodSvc.getOneProd(prodId);
	}
	
	
	@PostMapping("/addFav")
	public String addFav(HttpSession session, @RequestParam("prodId") Integer prodId) {
		MemberVO member = (MemberVO) session.getAttribute("loginMember");
		if (member == null) {
            return "pages/user/login";
        }
		ProdFavVO prodFav = new ProdFavVO();
		ProdVO prod = new ProdVO();
		prod.setProdId(prodId);
		
		prodFav.setMemberVO(member);
		prodFav.setProdVO(prod);
		prodFavSvc.addFavProd(prodFav);
		
		return "redirect:/prod/getOne_For_Display?prodId="+ prodId;
	}
	
	@PostMapping("/removeFav")
	public String removeFav(HttpSession session, @RequestParam("prodId") Integer prodId){
		MemberVO member = (MemberVO) session.getAttribute("loginMember");
		if (member == null) {
            return "pages/user/login";
        }
		
		prodFavSvc.removeFavProd(member.getMemberId(),prodId);
		return "redirect:/prod/getOne_For_Display?prodId="+ prodId;
	}
}
