package com.vanh.event_ticketing.checkin.service;

import com.vanh.event_ticketing.checkin.dto.CheckInRequest;
import com.vanh.event_ticketing.checkin.dto.CheckInResponse;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.common.util.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CheckInService {
    CheckInResponse checkIn(CheckInRequest request, CustomUserDetails userDetails);
    PageResponse<CheckInResponse> logs(Long gateId, java.time.Instant from, java.time.Instant to, Pageable pageable);
}
