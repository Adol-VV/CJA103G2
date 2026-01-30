package com.momento.emp.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmpAuthorityService {

    @Autowired
    private EmpAuthorityRepository repository;

    @Autowired
    private EmpRepository empRepository;

    @Autowired
    private BackendFunctionRepository functionRepository;

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    /**
     * 從資料庫取得該員工的所有權限
     */
    public List<EmpAuthorityVO> getAuthorities(Integer empId) {
        return repository.findByEmp_EmpId(empId);
    }

    /**
     * 更新指定員工的權限 (Explicit Repository Mode + Proxy)
     * 使用 getReferenceById 取得代理物件，避免 JPA 載入舊的集合。
     * 直接使用 deleteByEmpId 和 saveAll 進行物理操作。
     */
    @Transactional
    public void updatePermissions(Integer targetEmpId, List<Integer> functionIds) {

        // 1. 使用 getReferenceById 取得代理物件 (Proxy)
        // 這樣可以避免 Hibernate 去查詢舊的 authorities 集合，防止狀態污染
        EmpVO targetEmp = empRepository.getReferenceById(targetEmpId);

        // 【安全防護】(透過代理物件取 ID 是安全的)
        if (targetEmp.getEmpId() == 1) {
            throw new RuntimeException("操作被拒絕：超級管理員 (ID=1) 的權限不可變更！");
        }

        // 2. 物理刪除該員工所有舊權限
        // 先 Flush 確保之前的操作都已送出
        entityManager.flush();
        entityManager.clear(); // 清空 Persistence Context，確保記憶體乾淨

        // 這裡因為 context 已清空，重新拿一個乾淨的 Reference 準備給新建的 Authority 用
        EmpVO freshEmpRef = empRepository.getReferenceById(targetEmpId);

        repository.deleteByEmpId(targetEmpId);
        entityManager.flush(); // 強制立即執行 DELETE SQL

        // 3. 準備新的權限物件列表
        List<EmpAuthorityVO> newAuthorities = new ArrayList<>();
        if (functionIds != null) {
            for (Object fidObj : functionIds) {
                Integer fid = Integer.valueOf(fidObj.toString());

                // 檢查功能是否存在
                BackendFunctionVO function = functionRepository.findById(fid)
                        .orElseThrow(() -> new RuntimeException("錯誤：找不到功能 ID = " + fid));

                // 建立新的關聯物件
                EmpAuthorityVO auth = new EmpAuthorityVO(freshEmpRef, function);
                newAuthorities.add(auth);
            }
        }

        // 4. 批次寫入新權限 (直接下 INSERT SQL)
        if (!newAuthorities.isEmpty()) {
            repository.saveAll(newAuthorities);
            entityManager.flush(); // 強制立即執行 INSERT SQL
        }
    }
}
