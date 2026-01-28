package com.momento.event.scheduled;

import com.momento.event.model.EventRepository;
import com.momento.event.model.EventVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活動排程任務
 */
@Component
public class EventScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(EventScheduledTask.class);

    @Autowired
    private EventRepository eventRepository;

    /**
     * 每小時整點執行一次，自動下架已結束的活動
     * Cron: 0 0 * * * *
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void autoCloseExpiredEvents() {
        logger.info("[Scheduled] 開始執行自動下架已結束活動任務...");

        LocalDateTime now = LocalDateTime.now();

        // 查詢條件: STATUS = 3 (已上架) AND EVENT_END_AT < NOW
        List<EventVO> expiredEvents = eventRepository.findByStatusAndEventEndAtBefore(EventVO.STATUS_PUBLISHED, now);

        if (!expiredEvents.isEmpty()) {
            for (EventVO event : expiredEvents) {
                event.setStatus(EventVO.STATUS_CLOSED);
            }

            eventRepository.saveAll(expiredEvents);

            String closedIds = expiredEvents.stream()
                    .map(e -> String.valueOf(e.getEventId()))
                    .collect(Collectors.joining(", "));

            logger.info("[Scheduled] 自動下架完成。數量: {}, ID列表: [{}]", expiredEvents.size(), closedIds);
        } else {
            logger.info("[Scheduled] 無符合下架條件的活動。");
        }
    }
}
