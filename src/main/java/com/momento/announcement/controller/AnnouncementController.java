package com.momento.announcement.controller;

import com.momento.announcement.model.AnnouncementNotifyService;
import com.momento.announcement.model.AnnouncementVO;
import com.momento.emp.model.EmpService;
import com.momento.emp.model.EmpVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementNotifyService announcementService;

    @Autowired
    private EmpService empService;

    //  前台 API 

    // 前台取得所有公告(跑馬燈+公告列表)
    @GetMapping("/api/list")
    @ResponseBody
    public List<Map<String, Object>> getAnnouncementsForFrontend() {
        List<AnnouncementVO> list = announcementService.getAllByCreatedAtDesc();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return list.stream().map(a -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getAnnouncementId());
            map.put("title", a.getTitle());
            map.put("content", a.getContent());
            map.put("date", a.getCreatedAt() != null ? a.getCreatedAt().format(formatter) : "");
            map.put("type", "notice"); // 預設類型為公告
            return map;
        }).collect(Collectors.toList());
    }

    // 後台 API 

    // 後台取得所有公告列表
    @GetMapping("/admin/api/list")
    @ResponseBody
    public List<Map<String, Object>> getAnnouncementsForAdmin() {
        List<AnnouncementVO> list = announcementService.getAllByCreatedAtDesc();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        return list.stream().map(a -> {
            Map<String, Object> map = new HashMap<>();
            map.put("announcementId", a.getAnnouncementId());
            map.put("title", a.getTitle());
            map.put("content", a.getContent());
            map.put("createdAt", a.getCreatedAt() != null ? a.getCreatedAt().format(formatter) : "");
            map.put("updatedAt", a.getUpdatedAt() != null ? a.getUpdatedAt().format(formatter) : "");
            map.put("empName", a.getEmpVO() != null ? a.getEmpVO().getEmpName() : "系統");
            return map;
        }).collect(Collectors.toList());
    }

    // 後台取得單一公告
    @GetMapping("/admin/api/{id}")
    @ResponseBody
    public ResponseEntity<?> getOneAnnouncement(@PathVariable("id") Integer id) {
        AnnouncementVO announcement = announcementService.getOneAnnouncement(id);
        if (announcement == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "找不到此公告"));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("announcementId", announcement.getAnnouncementId());
        map.put("title", announcement.getTitle());
        map.put("content", announcement.getContent());
        return ResponseEntity.ok(map);
    }

    // 後台新增公告
    @PostMapping("/admin/api/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createAnnouncement(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        // 檢查登入
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        if (loginEmp == null) {
            response.put("success", false);
            response.put("message", "請先登入");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            AnnouncementVO announcement = new AnnouncementVO();
            announcement.setTitle(title);
            announcement.setContent(content);
            announcement.setEmpVO(loginEmp);

            AnnouncementVO saved = announcementService.addAnnouncement(announcement);

            response.put("success", true);
            response.put("message", "公告新增成功");
            response.put("announcementId", saved.getAnnouncementId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "新增失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 後台更新公告
    @PostMapping("/admin/api/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAnnouncement(
            @RequestParam("announcementId") Integer announcementId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        // 檢查登入
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        if (loginEmp == null) {
            response.put("success", false);
            response.put("message", "請先登入");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            AnnouncementVO existing = announcementService.getOneAnnouncement(announcementId);
            if (existing == null) {
                response.put("success", false);
                response.put("message", "找不到此公告");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            existing.setTitle(title);
            existing.setContent(content);
            announcementService.updateAnnouncement(existing);

            response.put("success", true);
            response.put("message", "公告更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 後台刪除公告
    @PostMapping("/admin/api/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteAnnouncement(
            @RequestParam("announcementId") Integer announcementId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        // 檢查登入
        EmpVO loginEmp = (EmpVO) session.getAttribute("loginEmp");
        if (loginEmp == null) {
            response.put("success", false);
            response.put("message", "請先登入");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            AnnouncementVO existing = announcementService.getOneAnnouncement(announcementId);
            if (existing == null) {
                response.put("success", false);
                response.put("message", "找不到此公告");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            announcementService.deleteAnnouncement(announcementId);

            response.put("success", true);
            response.put("message", "公告刪除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刪除失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
