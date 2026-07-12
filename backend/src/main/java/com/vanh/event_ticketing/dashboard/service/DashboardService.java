package com.vanh.event_ticketing.dashboard.service;

import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.dashboard.dto.DashboardSnapshotResponse;

public interface DashboardService {
    DashboardSnapshotResponse snapshot(Long eventId, CustomUserDetails userDetails);
}
