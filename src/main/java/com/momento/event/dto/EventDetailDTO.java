package com.momento.event.dto;

import com.momento.event.model.EventVO;
import com.momento.event.model.EventImageVO;
import com.momento.ticket.model.TicketVO;
import com.momento.organizer.model.OrganizerVO;
import lombok.Data;

import java.util.List;

/**
 * 活動詳情 DTO
 * 用於活動詳情頁顯示
 */
@Data
public class EventDetailDTO {

    /** 活動基本資訊 */
    private EventVO event;

    /** 活動圖片列表 */
    private List<EventImageVO> images;

    /** 票種列表 */
    private List<TicketVO> tickets;

    /** 主辦方資訊 */
    private OrganizerVO organizer;

    /** 最低票價 */
    private Integer minPrice;

    /** 最高票價 */
    private Integer maxPrice;

    /** 收藏數量 */
    private Long favoriteCount;

    /** 當前會員是否已收藏 */
    private Boolean isFavorited;

    /** 相關活動推薦 */
    private List<EventListItemDTO> relatedEvents;
}
