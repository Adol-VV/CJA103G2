package com.momento.event.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活動篩選條件 DTO
 * 用於封裝前端傳遞的篩選參數
 */
@Data
public class EventFilterDTO {

    /** 活動類型 ID */
    private Integer typeId;

    /** 地區關鍵字 */
    private String place;

    /** 活動開始日期 */
    private LocalDateTime startDate;

    /** 活動結束日期 */
    private LocalDateTime endDate;

    /** 最低票價 */
    private Integer minPrice;

    /** 最高票價 */
    private Integer maxPrice;

    /** 搜尋關鍵字 */
    private String keyword;

    // ========== 分頁參數 ==========

    /** 頁碼（從 0 開始） */
    private Integer page = 0;

    /** 每頁筆數 */
    private Integer size = 12;

    /** 排序欄位 */
    private String sort = "eventAt";

    /** 排序方向（ASC/DESC） */
    private String direction = "ASC";
}
