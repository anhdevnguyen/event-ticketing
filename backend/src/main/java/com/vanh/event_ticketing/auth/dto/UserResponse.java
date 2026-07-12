package com.vanh.event_ticketing.auth.dto;

public record UserResponse(
        Long id,
        String email,
        String role,
        String fullName,
        Long assignedEventId,
        boolean active
) {
}
