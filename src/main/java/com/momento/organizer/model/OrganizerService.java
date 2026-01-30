package com.momento.organizer.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizerService {

    @Autowired
    private OrganizerRepository organizerRepository;

    public OrganizerVO apply(com.momento.organizer.dto.OrganizerApplyDTO dto) {
        System.out.println("Service 接收到申請資料: " + dto.getAccount() + ", " + dto.getEmail());

        // 1. 檢查密碼與確認密碼是否一致
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("兩次輸入的密碼不一致");
        }

        // 2. 檢查帳號與 Email 是否重複
        if (organizerRepository.existsByAccount(dto.getAccount())) {
            System.out.println("帳號已存在: " + dto.getAccount());
            throw new IllegalArgumentException("帳號已存在");
        }

        if (organizerRepository.existsByEmail(dto.getEmail())) {
            System.out.println("Email 已被使用: " + dto.getEmail());
            throw new IllegalArgumentException("Email 已被使用");
        }

        // 3. 將 DTO 轉換為 VO (實體)
        OrganizerVO organizer = new OrganizerVO();
        organizer.setAccount(dto.getAccount());
        organizer.setPassword(dto.getPassword()); // TODO: 密碼加密
        organizer.setName(dto.getName());
        organizer.setOwnerName(dto.getOwnerName());
        organizer.setPhone(dto.getPhone());
        organizer.setEmail(dto.getEmail());
        organizer.setIntroduction(dto.getIntroduction());
        organizer.setBankCode(dto.getBankCode());
        organizer.setBankAccount(dto.getBankAccount());
        organizer.setAccountName(dto.getAccountName());

        // 4. 設定預設狀態為待審核 (0)
        organizer.setStatus((byte) 0);

        try {
            OrganizerVO saved = organizerRepository.save(organizer);
            System.out.println("成功存入資料庫，ID: " + saved.getOrganizerId());
            return saved;
        } catch (Exception e) {
            System.err.println("資料庫儲存失敗: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public List<OrganizerVO> getAllOrganizers() {
        return organizerRepository.findAll();
    }

    public List<OrganizerVO> getPendingOrganizers() {
        return organizerRepository.findByStatusOrderByCreatedAtAsc((byte) 0);
    }

    public List<OrganizerVO> getActiveOrganizers() {
        return organizerRepository.findByStatus((byte) 1);
    }

    public List<OrganizerVO> getSuspendedOrganizers() {
        return organizerRepository.findByStatus((byte) 2);
    }

    public OrganizerVO getOrganizer(Integer organizerId) {
        return organizerRepository.findById(organizerId).orElse(null);
    }

    /* 透過 Email 查詢主辦方 (用於忘記密碼) */

    public OrganizerVO findByEmail(String email) {
        return organizerRepository.findByEmail(email).orElse(null);
    }

    /* 透過帳號查詢主辦方（用於登入） */

    public OrganizerVO findByAccount(String account) {
        return organizerRepository.findByAccount(account).orElse(null);
    }

    /**
     * 檢查帳號是否已存在
     */
    public boolean existsByAccount(String account) {
        return organizerRepository.existsByAccount(account);
    }

    /**
     * 檢查 Email 是否已存在
     */
    public boolean existsByEmail(String email) {
        return organizerRepository.existsByEmail(email);
    }

    public void approve(Integer organizerId) {
        OrganizerVO organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("主辦方不存在"));

        if (organizer.getStatus() != 0) {
            throw new IllegalArgumentException("只能審核待審核的主辦方");
        }

        // 設為正常
        organizer.setStatus((byte) 1);
        organizerRepository.save(organizer);
    }

    public void suspend(Integer organizerId) {
        OrganizerVO organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("主辦方不存在"));

        if (organizer.getStatus() != 1) {
            throw new IllegalArgumentException("只能停權正常的主辦方");
        }

        organizer.setStatus((byte) 2); // 設為停權
        organizerRepository.save(organizer);
    }

    public void unsuspend(Integer organizerId) {
        OrganizerVO organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("主辦方不存在"));

        if (organizer.getStatus() != 2) {
            throw new IllegalArgumentException("只能解除停權的主辦方");
        }

        organizer.setStatus((byte) 1); // 設為正常
        organizerRepository.save(organizer);
    }

    /*
     * 拒絕申請（後台管理員使用）
     * 直接刪除申請記錄
     */
    public void rejectApplication(Integer organizerId) {
        OrganizerVO organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("主辦方不存在"));

        if (organizer.getStatus() != 0) {
            throw new IllegalArgumentException("只能拒絕待審核的申請");
        }

        // 拒絕 >申請直接刪除記錄
        organizerRepository.deleteById(organizerId);
    }

    /**
     * 永久刪除主辦單位 (註銷帳號)
     */
    public void deleteOrganizer(Integer organizerId) {
        if (!organizerRepository.existsById(organizerId)) {
            throw new IllegalArgumentException("主辦方不存在");
        }
        organizerRepository.deleteById(organizerId);
    }

    public OrganizerVO updateOrganizer(OrganizerVO organizer) {
        OrganizerVO existing = organizerRepository.findById(organizer.getOrganizerId())
                .orElseThrow(() -> new IllegalArgumentException("主辦方不存在"));

        // 不允許修改帳號
        if (!existing.getAccount().equals(organizer.getAccount())) {
            throw new IllegalArgumentException("不允許修改帳號");
        }

        return organizerRepository.save(organizer);
    }

    /**
     * 搜尋已認證的主辦方 (用於前台全站搜尋)
     */
    public List<com.momento.organizer.dto.OrganizerSearchDTO> searchActiveOrganizers(String keyword) {
        List<OrganizerVO> list = organizerRepository.findByNameContainingIgnoreCaseAndStatus(keyword, (byte) 1);
        return list.stream()
                .map(o -> new com.momento.organizer.dto.OrganizerSearchDTO(
                        o.getOrganizerId(),
                        o.getName(),
                        o.getIntroduction()))
                .collect(java.util.stream.Collectors.toList());
    }

    // 取得主辦方總數
    public long getOrganizerCount() {
        return organizerRepository.count();
    }

    // 取得活躍主辦方數 (status = 1)
    public long getActiveOrganizerCount() {
        return organizerRepository.findByStatus((byte) 1).size();
    }
}
