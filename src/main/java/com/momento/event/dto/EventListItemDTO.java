package com.momento.event.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活動列表項目 DTO
 * 用於活動列表頁顯示
 */
@Data
public class EventListItemDTO {

    /** 活動 ID */
    private Integer eventId;

    /** 活動標題 */
    private String title;

    /** 活動地點 */
    private String place;

    /** 活動舉辦時間 */
    private LocalDateTime eventAt;

    /** 封面圖片 URL */
    private String coverImageUrl;

    /** 最低票價 */
    private Integer minPrice;

    /** 活動類型名稱 */
    private String typeName;

    /** 主辦方名稱 */
    private String organizerName;

    /** 收藏數量 */
    private Long favoriteCount;

    /** 當前會員是否已收藏 */
    private Boolean isFavorited;
}
