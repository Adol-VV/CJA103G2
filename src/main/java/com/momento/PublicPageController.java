package com.momento;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicPageController {

    @GetMapping("/user/login")
    public String userLogin() {
        return "pages/user/login";
    }

    @GetMapping("/terms")
    public String terms() {
        return "pages/public/terms";
    }

    @GetMapping("/orginformation")
    public String organizer() {
    	return "pages/public/organizer-profile";
    }
    
    @GetMapping("/privacy")
    public String privacy() {
        return "pages/public/privacy";
    }

    @GetMapping("/about")
    public String about() {
        return "pages/public/about";
    }

    @GetMapping("/faq")
    public String faq() {
        return "pages/public/faq";
    }

    @GetMapping("/member/cart")
    public String prodCart(Model model) {
        return "pages/user/cart";
    }

    @GetMapping("/member/checkout")
    public String prodCheckout(Model model) {
        return "pages/user/checkout";
    }
    
}
