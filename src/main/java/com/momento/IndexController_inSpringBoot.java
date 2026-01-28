package com.momento;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.momento.article.model.ArticleRepository;
import com.momento.article.model.ArticleVO;
import com.momento.event.model.EventRepository;
import com.momento.event.model.TypeRepository;
import com.momento.event.model.TypeVO;
import com.momento.featured.model.FeaturedService;
import com.momento.prod.dto.ProdDTO;
import com.momento.prod.model.ProdService;

//@PropertySource("classpath:application.properties") // 於https://start.spring.io建立Spring Boot專案時, application.properties文件預設已經放在我們的src/main/resources 目錄中，它會被自動檢測到
@Controller
public class IndexController_inSpringBoot {

    // @Autowired (●自動裝配)(Spring ORM 課程)
    // 目前自動裝配了EmpService --> 供第66使用

    // 注入 ArticleRepository 以便在首頁撈取文章資料
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private FeaturedService featuredService;

    @Autowired
    private com.momento.event.model.EventService eventService;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ProdService prodSvc;

    // inject(注入資料) via application.properties
    @Value("${welcome.message}")
    private String message;

    private List<String> myList = Arrays.asList("Spring Boot Quickstart 官網 : https://start.spring.io", "IDE 開發工具",
            "直接使用(匯入)官方的 Maven Spring-Boot-demo Project + pom.xml",
            "直接使用官方現成的 @SpringBootApplication + SpringBootServletInitializer 組態檔",
            "依賴注入(DI) HikariDataSource (官方建議的連線池)", "Thymeleaf",
            "Java WebApp (<font color=red>快速完成 Spring Boot Web MVC</font>)");

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", message);
        model.addAttribute("myList", myList);

        List<FeaturedService.FeaturedCarouselDTO> carouselList = featuredService.getCarouselData();
        model.addAttribute("carouselList", carouselList);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<ArticleVO> page = articleRepository.findAll(pageable);

        // 將撈到的文章列表放入 Model，名稱為 "hotArticles"
        model.addAttribute("hotArticles", page.getContent());

        // 撈取最新活動 (5 筆)
        com.momento.event.dto.EventFilterDTO eventFilter = new com.momento.event.dto.EventFilterDTO();
        eventFilter.setPage(0);
        eventFilter.setSize(5);
        eventFilter.setSort("publishedAt");
        eventFilter.setDirection("DESC");
        org.springframework.data.domain.Page<com.momento.event.dto.EventListItemDTO> eventPage = eventService
                .filterEvents(eventFilter);
        model.addAttribute("latestEvents", eventPage.getContent());

        // 撈取活動類型及其數量
        List<TypeVO> types = typeRepository.findAll();
        List<TypeCountDTO> typeCounts = types.stream().map(type -> {
            long count = eventRepository.countByStatusAndReviewStatusAndType_TypeId((byte) 1, (byte) 1,
                    type.getTypeId());
            return new TypeCountDTO(type, count);
        }).collect(java.util.stream.Collectors.toList());
        model.addAttribute("eventTypes", typeCounts);
        
        //撈取最新活動(6筆)
        List<ProdDTO> latestProds = prodSvc.findLatestProds();
        model.addAttribute("latestProds", latestProds);       
        return "index"; // view
    }

    // 定義一個簡單的 DTO 用於首頁類型顯示
    public static class TypeCountDTO {
        private TypeVO type;
        private long count;
        private String iconClass;

        public TypeCountDTO(TypeVO type, long count) {
            this.type = type;
            this.count = count;
            this.iconClass = determineIcon(type.getTypeName());
        }

        private String determineIcon(String name) {
            if (name == null)
                return "fas fa-tag text-secondary";

            // 根據關鍵字分配不同的 Icon 與顏色
            if (name.contains("音樂") || name.contains("演唱"))
                return "fas fa-music text-success";
            if (name.contains("展覽") || name.contains("藝術"))
                return "fas fa-palette text-info";
            if (name.contains("表演") || name.contains("戲劇"))
                return "fas fa-theater-masks text-warning";
            if (name.contains("講座") || name.contains("研討"))
                return "fas fa-microphone text-info";
            if (name.contains("工坊") || name.contains("手作"))
                return "fas fa-hands text-danger";
            if (name.contains("影像") || name.contains("多媒體") || name.contains("電影"))
                return "fas fa-video text-secondary";
            if (name.contains("文化") || name.contains("節慶") || name.contains("民俗"))
                return "fas fa-landmark text-danger";
            if (name.contains("運動") || name.contains("休閒") || name.contains("賽事"))
                return "fas fa-trophy text-primary";
            if (name.contains("生活") || name.contains("風格"))
                return "fas fa-leaf text-success";
            if (name.contains("市集") || name.contains("購物"))
                return "fas fa-store text-warning";
            if (name.contains("旅遊") || name.contains("觀光"))
                return "fas fa-map-marked-alt text-success";
            if (name.contains("設計") || name.contains("創意"))
                return "fas fa-lightbulb text-warning";

            return "fas fa-tag text-secondary"; // 預設值
        }

        public TypeVO getType() {
            return type;
        }

        public long getCount() {
            return count;
        }

        public String getIconClass() {
            return iconClass;
        }
    }

    // http://......../hello?name=peter1
    @GetMapping("/hello")
    public String indexWithParam(
            @RequestParam(name = "name", required = false, defaultValue = "") String name, Model model) {
        model.addAttribute("message", name);
        return "index"; // view
    }

    // =========== 以下第63~75行是提供給
    // /src/main/resources/templates/back-end/emp/select_page.html 與 listAllEmp.html
    // 要使用的資料 ===================
    @GetMapping("/emp/select_page")
    public String select_page(Model model) {
        return "back-end/emp/select_page";
    }

}