package com.momento.event.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Type Repository - 活動類型資料存取層
 * 
 * 提供活動類型相關的資料庫查詢方法
 */
@Repository
public interface TypeRepository extends JpaRepository<TypeVO, Integer> {

    /**
     * 查詢所有活動類型（用於篩選選單）
     * 
     * @return 活動類型列表
     */
    List<TypeVO> findAll();

    /**
     * 依類型名稱查詢
     * 
     * @param typeName 類型名稱
     * @return 活動類型（可能為空）
     */
    Optional<TypeVO> findByTypeName(String typeName);

    /**
     * 檢查類型名稱是否存在
     * 
     * @param typeName 類型名稱
     * @return true 表示存在，false 表示不存在
     */
    boolean existsByTypeName(String typeName);
}
