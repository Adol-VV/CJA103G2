package com.momento.emp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpAuthorityRepository extends JpaRepository<EmpAuthorityVO, Integer> {

    // 透過關聯物件的 ID 進行查詢
    List<EmpAuthorityVO> findByEmp_EmpId(Integer empId);

    List<EmpAuthorityVO> findByFunction_FunctionId(Integer functionId);

    // 為了相容性與 Clean Code，提供基於 ID 的快速檢查
    boolean existsByEmp_EmpIdAndFunction_FunctionId(Integer empId, Integer functionId);

    @Modifying
    void deleteByEmp_EmpIdAndFunction_FunctionId(Integer empId, Integer functionId);

    @Modifying
    @Query("DELETE FROM EmpAuthorityVO ea WHERE ea.emp.empId = ?1")
    void deleteByEmpId(Integer empId);
}
