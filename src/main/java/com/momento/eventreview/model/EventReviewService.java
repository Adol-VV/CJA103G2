package com.momento.eventreview.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.momento.event.model.EventRepository;
import com.momento.event.model.EventVO;
import com.momento.notify.model.OrganizerNotifyRepository;
import com.momento.notify.model.OrganizerNotifyVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventReviewService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizerNotifyRepository organizerNotifyRepository;

    /**
     * 取得所有待審核的活動
     * status = 0 (草稿/下架) AND reviewStatus = 1 (待審核)
     */
    /**
     * 根據 Tab 類型取得活動列表
     * type: pending, rejected, approved
     */
    public List<EventVO> getEventsByTab(String tabType) {
        List<EventVO> allEvents = eventRepository.findAll();

        switch (tabType) {
            case "pending":
                return allEvents.stream()
                        .filter(this::isPending)
                        .toList();
            case "rejected":
                return allEvents.stream()
                        .filter(this::isRejected)
                        .toList();
            case "approved":
                return allEvents.stream()
                        .filter(this::isApproved)
                        .toList();
            default:
                return List.of();
        }
    }

    private boolean isPending(EventVO e) {
        // 專案慣例: status=1 為待審核
        return e.getStatus() != null && e.getStatus() == 1 && (e.getReviewStatus() == null || e.getReviewStatus() == 0);
    }

    private boolean isRejected(EventVO e) {
        // 專案慣例: status=0 且 reviewStatus=2 為已駁回
        return e.getStatus() != null && e.getStatus() == 0 && e.getReviewStatus() != null && e.getReviewStatus() == 2;
    }

    private boolean isApproved(EventVO e) {
        // 專案慣例: status=2 為已上架 (已通過)
        return e.getStatus() != null && e.getStatus() == 2;
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
            event.setStatus((byte) 2); // 2: 已上架
            event.setReviewStatus((byte) 1); // 1: 審核通過
            event.setPublishedAt(LocalDateTime.now());
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
            event.setStatus((byte) 0); // 0: 草稿 (退回修改)
            event.setReviewStatus((byte) 2); // 2: 審核未通過 (已駁回)
            eventRepository.save(event);

            // 發送通知
            OrganizerNotifyVO notify = new OrganizerNotifyVO();
            notify.setOrganizerVO(event.getOrganizer());
            notify.setTitle("活動審核未通過通知: " + event.getTitle());
            notify.setContent("您的活動「" + event.getTitle() + "」未能通過審核。\n退回原因: " + reason);
            notify.setNotifyStatus(0); // 0: 正常
            // targetId 可以存 eventId 方便前端跳轉，但 VO 定義是 String
            notify.setTargetId(String.valueOf(eventId));

            // EmpVO 暫時不設，或是需要從 Controller 傳入當前登入 Admin (這裡先略過)

            organizerNotifyRepository.save(notify);

        } else {
            throw new RuntimeException("活動不存在: " + eventId);
        }
    }

    /**
     * 取得各審核狀態的統計數量
     */
    public java.util.Map<String, Long> getReviewStats() {
        List<EventVO> allEvents = eventRepository.findAll();
        java.util.Map<String, Long> stats = new java.util.HashMap<>();

        stats.put("pending", allEvents.stream().filter(this::isPending).count());
        stats.put("rejected", allEvents.stream().filter(this::isRejected).count());
        stats.put("approved", allEvents.stream().filter(this::isApproved).count());

        return stats;
    }
}
