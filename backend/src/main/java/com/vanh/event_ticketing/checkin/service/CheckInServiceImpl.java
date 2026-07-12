package com.vanh.event_ticketing.checkin.service;

import com.vanh.event_ticketing.checkin.dto.CheckInRequest;
import com.vanh.event_ticketing.checkin.dto.CheckInResponse;
import com.vanh.event_ticketing.checkin.entity.CheckInLog;
import com.vanh.event_ticketing.checkin.mapper.CheckInLogMapper;
import com.vanh.event_ticketing.checkin.repository.CheckInLogRepository;
import com.vanh.event_ticketing.common.exception.BusinessException;
import com.vanh.event_ticketing.common.exception.ErrorCode;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.common.util.PageResponse;
import com.vanh.event_ticketing.dashboard.websocket.DashboardEventPublisher;
import com.vanh.event_ticketing.gate.entity.Gate;
import com.vanh.event_ticketing.gate.repository.GateRepository;
import com.vanh.event_ticketing.ticket.entity.Ticket;
import com.vanh.event_ticketing.ticket.repository.TicketRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {
    private final TicketRepository ticketRepository;
    private final GateRepository gateRepository;
    private final CheckInLogRepository checkInLogRepository;
    private final CheckInLogMapper checkInLogMapper;
    private final DashboardEventPublisher dashboardEventPublisher;

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public CheckInResponse checkIn(CheckInRequest request, CustomUserDetails userDetails) {
        // ponytail: lock by QR prevents double check-in for this MVP without a separate distributed lock.
        Ticket ticket = ticketRepository.findWithLockByQrCode(request.qrCode()).orElseThrow(() -> new BusinessException(ErrorCode.TICKET_NOT_FOUND));
        Gate gate = gateRepository.findById(request.gateId()).orElseThrow(() -> new BusinessException(ErrorCode.GATE_NOT_FOUND));
        if (!gate.getEvent().getId().equals(ticket.getTicketType().getEvent().getId())) {
            throw new BusinessException(ErrorCode.CHECKIN_GATE_EVENT_MISMATCH);
        }
        if (userDetails.getUser().getAssignedEventId() != null && !userDetails.getUser().getAssignedEventId().equals(gate.getEvent().getId())) {
            throw new BusinessException(ErrorCode.CHECKIN_STAFF_EVENT_MISMATCH);
        }
        CheckInLog log = new CheckInLog();
        log.setTicket(ticket);
        log.setGate(gate);
        log.setStaff(userDetails.getUser());
        if ("CHECKED_IN".equals(ticket.getStatus())) {
            log.setResult("DUPLICATE");
            checkInLogRepository.save(log);
            throw new BusinessException(ErrorCode.TICKET_ALREADY_CHECKED_IN);
        }
        if (!"CONFIRMED".equals(ticket.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_TICKET_STATUS);
        }
        ticket.setStatus("CHECKED_IN");
        ticket.setCheckedInAt(Instant.now());
        log.setResult("SUCCESS");
        checkInLogRepository.save(log);
        dashboardEventPublisher.publish(gate.getEvent().getId(), gate.getId());
        return checkInLogMapper.toResponse(log);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CheckInResponse> logs(Long gateId, Instant from, Instant to, Pageable pageable) {
        return PageResponse.from(checkInLogRepository.findByGateIdAndCheckedInAtBetween(gateId, from, to, pageable).map(checkInLogMapper::toResponse));
    }
}
