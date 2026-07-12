package com.vanh.event_ticketing.event.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TicketTypeResponse(Long id, Long eventId, String name, BigDecimal price, int quantityTotal, int quantityRemaining, Instant salesStartAt, Instant salesEndAt) {
}
