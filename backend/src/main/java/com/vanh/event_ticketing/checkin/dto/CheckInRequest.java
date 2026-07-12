package com.vanh.event_ticketing.checkin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckInRequest(@NotBlank String qrCode, @NotNull Long gateId) {
}
