package com.momento.prodorderitem.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdOrderItemRepository extends JpaRepository<ProdOrderItemVO,Integer>{

    /**
     * 計算特定商品的購買會員人數 (不重複)
     */
    @Query("SELECT COUNT(DISTINCT i.prodOrderId.memberId.memberId) FROM ProdOrderItemVO i WHERE i.prodId.prodId = :prodId")
    Integer countDistinctMembersByProdId(@Param("prodId") Integer prodId);
}
