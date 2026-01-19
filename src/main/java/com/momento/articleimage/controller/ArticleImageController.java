package com.momento.articleimage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.momento.articleimage.model.ArticleImageService;
import com.momento.articleimage.model.ArticleImageVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/articleimage")
public class ArticleImageController {

	@Autowired
	private ArticleImageService articleImageService;

	@GetMapping("/listAll")
	public String listAll(Model model) {
		List<ArticleImageVO> list = articleImageService.getAll();
		model.addAttribute("list", list);
		return "articleimage/listAll";
	}

	@PostMapping("/insert")
	public String insert(@Valid @ModelAttribute("articleImageVO") ArticleImageVO articleImageVO, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "articleimage/addPage";
		}
		articleImageService.addArticleImage(articleImageVO);
		return "redirect:/articleimage/listAll";
	}

	@PostMapping("/update")
	public String update(@Valid @ModelAttribute("articleImageVO") ArticleImageVO articleImageVO, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "articleimage/updatePage";
		}
		articleImageService.updateArticleImage(articleImageVO);
		return "redirect:/articleimage/listAll";
	}

	@PostMapping("/delete")
	public String delete(@RequestParam("articleImageId") Integer articleImageId) {
		articleImageService.deleteArticleImage(articleImageId);
		return "redirect:/articleimage/listAll";
	}

	@GetMapping("/getOne_For_Update")
	public String getOneForUpdate(@RequestParam("articleImageId") Integer articleImageId, Model model) {
		ArticleImageVO articleImageVO = articleImageService.getOneArticleImage(articleImageId);
		model.addAttribute("articleImageVO", articleImageVO);
		return "articleimage/updatePage";
	}
}