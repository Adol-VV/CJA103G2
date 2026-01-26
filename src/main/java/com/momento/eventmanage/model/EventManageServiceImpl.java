package com.momento.eventmanage.model;

import com.momento.event.model.*;
import com.momento.eventmanage.dto.EventCreateDTO;
import com.momento.ticket.model.TicketVO;
import com.momento.ticket.model.TicketRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    private com.momento.organizer.model.OrganizerRepository organizerRepository;

    /**
     * 建立活動
     */
    @Override
    @Transactional
    public Integer createEvent(EventCreateDTO dto) {
        // 時間邏輯驗證 (僅在兩者皆存在時檢查)
        if (dto.getStartedAt() != null && dto.getEndedAt() != null && !dto.getStartedAt().isBefore(dto.getEndedAt())) {
            throw new RuntimeException("售票開始時間必須早於售票結束時間");
        }
        if (dto.getEndedAt() != null && dto.getEventAt() != null && dto.getEndedAt().isAfter(dto.getEventAt())) {
            throw new RuntimeException("售票結束時間不能晚於活動舉辦時間");
        }

        // 1. 建立活動實體
        EventVO event = new EventVO();

        // 設定主辦方
        com.momento.organizer.model.OrganizerVO organizer = organizerRepository.findById(dto.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("主辦方不存在"));
        event.setOrganizer(organizer);

        // 設定活動類型 (允許為空)
        if (dto.getTypeId() != null) {
            TypeVO type = typeRepository.findById(dto.getTypeId())
                    .orElse(null); // 若找不到則設為 null，或保留 null
            event.setType(type);
        }

        // 設定基本資訊 (允許為空)
        event.setTitle(dto.getTitle());
        event.setPlace(dto.getPlace());
        event.setEventAt(dto.getEventAt());
        event.setContent(dto.getContent());

        // 設定售票時間
        event.setStartedAt(dto.getStartedAt());
        event.setEndedAt(dto.getEndedAt());

        // 設定狀態 (草稿)
        event.setStatus((byte) 0); // 草稿
        event.setReviewStatus((byte) 0); // 初始狀態
        event.setPublishedAt(null); // 尚未發布

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
        // 驗證檔案
        if (file.isEmpty()) {
            throw new RuntimeException("檔案不能為空");
        }

        // 驗證檔案類型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只能上傳圖片檔案");
        }

        // 驗證檔案大小 (限制 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new RuntimeException("圖片大小不能超過 5MB");
        }

        try {
            // 建立上傳目錄
            String uploadDir = "uploads/events";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("📁 建立目錄: " + uploadPath.toAbsolutePath());
            }

            // 生成唯一檔名 (時間戳 + UUID + 原始副檔名)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";

            String filename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + extension;

            // 儲存檔案
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 回傳可訪問的 URL
            String imageUrl = "/uploads/events/" + filename;

            System.out.println("✅ 圖片上傳成功: " + imageUrl);
            return imageUrl;

        } catch (IOException e) {
            System.err.println("❌ 圖片上傳失敗: " + e.getMessage());
            throw new RuntimeException("圖片上傳失敗: " + e.getMessage());
        }
    }

    /**
     * 撤回活動
     */
    @Override
    @Transactional
    public void withdrawEvent(Integer eventId) {
        EventVO event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        // 只能撤回待審核的活動 (P!=null) 且 S=0 (非已上架)
        if (event.getPublishedAt() == null || event.getStatus() != 0) {
            throw new RuntimeException("活動狀態不正確，無法撤回");
        }

        // 清空送審時間 -> 變回草稿
        event.setPublishedAt(null);
        eventRepository.save(event);
    }

    /**
     * 刪除活動
     */
    @Override
    @Transactional
    public void deleteEvent(Integer eventId) {
        EventVO event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        // 只能刪除草稿 (S=0, P=null)
        if (event.getStatus() != 0 || event.getPublishedAt() != null) {
            throw new RuntimeException("只能刪除草稿狀態的活動");
        }

        // TODO: 刪除關聯資料 (圖片、票種)
        // 目前 Cascade 設定不明，若有 FK 限制需先刪除子表
        // 假設 JPA Cascade 已設定或無限制
        // 先刪票種?
        ticketRepository.deleteAll(ticketRepository.findByEvent_EventId(eventId));
        eventImageRepository.deleteAll(eventImageRepository.findByEvent_EventIdOrderByEventImageIdAsc(eventId));

        eventRepository.delete(event);
    }

    /**
     * 更新活動
     */
    @Override
    @Transactional
    public void updateEvent(com.momento.eventmanage.dto.EventUpdateDTO dto) {
        // 時間邏輯驗證
        if (dto.getStartedAt() != null && dto.getEndedAt() != null && !dto.getStartedAt().isBefore(dto.getEndedAt())) {
            throw new RuntimeException("售票開始時間必須早於售票結束時間");
        }
        if (dto.getEndedAt() != null && dto.getEventAt() != null && dto.getEndedAt().isAfter(dto.getEventAt())) {
            throw new RuntimeException("售票結束時間不能晚於活動舉辦時間");
        }

        // 1. 查詢活動
        EventVO event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        // 嚴格檢查：只能編輯草稿 (S=0, P=null)
        // 若是駁回狀態 (R=2)，P=null，所以也符合草稿定義，可以編輯
        if (event.getStatus() != 0 || event.getPublishedAt() != null) {
            throw new RuntimeException("活動狀態不允許編輯 (僅限草稿)");
        }

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
        if (dto.getBannerUrl() != null && !dto.getBannerUrl().isEmpty()) {
            // 刪除目前活動的所有圖片 (MVP 簡化版：先全清再存主圖)
            eventImageRepository
                    .deleteAll(eventImageRepository.findByEvent_EventIdOrderByEventImageIdAsc(dto.getEventId()));

            // 儲存新主圖
            saveEventImage(event, dto.getBannerUrl(), 0);
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
     * 送審活動
     */
    @Override
    @Transactional
    public void submitEvent(Integer eventId) {
        EventVO event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        // 只允許草稿狀態送審
        if (event.getStatus() != 0) {
            throw new RuntimeException("活動狀態不正確，無法送審");
        }

        // ========== 嚴格驗證 (送審時必填) ==========
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new RuntimeException("活動標題不能為空");
        }
        if (event.getType() == null) {
            throw new RuntimeException("請選擇活動類型");
        }
        if (event.getPlace() == null || event.getPlace().trim().isEmpty()) {
            throw new RuntimeException("活動地點不能為空");
        }
        if (event.getEventAt() == null) {
            throw new RuntimeException("活動舉辦時間不能為空");
        }
        if (event.getStartedAt() == null || event.getEndedAt() == null) {
            throw new RuntimeException("售票時間不能為空");
        }
        if (event.getContent() == null || event.getContent().trim().isEmpty()) {
            throw new RuntimeException("活動內容簡介不能為空");
        }

        // 檢查票種 (至少一種)
        List<TicketVO> tickets = ticketRepository.findByEvent_EventId(eventId);
        if (tickets == null || tickets.isEmpty()) {
            throw new RuntimeException("至少需要設定一種票種");
        }

        // 時間邏輯再次確認
        if (!event.getStartedAt().isBefore(event.getEndedAt())) {
            throw new RuntimeException("售票開始時間必須早於售票結束時間");
        }
        if (event.getEndedAt().isAfter(event.getEventAt())) {
            throw new RuntimeException("售票結束時間不能晚於活動舉辦時間");
        }

        // 更新狀態: 標記送審時間 -> 變為待審核
        event.setPublishedAt(java.time.LocalDateTime.now());
        // 保持 S=0, R=0 (待審核狀態)
        event.setReviewStatus((byte) 0);

        eventRepository.save(event);
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
            case 1: // 上架
                event.setStatus((byte) 1);
                break;
            case 2: // 取消 (Cancelled)
                event.setStatus((byte) 2);
                break;
            case 3: // 結束/下架 (Closed)
                event.setStatus((byte) 3);
                break;
            default:
                throw new RuntimeException("無效的狀態值");
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

    @Override
    public Page<EventVO> getOrganizerEvents(
            Integer organizerId,
            java.util.Collection<Byte> statuses,
            Byte reviewStatus,
            String keyword,
            Pageable pageable) {

        // 使用 Repository 的複合查詢方法直接在資料庫層級進行篩選和分頁
        return eventRepository.searchOrganizerEvents(
                organizerId,
                statuses,
                reviewStatus,
                keyword,
                pageable);
    }

    @Autowired
    private com.momento.eventfav.model.EventFavRepository eventFavRepository;

    /**
     * 取得主辦方統計數據
     */
    @Override
    public com.momento.eventmanage.dto.EventStatsDTO getOrganizerStats(Integer organizerId) {
        // 1. 進行中活動 (Status = 1: 已上架)
        // 嚴格來說 ReviewStatus 應該也是 2，但通常上架隱含已通過
        long activeCount = eventRepository.countByOrganizer_OrganizerIdAndStatus(organizerId, (byte) 1);

        // 2. 待審核活動 (S=0, R=0, P!=null)
        long pendingCount = eventRepository.countByOrganizer_OrganizerIdAndStatusAndReviewStatusAndPublishedAtIsNotNull(
                organizerId, (byte) 0, (byte) 0);

        // 3. 總收藏數
        long totalFavorites = eventFavRepository.countByOrganizerId(organizerId);

        // 4. 已駁回活動 (S=0, R=2, P=null)
        long rejectedCount = eventRepository.countByOrganizer_OrganizerIdAndStatusAndReviewStatusAndPublishedAtIsNull(
                organizerId, (byte) 0, (byte) 2);

        // 5. 已結束/取消 (S=2,3)
        long endedCount = eventRepository.countByOrganizer_OrganizerIdAndStatusIn(organizerId,
                java.util.List.of((byte) 2, (byte) 3));

        // 6. 全部 (非草稿)
        // 合計所有非草稿分類
        long allCount = activeCount + pendingCount + rejectedCount + endedCount;

        return new com.momento.eventmanage.dto.EventStatsDTO(activeCount, pendingCount, totalFavorites, rejectedCount,
                endedCount, allCount);
    }

    @Autowired
    private com.momento.notify.model.OrganizerNotifyRepository organizerNotifyRepository;

    @Override
    public com.momento.event.dto.EventDetailDTO getEventDetail(Integer eventId) {
        EventVO event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("活動不存在"));

        List<com.momento.ticket.model.TicketVO> tickets = ticketRepository.findByEvent_EventId(eventId);
        List<EventImageVO> images = eventImageRepository.findByEvent_EventIdOrderByEventImageIdAsc(eventId);

        com.momento.event.dto.EventDetailDTO dto = new com.momento.event.dto.EventDetailDTO();
        dto.setEvent(event);
        dto.setTickets(tickets);
        dto.setImages(images);
        dto.setOrganizer(event.getOrganizer());

        // 如果是駁回狀態，去撈取最後一次的駁回通知
        if (event.getReviewStatus() == 2) {
            List<com.momento.notify.model.OrganizerNotifyVO> notifies = organizerNotifyRepository
                    .findByOrganizerVO_OrganizerIdAndTitleContainingOrderByCreatedAtDesc(
                            event.getOrganizer().getOrganizerId(),
                            "活動審核未通過通知: " + event.getTitle());

            if (notifies != null && !notifies.isEmpty()) {
                String fullContent = notifies.get(0).getContent();
                // 擷取 "退回原因:" 之後的文字
                if (fullContent != null && fullContent.contains("退回原因: ")) {
                    dto.setRejectReason(fullContent.split("退回原因: ")[1]);
                } else {
                    dto.setRejectReason(fullContent);
                }
            }
        }

        return dto;
    }
}
