package com.momento.eventmanage.model;

import com.momento.eventmanage.dto.EventCreateDTO;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

/**
 * Event Manage Service - 主辦方活動管理服務介面
 */
public interface EventManageService {

    /**
     * 儲存草稿
     * 
     * @param dto 活動建立 DTO
     * @return 活動 ID
     */
    Integer saveDraft(EventCreateDTO dto);

    /**
     * 建立活動 (與 saveDraft 同義或用於區分)
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
     * 更新草稿
     * 
     * @param dto 活動更新 DTO
     */
    void updateDraft(com.momento.eventmanage.dto.EventUpdateDTO dto);

    /**
     * 更新活動 (通用)
     */
    void updateEvent(com.momento.eventmanage.dto.EventUpdateDTO dto);

    /**
     * 送審活動 (0/4 -> 1)
     * 
     * @param eventId 活動 ID
     */
    void submitEvent(Integer eventId);

    /**
     * 設定時間並上架 (2 -> 3)
     */
    void setTimesAndPublish(Integer eventId, LocalDateTime publishedAt, LocalDateTime saleStartAt,
            LocalDateTime saleEndAt, LocalDateTime eventStartAt, LocalDateTime eventEndAt);

    /**
     * 撤回活動 (1 -> 0)
     * 
     * @param eventId 活動 ID
     */
    void withdrawEvent(Integer eventId);

    /**
     * 刪除活動 (僅限草稿/駁回)
     * 
     * @param eventId 活動 ID
     */
    void deleteEvent(Integer eventId);

    /**
     * 檢查票種是否可以編輯
     */
    boolean canEditTicket(Integer ticketId);

    /**
     * 變更活動狀態
     */
    void changeStatus(Integer eventId, Byte status, String reason);

    /**
     * 強制下架 (3 -> 5)
     */
    void forceClose(Integer eventId, String reason);

    /**
     * 取得所有活動 (暫時用於測試)
     */
    java.util.List<com.momento.event.model.EventVO> getAllEvents();

    /**
     * 查詢主辦方的活動列表 (支援篩選和分頁)
     * 
     * @param organizerId 主辦方 ID
     * @param statuses    活動狀態集合 (可選)
     * @param keyword     搜尋關鍵字 (可選)
     * @param pageable    分頁參數
     * @return 活動分頁列表
     */
    org.springframework.data.domain.Page<com.momento.event.model.EventVO> getOrganizerEvents(
            Integer organizerId,
            java.util.Collection<Byte> statuses,
            String keyword,
            org.springframework.data.domain.Pageable pageable);

    /**
     * 取得主辦方統計數據
     */
    com.momento.eventmanage.dto.EventStatsDTO getOrganizerStats(Integer organizerId);

    /**
     * 取得活動詳情 (用於編輯/查看)
     */
    com.momento.event.dto.EventDetailDTO getEventDetail(Integer eventId);
}
