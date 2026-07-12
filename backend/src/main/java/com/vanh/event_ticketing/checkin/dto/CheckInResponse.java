package com.vanh.event_ticketing.checkin.dto;

import java.time.Instant;

public record CheckInResponse(Long ticketId, String status, Instant checkedInAt, Long gateId) {
}
