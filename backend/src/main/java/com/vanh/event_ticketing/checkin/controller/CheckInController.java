package com.vanh.event_ticketing.checkin.controller;

import com.vanh.event_ticketing.checkin.dto.CheckInRequest;
import com.vanh.event_ticketing.checkin.dto.CheckInResponse;
import com.vanh.event_ticketing.checkin.service.CheckInService;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.common.util.PageResponse;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checkin")
@RequiredArgsConstructor
public class CheckInController {
    private final CheckInService checkInService;

    @PostMapping
    @PreAuthorize("hasRole('CHECKIN_STAFF')")
    public CheckInResponse checkIn(@Valid @RequestBody CheckInRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return checkInService.checkIn(request, userDetails);
    }

    @GetMapping("/logs")
    @PreAuthorize("hasAnyRole('ORGANIZER','CHECKIN_STAFF')")
    public PageResponse<CheckInResponse> logs(@RequestParam Long gateId, @RequestParam Instant from, @RequestParam Instant to, Pageable pageable) {
        return checkInService.logs(gateId, from, to, pageable);
    }
}
