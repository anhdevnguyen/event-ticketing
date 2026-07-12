package com.vanh.event_ticketing.ticket.mapper;

import com.vanh.event_ticketing.ticket.dto.TicketResponse;
import com.vanh.event_ticketing.ticket.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {
    public TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(ticket.getId(), ticket.getTicketType().getId(), ticket.getStatus(), 1, ticket.getQrCode(), ticket.getReservedAt(), ticket.getExpiresAt(), ticket.getConfirmedAt(), ticket.getCheckedInAt());
    }
}
