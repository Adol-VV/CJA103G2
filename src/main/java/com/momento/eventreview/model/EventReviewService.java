package com.momento.eventreview.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.momento.event.model.EventRepository;
import com.momento.event.model.EventVO;
import com.momento.notify.model.OrganizerNotifyRepository;
import com.momento.notify.model.OrganizerNotifyVO;

import java.util.List;
import java.util.Optional;

@Service
public class EventReviewService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizerNotifyRepository organizerNotifyRepository;

    /**
     * 根據 Tab 類型取得活動列表
     * type: pending, rejected, approved
     */
    /**
     * 根據 Tab 類型取得活動列表
     * type: pending, rejected, approved
     */
    public List<EventVO> getEventsByTab(String tabType, String keyword) {
        switch (tabType) {
            case "all":
                // 全部: 非草稿 (由 Repository query 過濾)
                return eventRepository.searchAdminEvents(null, null, keyword);
            case "pending":
                // 待審核: S=0, R=0, P!=null
                return eventRepository.searchAdminEvents(java.util.List.of((byte) 0), (byte) 0, keyword);
            case "rejected":
                // 已駁回: S=0, R=2
                return eventRepository.searchAdminEvents(java.util.List.of((byte) 0), (byte) 2, keyword);
            case "approved":
                // 上架中: S=1
                return eventRepository.searchAdminEvents(java.util.List.of((byte) 1), null, keyword);
            case "ended":
                // 已結束/取消: S=2,3
                return eventRepository.searchAdminEvents(java.util.List.of((byte) 2, (byte) 3), null, keyword);
            default:
                return List.of();
        }
    }

    public EventVO getEventById(Integer id) {
        return eventRepository.findById(id).orElse(null);
    }

    /**
     * 批准活動
     */
    @Transactional
    public void approveEvent(Integer eventId) {
        Optional<EventVO> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            EventVO event = eventOpt.get();
            event.setStatus((byte) 1); // 已上架
            event.setReviewStatus((byte) 1); // 審核通過
            eventRepository.save(event);
        } else {
            throw new RuntimeException("活動不存在: " + eventId);
        }
    }

    /**
     * 駁回活動
     */
    @Transactional
    public void rejectEvent(Integer eventId, String reason) {
        Optional<EventVO> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            EventVO event = eventOpt.get();
            event.setStatus((byte) 0);
            event.setReviewStatus((byte) 2);
            event.setPublishedAt(null); // 清空送審時間
            eventRepository.save(event);

            // 發送通知
            OrganizerNotifyVO notify = new OrganizerNotifyVO();
            notify.setOrganizerVO(event.getOrganizer());
            notify.setTitle("活動審核未通過通知: " + event.getTitle());
            notify.setContent("您的活動「" + event.getTitle() + "」未能通過審核。\n退回原因: " + reason);
            notify.setNotifyStatus(0);
            notify.setTargetId(String.valueOf(eventId));

            organizerNotifyRepository.save(notify);

        } else {
            throw new RuntimeException("活動不存在: " + eventId);
        }
    }

    /**
     * 取得各審核狀態的統計數量
     */
    public java.util.Map<String, Long> getReviewStats() {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();

        // Pending: S=0, R=0, P!=null
        stats.put("pending", eventRepository.countByStatusAndReviewStatusAndPublishedAtIsNotNull((byte) 0, (byte) 0));

        // Rejected: S=0, R=2
        stats.put("rejected", eventRepository.countByStatusAndReviewStatus((byte) 0, (byte) 2));

        // Approved (Published): S=1
        stats.put("approved", eventRepository.countByStatusAndReviewStatus((byte) 1, (byte) 1));

        // Ended/Cancelled: S=2,3
        stats.put("ended",
                eventRepository.countByStatusIn(java.util.List.of((byte) 2, (byte) 3)));

        // Total (Non-draft)
        stats.put("all", stats.get("pending") + stats.get("rejected") + stats.get("approved") + stats.get("ended"));

        return stats;
    }
}