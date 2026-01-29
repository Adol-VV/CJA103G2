package com.momento.emp.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpAuthorityService {

    @Autowired
    private EmpAuthorityRepository repository;

    /**
     * 從資料庫取得該員工的所有權限
     */
    public List<EmpAuthorityVO> getAuthorities(Integer empId) {
        return repository.findByEmpId(empId);
    }

    /**
     * 更新指定員工的權限 (Plan A 核心邏輯)
     * 
     * @param targetEmpId 目標員工 ID
     * @param functionIds 新勾選的功能 ID 列表 (例如 [1, 3, 5])
     */
    @Transactional
    public void updatePermissions(Integer targetEmpId, List<Integer> functionIds) {

        // 【重要】超級管理員防護機制 (God Mode Protection)
        // 絕對禁止任何人修改 ID=1 (admin) 的權限，避免系統鎖死
        if (targetEmpId == 1) {
            throw new RuntimeException("操作被拒絕：超級管理員 (ID=1) 的權限不可變更！");
        }

        // 1. 先刪除舊的所有權限 (Reset)
        repository.deleteByEmpId(targetEmpId);

        // 2. 如果有勾選任何功能，則逐筆新增
        if (functionIds != null && !functionIds.isEmpty()) {
            for (Integer fid : functionIds) {
                // 建立新的權限關聯物件
                EmpAuthorityVO auth = new EmpAuthorityVO(targetEmpId, fid);
                repository.save(auth);
            }
        }
    }
}
