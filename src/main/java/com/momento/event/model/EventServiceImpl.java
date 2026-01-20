package com.momento.event.model;

import com.momento.event.dto.*;
import com.momento.eventfav.model.EventFavVO;
import com.momento.eventfav.model.EventFavRepository;
import com.momento.ticket.model.TicketService;
import com.momento.ticket.model.TicketVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Event Service 實作
 * 實作活動相關的業務邏輯
 * 
 * 注意：票種 (Ticket) 相關功能由 ticket 模組負責
 */
@Service
@Transactional
public class EventServiceImpl implements EventService {

        @Autowired
        private EventRepository eventRepository;

        @Autowired
        private EventImageRepository eventImageRepository;

        @Autowired
        private EventFavRepository eventFavRepository;

        @Autowired
        private TicketService ticketService;

        // 常數：已上架且審核通過
        private static final Byte STATUS_PUBLISHED = 1;
        private static final Byte REVIEW_STATUS_APPROVED = 1;

        @Override
        public Page<EventListItemDTO> getAllEvents(int page, int size, String sort) {
                // 建立分頁與排序
                Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

                // 查詢已上架活動
                Page<EventVO> eventPage = eventRepository.findByStatusAndReviewStatus(
                                STATUS_PUBLISHED,
                                REVIEW_STATUS_APPROVED,
                                pageable);

                // 轉換為 DTO
                return eventPage.map(this::convertToListItemDTO);
        }

        @Override
        public Page<EventListItemDTO> filterEvents(EventFilterDTO filterDTO) {
                // 建立分頁與排序
                Sort.Direction direction = "DESC".equalsIgnoreCase(filterDTO.getDirection())
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC;
                Pageable pageable = PageRequest.of(
                                filterDTO.getPage(),
                                filterDTO.getSize(),
                                Sort.by(direction, filterDTO.getSort()));

                // 執行複合篩選查詢
                Page<EventVO> eventPage = eventRepository.filterEvents(
                                STATUS_PUBLISHED,
                                REVIEW_STATUS_APPROVED,
                                filterDTO.getTypeId(),
                                filterDTO.getPlace(),
                                filterDTO.getStartDate(),
                                filterDTO.getEndDate(),
                                filterDTO.getMinPrice(),
                                filterDTO.getMaxPrice(),
                                pageable);

                // 轉換為 DTO
                return eventPage.map(this::convertToListItemDTO);
        }

        @Override
        public Page<EventListItemDTO> searchEvents(String keyword, int page, int size) {
                Pageable pageable = PageRequest.of(page, size,
                                Sort.by("eventAt").ascending());

                Page<EventVO> eventPage = eventRepository
                                .findByStatusAndReviewStatusAndTitleContainingOrContentContaining(
                                                STATUS_PUBLISHED,
                                                REVIEW_STATUS_APPROVED,
                                                keyword,
                                                keyword,
                                                pageable);

                return eventPage.map(this::convertToListItemDTO);
        }

        @Override
        public EventDetailDTO getEventDetail(Integer eventId, Integer memberId) {
                // 查詢活動
                EventVO event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new RuntimeException("活動不存在"));

                // 查詢圖片 (按 ID 排序,確保與列表頁一致)
                List<EventImageVO> images = eventImageRepository.findByEvent_EventIdOrderByEventImageIdAsc(eventId);

                // 查詢收藏數量
                Long favoriteCount = eventFavRepository.countByEvent_EventId(eventId);

                // 檢查是否已收藏
                Boolean isFavorited = memberId != null &&
                                eventFavRepository.existsByMember_MemberIdAndEvent_EventId(memberId, eventId);

                // 查詢相關活動
                List<EventListItemDTO> relatedEvents = getRelatedEvents(eventId, 3);

                // 查詢票種資訊
                List<TicketVO> tickets = ticketService.getAvailableTickets(eventId);
                Integer minPrice = ticketService.getMinPrice(eventId);
                Integer maxPrice = ticketService.getMaxPrice(eventId);

                // 組裝 DTO
                EventDetailDTO dto = new EventDetailDTO();
                dto.setEvent(event);
                dto.setImages(images);
                dto.setOrganizer(event.getOrganizer());
                dto.setFavoriteCount(favoriteCount);
                dto.setIsFavorited(isFavorited);
                dto.setRelatedEvents(relatedEvents);
                dto.setTickets(tickets);
                dto.setMinPrice(minPrice);
                dto.setMaxPrice(maxPrice);

