package com.momento.organizer.controller;

import com.momento.article.model.ArticleService;
import com.momento.article.model.ArticleVO;
import com.momento.articleimage.model.ArticleImageVO;
import com.momento.notify.model.OrganizerNotifyService;
import com.momento.notify.model.OrganizerNotifyVO;
import com.momento.notify.model.SystemNotifyService;
import com.momento.notify.model.SystemNotifyVO;
import com.momento.organizer.model.OrganizerService;
import com.momento.organizer.model.OrganizerVO;
import com.momento.prod.dto.ProdDTO;
import com.momento.prod.model.ProdService;
import com.momento.prod.model.ProdSortService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/organizer")
public class OrganizerCenterController {

    @Autowired
    private OrganizerService organizerService;

    @Autowired
    private ProdService prodSvc;

    @Autowired
    private ProdSortService prodSortSvc;

    @Autowired
    private ArticleService articleSvc;

    @Autowired
    private com.momento.eventmanage.model.EventManageService eventManageService;

    @Autowired
    private SystemNotifyService sysNotifySvc;

    @Autowired
    private OrganizerNotifyService orgNotifySvc;

    @GetMapping("/login")
    public String showLoginPage() {
        return "pages/organizer/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String account,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        // 查詢主辦方
        OrganizerVO organizer = organizerService.findByAccount(account);

        if (organizer != null && organizer.getPassword().equals(password)) {
            // 檢查狀態
            if (organizer.getStatus() == 0) {
                model.addAttribute("errorMsg", "您的申請尚在審核中，請耐心等候");
                model.addAttribute("savedAccount", account);
                return "pages/organizer/login";
            } else if (organizer.getStatus() == 2) {
                model.addAttribute("errorMsg", "您的帳號已被停權，請聯繫客服");
                model.addAttribute("savedAccount", account);
                return "pages/organizer/login";
            }

            // 登入成功（狀態 = 1）
            session.setAttribute("loginOrganizer", organizer);
            return "redirect:/organizer/dashboard";

        } else if (organizer == null) {
            model.addAttribute("accountMsg", "此帳號不存在");
            return "pages/organizer/login";
        } else {
            model.addAttribute("passwordMsg", "密碼錯誤");
            model.addAttribute("savedAccount", account);
            return "pages/organizer/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }
        model.addAttribute("organizer", organizer);
        model.addAttribute("prodSortList", prodSortSvc.getAll());
        if (!model.containsAttribute("prodList")) {
            model.addAttribute("prodList", prodSvc.getProdsByOrg(organizer.getOrganizerId()));
        }

        // 抓通知資料
        List<OrganizerNotifyVO> orgNotifyList = orgNotifySvc.getByOrgId(organizer.getOrganizerId());
        List<SystemNotifyVO> sysNotifyList = sysNotifySvc.getByOrgId(organizer.getOrganizerId());
        if (orgNotifyList == null) orgNotifyList = new ArrayList<>();
        if (sysNotifyList == null) sysNotifyList = new ArrayList<>();

        // 主辦方"接收"通知
        List<OrganizerNotifyVO> receivedNotifies = orgNotifyList.stream()
                .filter(n -> n.getEmpVO() != null || (n.getTitle() != null && n.getTitle().contains("訂單")))
                .toList();
        // 主辦方"發送"的紀錄
        List<OrganizerNotifyVO> sentNotifies = orgNotifyList.stream()
                .filter(n -> n.getEmpVO() == null && (n.getTitle() != null && !n.getTitle().contains("訂單")))
                .toList();

        // 合併所有通知並按時間排序 (新到舊)
        List<Map<String, Object>> allNotifiesNormalized = new ArrayList<>();

        // 加入主辦方接收的通知
        for (OrganizerNotifyVO n : receivedNotifies) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", n.getOrganizerNotifyId());
            map.put("title", n.getTitle());
            map.put("content", n.getContent());
            map.put("createdAt", n.getCreatedAt());
            map.put("isRead", n.getIsRead());
            map.put("type", n.getEmpVO() == null ? "MEMBER" : "PLATFORM");
            map.put("notifyType", "ORG"); // 用於區分 API 路徑
            map.put("sourceName", n.getEmpVO() == null ? "會員" : "平台");
            allNotifiesNormalized.add(map);
        }

        // 加入系統公告
        for (SystemNotifyVO s : sysNotifyList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getSystemNotifyId());
            map.put("title", s.getTitle());
            map.put("content", s.getContent());
            map.put("createdAt", s.getCreatedAt());
            map.put("isRead", s.getIsRead());
            map.put("type", "PLATFORM");
            map.put("notifyType", "SYS"); // 用於區分 API 路徑
            map.put("sourceName", "Momento 官方");
            allNotifiesNormalized.add(map);
        }

