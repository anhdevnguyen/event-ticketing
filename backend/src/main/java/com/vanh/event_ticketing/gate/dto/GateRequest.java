package com.vanh.event_ticketing.gate.dto;

import jakarta.validation.constraints.NotBlank;

public record GateRequest(@NotBlank String name) {
}
