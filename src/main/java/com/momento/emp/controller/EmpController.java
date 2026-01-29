package com.momento.emp.controller;

import com.momento.emp.model.BackendFunctionVO;
import com.momento.emp.model.EmpAuthorityVO;
import com.momento.emp.model.EmpService;
import com.momento.emp.model.EmpVO;
import com.momento.message.model.MessageService;
import com.momento.notify.model.SystemNotifyService;
import com.momento.prod.dto.ProdDTO;
import com.momento.prod.model.ProdService;
import com.momento.prod.model.ProdSortService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class EmpController {

    @Autowired
    private EmpService empSvc;

    @Autowired
    private ProdService prodSvc;

    @Autowired
    private ProdSortService prodSortSvc;

    @Autowired
    private SystemNotifyService systemNotifyService;

    @Autowired
    private MessageService messageService;

    /**
     * 顯示登入頁面
     */
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // 如果已經登入,直接導向後台首頁
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        if (loginEmp != null) {
            return "redirect:/admin/dashboard";
        }
        return "pages/admin/login";
    }

    /* 處理登入請求 */

    @PostMapping("/login")
    public String login(
            @RequestParam("account") String account,
            @RequestParam("password") String password,
            HttpSession session,
            ModelMap model) {

        // 查詢員工
        EmpVO emp = empSvc.getEmployeeByAccount(account);

        if (emp != null && emp.getPassword().equals(password)) {
            // 檢查狀態
            if (emp.getStatus() == null || emp.getStatus() == 0) {
                model.addAttribute("errorMsg", "此帳號已被停用,請聯繫管理員");
                model.addAttribute("savedAccount", account);
                return "pages/admin/login";
            }

            // 登入成功 (狀態 = 1)
            session.setAttribute("loginEmp", emp);
            session.setAttribute("isSuperAdmin", empSvc.isSuperAdmin(emp.getEmpId()));
            return "redirect:/admin/dashboard";

        } else if (emp == null) {
            model.addAttribute("accountMsg", "此帳號不存在");
            return "pages/admin/login";
        } else {
            model.addAttribute("passwordMsg", "密碼錯誤");
            model.addAttribute("savedAccount", account);
            return "pages/admin/login";
        }
    }

    /* 登出 */

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 清除所有 session
        return "redirect:/admin/login";
    }

    /* 後台首頁 (Dashboard) */

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, ModelMap model) {
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        if (loginEmp == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("loginEmp", loginEmp);
        model.addAttribute("isSuperAdmin", empSvc.isSuperAdmin(loginEmp.getEmpId()));
        model.addAttribute("prodSortList", prodSortSvc.getAll());

        // 獲取權限列表
        List<EmpAuthorityVO> authorities = empSvc.getEmployeePermissions(loginEmp.getEmpId());
        model.addAttribute("authorities", authorities);

        // 提取權限 ID 集合以便前端比對 (修正為 java.util.stream.Collectors)
        java.util.Set<Integer> activeFunctionIds = authorities.stream()
                .map(EmpAuthorityVO::getFunctionId)
                .collect(java.util.stream.Collectors.toSet());
        model.addAttribute("activeFunctionIds", activeFunctionIds);

        // 員工管理所需的列表
        List<EmpVO> employeeList = empSvc.getAllEmployees();
        List<BackendFunctionVO> functionList = empSvc.getAllFunctions();

        model.addAttribute("employees", employeeList != null ? employeeList : new ArrayList<>());
        model.addAttribute("allFunctions", functionList != null ? functionList : new ArrayList<>());

        if (!model.containsAttribute("prodList")) {
            model.addAttribute("prodList", prodSvc.getAllProds());
        }

        // pei的
        model.addAttribute("messageNotifyRecords", systemNotifyService.getMessageNotifyRecords());

        // 留言管理：新增的留言 (Status = 1)
        model.addAttribute("newComments", messageService.getMessagesByStatus(1));

        return "pages/admin/dashboard";
    }

    // ========== 員工管理 ==========

    // 員工列表 (包含權限檢查 & 商品列表)
    @GetMapping("/listAllEmp")
    public String listAllEmp(ModelMap model, HttpSession session) {
        // 1. 檢查登入
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        if (loginEmp == null) {
            return "redirect:/admin/login"; // 導向登入頁面
        }

        // 2. 準備員工列表
        List<EmpVO> list = empSvc.getAllEmployees();
        model.addAttribute("empListData", list);

        // 3. 準備商品列表 (因應 index 需求)
        try {
            // 注意：ProdService.getAllProds() 此處假設回傳的是 List 或 Iterable。
            // 若回傳 Slice/Page，在 HTML th:each 中也能迭代，但屬性可能不同 (例如 .content)
            model.addAttribute("prodListData", prodSvc.getAllProds());
        } catch (Exception e) {
            System.err.println("取得商品列表失敗: " + e.getMessage());
            // 不阻擋員工列表顯示
        }

        // 4. 判斷權限 (控制前端顯示)
        boolean isSuper = empSvc.isSuperAdmin(loginEmp.getEmpId());
        model.addAttribute("isSuperAdmin", isSuper);
        model.addAttribute("loginEmp", loginEmp); // 傳入當前登入者資訊

        return "backend/emp/listAllEmp";
    }

    // 新增員工
    @PostMapping("/add")
    public String addEmp(EmpVO emp, HttpSession session, ModelMap model) {
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        if (loginEmp == null)
            return "redirect:/admin/login";

        try {
            empSvc.addEmployee(loginEmp.getEmpId(), emp);
        } catch (SecurityException e) {
            model.addAttribute("errorMessage", "無權限新增員工: " + e.getMessage());
            return listAllEmp(model, session);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "新增失敗: " + e.getMessage());
            return listAllEmp(model, session);
        }

        return "redirect:/admin/listAllEmp";
    }

    // 修改員工
    @PostMapping("/update")
    public String updateEmp(EmpVO emp, HttpSession session, ModelMap model) {
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        if (loginEmp == null)
            return "redirect:/admin/login";

        try {
            empSvc.updateEmployee(loginEmp.getEmpId(), emp);
        } catch (SecurityException e) {
            model.addAttribute("errorMessage", "無權限修改員工: " + e.getMessage());
            return listAllEmp(model, session);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "修改失敗: " + e.getMessage());
            return listAllEmp(model, session);
        }

        return "redirect:/admin/listAllEmp";
    }

    // 刪除員工
    @PostMapping("/delete")
    public String deleteEmp(@RequestParam("empId") Integer empId, HttpSession session, ModelMap model) {
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        if (loginEmp == null)
            return "redirect:/admin/login";

        try {
            empSvc.deleteEmployee(loginEmp.getEmpId(), empId);
        } catch (SecurityException e) {
            model.addAttribute("errorMessage", "無權限刪除員工: " + e.getMessage());
            return listAllEmp(model, session);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "刪除失敗: " + e.getMessage());
            return listAllEmp(model, session);
        }

        return "redirect:/admin/listAllEmp";
    }

    // 商品審核裡面的搜尋商品
    @PostMapping("/searchProds")
    public String searchProds(@RequestParam("prodNameLike") String s, HttpSession session, RedirectAttributes ra) {
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        if (loginEmp == null) {
            return "redirect:/admin/login";
        }

        ra.addFlashAttribute("prodList", prodSvc.searchProds(s));

        return "redirect:/admin/dashboard#product-approval";
    }

    // 審核商品詳頁
    @GetMapping("/api/getOneProd")
    @ResponseBody
    public ProdDTO getOneProd(@RequestParam("prodId") String prodId) {

        ProdDTO prod = prodSvc.getOneProd(Integer.valueOf(prodId));

        return prod;
    }

    // 變更商品審核狀態
    @PostMapping("/changeReviewStatus")
    public String changeReviewStatus(@RequestParam("prodId") Integer prodId,
            @RequestParam("reviewStatus") Byte reviewStatus) {
        System.out.println("收到 prodId: " + prodId + ", 收到狀態: " + reviewStatus);
        prodSvc.updateProdReviewStatus(prodId, reviewStatus);
        return "redirect:/admin/dashboard#product-approval";
    }
}
