package com.vanh.event_ticketing.event.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public record TicketTypeRequest(@NotBlank String name, @NotNull @Min(0) BigDecimal price, @Min(0) int quantityTotal, Instant salesStartAt, Instant salesEndAt) {
}
