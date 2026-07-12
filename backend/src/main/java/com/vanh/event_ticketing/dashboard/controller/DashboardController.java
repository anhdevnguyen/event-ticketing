package com.vanh.event_ticketing.dashboard.controller;

import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.dashboard.dto.DashboardSnapshotResponse;
import com.vanh.event_ticketing.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events/{eventId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public DashboardSnapshotResponse snapshot(@PathVariable Long eventId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return dashboardService.snapshot(eventId, userDetails);
    }
}
