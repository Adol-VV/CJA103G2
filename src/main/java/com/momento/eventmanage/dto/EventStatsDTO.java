package com.momento.eventmanage.dto;

import java.io.Serializable;

/**
 * Event Stats DTO
 * 用於傳輸主辦方儀表板的統計數據
 */
public class EventStatsDTO implements Serializable {

    private long activeCount; // 進行中活動 (Status=1, Review=2)
    private long pendingCount; // 待審核活動 (Status=0, Review=1)
    private long totalFavorites; // 總收藏數
    // private long totalRevenue; // 營收 (待整合)
    // private long totalOrders; // 訂單 (待整合)

    public EventStatsDTO() {
    }

    public EventStatsDTO(long activeCount, long pendingCount, long totalFavorites) {
        this.activeCount = activeCount;
        this.pendingCount = pendingCount;
        this.totalFavorites = totalFavorites;
    }

    public long getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(long activeCount) {
        this.activeCount = activeCount;
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public long getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(long totalFavorites) {
        this.totalFavorites = totalFavorites;
    }
}
