package com.momento.prod.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProdImageRepository extends JpaRepository<ProdImageVO, Integer> {
	
	@Transactional
	@Modifying
	@Query("DELETE FROM ProdImageVO pi WHERE pi.prodImageId = :imageId")
	void deleteByImageId(Integer imageId);
}
