package com.vanh.event_ticketing.ticket.dto;

import java.time.Instant;

public record TicketResponse(Long id, Long ticketTypeId, String status, int quantity, String qrCode, Instant reservedAt, Instant expiresAt, Instant confirmedAt, Instant checkedInAt) {
}
