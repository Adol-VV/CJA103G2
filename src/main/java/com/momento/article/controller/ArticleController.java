package com.momento.article.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 引入 Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.momento.article.model.ArticleService;
import com.momento.article.model.ArticleVO;

@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    
    @GetMapping
    public String articleList(Model model) {
        // 1. 從 Service 取得所有文章
        List<ArticleVO> list = articleService.getAll();
        
        // 2. 將資料加入 Model，讓 Thymeleaf 可以讀取 "articleList"
        model.addAttribute("articleList", list);
        
        // 3. 導向頁面
        return "pages/public/article-list";
    }
}