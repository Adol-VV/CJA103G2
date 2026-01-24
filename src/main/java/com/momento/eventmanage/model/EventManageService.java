package com.momento.eventmanage.model;

import com.momento.eventmanage.dto.EventCreateDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Event Manage Service - 主辦方活動管理服務介面
 */
public interface EventManageService {

    /**
     * 建立活動
     * 
     * @param dto 活動建立 DTO
     * @return 活動 ID
     */
    Integer createEvent(EventCreateDTO dto);

    /**
     * 上傳圖片
     * 
     * @param file 圖片檔案
     * @return 圖片 URL
     */
    String uploadImage(MultipartFile file);

    /**
     * 更新活動
     * 
     * @param dto 活動更新 DTO
     */
    void updateEvent(com.momento.eventmanage.dto.EventUpdateDTO dto);

    /**
     * 檢查票種是否可以編輯
     * 
     * @param ticketId 票種 ID
     * @return true 表示可以編輯, false 表示不可編輯
     */
    boolean canEditTicket(Integer ticketId);

    /**
     * 變更活動狀態
     * 
     * @param eventId 活動 ID
     * @param status  新狀態
     * @param reason  變更原因 (可選)
     */
    void changeStatus(Integer eventId, Byte status, String reason);

    /**
     * 取得所有活動 (暫時用於測試)
     * 
     * @return 活動列表
     */
    java.util.List<com.momento.event.model.EventVO> getAllEvents();

    /**
     * 查詢主辦方的活動列表 (支援篩選和分頁)
     * 
     * @param organizerId 主辦方 ID
     * @param status      活動狀態 (可選)
     * @param keyword     搜尋關鍵字 (可選)
     * @param pageable    分頁參數
     * @return 活動分頁列表
     */
    org.springframework.data.domain.Page<com.momento.event.model.EventVO> getOrganizerEvents(
            Integer organizerId,
            Byte status,
            Byte reviewStatus,
            String keyword,
            org.springframework.data.domain.Pageable pageable);
}
