package com.momento.eventreview.model;

import com.momento.notify.model.NotificationBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.momento.event.model.EventRepository;
import com.momento.event.model.EventVO;
import com.momento.notify.model.OrganizerNotifyRepository;
import com.momento.notify.model.OrganizerNotifyVO;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@Service
public class EventReviewService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizerNotifyRepository organizerNotifyRepository;

    @Autowired //pei
    private NotificationBridgeService bridgeService;

    public List<EventVO> getEventsByTab(String tabType, String keyword) {
        if (tabType == null)
            tabType = "all";

        switch (tabType.toLowerCase()) {
            case "all":
                return eventRepository.searchAdminEvents(List.of(
                        EventVO.STATUS_PENDING, EventVO.STATUS_APPROVED, EventVO.STATUS_PUBLISHED,
                        EventVO.STATUS_REJECTED, EventVO.STATUS_CLOSED), keyword);
            case "pending":
            case "1":
                return eventRepository.searchAdminEvents(List.of(EventVO.STATUS_PENDING), keyword);
            case "rejected":
            case "4":
                return eventRepository.searchAdminEvents(List.of(EventVO.STATUS_REJECTED), keyword);
            case "approved":
            case "2":
                return eventRepository.searchAdminEvents(List.of(EventVO.STATUS_APPROVED), keyword);
            case "published":
            case "3":
                return eventRepository.searchAdminEvents(List.of(EventVO.STATUS_PUBLISHED), keyword);
            case "ended":
            case "5":
                return eventRepository.searchAdminEvents(List.of(EventVO.STATUS_CLOSED), keyword);
            default:
                // 如果找不到對應標籤，嘗試當作單一狀態查詢
                try {
                    byte status = Byte.parseByte(tabType);
                    return eventRepository.searchAdminEvents(List.of(status), keyword);
                } catch (NumberFormatException e) {
                    return eventRepository.searchAdminEvents(null, keyword);
                }
        }
    }

    public EventVO getEventById(Integer id) {
        return eventRepository.findById(id).orElse(null);
    }

    @Transactional
    public void approveEvent(Integer eventId) {
        Optional<EventVO> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            EventVO event = eventOpt.get();
            if (!event.isPending())
                throw new RuntimeException("活動非待審核狀態");
            event.setStatus(EventVO.STATUS_APPROVED); // 審核通過，等待主辦設定時間
            eventRepository.save(event);

            // pei
            bridgeService.processEventReviewNotify(event, true, null);
        } else {
            throw new RuntimeException("活動不存在: " + eventId);
        }
    }

    @Transactional
    public void rejectEvent(Integer eventId, String reason, com.momento.emp.model.EmpVO emp) {
        Optional<EventVO> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            EventVO event = eventOpt.get();
            if (!event.isPending())
                throw new RuntimeException("活動非待審核狀態");
            event.setStatus(EventVO.STATUS_REJECTED);
            event.setPublishedAt(null);
            eventRepository.save(event);

            OrganizerNotifyVO notify = new OrganizerNotifyVO();
            notify.setOrganizerVO(event.getOrganizer());
            notify.setEmpVO(emp);
            notify.setTitle("活動審核未通過通知: " + event.getTitle());
            notify.setContent("您的活動「" + event.getTitle() + "」未能通過審核。\n退回原因: " + reason);
            notify.setNotifyStatus(0);
            notify.setTargetId(String.valueOf(eventId));
            organizerNotifyRepository.save(notify);

//            // pei
//            bridgeService.processEventReviewNotify(event, false, reason);
        } else {
            throw new RuntimeException("活動不存在: " + eventId);
        }
    }

    public Map<String, Long> getReviewStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("pending", eventRepository.countByStatus(EventVO.STATUS_PENDING));
        stats.put("rejected", eventRepository.countByStatus(EventVO.STATUS_REJECTED));
        stats.put("approved", eventRepository.countByStatus(EventVO.STATUS_APPROVED));
        stats.put("published", eventRepository.countByStatus(EventVO.STATUS_PUBLISHED));
        stats.put("ended", eventRepository.countByStatus(EventVO.STATUS_CLOSED));
        stats.put("all", eventRepository.countByStatusIn(List.of(
                EventVO.STATUS_PENDING, EventVO.STATUS_REJECTED, EventVO.STATUS_APPROVED,
                EventVO.STATUS_PUBLISHED, EventVO.STATUS_CLOSED)));
        return stats;
    }
}