        // 依照時間由新到舊排序
        allNotifiesNormalized.sort((a, b) -> {
            java.time.LocalDateTime t1 = (java.time.LocalDateTime) a.get("createdAt");
            java.time.LocalDateTime t2 = (java.time.LocalDateTime) b.get("createdAt");
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return t2.compareTo(t1); // DESC 排序
        });

        // 計算數量
        long memberCount = receivedNotifies.stream().filter(n -> n.getEmpVO() == null).count();
        long platformCount = sysNotifyList.size() + receivedNotifies.stream().filter(n -> n.getEmpVO() != null).count();
        long totalCount = memberCount + platformCount;

        // 計算未讀數量
        long unreadNotifyCount = sysNotifyList.stream().filter(n -> n.getIsRead() == 0).count() +
                receivedNotifies.stream().filter(n -> n.getIsRead() == 0).count();

        model.addAttribute("allNotifies", allNotifiesNormalized); // 合併排序後的通知列表
        model.addAttribute("receivedNotifyList", receivedNotifies); // 給通知中心遍歷
        model.addAttribute("sentNotifyHistoryList", sentNotifies);    // 給已發送紀錄遍歷
        model.addAttribute("sysAnnouncementList", sysNotifyList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("platformCount", platformCount);
        model.addAttribute("memberCount", memberCount);
        model.addAttribute("unreadNotifyCount", unreadNotifyCount);
        model.addAttribute("organizerNotifyVO", new OrganizerNotifyVO());

        // 載入文章列表
        model.addAttribute("articleList", articleSvc.getArticlesByOrganizer(organizer.getOrganizerId()));

        // 載入主辦方的活動列表 (用於通知會員下拉選單)
        org.springframework.data.domain.Page<com.momento.event.model.EventVO> eventPage = eventManageService
                .getOrganizerEvents(organizer.getOrganizerId(), null, null,
                        org.springframework.data.domain.PageRequest.of(0, 100));
        model.addAttribute("eventList", eventPage.getContent());

        // 載入統計數據 (您的統計功能)
        com.momento.eventmanage.dto.EventStatsDTO stats = eventManageService
                .getOrganizerStats(organizer.getOrganizerId());
        model.addAttribute("organizerStats", stats);

        // 隨機產生本月增長數字 (您的展示功能)
        int randomTrend = (int) (Math.random() * 5) + 1;
        model.addAttribute("randomTrendMonth", "+" + randomTrend);

        // 使用新版 ProdDTO 對應前端表單
        model.addAttribute("prod", new ProdDTO());
        return "pages/organizer/dashboard";
    }

    @GetMapping("/events")
    public String events(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/events";
    }

    @GetMapping("/products")
    public String products(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/products";
    }

    @GetMapping("/orders")
    public String orders(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/orders";
    }

    @GetMapping("/settlements")
    public String settlements(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/settlements";
    }

    /* 數據分析頁面（需要登入） */

    @GetMapping("/analytics")
    public String analytics(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/analytics";
    }

    /* 帳戶設定頁面（需要登入） */

    @GetMapping("/settings")
    public String settings(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/settings";
    }

    /* 更新基本資料 */
    @PostMapping("/settings/update")
    public String updateBasicInfo(
            @RequestParam String name,
            @RequestParam String ownerName,
            @RequestParam(required = false) String phone,
            @RequestParam String email,
            @RequestParam(required = false) String introduction,
            HttpSession session,
            Model model) {

        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }

        try {
            // 更新資料
            organizer.setName(name);
            organizer.setOwnerName(ownerName);
            organizer.setPhone(phone);
            organizer.setEmail(email);
            organizer.setIntroduction(introduction);

            // 儲存到資料庫
            OrganizerVO updated = organizerService.updateOrganizer(organizer);

            // 更新 Session
            session.setAttribute("loginOrganizer", updated);

            // TODO: 顯示成功訊息
            model.addAttribute("successMsg", "基本資料已更新");

        } catch (Exception e) {
            model.addAttribute("errorMsg", "更新失敗: " + e.getMessage());
        }

