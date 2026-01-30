package com.momento.event.model;

import com.momento.event.dto.EventDetailDTO;
import com.momento.event.dto.EventFilterDTO;
import com.momento.event.dto.EventListItemDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Event Service 介面
 * 定義活動相關的業務邏輯方法
 */
public interface EventService {

    /**
     * 查詢所有已上架活動（分頁）
     * 
     * @param page 頁碼
     * @param size 每頁筆數
     * @param sort 排序欄位
     * @return 活動分頁列表
     */
    Page<EventListItemDTO> getAllEvents(int page, int size, String sort);

    /**
     * 複合篩選活動
     * 
     * @param filterDTO 篩選條件
     * @return 活動分頁列表
     */
    Page<EventListItemDTO> filterEvents(EventFilterDTO filterDTO);

    /**
     * 關鍵字搜尋活動
     * 
     * @param keyword 搜尋關鍵字
     * @param page    頁碼
     * @param size    每頁筆數
     * @return 活動分頁列表
     */
    Page<EventListItemDTO> searchEvents(String keyword, int page, int size);

    /**
     * 查詢單一活動詳情
     * 
     * @param eventId  活動 ID
     * @param memberId 會員 ID（可為 null）
     * @return 活動詳情
     */
    EventDetailDTO getEventDetail(Integer eventId, Integer memberId);

    /**
     * 查詢相關活動推薦（同類型）
     * 
     * @param eventId 活動 ID
     * @param limit   推薦數量
     * @return 相關活動列表
     */
    List<EventListItemDTO> getRelatedEvents(Integer eventId, int limit);

    /**
     * 查詢主辦方的其他活動
     * 
     * @param organizerId 主辦方 ID
     * @param limit       推薦數量
     * @return 活動列表
     */
    List<EventListItemDTO> getOrganizerEvents(Integer organizerId, int limit);

    /**
     * 收藏/取消收藏活動
     * 
     * @param eventId  活動 ID
     * @param memberId 會員 ID
     * @return true 表示已收藏，false 表示已取消收藏
     */
    boolean toggleFavorite(Integer eventId, Integer memberId);

    /**
     * 查詢活動的收藏數量
     * 
     * @param eventId 活動 ID
     * @return 收藏數量
     */
    Long getFavoriteCount(Integer eventId);

    /**
     * 查詢會員的收藏活動
     * 
     * @param memberId 會員 ID
     * @return 收藏活動列表
     */
    List<EventListItemDTO> getMemberFavorites(Integer memberId);

    /**
     * 查詢會員的收藏活動數量
     * 
     * @param memberId 會員 ID
     * @return 收藏活動數量
     */
    Long getMemberFavoriteCount(Integer memberId);
}
