package com.momento.recommendedprod.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendedProdRepository extends JpaRepository<RecommendedProdVO, Integer> {
	// 若將來需要根據主辦方查詢，可直接定義：
	 List<RecommendedProdVO> findByOrganizerVO_OrganizerId(Integer organizerId);
}