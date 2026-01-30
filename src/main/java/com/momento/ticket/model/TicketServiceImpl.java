package com.momento.ticket.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 票種業務邏輯層實作
 * 實作票種查詢、庫存管理、票價計算等功能
 */
@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public List<TicketVO> getTicketsByEventId(Integer eventId) {
        return ticketRepository.findByEvent_EventId(eventId);
    }

    @Override
    public List<TicketVO> getAvailableTickets(Integer eventId) {
        return ticketRepository.findByEvent_EventIdAndRemainGreaterThan(eventId, 0);
    }

    @Override
    public Integer getMinPrice(Integer eventId) {
        return ticketRepository.findMinPriceByEventId(eventId);
    }

    @Override
    public Integer getMaxPrice(Integer eventId) {
        return ticketRepository.findMaxPriceByEventId(eventId);
    }

    @Override
    public Integer calculateTotalPrice(Map<Integer, Integer> ticketQuantityMap) {
        Integer total = 0;
        for (Map.Entry<Integer, Integer> entry : ticketQuantityMap.entrySet()) {
            TicketVO ticket = ticketRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("票種不存在：ID = " + entry.getKey()));
            total += ticket.getPrice() * entry.getValue();
        }
        return total;
    }

    @Override
    public boolean checkAvailability(Integer ticketId, Integer quantity) {
        return ticketRepository.checkAvailability(ticketId, quantity);
    }

    @Override
    public void reduceStock(Integer ticketId, Integer quantity) {
        TicketVO ticket = ticketRepository.findByIdForUpdate(ticketId)
                .orElseThrow(() -> new RuntimeException("票種不存在：ID = " + ticketId));

        if (ticket.getRemain() < quantity) {
            throw new RuntimeException("票券數量不足：票種「" + ticket.getTicketName() +
                    "」剩餘 " + ticket.getRemain() + " 張，需要 " + quantity + " 張");
        }

        ticket.setRemain(ticket.getRemain() - quantity);
        ticketRepository.save(ticket);
    }

    @Override
    public void restoreStock(Integer ticketId, Integer quantity) {
        TicketVO ticket = ticketRepository.findByIdForUpdate(ticketId)
                .orElseThrow(() -> new RuntimeException("票種不存在：ID = " + ticketId));

        ticket.setRemain(ticket.getRemain() + quantity);
        ticketRepository.save(ticket);
    }
}