                return dto;
        }

        @Override
        public List<EventListItemDTO> getRelatedEvents(Integer eventId, int limit) {
                // 查詢當前活動
                EventVO currentEvent = eventRepository.findById(eventId)
                                .orElseThrow(() -> new RuntimeException("活動不存在"));

                // 查詢同類型的其他活動
                Pageable pageable = PageRequest.of(0, limit + 1);
                Page<EventVO> relatedPage = eventRepository
                                .findByStatusAndReviewStatusAndType_TypeId(
                                                STATUS_PUBLISHED,
                                                REVIEW_STATUS_APPROVED,
                                                currentEvent.getType().getTypeId(),
                                                pageable);

                // 排除當前活動
                return relatedPage.getContent().stream()
                                .filter(e -> !e.getEventId().equals(eventId))
                                .limit(limit)
                                .map(this::convertToListItemDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public List<EventListItemDTO> getOrganizerEvents(Integer organizerId, int limit) {
                List<EventVO> events = eventRepository
                                .findByOrganizer_OrganizerIdAndStatusAndReviewStatus(
                                                organizerId,
                                                STATUS_PUBLISHED,
                                                REVIEW_STATUS_APPROVED);

                return events.stream()
                                .limit(limit)
                                .map(this::convertToListItemDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public boolean toggleFavorite(Integer eventId, Integer memberId) {
                // 檢查是否已收藏
                Optional<EventFavVO> existing = eventFavRepository
                                .findByMember_MemberIdAndEvent_EventId(memberId, eventId);

                if (existing.isPresent()) {
                        // 已收藏 → 取消收藏
                        eventFavRepository.delete(existing.get());
                        return false;
                } else {
                        // 未收藏 → 新增收藏
                        EventVO event = eventRepository.findById(eventId)
                                        .orElseThrow(() -> new RuntimeException("活動不存在"));

                        // 臨時方案：創建只包含 ID 的 MemberVO
                        com.momento.member.model.MemberVO member = new com.momento.member.model.MemberVO();
                        member.setMemberId(memberId);

                        // 使用建構子創建收藏
                        EventFavVO fav = new EventFavVO(event, member);
                        eventFavRepository.save(fav);
                        return true;
                }
        }

        @Override
        public Long getFavoriteCount(Integer eventId) {
                return eventFavRepository.countByEvent_EventId(eventId);
        }

        @Override
        public List<EventListItemDTO> getMemberFavorites(Integer memberId) {
                List<EventFavVO> favorites = eventFavRepository.findByMember_MemberId(memberId);

                return favorites.stream()
                                .map(fav -> convertToListItemDTO(fav.getEvent()))
                                .collect(Collectors.toList());
        }

        // ========== 私有輔助方法 ==========

        /**
         * 將 EventVO 轉換為 EventListItemDTO
         * 🔥 修改：使用 Picsum 網路圖片
         */
        private EventListItemDTO convertToListItemDTO(EventVO event) {
                EventListItemDTO dto = new EventListItemDTO();
                dto.setEventId(event.getEventId());
                dto.setTitle(event.getTitle());
                dto.setPlace(event.getPlace());
                dto.setEventAt(event.getEventAt());
                dto.setTypeName(event.getType().getTypeName());
                dto.setOrganizerName(event.getOrganizer().getName());

                // 查詢封面圖片 URL
                Optional<EventImageVO> coverImage = eventImageRepository
                                .findFirstByEvent_EventIdOrderByEventImageIdAsc(event.getEventId());

                dto.setCoverImageUrl(
                                coverImage.isPresent() && coverImage.get().getImageUrl() != null
                                                ? coverImage.get().getImageUrl()
                                                : "https://picsum.photos/seed/evento" + event.getEventId()
                                                                + "/800/450");

                // 查詢收藏數量
                Long favoriteCount = eventFavRepository.countByEvent_EventId(event.getEventId());
                dto.setFavoriteCount(favoriteCount);

                // 查詢最低票價
                Integer minPrice = ticketService.getMinPrice(event.getEventId());
                dto.setMinPrice(minPrice);

                return dto;
        }
}