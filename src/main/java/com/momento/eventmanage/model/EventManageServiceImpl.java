package com.momento.eventmanage.model;

import com.momento.event.model.*;
import com.momento.eventmanage.dto.EventCreateDTO;
import com.momento.ticket.model.TicketVO;
import com.momento.ticket.model.TicketRepository;
import com.momento.organizer.model.OrganizerVO;
import com.momento.organizer.model.OrganizerRepository;
import com.momento.notify.model.OrganizerNotifyRepository;
import com.momento.notify.model.OrganizerNotifyVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Event Manage Service Implementation - 主辦方活動管理服務實作
 */
@Service
public class EventManageServiceImpl implements EventManageService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventImageRepository eventImageRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private OrganizerNotifyRepository organizerNotifyRepository;

    @Override
    @Transactional
    public Integer saveDraft(EventCreateDTO dto) {
        return createEvent(dto);
    }

    @Override
    @Transactional
    public Integer createEvent(EventCreateDTO dto) {
        // 1. 建立活動實體 (初始狀態為草稿)
        EventVO event = new EventVO();

        if (dto.getOrganizerId() == null)
            throw new RuntimeException("主辦方ID不可為空");
        OrganizerVO organizer = organizerRepository.findById(dto.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("主辦方不存在"));
        event.setOrganizer(organizer);

        if (dto.getTypeId() != null) {
            TypeVO type = typeRepository.findById(dto.getTypeId()).orElse(null);
            event.setType(type);
        }

        event.setTitle(dto.getTitle());
        event.setPlace(dto.getPlace());
        event.setContent(dto.getContent());
        event.setStatus(EventVO.STATUS_DRAFT);

        // 儲存活動
        EventVO savedEvent = eventRepository.save(event);

        // 2. 儲存圖片
        if (dto.getBannerUrl() != null) {
            saveEventImage(savedEvent, dto.getBannerUrl(), 0);
        }
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            int order = 1;
            for (String imageUrl : dto.getImageUrls()) {
                saveEventImage(savedEvent, imageUrl, order++);
            }
        }

        // 3. 儲存票種
        if (dto.getTickets() != null && !dto.getTickets().isEmpty()) {
            for (EventCreateDTO.TicketDTO ticketDTO : dto.getTickets()) {
                TicketVO ticket = new TicketVO();
                ticket.setEvent(savedEvent);
                ticket.setTicketName(ticketDTO.getName());
                ticket.setPrice(ticketDTO.getPrice());
                ticket.setTotal(ticketDTO.getTotal());
                ticket.setRemain(ticketDTO.getTotal());
                ticketRepository.save(ticket);
            }
        }

        return savedEvent.getEventId();
    }

    private void saveEventImage(EventVO event, String imageUrl, int displayOrder) {
        EventImageVO eventImage = new EventImageVO();
        eventImage.setEvent(event);
        eventImage.setImageUrl(imageUrl);
        eventImageRepository.save(eventImage);
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.momento.config.FileUploadService fileUploadService;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // 呼叫統一的檔案上傳服務，指定類型為 "event"
            return fileUploadService.storeFile(file, "event");
        } catch (Exception e) {
            throw new RuntimeException("活動圖片上傳失敗: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateDraft(com.momento.eventmanage.dto.EventUpdateDTO dto) {
        updateEvent(dto);
    }

    @Override
    @Transactional
    public void updateEvent(com.momento.eventmanage.dto.EventUpdateDTO dto) {
        if (dto.getEventId() == null)
            throw new RuntimeException("活動ID不可為空");
        EventVO event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        // 僅限草稿(0) 或 駁回(4) 狀態可編輯
        if (!event.isDraft() && !event.isRejected()) {
            throw new RuntimeException("目前狀態不允許編輯");
        }

        event.setTitle(dto.getTitle());
        event.setPlace(dto.getPlace());
        event.setContent(dto.getContent());
        if (dto.getTypeId() != null) {
            TypeVO type = typeRepository.findById(dto.getTypeId())
                    .orElseThrow(() -> new RuntimeException("類型不存在"));
            event.setType(type);
        }

        // 圖片處理 (簡化版: 若有新主圖則替換舊的)
        if (dto.getBannerUrl() != null && !dto.getBannerUrl().isEmpty()) {
            eventImageRepository
                    .deleteAll(eventImageRepository.findByEvent_EventIdOrderByEventImageIdAsc(event.getEventId()));
            saveEventImage(event, dto.getBannerUrl(), 0);
        }

        // 票種處理
        if (dto.getTickets() != null) {
            List<TicketVO> currentTickets = ticketRepository.findByEvent_EventId(event.getEventId());
            Set<Integer> incomingIds = dto.getTickets().stream()
                    .map(com.momento.eventmanage.dto.EventUpdateDTO.TicketUpdateDTO::getTicketId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 1. 刪除不再傳入清單中的票種
            for (TicketVO current : currentTickets) {
                if (!incomingIds.contains(current.getTicketId())) {
                    try {
                        ticketRepository.delete(current);
                        ticketRepository.flush(); // 強制檢查約束
                    } catch (Exception e) {
                        throw new RuntimeException("票種 [" + current.getTicketName() + "] 已有訂單關聯，不可刪除。請將其保留在清單中。");
                    }
                }
            }

            // 2. 更新或新增
            for (com.momento.eventmanage.dto.EventUpdateDTO.TicketUpdateDTO tDto : dto.getTickets()) {
                TicketVO ticket;
                if (tDto.getTicketId() != null) {
                    ticket = ticketRepository.findById(tDto.getTicketId())
                            .orElseThrow(() -> new RuntimeException("找不到編號為 " + tDto.getTicketId() + " 的票種"));

                    // 驗證票種是否屬於此活動
                    if (!ticket.getEvent().getEventId().equals(event.getEventId())) {
                        throw new RuntimeException("票種編號不屬於此活動");
                    }

                    // 庫存連動更新
                    int oldTotal = (ticket.getTotal() != null) ? ticket.getTotal() : 0;
                    int diff = tDto.getTotal() - oldTotal;
                    int newRemain = (ticket.getRemain() != null ? ticket.getRemain() : 0) + diff;

                    if (newRemain < 0) {
                        throw new RuntimeException("票種 [" + tDto.getName() + "] 總數不可低於已售出數量");
                    }

                    ticket.setTotal(tDto.getTotal());
                    ticket.setRemain(newRemain);
                } else {
                    ticket = new TicketVO();
                    ticket.setEvent(event);
                    ticket.setTotal(tDto.getTotal());
                    ticket.setRemain(tDto.getTotal());
                }

                ticket.setTicketName(tDto.getName());
                ticket.setPrice(tDto.getPrice());
                ticketRepository.save(ticket);
            }
        }

        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void submitEvent(Integer eventId) {
        EventVO event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        if (!event.isDraft() && !event.isRejected()) {
            throw new RuntimeException("僅限草稿或駁回狀態可送審");
        }

        // 必填欄位檢查
        if (event.getTitle() == null || event.getTitle().isEmpty())
            throw new RuntimeException("標題必填");
        if (event.getContent() == null || event.getContent().isEmpty())
            throw new RuntimeException("內容必填");
        if (event.getPlace() == null || event.getPlace().isEmpty())
            throw new RuntimeException("地點必填");

        List<TicketVO> tickets = ticketRepository.findByEvent_EventId(eventId);
        if (tickets.isEmpty())
            throw new RuntimeException("至少需一個票種");

        event.setStatus(EventVO.STATUS_PENDING);
        event.setPublishedAt(LocalDateTime.now()); // 此時作為「送審時間」
        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void setTimesAndPublish(Integer eventId, LocalDateTime publishedAt, LocalDateTime saleStartAt,
            LocalDateTime saleEndAt, LocalDateTime eventStartAt, LocalDateTime eventEndAt) {
        EventVO event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        if (!event.isApproved())
            throw new RuntimeException("活動尚未通過審核");

        // 時間邏輯驗證
        // publishedAt ≤ saleStartAt < saleEndAt ≤ eventStartAt < eventEndAt
        if (saleStartAt.isBefore(publishedAt))
            throw new RuntimeException("售票開始不可早於上架時間");
        if (!saleEndAt.isAfter(saleStartAt))
            throw new RuntimeException("售票結束必須晚於開始");
        if (eventStartAt.isBefore(saleEndAt))
            throw new RuntimeException("活動開始應晚於售票結束");
        if (!eventEndAt.isAfter(eventStartAt))
            throw new RuntimeException("活動結束必須晚於開始");

        event.setPublishedAt(publishedAt);
        event.setSaleStartAt(saleStartAt);
        event.setSaleEndAt(saleEndAt);
        event.setEventStartAt(eventStartAt);
        event.setEventEndAt(eventEndAt);
        event.setStatus(EventVO.STATUS_PUBLISHED);

        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void withdrawEvent(Integer eventId) {
        EventVO event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        if (!event.isPending())
            throw new RuntimeException("僅限待審核狀態可撤回");

        event.setStatus(EventVO.STATUS_DRAFT);
        event.setPublishedAt(null);
        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void deleteEvent(Integer eventId) {
        EventVO event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        if (!event.isDraft() && !event.isRejected()) {
            throw new RuntimeException("僅限草稿或駁回狀態可刪除");
        }

        ticketRepository.deleteAll(ticketRepository.findByEvent_EventId(eventId));
        eventImageRepository.deleteAll(eventImageRepository.findByEvent_EventIdOrderByEventImageIdAsc(eventId));
        eventRepository.delete(event);
    }

    @Override
    public boolean canEditTicket(Integer ticketId) {
        // MVP 階段暫時允許
        return true;
    }

    @Override
    @Transactional
    public void changeStatus(Integer eventId, Byte status, String reason) {
        EventVO event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("活動不存在"));
        event.setStatus(status);
        eventRepository.save(event);
    }

    @Override
    @Transactional
    public void forceClose(Integer eventId, String reason) {
        changeStatus(eventId, EventVO.STATUS_CLOSED, reason);
    }

    @Override
    public List<EventVO> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Page<EventVO> getOrganizerEvents(Integer organizerId, java.util.Collection<Byte> statuses,
            String keyword, Pageable pageable) {
        if (statuses != null && statuses.isEmpty()) {
            statuses = null;
        }
        return eventRepository.searchOrganizerEvents(organizerId, statuses, keyword, pageable);
    }

    @Autowired
    private com.momento.eventfav.model.EventFavRepository eventFavRepository;

    @Override
    public com.momento.eventmanage.dto.EventStatsDTO getOrganizerStats(Integer organizerId) {
        long activeCount = eventRepository.countByOrganizer_OrganizerIdAndStatus(organizerId, EventVO.STATUS_PUBLISHED);
        long pendingCount = eventRepository.countByOrganizer_OrganizerIdAndStatus(organizerId, EventVO.STATUS_PENDING);
        long totalFavorites = eventFavRepository.countByOrganizerId(organizerId);
        long rejectedCount = eventRepository.countByOrganizer_OrganizerIdAndStatus(organizerId,
                EventVO.STATUS_REJECTED);
        long endedCount = eventRepository.countByOrganizer_OrganizerIdAndStatus(organizerId, EventVO.STATUS_CLOSED);
        long approvedCount = eventRepository.countByOrganizer_OrganizerIdAndStatus(organizerId,
                EventVO.STATUS_APPROVED);
        long allCount = eventRepository.countByOrganizer_OrganizerIdAndStatusNot(organizerId, EventVO.STATUS_DRAFT);

        return new com.momento.eventmanage.dto.EventStatsDTO(activeCount, pendingCount, totalFavorites, rejectedCount,
                endedCount, approvedCount, allCount);
    }

    @Override
    public com.momento.event.dto.EventDetailDTO getEventDetail(Integer eventId) {
        EventVO event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        List<TicketVO> tickets = ticketRepository.findByEvent_EventId(eventId);
        List<EventImageVO> images = eventImageRepository.findByEvent_EventIdOrderByEventImageIdAsc(eventId);

        com.momento.event.dto.EventDetailDTO dto = new com.momento.event.dto.EventDetailDTO();
        dto.setEvent(event);
        dto.setTickets(tickets);
        dto.setImages(images);
        dto.setOrganizer(event.getOrganizer());

        if (event.isRejected()) {
            List<OrganizerNotifyVO> notifies = organizerNotifyRepository
                    .findByOrganizerVO_OrganizerIdAndTitleContainingOrderByCreatedAtDesc(
                            event.getOrganizer().getOrganizerId(),
                            "活動審核未通過通知: " + event.getTitle());

            if (notifies != null && !notifies.isEmpty()) {
                String content = notifies.get(0).getContent();
                if (content != null && content.contains("退回原因: ")) {
                    dto.setRejectReason(content.split("退回原因: ")[1]);
                } else {
                    dto.setRejectReason(content);
                }
            }
        }

        return dto;
    }
}
