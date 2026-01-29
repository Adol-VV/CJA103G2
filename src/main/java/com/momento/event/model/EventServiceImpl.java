package com.momento.event.model;

import com.momento.event.dto.*;
import com.momento.eventfav.model.EventFavVO;
import com.momento.eventfav.model.EventFavRepository;
import com.momento.ticket.model.TicketService;
import com.momento.ticket.model.TicketVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Event Service 實作
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
        private com.momento.member.model.MemberRepository memberRepository;

        @Autowired
        private TicketService ticketService;

        @Autowired
        private com.momento.prod.model.ProdService prodService;

        @Override
        public Page<EventListItemDTO> getAllEvents(int page, int size, String sort) {
                // 僅顯示 STATUS=3 (上架中) 且 已過上架時間
                Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
                Page<EventVO> eventPage = eventRepository.findAvailableEvents(EventVO.STATUS_PUBLISHED,
                                java.time.LocalDateTime.now(), pageable);
                return eventPage.map(this::convertToListItemDTO);
        }

        @Override
        public Page<EventListItemDTO> filterEvents(EventFilterDTO filterDTO) {
                Sort.Direction direction = "DESC".equalsIgnoreCase(filterDTO.getDirection()) ? Sort.Direction.DESC
                                : Sort.Direction.ASC;
                Pageable pageable = PageRequest.of(filterDTO.getPage(), filterDTO.getSize(),
                                Sort.by(direction, filterDTO.getSort()));

                // 僅顯示已上架 (3)
                java.util.List<Byte> statuses = java.util.List.of(EventVO.STATUS_PUBLISHED);
                Page<EventVO> eventPage = eventRepository.filterEvents(
                                statuses,
                                filterDTO.getTypeId(),
                                filterDTO.getPlace(),
                                filterDTO.getStartDate(),
                                filterDTO.getEndDate(),
                                filterDTO.getMinPrice(),
                                filterDTO.getMaxPrice(),
                                filterDTO.getOnSaleOnly(),
                                java.time.LocalDateTime.now(),
                                pageable);

                return eventPage.map(this::convertToListItemDTO);
        }

        @Override
        public Page<EventListItemDTO> searchEvents(String keyword, int page, int size) {
                Pageable pageable = PageRequest.of(page, size, Sort.by("eventStartAt").ascending());
                Page<EventVO> eventPage = eventRepository.searchAvailableEvents(
                                EventVO.STATUS_PUBLISHED, keyword, java.time.LocalDateTime.now(), pageable);
                return eventPage.map(this::convertToListItemDTO);
        }

        @Override
        public EventDetailDTO getEventDetail(Integer eventId, Integer memberId) {
                EventVO event = eventRepository.findById(java.util.Objects.requireNonNull(eventId))
                                .orElseThrow(() -> new RuntimeException("活動不存在"));
                List<EventImageVO> images = eventImageRepository.findByEvent_EventIdOrderByEventImageIdAsc(eventId);
                Long favoriteCount = eventFavRepository.countByEvent_EventId(eventId);
                Boolean isFavorited = memberId != null
                                && eventFavRepository.existsByMember_MemberIdAndEvent_EventId(memberId, eventId);
                List<EventListItemDTO> relatedEvents = getRelatedEvents(eventId, 3);
                List<TicketVO> tickets = ticketService.getTicketsByEventId(eventId);
                Integer minPrice = ticketService.getMinPrice(eventId);
                Integer maxPrice = ticketService.getMaxPrice(eventId);

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

                // 抓取同主辦的相關商品
                if (event.getOrganizer() != null) {
                        List<com.momento.prod.dto.ProdDTO> relatedProducts = prodService
                                        .getProdsByOrg(event.getOrganizer().getOrganizerId());

                        // 嘗試過濾已上架並通過審核的商品
                        List<com.momento.prod.dto.ProdDTO> filteredProducts = relatedProducts.stream()
                                        .filter(p -> p.getProdStatus() != null && p.getProdStatus().contains("上架")
                                                        && p.getReviewStatus() != null
                                                        && p.getReviewStatus().contains("通過"))
                                        .limit(4)
                                        .collect(Collectors.toList());

                        // 如果過濾後為空，且原始列表不為空，則顯示原始列表前4筆 (供開發測試用)
                        if (filteredProducts.isEmpty() && !relatedProducts.isEmpty()) {
                                filteredProducts = relatedProducts.stream().limit(4).collect(Collectors.toList());
                        }

                        dto.setRelatedProducts(filteredProducts);
                }

                return dto;
        }

        @Override
        public List<EventListItemDTO> getRelatedEvents(Integer eventId, int limit) {
                EventVO currentEvent = eventRepository.findById(eventId)
                                .orElseThrow(() -> new RuntimeException("活動不存在"));
                Pageable pageable = PageRequest.of(0, limit + 1);
                Page<EventVO> relatedPage = eventRepository.findAvailableEventsByType(
                                EventVO.STATUS_PUBLISHED,
                                currentEvent.getType().getTypeId(),
                                java.time.LocalDateTime.now(),
                                pageable);

                return relatedPage.getContent().stream()
                                .filter(e -> !e.getEventId().equals(eventId))
                                .limit(limit)
                                .map(this::convertToListItemDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public List<EventListItemDTO> getOrganizerEvents(Integer organizerId, int limit) {
                List<EventVO> events = eventRepository.findByOrganizer_OrganizerIdAndStatus(organizerId,
                                EventVO.STATUS_PUBLISHED);
                return events.stream().limit(limit).map(this::convertToListItemDTO).collect(Collectors.toList());
        }

        @Transactional
        public boolean toggleFavorite(Integer eventId, Integer memberId) {
                Optional<EventFavVO> existing = eventFavRepository.findByMember_MemberIdAndEvent_EventId(memberId,
                                eventId);
                if (existing.isPresent()) {
                        eventFavRepository.delete(existing.get());
                        eventFavRepository.flush();
                        return false;
                } else {
                        EventVO event = eventRepository.findById(eventId)
                                        .orElseThrow(() -> new RuntimeException("活動不存在"));
                        com.momento.member.model.MemberVO member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new RuntimeException("會員不存在"));
                        EventFavVO fav = new EventFavVO(event, member);
                        eventFavRepository.save(fav);
                        eventFavRepository.flush();
                        return true;
                }
        }

        @Override
        public Long getFavoriteCount(Integer eventId) {
                return eventFavRepository.countByEvent_EventId(eventId);
        }

        @Override
        @org.springframework.transaction.annotation.Transactional(readOnly = true)
        public List<EventListItemDTO> getMemberFavorites(Integer memberId) {
                eventFavRepository.flush();
                List<EventFavVO> favorites = eventFavRepository.findByMember_MemberId(memberId);
                return favorites.stream().map(fav -> convertToListItemDTO(fav.getEvent())).collect(Collectors.toList());
        }

        @Override
        @org.springframework.transaction.annotation.Transactional(readOnly = true)
        public Long getMemberFavoriteCount(Integer memberId) {
                return eventFavRepository.countByMember_MemberId(memberId);
        }

        private EventListItemDTO convertToListItemDTO(EventVO event) {
                EventListItemDTO dto = new EventListItemDTO();
                dto.setEventId(event.getEventId());
                dto.setTitle(event.getTitle());
                dto.setPlace(event.getPlace());
                dto.setSaleStartAt(event.getSaleStartAt());
                dto.setSaleEndAt(event.getSaleEndAt());
                dto.setEventStartAt(event.getEventStartAt());
                dto.setEventEndAt(event.getEventEndAt());
                dto.setStatus(event.getStatus());
                dto.setTypeName(event.getType() != null ? event.getType().getTypeName() : null);
                dto.setOrganizerName(event.getOrganizer().getName());
                dto.setOrganizerId(event.getOrganizer().getOrganizerId());

                Optional<EventImageVO> coverImage = eventImageRepository
                                .findFirstByEvent_EventIdOrderByEventImageIdAsc(event.getEventId());
                dto.setCoverImageUrl(coverImage.isPresent() && coverImage.get().getImageUrl() != null
                                ? coverImage.get().getImageUrl()
                                : "https://picsum.photos/seed/evento" + event.getEventId() + "/800/450");
                dto.setFavoriteCount(eventFavRepository.countByEvent_EventId(event.getEventId()));
                dto.setMinPrice(ticketService.getMinPrice(event.getEventId()));

                return dto;
        }
}