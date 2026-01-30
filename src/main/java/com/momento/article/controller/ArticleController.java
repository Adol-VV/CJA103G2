package com.momento.article.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 引入 Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.momento.article.model.ArticleRepository;
import com.momento.article.model.ArticleService;
import com.momento.article.model.ArticleVO;
import com.momento.member.model.MemberVO;
import com.momento.message.model.MessageService;
import com.momento.message.model.MessageVO;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import java.sql.Timestamp;
import java.util.Date;

@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MessageService messageService;

    @GetMapping
    public String articleList(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        // 1. 從 Service 取得分頁文章
        Page<ArticleVO> articlePage = articleService.getAll(page, size);

        // 2. 隨機挑選一篇置頂文章 (從全部文章中隨機挑選)
        List<ArticleVO> allArticles = articleService.getAll();
        if (!allArticles.isEmpty()) {
            int randomIndex = (int) (Math.random() * allArticles.size());
            model.addAttribute("featuredArticle", allArticles.get(randomIndex));
        }

        // 3. 將資料加入 Model
        model.addAttribute("articlePage", articlePage);

        // 4. 導向頁面
        return "pages/public/article-list";
    }

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Integer id, Model model) {
        // 1. 根據 ID 查詢文章
        ArticleVO article = articleService.getOneArticle(id);

        // 2. 查詢該文章的所有留言
        List<MessageVO> messages = messageService.getMessagesByArticleId(id);

        // 3. 將文章物件和留言列表放入 Model，讓 HTML 可以讀取
        model.addAttribute("article", article);
        model.addAttribute("messages", messages);

        // 4. 回傳 article-detail.html 模板
        return "pages/public/article-detail";
    }

    @PostMapping("/comment")
    public String addComment(@RequestParam Integer articleId,
            @RequestParam String content,
            HttpSession session) {

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");

        if (loginMember != null) {
            MessageVO message = new MessageVO();
            ArticleVO article = articleService.getOneArticle(articleId);

            message.setArticleVO(article);
            message.setMemberVO(loginMember);
            message.setContent(content);
            message.setStatus(1); // Default status: Active/Showing

            // Current time
            long now = new Date().getTime();
            message.setCreatedAt(new Timestamp(now));
            message.setUpdatedAt(new Timestamp(now));

            messageService.addMessage(message);
        }

        return "redirect:/article/detail?id=" + articleId;
    }

}