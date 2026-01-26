package com.momento.featured.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.momento.featured.model.FeaturedService;
import com.momento.featured.model.FeaturedVO;

@RestController
@RequestMapping("/featured")
public class FeaturedController {

    @Autowired
    private FeaturedService featuredService;

    /**
     * 取得所有主打活動
     * GET /featured
     * 
     * 前端可透過 eventVO.eventImageVOs 取得活動圖片 (EventImageVO)
     */
    @GetMapping
    public ResponseEntity<List<FeaturedVO>> getAllFeatured() {
        List<FeaturedVO> featuredList = featuredService.getAll();
        return ResponseEntity.ok(featuredList);
    }

    /**
     * 取得單一主打活動
     * GET /featured/{id}
     * 
     * 回傳的 FeaturedVO 包含 EventVO，
     * EventVO 中有 eventImageVOs 集合可取得活動圖片
     */
    @GetMapping("/{id}")
    public ResponseEntity<FeaturedVO> getOneFeatured(@PathVariable Integer id) {
        FeaturedVO featured = featuredService.getOneFeatured(id);
        if (featured == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(featured);
    }

    /**
     * 新增主打活動
     * POST /featured
     */
    @PostMapping
    public ResponseEntity<FeaturedVO> addFeatured(@RequestBody FeaturedVO featuredVO) {
        try {
            FeaturedVO savedFeatured = featuredService.addFeatured(featuredVO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedFeatured);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 更新主打活動（例如：更新開始/結束時間）
     * PUT /featured/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<FeaturedVO> updateFeatured(@PathVariable Integer id, @RequestBody FeaturedVO featuredVO) {
        FeaturedVO existingFeatured = featuredService.getOneFeatured(id);
        if (existingFeatured == null) {
            return ResponseEntity.notFound().build();
        }
        featuredVO.setFeaturedId(id);
        FeaturedVO updatedFeatured = featuredService.updateFeatured(featuredVO);
        return ResponseEntity.ok(updatedFeatured);
    }

    /**
     * 刪除主打活動
     * DELETE /featured/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeatured(@PathVariable Integer id) {
        FeaturedVO featured = featuredService.getOneFeatured(id);
        if (featured == null) {
            return ResponseEntity.notFound().build();
        }
        featuredService.deleteFeatured(id);
        return ResponseEntity.noContent().build();
    }
}
