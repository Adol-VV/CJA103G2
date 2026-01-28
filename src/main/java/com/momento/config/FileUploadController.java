package com.momento.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 檔案上傳控制器 - 提供統一 API
 */
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 處理檔案上傳
     * 
     * @param file 檔案物件
     * @param type 類別 (event/product/article)
     * @return JSON 結果
     */
    @PostMapping("/{type}")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @PathVariable("type") String type) {

        Map<String, Object> response = new HashMap<>();
        try {
            // 驗證類型
            if (!"event".equals(type) && !"product".equals(type) && !"article".equals(type)) {
                throw new IllegalArgumentException("無效的上傳類型");
            }

            String fileUrl = fileUploadService.storeFile(file, type);
            response.put("success", true);
            response.put("url", fileUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
