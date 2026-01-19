package com.momento.organizer.controller;

import com.momento.organizer.model.OrganizerService;
import com.momento.organizer.model.OrganizerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizer")
public class OrganizerController {

    @Autowired
    private OrganizerService organizerService;

    /* 主辦方申請（公開 API） */

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody OrganizerVO organizer) {
        try {
            OrganizerVO result = organizerService.apply(organizer);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 檢查帳號是否已存在（公開 API，用於即時驗證）
     */
    @GetMapping("/check-account")
    public ResponseEntity<Boolean> checkAccount(@RequestParam String account) {
        boolean exists = organizerService.existsByAccount(account);
        return ResponseEntity.ok(exists);
    }

    /**
     * 檢查 Email 是否已存在（公開 API，用於即時驗證）
     */
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = organizerService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    /* 查詢所有主辦方（後台管理員） */

    @GetMapping("/all")
    public ResponseEntity<List<OrganizerVO>> getAllOrganizers() {
        List<OrganizerVO> organizers = organizerService.getAllOrganizers();
        return ResponseEntity.ok(organizers);
    }

    /* 查詢待審核的主辦方（後台管理員） */

    @GetMapping("/pending")
    public ResponseEntity<List<OrganizerVO>> getPendingOrganizers() {
        List<OrganizerVO> organizers = organizerService.getPendingOrganizers();
        return ResponseEntity.ok(organizers);
    }

    /* 查詢正常的主辦方 */

    @GetMapping("/active")
    public ResponseEntity<List<OrganizerVO>> getActiveOrganizers() {
        List<OrganizerVO> organizers = organizerService.getActiveOrganizers();
        return ResponseEntity.ok(organizers);
    }

    /* 查詢被停權的主辦方 */

    @GetMapping("/suspended")
    public ResponseEntity<List<OrganizerVO>> getSuspendedOrganizers() {
        List<OrganizerVO> organizers = organizerService.getSuspendedOrganizers();
        return ResponseEntity.ok(organizers);
    }

    /* 查詢單一主辦方 */

    @GetMapping("/{organizerId}")
    public ResponseEntity<?> getOrganizer(@PathVariable Integer organizerId) {
        OrganizerVO organizer = organizerService.getOrganizer(organizerId);
        if (organizer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(organizer);
    }

    /* 審核通過（後台管理員） 狀態：0 → 1 */
    @PutMapping("/{organizerId}/approve")
    public ResponseEntity<?> approve(@PathVariable Integer organizerId) {
        try {
            organizerService.approve(organizerId);
            return ResponseEntity.ok("審核通過");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* 拒絕申請（後台管理員）直接刪除記錄 */

    @DeleteMapping("/{organizerId}/reject")
    public ResponseEntity<?> rejectApplication(@PathVariable Integer organizerId) {
        try {
            organizerService.rejectApplication(organizerId);
            return ResponseEntity.ok("已拒絕申請");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* 停權（後台管理員）狀態：1 → 2 */

    @PutMapping("/{organizerId}/suspend")
    public ResponseEntity<?> suspend(@PathVariable Integer organizerId) {
        try {
            organizerService.suspend(organizerId);
            return ResponseEntity.ok("已停權");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* 狀態：2 → 1 */

    @PutMapping("/{organizerId}/unsuspend")
    public ResponseEntity<?> unsuspend(@PathVariable Integer organizerId) {
        try {
            organizerService.unsuspend(organizerId);
            return ResponseEntity.ok("已解除停權");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* 更新主辦方資料 */

    @PutMapping("/{organizerId}")
    public ResponseEntity<?> updateOrganizer(
            @PathVariable Integer organizerId,
            @RequestBody OrganizerVO organizer) {
        try {
            organizer.setOrganizerId(organizerId);
            OrganizerVO result = organizerService.updateOrganizer(organizer);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
