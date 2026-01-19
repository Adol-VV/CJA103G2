package com.momento.emp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpRepository extends JpaRepository<EmpVO, Integer> {

    Optional<EmpVO> findByAccount(String account);

    boolean existsByAccount(String account);

    List<EmpVO> findByStatus(Byte status);
}
