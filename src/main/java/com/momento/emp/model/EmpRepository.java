package com.momento.emp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpRepository extends JpaRepository<EmpVO, Integer> {

    @Modifying
    @Query("DELETE FROM EmpAuthorityVO ea WHERE ea.emp.empId = ?1")
    void deleteByEmpId(Integer empId);

    Optional<EmpVO> findByAccount(String account);

    boolean existsByAccount(String account);

    List<EmpVO> findByStatus(Byte status);
}
