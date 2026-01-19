package com.momento.emp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpAuthorityRepository extends JpaRepository<EmpAuthorityVO, Integer> {

    boolean existsByEmpIdAndFunctionId(Integer empId, Integer functionId);

    List<EmpAuthorityVO> findByEmpId(Integer empId);

    List<EmpAuthorityVO> findByFunctionId(Integer functionId);

    void deleteByEmpIdAndFunctionId(Integer empId, Integer functionId);
}
