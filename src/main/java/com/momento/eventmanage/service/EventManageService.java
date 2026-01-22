package com.momento.eventmanage.service;

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

    // TODO: 其他方法
    // - listEventsByOrganizer()
    // - getEventDetail()
    // - resubmitEvent()
}