        return "redirect:/organizer/dashboard#settings";
    }

    /* 更新銀行帳戶資訊 */
    @PostMapping("/settings/update_bank")
    public String updateBankInfo(
            @RequestParam String bankCode,
            @RequestParam String bankAccount,
            @RequestParam String accountName,
            HttpSession session,
            Model model) {

        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }

        try {
            // 驗證銀行代碼格式 (3碼數字)
            if (!bankCode.matches("^\\d{3}$")) {
                model.addAttribute("errorMsg", "銀行代碼格式錯誤,應為3碼數字");
                return "redirect:/organizer/dashboard#settings";
            }

            // 更新銀行資訊
            organizer.setBankCode(bankCode);
            organizer.setBankAccount(bankAccount);
            organizer.setAccountName(accountName);

            // 儲存到資料庫
            OrganizerVO updated = organizerService.updateOrganizer(organizer);

            // 更新 Session
            session.setAttribute("loginOrganizer", updated);

            // TODO: 顯示成功訊息
            model.addAttribute("successMsg", "銀行資訊已更新");

        } catch (Exception e) {
            model.addAttribute("errorMsg", "更新失敗: " + e.getMessage());
        }

        return "redirect:/organizer/dashboard#settings";
    }

    /* 變更密碼 */
    @PostMapping("/settings/change_password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {

        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }

        try {
            // 驗證目前密碼
            if (!organizer.getPassword().equals(currentPassword)) {
                model.addAttribute("errorMsg", "目前密碼錯誤");
                return "redirect:/organizer/dashboard#settings";
            }

            // 驗證新密碼與確認密碼是否一致
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("errorMsg", "新密碼與確認密碼不一致");
                return "redirect:/organizer/dashboard#settings";
            }

            // 驗證新密碼長度
            if (newPassword.length() < 8) {
                model.addAttribute("errorMsg", "新密碼至少需要8個字元");
                return "redirect:/organizer/dashboard#settings";
            }

            // 更新密碼
            organizer.setPassword(newPassword);
            // TODO: 應該要加密密碼
            // organizer.setPassword(passwordEncoder.encode(newPassword));

            // 儲存到資料庫
            OrganizerVO updated = organizerService.updateOrganizer(organizer);

            // 更新 Session
            session.setAttribute("loginOrganizer", updated);

            // TODO: 顯示成功訊息
            model.addAttribute("successMsg", "密碼已變更");

        } catch (Exception e) {
            model.addAttribute("errorMsg", "變更失敗: " + e.getMessage());
        }

        return "redirect:/organizer/dashboard#settings";
    }

    // 商品列表裡面的搜尋商品
    @PostMapping("/orgSearchProds")
    public String orgSearchProds(@RequestParam("prodNameLike") String s, HttpSession session, RedirectAttributes ra) {
        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }
        ra.addFlashAttribute("prodList", prodSvc.orgSearchProds(organizer.getOrganizerId(), s));

        return "redirect:/organizer/dashboard#product-list";
    }

    // 變更商品上下架狀態
    @PostMapping("/changeProdStatus")
    public String changeReviewStatus(@RequestParam("prodId") Integer prodId,
            @RequestParam("prodStatus") Byte prodStatus) {
        prodSvc.updateProdStatus(prodId, prodStatus);
        return "redirect:/organizer/dashboard#product-list";
    }

    // 新增商品 (結合上傳功能與主辦方ID綁定)
    @PostMapping("/addProd")
    public String addProd(@Valid ProdDTO prodDTO, HttpSession session,
            @RequestParam("imageFiles") MultipartFile[] files) {
        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }

        // 綁定主辦方ID以確保安全，並呼叫支援多圖上傳的 Service 方法
        prodDTO.setOrganizerId(organizer.getOrganizerId());
        prodSvc.addProd(prodDTO, files);

        return "redirect:/organizer/dashboard#product-list";
    }
    
    //更新商品
    @PostMapping("/updateProd")
    public String updateProd(@Valid ProdDTO prodDTO, HttpSession session,
            @RequestParam("imageFiles") MultipartFile[] files) {
        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }

        prodDTO.setOrganizerId(organizer.getOrganizerId());
        prodSvc.addProd(prodDTO, files);

        return "redirect:/organizer/dashboard#product-list";
    }
    
    // 新增文章
    @PostMapping("/article/create")
    @ResponseBody
    public ResponseEntity<?> createArticle(@RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String imageUrl,
            HttpSession session) {
        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "請先登入"));
        }

        try {
            ArticleVO article = new ArticleVO();
            article.setTitle(title);
            article.setContent(content);
            article.setOrganizerVO(organizer);
            article.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            article.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                ArticleImageVO image = new ArticleImageVO();
                image.setImageUrl(imageUrl);
                image.setArticleVO(article);
                image.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                article.getArticleImages().add(image);
            }

            articleSvc.addArticle(article);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "新增失敗: " + e.getMessage()));
        }
    }

    // 取得單筆文章資料 (JSON)
    @GetMapping("/article/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getArticleData(@PathVariable Integer id, HttpSession session) {
        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "請先登入"));
        }

        ArticleVO article = articleSvc.getOneArticle(id);
        if (article == null || !article.getOrganizerVO().getOrganizerId().equals(organizer.getOrganizerId())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "找不到文章或無權限"));
        }

        // 手動構建 JSON Map 以避免循環參照 VO -> Organizer -> List<Article>
        Map<String, Object> data = new HashMap<>();
        data.put("articleId", article.getArticleId());
        data.put("title", article.getTitle());
        data.put("content", article.getContent());

        // 圖片
        if (!article.getArticleImages().isEmpty()) {
            data.put("imageUrl", article.getArticleImages().get(0).getImageUrl());
        } else {
            data.put("imageUrl", "");
        }

        return ResponseEntity.ok(data);
    }

    // 更新文章
    @PostMapping("/article/update")
    @ResponseBody
    public ResponseEntity<?> updateArticle(@RequestParam Integer articleId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String imageUrl,
            HttpSession session) {
        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "請先登入"));
        }

        try {
            ArticleVO article = articleSvc.getOneArticle(articleId);
            if (article == null || !article.getOrganizerVO().getOrganizerId().equals(organizer.getOrganizerId())) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "找不到文章或無權限"));
            }

            article.setTitle(title);
            article.setContent(content);
            article.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            // 更新圖片邏輯
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                if (!article.getArticleImages().isEmpty()) {
                    // 如果原本有圖，更新第一張圖的 URL
                    ArticleImageVO image = article.getArticleImages().get(0);
                    image.setImageUrl(imageUrl);
                } else {
                    // 如果原本沒有圖，新增一張
                    ArticleImageVO image = new ArticleImageVO();
                    image.setImageUrl(imageUrl);
                    image.setArticleVO(article);
                    image.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    article.getArticleImages().add(image);
                }
            } else {
                // 如果傳入空值，視為要刪除圖片？目前 UI 是必填，但為了健壯性，若空則清除所有圖片
                article.getArticleImages().clear();
            }

            articleSvc.updateArticle(article);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "更新失敗: " + e.getMessage()));
        }
    }

    // 刪除文章
    @PostMapping("/article/delete")
    @ResponseBody
    public ResponseEntity<?> deleteArticle(@RequestParam Integer articleId, HttpSession session) {
        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "請先登入"));
        }

        try {
            ArticleVO article = articleSvc.getOneArticle(articleId);
            if (article == null || !article.getOrganizerVO().getOrganizerId().equals(organizer.getOrganizerId())) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "找不到文章或無權限"));
            }

            articleSvc.deleteArticle(articleId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "刪除失敗: " + e.getMessage()));
        }
    }

    // 進入商品編輯頁面
    @PostMapping("/prodEdit")
    public String prodEdit(@SessionAttribute("loginOrganizer") OrganizerVO organizer, Integer prodId, ModelMap model) {
        if (organizer == null) {
            return "redirect:/organizer/login";
        }
        model.addAttribute("prod", prodSvc.getOneProd(prodId));
        model.addAttribute("prodSortList", prodSortSvc.getAll());
        return "pages/organizer/product-edit";
    }

    // 進入商品列表頁面
    @GetMapping("/goToProdList")
    public String goToProdList() {
        return "redirect:/organizer/dashboard#product-list";
    }

    @PostMapping("/dashboard/notifications/mark-read")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAsRead(@RequestParam Integer notifyId, HttpSession session) {
        Map<String, Object> response = new java.util.HashMap<>();
        try {
            if (session.getAttribute("loginOrganizer") == null) {
                response.put("success", false);
                response.put("message", "請先登入");
                return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body(response);
            }
            sysNotifySvc.updateReadStatus(notifyId, 1);
            response.put("success", true);
            response.put("message", "單則通知已讀成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "操作失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/dashboard/notifications/mark-all-read")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> markAllAsRead(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrganizerVO loginOrganizer = (OrganizerVO) session.getAttribute("loginOrganizer");
            if (loginOrganizer == null) {
                response.put("success", false);
                response.put("message", "連線逾時，請重新登入");
                return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body(response);
            }
            sysNotifySvc.markAllAsReadForOrg(loginOrganizer.getOrganizerId());

            response.put("success", true);
            response.put("message", "所有通知已標記為已讀");
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批次更新失敗: " + e.getMessage());
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
