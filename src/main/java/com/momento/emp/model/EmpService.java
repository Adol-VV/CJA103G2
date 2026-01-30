package com.momento.emp.model;

import com.momento.config.SuperAdminConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmpService {

    @Autowired
    private SuperAdminConfig superAdminConfig;

    @Autowired
    private EmpRepository empRepository;

    @Autowired
    private EmpAuthorityRepository empAuthorityRepository;

    @Autowired
    private BackendFunctionRepository backendFunctionRepository;

    public boolean isSuperAdmin(Integer empId) {
        EmpVO emp = empRepository.findById(empId).orElse(null);
        if (emp == null) {
            return false;
        }
        return superAdminConfig.isSuperAdmin(emp.getAccount());
    }

    public boolean isSuperAdminByAccount(String account) {
        return superAdminConfig.isSuperAdmin(account);
    }

    public EmpVO getEmployee(Integer empId) {
        return empRepository.findById(empId).orElse(null);
    }

    public EmpVO getEmployeeByAccount(String account) {
        return empRepository.findByAccount(account).orElse(null);
    }

    public List<EmpVO> getAllEmployees() {
        return empRepository.findAll();
    }

    public EmpVO addEmployee(Integer currentEmpId, EmpVO newEmp) {

        if (!isSuperAdmin(currentEmpId)) {
            throw new SecurityException("只有超級管理員可以新增員工");
        }

        if (empRepository.existsByAccount(newEmp.getAccount())) {
            throw new IllegalArgumentException("帳號已存在");
        }

        return empRepository.save(newEmp);
    }

    public EmpVO updateEmployee(Integer currentEmpId, EmpVO emp) {

        if (!isSuperAdmin(currentEmpId)) {
            throw new SecurityException("只有超級管理員可以修改員工資料");
        }

        EmpVO existing = empRepository.findById(emp.getEmpId()).orElse(null);
        if (existing == null) {
            throw new IllegalArgumentException("員工不存在");
        }

        if (isSuperAdmin(emp.getEmpId()) && !existing.getAccount().equals(emp.getAccount())) {
            throw new IllegalArgumentException("不允許修改超級管理員帳號");
        }

        return empRepository.save(emp);
    }

    public void deleteEmployee(Integer currentEmpId, Integer targetEmpId) {

        if (!isSuperAdmin(currentEmpId)) {
            throw new SecurityException("只有超級管理員可以刪除員工");
        }

        if (isSuperAdmin(targetEmpId)) {
            throw new IllegalArgumentException("不允許刪除超級管理員");
        }

        if (currentEmpId.equals(targetEmpId)) {
            throw new IllegalArgumentException("不允許刪除自己");
        }

        empRepository.deleteById(targetEmpId);
    }

    public void grantPermission(Integer currentEmpId, Integer targetEmpId, Integer functionId) {

        if (!isSuperAdmin(currentEmpId)) {
            throw new SecurityException("只有超級管理員可以分配權限");
        }

        EmpVO targetEmp = empRepository.findById(targetEmpId).orElse(null);
        if (targetEmp == null) {
            throw new IllegalArgumentException("目標員工不存在");
        }

        if (isSuperAdmin(targetEmpId)) {
            throw new IllegalArgumentException("超級管理員自動擁有所有權限，無需分配");
        }

        if (empAuthorityRepository.existsByEmp_EmpIdAndFunction_FunctionId(targetEmpId, functionId)) {
            throw new IllegalArgumentException("該員工已有此權限");
        }

        BackendFunctionVO func = backendFunctionRepository.findById(functionId).orElse(null);
        if (func != null) {
            EmpAuthorityVO auth = new EmpAuthorityVO(targetEmp, func);
            empAuthorityRepository.save(auth);
        }
    }

    public void revokePermission(Integer currentEmpId, Integer targetEmpId, Integer functionId) {

        if (!isSuperAdmin(currentEmpId)) {
            throw new SecurityException("只有超級管理員可以移除權限");
        }

        if (isSuperAdmin(targetEmpId)) {
            throw new IllegalArgumentException("超級管理員權限無法移除");
        }

        if (!empAuthorityRepository.existsByEmp_EmpIdAndFunction_FunctionId(targetEmpId, functionId)) {
            throw new IllegalArgumentException("該員工沒有此權限");
        }

        empAuthorityRepository.deleteByEmp_EmpIdAndFunction_FunctionId(targetEmpId, functionId);
    }

    public List<EmpAuthorityVO> getEmployeePermissions(Integer empId) {

        if (isSuperAdmin(empId)) {
            // 超級管理員：回傳空列表
            // 前端透過 isSuperAdmin 判斷來顯示所有選單
            return new ArrayList<>();
        }
        return empAuthorityRepository.findByEmp_EmpId(empId);
    }

    public boolean hasPermission(Integer empId, Integer functionId) {

        if (isSuperAdmin(empId)) {
            return true;
        }

        return empAuthorityRepository.existsByEmp_EmpIdAndFunction_FunctionId(empId, functionId);
    }

    public List<BackendFunctionVO> getAllFunctions() {
        return backendFunctionRepository.findAll();
    }

    /**
     * 員工登入驗證
     * 
     * @param account  帳號
     * @param password 密碼
     * @return 驗證成功返回 EmpVO，失敗返回 null
     */
    public EmpVO authenticateEmployee(String account, String password) {
        EmpVO emp = empRepository.findByAccount(account).orElse(null);

        if (emp == null) {
            return null; // 帳號不存在
        }

        // 檢查員工狀態 (1=啟用, 0=停用)
        if (emp.getStatus() == null || emp.getStatus() != 1) {
            throw new IllegalStateException("此帳號已被停用");
        }

        // 驗證密碼 (目前是明文比對，之後可以改用 BCrypt)
        if (!emp.getPassword().equals(password)) {
            return null; // 密碼錯誤
        }

        return emp;
    }

    // ========== 員工資料管理 ==========

    /**
     * 獲取單一員工資料
     */
    public EmpVO getOneEmp(Integer empId) {
        return empRepository.findById(empId).orElse(null);
    }

    /**
     * 更新員工基本資料
     */
    public void updateEmployeeInfo(Integer empId, String empName, String jobTitle, Byte status) {
        EmpVO emp = empRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("員工不存在"));

        emp.setEmpName(empName);
        emp.setJobTitle(jobTitle);
        emp.setStatus(status);

        empRepository.save(emp);
    }

    /**
     * 員工自己修改密碼（需驗證舊密碼）
     */
    public void changePassword(Integer empId, String oldPassword, String newPassword) {
        EmpVO emp = empRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("員工不存在"));

        // 驗證舊密碼
        if (!emp.getPassword().equals(oldPassword)) {
            throw new RuntimeException("目前密碼錯誤");
        }

        // 驗證新密碼長度
        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("新密碼長度至少 8 個字元");
        }

        // 更新密碼
        emp.setPassword(newPassword);
        empRepository.save(emp);
    }

    /**
     * 直接更新密碼（無需驗證舊密碼，適合已登入狀態）
     */
    public void updatePassword(Integer empId, String newPassword) {
        EmpVO emp = empRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("員工不存在"));

        // 驗證新密碼長度
        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("新密碼長度至少 8 個字元");
        }

        // 更新密碼
        emp.setPassword(newPassword);
        empRepository.save(emp);
    }

    /**
     * 管理員重設員工密碼為預設值 12345678
     */
    public void resetPassword(Integer empId) {
        EmpVO emp = empRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("員工不存在"));

        emp.setPassword("12345678");
        empRepository.save(emp);
    }

}
