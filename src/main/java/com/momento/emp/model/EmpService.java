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

    public void grantPermission(Integer currentEmpId,Integer targetEmpId, Integer functionId){

        if(!isSuperAdmin(currentEmpId)){
            throw new SecurityException("只有超級管理員可以分配權限");
        }

        EmpVO targetEmp = empRepository.findById(targetEmpId).orElse(null);
        if (targetEmp == null) {
            throw new IllegalArgumentException("目標員工不存在");
        }

        if (isSuperAdmin(targetEmpId)) {
            throw new IllegalArgumentException("超級管理員自動擁有所有權限，無需分配");
        }

        if (empAuthorityRepository.existsByEmpIdAndFunctionId(targetEmpId, functionId)) {
            throw new IllegalArgumentException("該員工已有此權限");
        }

        EmpAuthorityVO auth = new EmpAuthorityVO(targetEmpId, functionId);
        empAuthorityRepository.save(auth);
    }

    public void revokePermission(Integer currentEmpId, Integer targetEmpId, Integer functionId) {

        if (!isSuperAdmin(currentEmpId)) {
            throw new SecurityException("只有超級管理員可以移除權限");
        }

        if (isSuperAdmin(targetEmpId)) {
            throw new IllegalArgumentException("超級管理員權限無法移除");
        }

        if (!empAuthorityRepository.existsByEmpIdAndFunctionId(targetEmpId, functionId)) {
            throw new IllegalArgumentException("該員工沒有此權限");
        }

        empAuthorityRepository.deleteByEmpIdAndFunctionId(targetEmpId, functionId);
    }

    public List<EmpAuthorityVO> getEmployeePermissions(Integer empId) {

        if (isSuperAdmin(empId)) {

            return new ArrayList<>();  //
        }
        return empAuthorityRepository.findByEmpId(empId);
    }


    public boolean hasPermission(Integer empId, Integer functionId) {

        if (isSuperAdmin(empId)) {
            return true;
        }

        return empAuthorityRepository.existsByEmpIdAndFunctionId(empId, functionId);
    }

}
