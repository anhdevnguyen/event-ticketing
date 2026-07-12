package com.vanh.event_ticketing.auth.dto;

import jakarta.validation.constraints.NotNull;

public record UserStatusRequest(@NotNull Boolean active) {
}
