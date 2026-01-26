package com.momento.recommendedprod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.momento.recommendedprod.model.RecommendedProdService;
import com.momento.recommendedprod.model.RecommendedProdVO;

@RestController
@RequestMapping("/recommendedprod")
public class RecommendedProdController {

    @Autowired
    private RecommendedProdService recommendedProdService;

    /**
     * 取得所有推薦商品
     * GET /recommendedprod
     * 
     * 前端可透過 prodVO.prodImageVOs 取得商品圖片 (ProdImageVO)
     */
    @GetMapping
    public ResponseEntity<List<RecommendedProdVO>> getAllRecommendedProds() {
        List<RecommendedProdVO> recommendedProds = recommendedProdService.getAll();
        return ResponseEntity.ok(recommendedProds);
    }

    /**
     * 取得單一推薦商品
     * GET /recommendedprod/{id}
     * 
     * 回傳的 RecommendedProdVO 包含 ProdVO，
     * ProdVO 中有 prodImageVOs 集合可取得商品圖片
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecommendedProdVO> getOneRecommendedProd(@PathVariable Integer id) {
        RecommendedProdVO recommendedProd = recommendedProdService.getOneRecommendedProd(id);
        if (recommendedProd == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recommendedProd);
    }

    /**
     * 新增推薦商品
     * POST /recommendedprod
     */
    @PostMapping
    public ResponseEntity<RecommendedProdVO> addRecommendedProd(@RequestBody RecommendedProdVO recommendedProdVO) {
        try {
            RecommendedProdVO savedRecommendedProd = recommendedProdService.addRecommendedProd(recommendedProdVO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRecommendedProd);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 刪除推薦商品
     * DELETE /recommendedprod/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendedProd(@PathVariable Integer id) {
        RecommendedProdVO recommendedProd = recommendedProdService.getOneRecommendedProd(id);
        if (recommendedProd == null) {
            return ResponseEntity.notFound().build();
        }
        recommendedProdService.deleteRecommendedProd(id);
        return ResponseEntity.noContent().build();
    }
}
