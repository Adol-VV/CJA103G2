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
    public List<EventVO> getEventsByTab(String tabType) {
        switch (tabType) {
            case "pending":
                // 待審核: S=0, R=0, P!=null
                return eventRepository.findByStatusAndReviewStatusAndPublishedAtIsNotNull((byte) 0, (byte) 0);
            case "rejected":
                // 已駁回: S=0, R=2
                return eventRepository.findByStatusAndReviewStatus((byte) 0, (byte) 2);
            case "approved":
                // 已通過: S=1, R=1
                return eventRepository.findByStatusAndReviewStatus((byte) 1, (byte) 1);
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

        // Approved: S=1, R=1
        stats.put("approved", eventRepository.countByStatusAndReviewStatus((byte) 1, (byte) 1));

        return stats;
    }
}