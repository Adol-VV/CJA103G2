package com.momento.prod.model;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ProdRepository extends JpaRepository<ProdVO, Integer> {
	

//	Slice<ProdVO> findBy(Pageable pageable);
	
	
	//名稱模糊查詢
	@Query(value = "select p from ProdVO p where p.prodName like %?1% order by p.prodId")
	List<ProdVO> findByName(String prodName);
	
	//主辦方平台的名稱模糊查詢
	@Query(value = "select * from prod where organizer_id = ?1 and prod_name like %?2% order by prod_id", nativeQuery = true)
	List<ProdVO> findByOrgAndName(Integer organizerId, String prodName);
	
	//依主辦方查詢商品
	@Query(value = "select * from prod where organizer_id = ?1 order by prod_id", nativeQuery = true)
	List<ProdVO> findProdsByOrgId(Integer organizerId);
	
	//首頁最新商品(6筆)
	
	@Query(value = "select * from prod where prod_status = 1 order by created_at desc limit 6;", nativeQuery = true)
	List<ProdVO> findLatestProds();
}
