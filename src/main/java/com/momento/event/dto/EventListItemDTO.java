package com.momento.event.dto;

import java.time.LocalDateTime;

/**
 * 活動列表項目 DTO
 * 用於活動列表頁顯示
 */
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

    // ========== Getter & Setter ==========

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public LocalDateTime getEventAt() {
        return eventAt;
    }

    public void setEventAt(LocalDateTime eventAt) {
        this.eventAt = eventAt;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public Long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Boolean getIsFavorited() {
        return isFavorited;
    }

    public void setIsFavorited(Boolean isFavorited) {
        this.isFavorited = isFavorited;
    }
}
