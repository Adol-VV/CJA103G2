package com.momento.emp.controller;

import com.momento.emp.model.EmpAuthorityService;
import com.momento.emp.model.EmpAuthorityVO;
import com.momento.emp.model.EmpService;
import com.momento.emp.model.EmpVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/permissions")
public class EmpAuthorityController {

    @Autowired
    private EmpAuthorityService authorityService;

    @Autowired
    private EmpService empService;

    /**
     * 只有擁有「權限管理 (ID=9)」的人才能進入此頁面
     */
    @GetMapping
    public String showPermissionPage(HttpSession session, Model model) {
        EmpVO currentEmp = (EmpVO) session.getAttribute("empVO");

        // 1. 檢查是否有登入
        if (currentEmp == null) {
            return "redirect:/admin/login";
        }

        // 2. 檢查權限 (Plan A: 檢查是否有 functionId = 9)
        // 為了方便，我們假設 session 裡的 empVO 已經是最新的。
        // 如果 session 裡的不是最新的，建議重新從 DB 撈一次權限檢查，這裡暫時簡化。
        boolean isAdmin = false;

        // 【注意】這裡需要確認 EmpVO 裡面有沒有 authorities 這個 Set/List
        // 如果沒有，我們需要透過 service 查一下
        List<EmpAuthorityVO> myAuths = authorityService.getAuthorities(currentEmp.getEmpId());
        for (EmpAuthorityVO auth : myAuths) {
            if (auth.getFunctionId() == 9) {
                isAdmin = true;
                break;
            }
        }

        if (!isAdmin) {
            // 沒有權限，踢回首頁或顯示錯誤
            return "redirect:/admin/dashboard"; // 或是 return "error/403";
        }

        // 3. 準備頁面資料：列出所有員工 (除了已離職的?)
        List<EmpVO> allEmployees = empService.getAllEmployees();
        model.addAttribute("employees", allEmployees);

        return "pages/admin/partials/panel-staff-management"; // 對應
                                                              // templates/pages/admin/partials/panel-staff-management.html
    }

    /**
     * AJAX API: 取得某個員工目前的權限 ID 列表
     */
    @GetMapping("/get/{empId}")
    @ResponseBody
    public ResponseEntity<?> getEmployeePermissions(@PathVariable Integer empId) {
        List<EmpAuthorityVO> auths = authorityService.getAuthorities(empId);
        // 只回傳 Function ID 的 List 即可
        List<Integer> functionIds = auths.stream()
                .map(EmpAuthorityVO::getFunctionId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(functionIds);
    }

    /**
     * AJAX API: 更新權限
     */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updatePermissions(@RequestBody Map<String, Object> payload, HttpSession session) {
        // 安全檢查 (再次確認發送請求的人有權限)
        EmpVO currentEmp = (EmpVO) session.getAttribute("empVO");
        if (currentEmp == null)
            return ResponseEntity.status(401).body("請先登入");

        // 再次檢查是否擁有管理權限 (ID=9)
        List<EmpAuthorityVO> myAuths = authorityService.getAuthorities(currentEmp.getEmpId());
        boolean isAdmin = myAuths.stream().anyMatch(a -> a.getFunctionId() == 9);
        if (!isAdmin) {
            return ResponseEntity.status(403).body("無權限執行此操作");
        }

        try {
            Integer targetEmpId = Integer.parseInt(payload.get("empId").toString());
            List<Integer> functionIds = (List<Integer>) payload.get("functionIds");

            authorityService.updatePermissions(targetEmpId, functionIds);

            return ResponseEntity.ok(Map.of("success", true, "message", "權限更新成功！"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
