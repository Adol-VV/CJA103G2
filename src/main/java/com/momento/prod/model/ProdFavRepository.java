package com.momento.prod.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProdFavRepository extends JpaRepository<ProdFavVO, Integer>{
	
	//依memberId查詢收藏商品列表
	@Query("SELECT pf FROM ProdFavVO pf JOIN FETCH pf.prodVO p LEFT JOIN FETCH p.prodSortVO WHERE pf.memberVO.memberId = ?1")
	List<ProdFavVO> findByMemberId(Integer memberId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM ProdFavVO pf WHERE pf.memberVO.memberId = :memberId AND pf.prodVO.prodId = :prodId")
	public void deleteByMemberAndProdId(Integer memberId, Integer prodId);
}
