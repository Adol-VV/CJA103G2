package com.momento.organizer.controller;

import com.momento.emp.model.EmpVO;
import com.momento.organizer.model.OrganizerService;
import com.momento.organizer.model.OrganizerVO;
import com.momento.prod.model.ProdService;
import com.momento.prod.model.ProdSortService;
import com.momento.prod.model.ProdVO;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/organizer")
public class OrganizerCenterController {

    @Autowired
    private OrganizerService organizerService;

    @Autowired
    private ProdService prodSvc;
    
    @Autowired
    private ProdSortService prodSortSvc;


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
		model.addAttribute("prodVO",new ProdVO());
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

    /* 數據分析頁面（需要登入）*/

    @GetMapping("/analytics")
    public String analytics(HttpSession session) {
        if (session.getAttribute("loginOrganizer") == null) {
            return "redirect:/organizer/login";
        }
        return "pages/organizer/analytics";
    }

    /* 帳戶設定頁面（需要登入）*/

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
    
    //商品列表裡面的搜尋商品
	@PostMapping("/orgSearchProds")
	public String orgSearchProds(@RequestParam("prodNameLike") String s, HttpSession session, RedirectAttributes ra) {
        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }
		ra.addFlashAttribute("prodList",prodSvc.orgSearchProds(organizer.getOrganizerId(),s));

		return "redirect:/organizer/dashboard#product-list";
	}
	
	//變更商品上下架狀態
	@PostMapping("/changeProdStatus")
	public String changeReviewStatus(@RequestParam("prodId") Integer prodId, @RequestParam("prodStatus") Byte prodStatus) {
		prodSvc.updateProdStatus(prodId, prodStatus);
		return "redirect:/organizer/dashboard#product-list";
	}
	
	//新增商品
	@PostMapping("/addProd")
	public String addProd(@Valid ProdVO prodVO, HttpSession session) {
        OrganizerVO organizer = (OrganizerVO) session.getAttribute("loginOrganizer");
        if (organizer == null) {
            return "redirect:/organizer/login";
        }
        prodVO.getOrganizerVO().setOrganizerId(organizer.getOrganizerId());
        prodVO.getEmpVO().setEmpId(8);
        prodVO.setCreatedAt(LocalDateTime.now());
        prodVO.setUpdatedAt(LocalDateTime.now());
        prodVO.setProdStatus((byte) 0);
        prodVO.setReviewStatus((byte) 0);
        
        
        prodSvc.addProd(prodVO);
        return "redirect:/organizer/dashboard#product-list";
	}
}
