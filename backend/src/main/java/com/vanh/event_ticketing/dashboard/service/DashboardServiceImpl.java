package com.vanh.event_ticketing.dashboard.service;

import com.vanh.event_ticketing.checkin.repository.CheckInLogRepository;
import com.vanh.event_ticketing.common.exception.BusinessException;
import com.vanh.event_ticketing.common.exception.ErrorCode;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.dashboard.dto.DashboardSnapshotResponse;
import com.vanh.event_ticketing.event.entity.Event;
import com.vanh.event_ticketing.event.repository.EventRepository;
import com.vanh.event_ticketing.event.repository.TicketTypeRepository;
import com.vanh.event_ticketing.gate.repository.GateRepository;
import com.vanh.event_ticketing.ticket.repository.TicketRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final GateRepository gateRepository;
    private final CheckInLogRepository checkInLogRepository;

    @Override
    public DashboardSnapshotResponse snapshot(Long eventId, CustomUserDetails userDetails) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));
        if (userDetails != null && !event.getOrganizer().getId().equals(userDetails.getId())) {
            throw new BusinessException(ErrorCode.EVENT_OWNERSHIP_VIOLATION);
        }
        long sold = ticketRepository.countByTicketTypeEventIdAndStatusIn(eventId, List.of("CONFIRMED", "CHECKED_IN"));
        long checked = ticketRepository.countByTicketTypeEventIdAndStatus(eventId, "CHECKED_IN");
        long remaining = ticketTypeRepository.findByEventIdAndDeletedAtIsNull(eventId).stream()
                .mapToLong(ticketType -> ticketType.getQuantityRemaining())
                .sum();
        List<DashboardSnapshotResponse.GateStats> byGate = gateRepository.findByEventId(eventId).stream()
                .map(gate -> new DashboardSnapshotResponse.GateStats(gate.getId(), gate.getName(), checkInLogRepository.countByGateIdAndResult(gate.getId(), "SUCCESS")))
                .toList();
        return new DashboardSnapshotResponse(eventId, sold, checked, remaining, byGate);
    }
}
