package com.vanh.event_ticketing.event.mapper;

import com.vanh.event_ticketing.event.dto.TicketTypeResponse;
import com.vanh.event_ticketing.event.entity.TicketType;
import org.springframework.stereotype.Component;

@Component
public class TicketTypeMapper {
    public TicketTypeResponse toResponse(TicketType ticketType) {
        return new TicketTypeResponse(ticketType.getId(), ticketType.getEvent().getId(), ticketType.getName(), ticketType.getPrice(), ticketType.getQuantityTotal(), ticketType.getQuantityRemaining(), ticketType.getSalesStartAt(), ticketType.getSalesEndAt());
    }
}
