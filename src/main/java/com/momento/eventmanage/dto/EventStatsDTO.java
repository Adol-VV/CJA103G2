package com.momento.eventmanage.dto;

import java.io.Serializable;

/**
 * Event Stats DTO
 * 用於傳輸主辦方儀表板的統計數據
 */
public class EventStatsDTO implements Serializable {

    private long activeCount; // 上架中活動 (STATUS=3)
    private long pendingCount; // 待審核活動 (STATUS=1)
    private long totalFavorites; // 總收藏數
    private long rejectedCount; // 已駁回 (STATUS=4)
    private long endedCount; // 已結束/取消 (STATUS=5)
    private long approvedCount; // 審核成功 (STATUS=2)
    private long draftCount; // 草稿箱 (STATUS=0)
    private long allCount; // 全部 (包含草稿)

    public EventStatsDTO() {
    }

    public EventStatsDTO(long activeCount, long pendingCount, long totalFavorites, long rejectedCount,
            long endedCount, long approvedCount, long draftCount, long allCount) {
        this.activeCount = activeCount;
        this.pendingCount = pendingCount;
        this.totalFavorites = totalFavorites;
        this.rejectedCount = rejectedCount;
        this.endedCount = endedCount;
        this.approvedCount = approvedCount;
        this.draftCount = draftCount;
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

    public long getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(long approvedCount) {
        this.approvedCount = approvedCount;
    }

    public long getDraftCount() {
        return draftCount;
    }

    public void setDraftCount(long draftCount) {
        this.draftCount = draftCount;
    }

    public long getAllCount() {
        return allCount;
    }

    public void setAllCount(long allCount) {
        this.allCount = allCount;
    }
}
