package com.vanh.event_ticketing.auth.dto;

public record LoginResponse(
        String accessToken,
        UserResponse user
) {
}
