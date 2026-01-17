package com.momento.featured.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeaturedRepository extends JpaRepository<FeaturedVO, Integer> {
	// 若需要查詢特定主辦方的精選活動：
	 List<FeaturedVO> findByOrganizerVO_OrganizerId(Integer organizerId);
}