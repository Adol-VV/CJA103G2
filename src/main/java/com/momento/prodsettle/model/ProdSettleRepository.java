package com.momento.prodsettle.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdSettleRepository extends JpaRepository<ProdSettleVO,Integer>{
	List<ProdSettleVO> findByOrganizerId_OrganizerId(Integer organizerId);
}
