package com.momento;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicPageController {

    @GetMapping("/organizer/apply")
    public String organizerApply() {
        return "pages/public/organizer-apply";
    }

    @GetMapping("/terms")
    public String terms() {
        return "pages/public/terms";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "pages/public/privacy";
    }

    @GetMapping("/article/list")
    public String articleList() {
        return "pages/public/article-list";
    }
}
