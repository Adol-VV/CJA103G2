package com.momento.eventmanage.model;

import com.momento.event.model.*;
import com.momento.eventmanage.dto.EventCreateDTO;
import com.momento.ticket.model.TicketVO;
import com.momento.ticket.model.TicketRepository;
import com.momento.organizer.model.OrganizerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.List;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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

    // 圖片儲存路徑
    private static final String UPLOAD_DIR = "uploads/events/";

    /**
     * 建立活動
     */
    @Override
    @Transactional
    public Integer createEvent(EventCreateDTO dto) {
        // 1. 建立活動實體
        EventVO event = new EventVO();

        // 設定主辦方
        // TODO: 從 DTO 取得 organizerId 後設定
        // OrganizerVO organizer = organizerRepository.findById(dto.getOrganizerId())
        // .orElseThrow(() -> new RuntimeException("主辦方不存在"));
        // event.setOrganizer(organizer);

        // 設定活動類型
        TypeVO type = typeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new RuntimeException("活動類型不存在"));
        event.setType(type);

        // 設定基本資訊
        event.setTitle(dto.getTitle());
        event.setPlace(dto.getPlace());
        event.setEventAt(dto.getEventAt());
        event.setContent(dto.getContent());

        // 設定售票時間
        event.setStartedAt(dto.getStartedAt());
        event.setEndedAt(dto.getEndedAt());

        // 設定狀態 (待審核)
        event.setStatus((byte) 1); // 待審核
        event.setReviewStatus((byte) 0); // 未審核

        // 儲存活動
        EventVO savedEvent = eventRepository.save(event);

        // 2. 儲存活動圖片
        if (dto.getBannerUrl() != null) {
            saveEventImage(savedEvent, dto.getBannerUrl(), 0);
        }

        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            int order = 1;
            for (String imageUrl : dto.getImageUrls()) {
                saveEventImage(savedEvent, imageUrl, order++);
            }
        }

        // 3. 儲存票種資訊
        if (dto.getTickets() != null && !dto.getTickets().isEmpty()) {
            for (EventCreateDTO.TicketDTO ticketDTO : dto.getTickets()) {
                TicketVO ticket = new TicketVO();
                ticket.setEvent(savedEvent);
                ticket.setTicketName(ticketDTO.getName());
                ticket.setPrice(ticketDTO.getPrice());
                ticket.setTotal(ticketDTO.getTotal());
                ticket.setRemain(ticketDTO.getTotal()); // 初始剩餘 = 總數

                // TODO: 每人限購欄位 (待確認)
                // ticket.setLimitPerPerson(ticketDTO.getLimitPerPerson());

                ticketRepository.save(ticket);
            }
        }

        return savedEvent.getEventId();
    }

    /**
     * 儲存活動圖片
     */
    private void saveEventImage(EventVO event, String imageUrl, int displayOrder) {
        EventImageVO eventImage = new EventImageVO();
        eventImage.setEvent(event);
        eventImage.setImageUrl(imageUrl);
        // TODO: 如果 EventImageVO 有 displayOrder 欄位,設定順序
        // eventImage.setDisplayOrder(displayOrder);
        eventImageRepository.save(eventImage);
    }

    /**
     * 上傳圖片
     */
    @Override
    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("檔案不能為空");
        }

        try {
            // 1. 檢查檔案類型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("只能上傳圖片檔案");
            }

            // 2. 生成唯一檔名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            // 3. 確保目錄存在
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 4. 儲存檔案
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile());

            // 5. 返回相對路徑 URL
            return "/" + UPLOAD_DIR + filename;

        } catch (IOException e) {
            throw new RuntimeException("圖片上傳失敗: " + e.getMessage());
        }
    }

    /**
     * 更新活動
     */
    @Override
    @Transactional
    public void updateEvent(com.momento.eventmanage.dto.EventUpdateDTO dto) {
        // 1. 查詢活動
        EventVO event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        // 2. 更新基本資訊 (永遠可以修改)
        event.setTitle(dto.getTitle());
        event.setPlace(dto.getPlace());
        event.setEventAt(dto.getEventAt());
        event.setContent(dto.getContent());
        event.setStartedAt(dto.getStartedAt());
        event.setEndedAt(dto.getEndedAt());

        // 更新活動類型
        if (dto.getTypeId() != null) {
            TypeVO type = typeRepository.findById(dto.getTypeId())
                    .orElseThrow(() -> new RuntimeException("活動類型不存在"));
            event.setType(type);
        }

        // 儲存活動基本資訊
        eventRepository.save(event);

        // 3. 更新圖片 (如果有提供新圖片)
        if (dto.getBannerUrl() != null) {
            // TODO: 刪除舊圖片,新增新圖片
        }

        // 4. 更新票種資訊 (需要檢查是否可編輯)
        if (dto.getTickets() != null && !dto.getTickets().isEmpty()) {
            for (com.momento.eventmanage.dto.EventUpdateDTO.TicketUpdateDTO ticketDTO : dto.getTickets()) {
                if (ticketDTO.getTicketId() != null) {
                    // 更新現有票種
                    TicketVO ticket = ticketRepository.findById(ticketDTO.getTicketId())
                            .orElseThrow(() -> new RuntimeException("票種不存在"));

                    // 檢查是否可以編輯
                    if (!canEditTicket(ticketDTO.getTicketId())) {
                        throw new RuntimeException("票種「" + ticket.getTicketName() + "」已有訂單,無法修改價格和數量");
                    }

                    // 可以修改
                    ticket.setTicketName(ticketDTO.getName());
                    ticket.setPrice(ticketDTO.getPrice());
                    ticket.setTotal(ticketDTO.getTotal());
                    // 更新剩餘票數 (total - 已售出)
                    int sold = ticket.getTotal() - ticket.getRemain();
                    ticket.setRemain(ticketDTO.getTotal() - sold);

                    ticketRepository.save(ticket);
                } else {
                    // 新增票種
                    TicketVO newTicket = new TicketVO();
                    newTicket.setEvent(event);
                    newTicket.setTicketName(ticketDTO.getName());
                    newTicket.setPrice(ticketDTO.getPrice());
                    newTicket.setTotal(ticketDTO.getTotal());
                    newTicket.setRemain(ticketDTO.getTotal());
                    ticketRepository.save(newTicket);
                }
            }
        }
    }

    /**
     * 檢查票種是否可以編輯
     */
    @Override
    public boolean canEditTicket(Integer ticketId) {
        // TODO: 需要其他成員提供 OrderDetailRepository
        // 檢查是否有未取消的訂單
        // int orderCount = orderDetailRepository
        // .countByTicketIdAndOrderStatusNot(ticketId, "CANCELLED");
        // return orderCount == 0;

        // 暫時返回 true (允許編輯)
        return true;
    }

    /**
     * 變更活動狀態
     */
    @Override
    @Transactional
    public void changeStatus(Integer eventId, Byte status, String reason) {
        // 查詢活動
        EventVO event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        // 根據狀態執行不同邏輯
        switch (status) {
            case 3: // 下架
                event.setStatus((byte) 3);
                break;
            case 4: // 取消
                event.setStatus((byte) 4);
                break;
            default:
                throw new RuntimeException("不支援的狀態變更");
        }

        // 儲存變更
        eventRepository.save(event);
    }

    /**
     * 取得所有活動 (暫時用於測試)
     */
    @Override
    public java.util.List<EventVO> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * 查詢主辦方的活動列表 (支援篩選和分頁)
     */
    /**
     * 查詢主辦方的活動列表 (支援篩選和分頁)
     */
    @Override
    public Page<EventVO> getOrganizerEvents(
            Integer organizerId,
            Byte status,
            Byte reviewStatus,
            String keyword,
            Pageable pageable) {

        // 情況 1: 指定 ReviewStatus (例如: 查詢已駁回)
        if (reviewStatus != null) {
            // 目前只支援 Status=0 (草稿) + ReviewStatus=2 (未通過)
            // 若未來有其他需求可再擴充
            // 使用 findAll + Filter 或者 Repository 新增方法
            // Repository 已有 findByOrganizer_OrganizerIdAndStatusAndReviewStatus 但回傳 List
            // 這裡使用 findAll Example 或者需要新增 Page 版本的方法
            // 暫時使用 Stream 過濾 (為了快速實作且資料量不大)
            // 更好的方式是在 EventRepository 新增
            // findByOrganizer_OrganizerIdAndStatusAndReviewStatus(..., Pageable)

            // 假設 Repository 沒有 Pageable 版本，我們先用 status 查詢再過濾 (比較沒效率但可行)
            // 但 EventRepository 似乎沒有 findByOrganizer...AndReviewStatus 的 Page 版本
            // 我們先新增一個 Repository 方法比較好，但為了不更動 Repository (User Requirement)，
            // 我們改用 findByOrganizer_OrganizerIdAndStatus 查出 Status=0 的，再過濾 ReviewStatus

            if (status != null) {
                Page<EventVO> page = eventRepository.findByOrganizer_OrganizerIdAndStatus(organizerId, status,
                        pageable);
                // 這裡 Page 無法直接過濾，必須在 Repository 層做
                // 既然 User 說 "不更動 DB"，沒說不更動 JPQL
                // 但前面看 EventRepository 其實已經有很完整的查詢了
                // 讓我們看看 step 2427 output...
                // findByStatusAndReviewStatus 有 Pageable
                // findByOrganizer_OrganizerIdAndStatus 有 Pageable
                // 但沒有 findByOrganizer_OrganizerIdAndStatusAndReviewStatus (Pageable)
                // 只有 List 版本 line 174

                // 替代方案:
                // 1. 新增 Repository 方法 (最乾淨)
                // 2. 用 existing methods 拼裝

                // 鑑於 Phase 3 原則，我們假設可以加 Repository 方法?
                // USER said: "不更動 DB Schema"。沒說不能加 query。
                // 但為了保險，我先用 List 版本轉 Page，或是直接不做 Page 邏輯 (只顯示前 N 筆?)
                // 不，分頁很重要。

                // 讓我們回頭看 EventRepository 是否有彈性的 filter?
                // filterEvents (Line 142) @Query ...
                // 它的 WHERE 條件: e.status = :status AND e.reviewStatus = :reviewStatus ...
                // 但它沒有 filter by OrganizerId !!

                // 所以我必須在 Repository 增加方法，或者在 Service 硬幹。
                // 硬幹:
                List<EventVO> all = eventRepository.findByOrganizer_OrganizerIdAndStatusAndReviewStatus(organizerId,
                        status, reviewStatus);
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), all.size());
                if (start > all.size())
                    return new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList(), pageable,
                            all.size());
                return new org.springframework.data.domain.PageImpl<>(all.subList(start, end), pageable, all.size());
            }
        }

        // 以下為其餘舊邏輯 (Delegate to original method or inline it)
        // 情況 1: 有狀態 + 有關鍵字
        if (status != null && keyword != null && !keyword.trim().isEmpty()) {
            return eventRepository.findByOrganizer_OrganizerIdAndStatusAndTitleContaining(
                    organizerId, status, keyword.trim(), pageable);
        }

        // 情況 2: 只有狀態
        if (status != null) {
            return eventRepository.findByOrganizer_OrganizerIdAndStatus(
                    organizerId, status, pageable);
        }

        // 情況 3: 只有關鍵字
        if (keyword != null && !keyword.trim().isEmpty()) {
            return eventRepository.findByOrganizer_OrganizerIdAndTitleContaining(
                    organizerId, keyword.trim(), pageable);
        }

        // 情況 4: 無篩選條件
        return eventRepository.findByOrganizer_OrganizerId(organizerId, pageable);
    }
}
