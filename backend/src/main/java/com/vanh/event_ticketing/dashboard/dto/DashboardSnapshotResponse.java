package com.vanh.event_ticketing.dashboard.dto;

import java.util.List;

public record DashboardSnapshotResponse(Long eventId, long totalTicketsSold, long totalCheckedIn, long totalRemaining, List<GateStats> byGate) {
    public record GateStats(Long gateId, String gateName, long checkedIn) {
    }
}
