package com.vanh.event_ticketing.event.dto;

import java.time.Instant;

public record EventResponse(Long id, String name, String description, String location, Long organizerId, String status, Instant startTime, Instant endTime, String bannerUrl, Instant createdAt) {
}
