package com.momento.event.model;

import com.momento.config.FileUploadService;
import com.momento.emp.model.EmpVO;
import com.momento.eventmanage.dto.EventCreateDTO;
import com.momento.event.dto.EventDetailDTO;
import com.momento.event.dto.EventFilterDTO;
import com.momento.event.dto.EventListItemDTO;
import com.momento.eventmanage.dto.EventUpdateDTO;
import com.momento.eventfav.model.EventFavRepository;
import com.momento.eventfav.model.EventFavVO;
import com.momento.eventmanage.model.EventManageServiceImpl;
import com.momento.eventorder.model.EventOrderRepository;
import com.momento.eventreview.model.EventReviewService;
import com.momento.member.model.MemberRepository;
import com.momento.member.model.MemberVO;
import com.momento.notify.model.OrganizerNotifyRepository;
import com.momento.notify.model.OrganizerNotifyVO;
import com.momento.organizer.model.OrganizerRepository;
import com.momento.organizer.model.OrganizerVO;
import com.momento.ticket.model.TicketRepository;
import com.momento.ticket.model.TicketService;
import com.momento.ticket.model.TicketServiceImpl;
import com.momento.ticket.model.TicketVO;
import com.momento.prod.model.ProdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    // Repositories
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventImageRepository eventImageRepository;
    @Mock
    private EventFavRepository eventFavRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private TypeRepository typeRepository;
    @Mock
    private OrganizerRepository organizerRepository;
    @Mock
    private OrganizerNotifyRepository organizerNotifyRepository;
    @Mock
    private EventOrderRepository eventOrderRepository;
    @Mock
    private FileUploadService fileUploadService;
    @Mock
    private ProdService prodService;

    // Services under test
    @InjectMocks
    private EventServiceImpl eventService;
    @InjectMocks
    private EventManageServiceImpl eventManageService;
    @InjectMocks
    private EventReviewService eventReviewService;
    @InjectMocks
    private TicketServiceImpl ticketServiceImpl;

    @Mock
    private TicketService ticketServiceMock; // Used by EventService mocks

    // Shared Data
    private EventVO eventVO;
    private MemberVO memberVO;
    private OrganizerVO organizerVO;
    private TicketVO ticketVO;
    private EmpVO empVO;

    @BeforeEach
    void setUp() {
        // Basic Setup
        organizerVO = new OrganizerVO();
        organizerVO.setOrganizerId(1);
        organizerVO.setName("Test Organizer");

        eventVO = new EventVO();
        eventVO.setEventId(1);
        eventVO.setTitle("Test Event");
        eventVO.setOrganizer(organizerVO);
        eventVO.setStatus(EventVO.STATUS_PUBLISHED);

        TypeVO typeVO = new TypeVO();
        typeVO.setTypeId(1);
        typeVO.setTypeName("Test Type");
        eventVO.setType(typeVO);

        memberVO = new MemberVO();
        memberVO.setMemberId(1);
        memberVO.setName("Test Member");

        ticketVO = new TicketVO();
        ticketVO.setTicketId(1);
        ticketVO.setTicketName("Early Bird");
        ticketVO.setPrice(1000);
        ticketVO.setRemain(50);
        ticketVO.setTotal(100);
        ticketVO.setEvent(eventVO);

        empVO = new EmpVO();
        empVO.setEmpId(1);
    }

    @Nested
    @DisplayName("1. Event Service (Front-end)")
    class EventHandler {

        @Test
        @DisplayName("Get All Events")
        void testGetAllEvents() {
            Page<EventVO> page = new PageImpl<>(Collections.singletonList(eventVO));
            when(eventRepository.findAvailableEvents(anyByte(), any(LocalDateTime.class), any(Pageable.class)))
                    .thenReturn(page);
            when(eventImageRepository.findFirstByEvent_EventIdOrderByImageOrderAscEventImageIdAsc(1))
                    .thenReturn(Optional.empty());

            Page<EventListItemDTO> result = eventService.getAllEvents(0, 10, "newest");

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("Filter Events")
        void testFilterEvents() {
            EventFilterDTO filterDTO = new EventFilterDTO();
            filterDTO.setPage(0);
            filterDTO.setSize(10);
            filterDTO.setSort("newest");

            Page<EventVO> page = new PageImpl<>(Collections.singletonList(eventVO));
            when(eventRepository.filterEvents(any(), any(), any(), any(), any(), any(), any(),
                    any(LocalDateTime.class), any(Pageable.class)))
                    .thenReturn(page);
            when(eventImageRepository.findFirstByEvent_EventIdOrderByImageOrderAscEventImageIdAsc(1))
                    .thenReturn(Optional.empty());

            Page<EventListItemDTO> result = eventService.filterEvents(filterDTO);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("Get Detail (Not Logged In)")
        void testGetEventDetail_NotLoggedIn() {
            when(eventRepository.findById(1)).thenReturn(Optional.of(eventVO));
            when(ticketServiceMock.getTicketsByEventId(1)).thenReturn(new ArrayList<>());
            when(eventImageRepository.findByEvent_EventIdOrderByImageOrderAscEventImageIdAsc(1))
                    .thenReturn(new ArrayList<>());

            // Mock related events query to prevent NPE in getRelatedEvents
            when(eventRepository.findAvailableEventsByType(
                    anyByte(), anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(new ArrayList<>()));

            // Mock prodService to prevent NPE
            when(prodService.getProdsByOrg(anyInt())).thenReturn(Collections.emptyList());

            EventDetailDTO result = eventService.getEventDetail(1, null);

            assertNotNull(result);
            assertEquals(eventVO.getTitle(), result.getEvent().getTitle());
            assertFalse(result.getIsFavorited());
        }

        @Test
        @DisplayName("Get Detail (Not Found)")
        void testGetEventDetail_NotFound() {
            when(eventRepository.findById(999)).thenReturn(Optional.empty());
            assertThrows(RuntimeException.class, () -> eventService.getEventDetail(999, null));
        }

        @Test
        @DisplayName("Toggle Favorite (Add)")
        void testToggleFavorite_Add() {
            when(eventFavRepository.findByMember_MemberIdAndEvent_EventId(1, 1)).thenReturn(Optional.empty());
            when(memberRepository.findById(1)).thenReturn(Optional.of(memberVO));
            when(eventRepository.findById(1)).thenReturn(Optional.of(eventVO));

            boolean result = eventService.toggleFavorite(1, 1);

            assertTrue(result);
            verify(eventFavRepository).save(any(EventFavVO.class));
        }

        @Test
        @DisplayName("Toggle Favorite (Remove)")
        void testToggleFavorite_Remove() {
            EventFavVO fav = new EventFavVO();
            when(eventFavRepository.findByMember_MemberIdAndEvent_EventId(1, 1)).thenReturn(Optional.of(fav));

            boolean result = eventService.toggleFavorite(1, 1);

            assertFalse(result);
            verify(eventFavRepository).delete(any(EventFavVO.class));
        }

        @Test
        @DisplayName("Get Favorite Count")
        void testGetFavoriteCount() {
            when(eventFavRepository.countByEvent_EventId(1)).thenReturn(5L);
            assertEquals(5L, eventService.getFavoriteCount(1));
        }

        @Test
        @DisplayName("Get Member Favorites")
        void testGetMemberFavorites() {
            EventFavVO fav = new EventFavVO();
            fav.setEvent(eventVO);
            fav.setMember(memberVO);

            when(eventFavRepository.findByMember_MemberId(1)).thenReturn(Collections.singletonList(fav));
            // Mock image repo for DTO conversion which happens inside getMemberFavorites ->
            // convertToListItemDTO
            when(eventImageRepository.findFirstByEvent_EventIdOrderByImageOrderAscEventImageIdAsc(1))
                    .thenReturn(Optional.empty());

            List<EventListItemDTO> result = eventService.getMemberFavorites(1);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(eventVO.getTitle(), result.get(0).getTitle());
        }

        @Test
        @DisplayName("Get Member Favorite Count")
        void testGetMemberFavoriteCount() {
            when(eventFavRepository.countByMember_MemberId(1)).thenReturn(3L);
            Long count = eventService.getMemberFavoriteCount(1);
            assertEquals(3L, count);
        }
    }

    @Nested
    @DisplayName("2. Event Manage Service (Organizer)")
    class EventManageHandler {

        @Test
        @DisplayName("Create Event (Success)")
        void testCreateEvent_Success() {
            EventCreateDTO createDTO = new EventCreateDTO();
            createDTO.setOrganizerId(1);
            createDTO.setTitle("New Event");
            createDTO.setContent("Content");
            createDTO.setPlace("Place");
            createDTO.setTypeId(1);

            EventCreateDTO.TicketDTO t = new EventCreateDTO.TicketDTO();
            t.setName("VIP");
            t.setPrice(100);
            t.setTotal(10);
            createDTO.setTickets(Collections.singletonList(t));

            when(eventRepository.countByOrganizer_OrganizerIdAndStatus(1, EventVO.STATUS_DRAFT)).thenReturn(0L);
            when(organizerRepository.findById(1)).thenReturn(Optional.of(organizerVO));
            when(typeRepository.findById(1)).thenReturn(Optional.of(new TypeVO()));
            when(eventRepository.save(any(EventVO.class))).thenReturn(eventVO);

            Integer eventId = eventManageService.createEvent(createDTO);

            assertNotNull(eventId);
            verify(eventRepository).save(any(EventVO.class));
        }

        @Test
        @DisplayName("Create Event (Draft Limit)")
        void testCreateEvent_DraftLimit() {
            EventCreateDTO createDTO = new EventCreateDTO();
            createDTO.setOrganizerId(1);
            when(eventRepository.countByOrganizer_OrganizerIdAndStatus(1, EventVO.STATUS_DRAFT)).thenReturn(3L);
            assertThrows(RuntimeException.class, () -> eventManageService.createEvent(createDTO));
        }

        @Test
        @DisplayName("Submit Event (Success)")
        void testSubmitEvent_Success() {
            eventVO.setStatus(EventVO.STATUS_DRAFT);
            eventVO.setPlace("Taipei");
            eventVO.setContent("Desc");

            when(eventRepository.findById(1)).thenReturn(Optional.of(eventVO));
            when(ticketRepository.findByEvent_EventId(1)).thenReturn(Collections.singletonList(ticketVO));

            eventManageService.submitEvent(1);

            assertEquals(EventVO.STATUS_PENDING, eventVO.getStatus());
        }

        @Test
        @DisplayName("Submit Event (Validation Fail)")
        void testSubmitEvent_ValidationFail() {
            eventVO.setStatus(EventVO.STATUS_DRAFT);
            eventVO.setTitle(null); // Missing title
            when(eventRepository.findById(1)).thenReturn(Optional.of(eventVO));
            assertThrows(RuntimeException.class, () -> eventManageService.submitEvent(1));
        }

        @Test
        @DisplayName("Update Event")
        void testUpdateEvent_Success() {
            EventUpdateDTO updateDTO = new EventUpdateDTO();
            updateDTO.setEventId(1);
            updateDTO.setTitle("Updated Title");

            eventVO.setStatus(EventVO.STATUS_DRAFT);
            when(eventRepository.findById(1)).thenReturn(Optional.of(eventVO));

            eventManageService.updateEvent(updateDTO);

            assertEquals("Updated Title", eventVO.getTitle());
            verify(eventRepository).save(eventVO);
        }

        @Test
        @DisplayName("Withdraw Event")
        void testWithdrawEvent_Success() {
            eventVO.setStatus(EventVO.STATUS_PENDING);
            when(eventRepository.findById(1)).thenReturn(Optional.of(eventVO));
            when(eventRepository.countByOrganizer_OrganizerIdAndStatus(1, EventVO.STATUS_DRAFT)).thenReturn(0L);

            eventManageService.withdrawEvent(1);

            assertEquals(EventVO.STATUS_DRAFT, eventVO.getStatus());
            assertNull(eventVO.getPublishedAt());
        }

        @Test
        @DisplayName("Delete Event")
        void testDeleteEvent_Success() {
            eventVO.setStatus(EventVO.STATUS_DRAFT);
            when(eventRepository.findById(1)).thenReturn(Optional.of(eventVO));

            eventManageService.deleteEvent(1);

            verify(eventRepository).delete(eventVO);
        }

        @Test
        @DisplayName("Set Times And Publish")
        void testSetTimesAndPublish_Success() {
            eventVO.setStatus(EventVO.STATUS_APPROVED); // Must be approved first
            when(eventRepository.findById(1)).thenReturn(Optional.of(eventVO));

            LocalDateTime now = LocalDateTime.now();
            eventManageService.setTimesAndPublish(1, now.plusDays(1), now.plusDays(2), now.plusDays(3), now.plusDays(4),
                    now.plusDays(5));

            assertEquals(EventVO.STATUS_PUBLISHED, eventVO.getStatus());
        }

        @Test
        @DisplayName("Get Organizer Stats")
        void testGetOrganizerStats() {
            when(eventRepository.countByOrganizer_OrganizerIdAndStatus(anyInt(), anyByte())).thenReturn(1L);
            when(eventRepository.countByOrganizer_OrganizerId(anyInt())).thenReturn(10L);
            when(eventFavRepository.countByOrganizerId(anyInt())).thenReturn(5L);

            var stats = eventManageService.getOrganizerStats(1);

            assertEquals(10L, stats.getAllCount());
            assertEquals(5L, stats.getTotalFavorites());
        }
    }

    @Nested
    @DisplayName("3. Event Review Service (Admin)")
    class EventReviewHandler {

        @Test
        @DisplayName("Get Events By Tab")
        void testGetEventsByTab_All() {
            when(eventRepository.searchAdminEvents(anyList(), any())).thenReturn(Collections.singletonList(eventVO));
            List<EventVO> result = eventReviewService.getEventsByTab("all", "test");
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Approve Event")
        void testApproveEvent_Success() {
            eventVO.setStatus(EventVO.STATUS_PENDING);
            when(eventRepository.findById(1)).thenReturn(Optional.of(eventVO));

            eventReviewService.approveEvent(1, empVO);

            assertEquals(EventVO.STATUS_APPROVED, eventVO.getStatus());
        }

        @Test
        @DisplayName("Approve Event (Invalid Status)")
        void testApproveEvent_NotPending() {
            eventVO.setStatus(EventVO.STATUS_DRAFT);
            when(eventRepository.findById(1)).thenReturn(Optional.of(eventVO));
            assertThrows(RuntimeException.class, () -> eventReviewService.approveEvent(1, empVO));
        }

        @Test
        @DisplayName("Reject Event")
        void testRejectEvent_Success() {
            eventVO.setStatus(EventVO.STATUS_PENDING);
            when(eventRepository.findById(1)).thenReturn(Optional.of(eventVO));

            eventReviewService.rejectEvent(1, "Bad Content", empVO);

            assertEquals(EventVO.STATUS_REJECTED, eventVO.getStatus());
            verify(organizerNotifyRepository).save(any(OrganizerNotifyVO.class));
        }

        @Test
        @DisplayName("Get Review Stats")
        void testGetReviewStats() {
            when(eventRepository.countByStatus(anyByte())).thenReturn(5L);
            when(eventRepository.countByStatusIn(anyList())).thenReturn(25L);

            Map<String, Long> stats = eventReviewService.getReviewStats();
            assertEquals(5L, stats.get("pending"));
            assertEquals(25L, stats.get("all"));
        }
    }

    @Nested
    @DisplayName("4. Ticket Service")
    class TicketHandler {

        @Test
        @DisplayName("Get Tickets By EventId")
        void testGetTicketsByEventId() {
            when(ticketRepository.findByEvent_EventId(1)).thenReturn(Collections.singletonList(ticketVO));
            List<TicketVO> result = ticketServiceImpl.getTicketsByEventId(1);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Get Available Tickets")
        void testGetAvailableTickets() {
            when(ticketRepository.findByEvent_EventIdAndRemainGreaterThan(1, 0))
                    .thenReturn(Collections.singletonList(ticketVO));
            assertEquals(1, ticketServiceImpl.getAvailableTickets(1).size());
        }

        @Test
        @DisplayName("Check Availability")
        void testCheckAvailability() {
            when(ticketRepository.checkAvailability(1, 5)).thenReturn(true);
            assertTrue(ticketServiceImpl.checkAvailability(1, 5));
        }

        @Test
        @DisplayName("Get Min/Max Prices")
        void testGetPrices() {
            when(ticketRepository.findMinPriceByEventId(1)).thenReturn(500);
            when(ticketRepository.findMaxPriceByEventId(1)).thenReturn(2000);
            assertEquals(500, ticketServiceImpl.getMinPrice(1));
            assertEquals(2000, ticketServiceImpl.getMaxPrice(1));
        }

        @Test
        @DisplayName("Reduce Stock (Success)")
        void testReduceStock_Success() {
            when(ticketRepository.findById(1)).thenReturn(Optional.of(ticketVO));
            ticketServiceImpl.reduceStock(1, 10);
            assertEquals(40, ticketVO.getRemain());
        }

        @Test
        @DisplayName("Reduce Stock (Insufficient)")
        void testReduceStock_Insufficient() {
            ticketVO.setRemain(5);
            when(ticketRepository.findById(1)).thenReturn(Optional.of(ticketVO));
            assertThrows(RuntimeException.class, () -> ticketServiceImpl.reduceStock(1, 10));
        }

        @Test
        @DisplayName("Restore Stock")
        void testRestoreStock() {
            when(ticketRepository.findById(1)).thenReturn(Optional.of(ticketVO));
            ticketServiceImpl.restoreStock(1, 10);
            assertEquals(60, ticketVO.getRemain());
        }

        @Test
        @DisplayName("Calculate Total Price")
        void testCalculateTotalPrice() {
            Map<Integer, Integer> map = new HashMap<>();
            map.put(1, 2);
            when(ticketRepository.findById(1)).thenReturn(Optional.of(ticketVO));
            Integer total = ticketServiceImpl.calculateTotalPrice(map);
            assertEquals(2000, total);
        }
    }
}
