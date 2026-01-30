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
     * 只有超級管理員才能進入此頁面
     */
    @GetMapping
    public String showPermissionPage(HttpSession session, Model model) {
        EmpVO currentEmp = (EmpVO) session.getAttribute("loginEmp");

        // 1. 檢查是否有登入
        if (currentEmp == null) {
            return "redirect:/admin/login";
        }

        // 2. 檢查是否為超級管理員
        if (!empService.isSuperAdmin(currentEmp.getEmpId())) {
            return "redirect:/admin/dashboard"; // 無權限，踢回首頁
        }

        // 3. 準備頁面資料：列出所有員工
        List<EmpVO> allEmployees = empService.getAllEmployees();
        model.addAttribute("employees", allEmployees);

        return "pages/admin/partials/panel-permission-matrix";
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
    @PostMapping(value = "/update", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePermissions(@RequestBody Map<String, Object> payload,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        // 安全檢查：只有超級管理員可以執行
        EmpVO currentEmp = (EmpVO) session.getAttribute("loginEmp");
        if (currentEmp == null) {
            response.put("success", false);
            response.put("message", "請先登入");
            return ResponseEntity.status(401).body(response);
        }

        // 檢查是否為超級管理員
        if (!empService.isSuperAdmin(currentEmp.getEmpId())) {
            response.put("success", false);
            response.put("message", "無權限執行此操作");
            return ResponseEntity.status(403).body(response);
        }

        try {
            Integer targetEmpId = Integer.parseInt(payload.get("empId").toString());
            List<Integer> functionIds = (List<Integer>) payload.get("functionIds");

            authorityService.updatePermissions(targetEmpId, functionIds);

            // 【最新補強】如果修改的是「自己」的權限，必須同步更新 Session 裡的物件
            if (currentEmp.getEmpId().equals(targetEmpId)) {
                EmpVO updatedEmp = empService.getOneEmp(targetEmpId);
                session.setAttribute("loginEmp", updatedEmp);
            }

            response.put("success", true);
            response.put("message", "權限更新成功！");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
