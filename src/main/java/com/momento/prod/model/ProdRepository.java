package com.momento.prod.model;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ProdRepository extends JpaRepository<ProdVO, Integer> {
	

//	Slice<ProdVO> findBy(Pageable pageable);
	
	
	//模糊查詢
	@Query(value = "select p from ProdVO p where p.prodName like %?1% order by p.prodId")
	List<ProdVO> findByName(String prodName);
}
