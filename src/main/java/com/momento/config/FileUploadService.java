package com.momento.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

/**
 * 檔案上傳服務 - 處理外部路徑 I/O
 */
@Service
public class FileUploadService {

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 初始化目錄結構
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath, "event"));
            Files.createDirectories(Paths.get(uploadPath, "product"));
            Files.createDirectories(Paths.get(uploadPath, "article"));
        } catch (IOException e) {
            throw new RuntimeException("無法建立上傳目錄: " + uploadPath, e);
        }
    }

    /**
     * 儲存檔案
     * 
     * @param file 檔案物件
     * @param type 類別 (event/product/article)
     * @return 相對 URL 路徑 (含前綴 /uploads/)
     */
    public String storeFile(MultipartFile file, String type) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("檔案不可為空");
        }

        // 建立隨機檔名防止衝突
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // 物理路徑
        Path targetLocation = Paths.get(uploadPath, type).resolve(fileName);

        // 儲存檔案
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 返回 Web 存取路徑
        return "/uploads/" + type + "/" + fileName;
    }
}
