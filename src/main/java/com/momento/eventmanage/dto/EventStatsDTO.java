package com.momento.eventmanage.dto;

import java.io.Serializable;

/**
 * Event Stats DTO
 * 用於傳輸主辦方儀表板的統計數據
 */
public class EventStatsDTO implements Serializable {

    private long activeCount; // 進行中活動 (Status=1, Review=1)
    private long pendingCount; // 待審核活動 (Status=0, Review=0, P!=null)
    private long totalFavorites; // 總收藏數
    private long rejectedCount; // 已駁回 (Status=0, Review=2)
    private long endedCount; // 已結束/取消 (Status=2,3)
    private long allCount; // 全部 (非草稿)

    public EventStatsDTO() {
    }

    public EventStatsDTO(long activeCount, long pendingCount, long totalFavorites, long rejectedCount,
            long endedCount, long allCount) {
        this.activeCount = activeCount;
        this.pendingCount = pendingCount;
        this.totalFavorites = totalFavorites;
        this.rejectedCount = rejectedCount;
        this.endedCount = endedCount;
        this.allCount = allCount;
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

    public long getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(long rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public long getEndedCount() {
        return endedCount;
    }

    public void setEndedCount(long endedCount) {
        this.endedCount = endedCount;
    }

    public long getAllCount() {
        return allCount;
    }

    public void setAllCount(long allCount) {
        this.allCount = allCount;
    }
}
