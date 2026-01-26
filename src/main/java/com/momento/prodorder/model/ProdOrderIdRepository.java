package com.momento.prodorder.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdOrderIdRepository extends JpaRepository<ProdOrderIdVO,Integer>{
	List<ProdOrderIdVO> findByMemberId_MemberId(Integer memberId);
}
