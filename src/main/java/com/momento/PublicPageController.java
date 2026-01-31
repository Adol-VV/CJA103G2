package com.momento;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/member-terms")
    public String memberTerms() {
        return "pages/public/member-terms";
    }

    @Autowired
    private com.momento.organizer.model.OrganizerService organizerService;

    @Autowired
    private com.momento.event.model.EventService eventService;

    @GetMapping({ "/orginformation", "/orginformation/{id}" })
    public String organizer(
            @org.springframework.web.bind.annotation.PathVariable(name = "id", required = false) Integer id,
            @org.springframework.web.bind.annotation.RequestParam(name = "id", required = false) Integer queryId,
            Model model) {

        // 優先使用 PathVariable，其次使用 QueryParam (為了相容性)
        Integer organizerId = (id != null) ? id : queryId;

        if (organizerId == null) {
            // 如果沒給 ID，預設抓第一個主辦單位
            java.util.List<com.momento.organizer.model.OrganizerVO> actives = organizerService.getActiveOrganizers();
            if (!actives.isEmpty()) {
                organizerId = actives.get(0).getOrganizerId();
            } else {
                return "pages/public/organizer-profile";
            }
        }

        com.momento.organizer.model.OrganizerVO organizer = organizerService.getOrganizer(organizerId);
        if (organizer != null) {
            model.addAttribute("organizer", organizer);
            // 撈取主辦方活動 (Status 3 & 5)
            java.util.List<com.momento.event.dto.EventListItemDTO> events = eventService.getOrganizerEvents(organizerId,
                    100);
            model.addAttribute("events", events);
        }

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

    @GetMapping("/contact")
    public String contact() {
        return "pages/public/contact";
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("keyword", q);
        return "pages/public/search";
    }

    @GetMapping("/member/cart")
    public String prodCart(Model model) {
        return "pages/user/cart";
    }

    @GetMapping("/member/checkout")
    public String prodCheckout(Model model) {
        return "pages/user/checkout";
    }

    /**
     * AJAX 搜尋主辦方 API (公用)
     */
    @GetMapping("/orginformation/api/search")
    @org.springframework.web.bind.annotation.ResponseBody
    public java.util.List<com.momento.organizer.dto.OrganizerSearchDTO> searchOrganizersJson(
            @RequestParam String keyword) {
        return organizerService.searchActiveOrganizers(keyword);
    }

}
