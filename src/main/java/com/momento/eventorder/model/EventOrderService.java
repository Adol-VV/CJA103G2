package com.momento.eventorder.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.momento.event.model.EventRepository;
import com.momento.event.model.EventVO;
import com.momento.eventorder.dto.CheckoutDTO;
import com.momento.eventorder.dto.OrderCreateDTO;
import com.momento.eventorder.dto.OrderItemDTO;
import com.momento.eventorder.dto.SelectionFormDTO;
import com.momento.eventorder.dto.TicketItemsDTO;
import com.momento.member.model.MemberRepository;
import com.momento.member.model.MemberVO;
import com.momento.organizer.model.OrganizerVO;
import com.momento.ticket.model.TicketRepository;
import com.momento.ticket.model.TicketVO;

@Service
public class EventOrderService {

	@Autowired
	EventOrderRepository repository;

	@Autowired
	MemberRepository memberRepo;

	@Autowired
	TicketRepository ticketRepo;

	@Autowired
	EventRepository eventRepo;

	@Autowired
	EventOrderRepository eventOrderRepo;

	@Autowired
	EventOrderItemRepository eventOrderItemRepo;

	@Transactional
	public EventOrderVO createOrder(OrderCreateDTO dto, Integer memberId) {

		// 連結活動跟會員
		EventOrderVO newOrder = new EventOrderVO();
		
		Integer firstTicketId = dto.getItems().get(0).getTicketId();
		

		MemberVO member = memberRepo.findById(memberId).orElseThrow((() -> new RuntimeException("查無此會員")));
		
		TicketVO firstTicket = ticketRepo.getById(firstTicketId);

		EventVO event = eventRepo.findById(firstTicket.getTicketId()).orElseThrow((() -> new RuntimeException("此活動不存在")));
		
		OrganizerVO organizer = event.getOrganizer();


		// 檢查活動是否可以購票
		LocalDateTime now = LocalDateTime.now();

		if (now.isBefore(event.getStartedAt())) {
			throw new RuntimeException("購票時間尚未開始");
		}
//		if (now.isAfter(event.getEndedAt())) {
//			System.out.println(event.getEndedAt());
//			throw new RuntimeException("購票時間已經結束");
//		}

		// 處理票券
		int totalAmount = 0;
		int tokenUsed = 0;
		int payable = 0;
		int token = member.getToken();
		List<EventOrderItemVO> orderItemList = new ArrayList();

		for (OrderItemDTO itemDto : dto.getItems()) {
			TicketVO ticket = ticketRepo.findById(itemDto.getTicketId())
					.orElseThrow((() -> new RuntimeException("查無此票券")));

			// 檢查庫存
			if (ticket.getRemain() < itemDto.getQuantity()) {
				throw new RuntimeException(ticket.getTicketName() + " 庫存不足");
			}

			// 設定每個訂單明細
			for (int i = 0; i < itemDto.getQuantity(); i++) {
				EventOrderItemVO orderItem = new EventOrderItemVO();

				orderItem.setTicket(ticket);
				orderItem.setPrice(ticket.getPrice());
				orderItem.setQrcode(UUID.randomUUID().toString());

				orderItemList.add(orderItem);

				totalAmount += ticket.getPrice();
				payable = totalAmount;
			}

			// 減會員剩餘代幣
			
			
			// 減庫存
			ticket.setRemain(ticket.getRemain() - itemDto.getQuantity());

		}
		if(dto.isUseToken()) {
			tokenUsed = Math.min(totalAmount, token);
			payable = totalAmount - tokenUsed;
			tokenUsed = Math.min(totalAmount, token);
		}
		// 更新token
		int tokenReward = (payable / 300) * 5;
		memberRepo.updateToken(token - tokenUsed + tokenReward, memberId);

		EventOrderVO eventOrder = new EventOrderVO();
		eventOrder.setMember(member);
		eventOrder.setOrganizer(organizer);
		eventOrder.setEvent(event);
		eventOrder.setTotal(totalAmount);
		eventOrder.setPayStatus(1);
		eventOrder.setCreatedAt(now);
		eventOrder.setTokenUsed(tokenUsed);
		eventOrder.setPayable(payable);
		eventOrderRepo.save(eventOrder);

		for (EventOrderItemVO orderItem : orderItemList) {
			orderItem.setEventOrder(eventOrder);
		}
		
		eventOrderItemRepo.saveAll(orderItemList);
		eventOrder.setEventOrderItems(new HashSet<>(orderItemList));
		
		return eventOrder;
	}
	
	// 把event-detail的資訊帶到event-checkout頁面
	public List<TicketItemsDTO> processSelectedTickets(SelectionFormDTO selectionForm) {
		
		List<TicketItemsDTO> selectedItems = new ArrayList();
		
		if(selectionForm.getItems() != null) {
			
			for( TicketItemsDTO item: selectionForm.getItems() ) {
				
				if( item.getQuantity() != null && item.getQuantity() > 0 ) {
					
					Optional<TicketVO> ticketInformation = ticketRepo.findById(item.getTicketId());
					
					if( ticketInformation != null) {
						item.setEventId(ticketInformation.get().getEvent().getEventId());;
						item.setTicketId(ticketInformation.get().getTicketId());
						item.setTicketName(ticketInformation.get().getTicketName());
						item.setEventName(ticketInformation.get().getEvent().getTitle());
						item.setPrice(ticketInformation.get().getPrice());
						item.setEventTime(ticketInformation.get().getEvent().getEventAt());
						
					}
					
					selectedItems.add(item);
				}
						
			}
		}
		return selectedItems;
	}
	
	public CheckoutDTO calculateCheckout(SelectionFormDTO selectionForm, boolean useToken, Integer memberId) {
		
		List<TicketItemsDTO> selectedItems = processSelectedTickets(selectionForm);
		
		int total = 0;
		
		for(TicketItemsDTO items: selectedItems) {
			
			total += items.getPrice() * items.getQuantity();
			
		}
		
		int tokenUsed = 0;
		int payable = total;
		if(useToken) {
			
			MemberVO member = memberRepo.getById(memberId);
			int balance = member.getToken();
			
			tokenUsed = Math.min(total, balance);
			payable = payable - tokenUsed;
		}
		
		CheckoutDTO checkoutData = new CheckoutDTO();
		
		checkoutData.setTotal(total);
		checkoutData.setTokenUsed(tokenUsed);
		checkoutData.setPayable(payable);
		
		return checkoutData;
		
	}
	
}
