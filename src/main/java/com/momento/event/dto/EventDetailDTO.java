package com.momento.event.dto;

import com.momento.event.model.EventVO;
import com.momento.event.model.EventImageVO;
import com.momento.ticket.model.TicketVO;
import com.momento.organizer.model.OrganizerVO;

import java.util.List;

/**
 * 活動詳情 DTO
 * 用於活動詳情頁顯示
 */
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

    // ========== Getter & Setter ==========

    public EventVO getEvent() {
        return event;
    }

    public void setEvent(EventVO event) {
        this.event = event;
    }

    public List<EventImageVO> getImages() {
        return images;
    }

    public void setImages(List<EventImageVO> images) {
        this.images = images;
    }

    public List<TicketVO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketVO> tickets) {
        this.tickets = tickets;
    }

    public OrganizerVO getOrganizer() {
        return organizer;
    }

    public void setOrganizer(OrganizerVO organizer) {
        this.organizer = organizer;
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

    public List<EventListItemDTO> getRelatedEvents() {
        return relatedEvents;
    }

    public void setRelatedEvents(List<EventListItemDTO> relatedEvents) {
        this.relatedEvents = relatedEvents;
    }
}
