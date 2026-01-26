package com.momento.organizer.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizerService {

    @Autowired
    private OrganizerRepository organizerRepository;

    public OrganizerVO apply(OrganizerVO organizer) {

        if (organizerRepository.existsByAccount(organizer.getAccount())) {
            throw new IllegalArgumentException("帳號已存在");
        }

        if (organizerRepository.existsByEmail(organizer.getEmail())) {
            throw new IllegalArgumentException("Email 已被使用");
        }

        // 設定預設狀態為待審核
        organizer.setStatus((byte) 0);

        // TODO: 密碼加密
        // organizer.setPassword(passwordEncoder.encode(organizer.getPassword()));

        return organizerRepository.save(organizer);
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

    /*透過 Email 查詢主辦方 (用於忘記密碼)*/

    public OrganizerVO findByEmail(String email) {
        return organizerRepository.findByEmail(email).orElse(null);
    }

    /* 透過帳號查詢主辦方（用於登入）*/

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

    public OrganizerVO updateOrganizer(OrganizerVO organizer) {
        OrganizerVO existing = organizerRepository.findById(organizer.getOrganizerId())
                .orElseThrow(() -> new IllegalArgumentException("主辦方不存在"));

        // 不允許修改帳號
        if (!existing.getAccount().equals(organizer.getAccount())) {
            throw new IllegalArgumentException("不允許修改帳號");
        }

        return organizerRepository.save(organizer);
    }
}
