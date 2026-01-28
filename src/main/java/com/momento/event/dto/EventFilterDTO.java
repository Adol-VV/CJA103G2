package com.momento.event.dto;

import java.time.LocalDateTime;

/**
 * 活動篩選條件 DTO
 * 用於封裝前端傳遞的篩選參數
 */
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

    /** 是否僅顯示開賣中 */
    private Boolean onSaleOnly = false;

    // ========== 分頁參數 ==========

    /** 頁碼（從 0 開始） */
    private Integer page = 0;

    /** 每頁筆數 */
    private Integer size = 12;

    /** 排序欄位 */
    private String sort = "eventStartAt";

    /** 排序方向（ASC/DESC） */
    private String direction = "ASC";

    // ========== Getter & Setter ==========

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Boolean getOnSaleOnly() {
        return onSaleOnly;
    }

    public void setOnSaleOnly(Boolean onSaleOnly) {
        this.onSaleOnly = onSaleOnly;
    }
}
