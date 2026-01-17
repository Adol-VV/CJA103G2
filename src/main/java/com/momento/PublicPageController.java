package com.momento;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 公開頁面 Controller
 * 處理不需要登入就能訪問的頁面
 */
@Controller
public class PublicPageController {
    
    /**
     * 主辦方申請頁面
     * 訪問: http://localhost:8080/organizer/apply
     */
    @GetMapping("/organizer/apply")
    public String organizerApply() {
        return "pages/public/organizer-apply";
    }
    
    /**
     * 服務條款頁面
     * 訪問: http://localhost:8080/terms
     */
    @GetMapping("/terms")
    public String terms() {
        return "pages/public/terms";
    }
    
    /**
     * 隱私權政策頁面
     * 訪問: http://localhost:8080/privacy
     */
    @GetMapping("/privacy")
    public String privacy() {
        return "pages/public/privacy";
    }
}
