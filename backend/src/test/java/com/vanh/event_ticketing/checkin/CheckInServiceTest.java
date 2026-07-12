package com.vanh.event_ticketing.checkin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vanh.event_ticketing.auth.entity.User;
import com.vanh.event_ticketing.checkin.dto.CheckInRequest;
import com.vanh.event_ticketing.checkin.dto.CheckInResponse;
import com.vanh.event_ticketing.checkin.entity.CheckInLog;
import com.vanh.event_ticketing.checkin.mapper.CheckInLogMapper;
import com.vanh.event_ticketing.checkin.repository.CheckInLogRepository;
import com.vanh.event_ticketing.checkin.service.CheckInServiceImpl;
import com.vanh.event_ticketing.common.exception.BusinessException;
import com.vanh.event_ticketing.common.exception.ErrorCode;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.dashboard.websocket.DashboardEventPublisher;
import com.vanh.event_ticketing.event.entity.Event;
import com.vanh.event_ticketing.event.entity.TicketType;
import com.vanh.event_ticketing.gate.entity.Gate;
import com.vanh.event_ticketing.gate.repository.GateRepository;
import com.vanh.event_ticketing.ticket.entity.Ticket;
import com.vanh.event_ticketing.ticket.repository.TicketRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckInServiceTest {
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private GateRepository gateRepository;
    @Mock
    private CheckInLogRepository checkInLogRepository;
    @Mock
    private DashboardEventPublisher dashboardEventPublisher;

    @Test
    void checkIn_shouldRejectSecondScanAndSaveDuplicateLog() {
        CheckInServiceImpl service = new CheckInServiceImpl(ticketRepository, gateRepository, checkInLogRepository, new CheckInLogMapper(), dashboardEventPublisher);
        when(ticketRepository.findWithLockByQrCode("qr-1")).thenReturn(Optional.of(ticket("CHECKED_IN", 1L)));
        when(gateRepository.findById(1L)).thenReturn(Optional.of(gate(1L)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.checkIn(new CheckInRequest("qr-1", 1L), userDetails(null)));

        assertEquals(ErrorCode.TICKET_ALREADY_CHECKED_IN, ex.getErrorCode());
        ArgumentCaptor<CheckInLog> log = ArgumentCaptor.forClass(CheckInLog.class);
        verify(checkInLogRepository).save(log.capture());
        assertEquals("DUPLICATE", log.getValue().getResult());
    }

    @Test
    void checkIn_shouldRejectWhenTicketNotConfirmed() {
        CheckInServiceImpl service = new CheckInServiceImpl(ticketRepository, gateRepository, checkInLogRepository, new CheckInLogMapper(), dashboardEventPublisher);
        when(ticketRepository.findWithLockByQrCode("qr-1")).thenReturn(Optional.of(ticket("RESERVED", 1L)));
        when(gateRepository.findById(1L)).thenReturn(Optional.of(gate(1L)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.checkIn(new CheckInRequest("qr-1", 1L), userDetails(null)));

        assertEquals(ErrorCode.INVALID_TICKET_STATUS, ex.getErrorCode());
    }

    @Test
    void checkIn_shouldRejectWhenGateEventMismatch() {
        CheckInServiceImpl service = new CheckInServiceImpl(ticketRepository, gateRepository, checkInLogRepository, new CheckInLogMapper(), dashboardEventPublisher);
        // Ticket belongs to event 1, gate belongs to event 2
        when(ticketRepository.findWithLockByQrCode("qr-1")).thenReturn(Optional.of(ticket("CONFIRMED", 1L)));
        when(gateRepository.findById(1L)).thenReturn(Optional.of(gate(2L)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.checkIn(new CheckInRequest("qr-1", 1L), userDetails(null)));

        assertEquals(ErrorCode.CHECKIN_GATE_EVENT_MISMATCH, ex.getErrorCode());
    }

    @Test
    void checkIn_shouldRejectWhenStaffAssignedToDifferentEvent() {
        CheckInServiceImpl service = new CheckInServiceImpl(ticketRepository, gateRepository, checkInLogRepository, new CheckInLogMapper(), dashboardEventPublisher);
        // Ticket belongs to event 1, gate is for event 1, but staff is assigned to event 99
        when(ticketRepository.findWithLockByQrCode("qr-1")).thenReturn(Optional.of(ticket("CONFIRMED", 1L)));
        when(gateRepository.findById(1L)).thenReturn(Optional.of(gate(1L)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.checkIn(new CheckInRequest("qr-1", 1L), userDetails(99L)));

        assertEquals(ErrorCode.CHECKIN_STAFF_EVENT_MISMATCH, ex.getErrorCode());
    }

    @Test
    void checkIn_shouldSucceedWhenConfirmedAndMatchingGate() {
        CheckInLogMapper mapper = new CheckInLogMapper();
        CheckInServiceImpl service = new CheckInServiceImpl(ticketRepository, gateRepository, checkInLogRepository, mapper, dashboardEventPublisher);
        Ticket ticket = ticket("CONFIRMED", 1L);
        Gate gate = gate(1L);
        when(ticketRepository.findWithLockByQrCode("qr-1")).thenReturn(Optional.of(ticket));
        when(gateRepository.findById(1L)).thenReturn(Optional.of(gate));
        when(checkInLogRepository.save(any())).thenAnswer(inv -> {
            CheckInLog log = inv.getArgument(0);
            log.setId(1L);
            log.setTicket(ticket);
            log.setGate(gate);
            return log;
        });

        CheckInResponse response = service.checkIn(new CheckInRequest("qr-1", 1L), userDetails(null));

        assertNotNull(response);
        assertEquals("CHECKED_IN", ticket.getStatus());
        ArgumentCaptor<CheckInLog> log = ArgumentCaptor.forClass(CheckInLog.class);
        verify(checkInLogRepository).save(log.capture());
        assertEquals("SUCCESS", log.getValue().getResult());
    }

    @Test
    void checkIn_shouldRejectWhenTicketNotFound() {
        CheckInServiceImpl service = new CheckInServiceImpl(ticketRepository, gateRepository, checkInLogRepository, new CheckInLogMapper(), dashboardEventPublisher);
        when(ticketRepository.findWithLockByQrCode("qr-unknown")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.checkIn(new CheckInRequest("qr-unknown", 1L), userDetails(null)));

        assertEquals(ErrorCode.TICKET_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void checkIn_shouldRejectWhenGateNotFound() {
        CheckInServiceImpl service = new CheckInServiceImpl(ticketRepository, gateRepository, checkInLogRepository, new CheckInLogMapper(), dashboardEventPublisher);
        when(ticketRepository.findWithLockByQrCode("qr-1")).thenReturn(Optional.of(ticket("CONFIRMED", 1L)));
        when(gateRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.checkIn(new CheckInRequest("qr-1", 99L), userDetails(null)));

        assertEquals(ErrorCode.GATE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void checkIn_shouldAllowStaffWithNoEventAssignment() {
        // Staff with null assignedEventId should be allowed to check-in at any event
        CheckInLogMapper mapper = new CheckInLogMapper();
        CheckInServiceImpl service = new CheckInServiceImpl(ticketRepository, gateRepository, checkInLogRepository, mapper, dashboardEventPublisher);
        Ticket ticket = ticket("CONFIRMED", 1L);
        Gate gate = gate(1L);
        when(ticketRepository.findWithLockByQrCode("qr-1")).thenReturn(Optional.of(ticket));
        when(gateRepository.findById(1L)).thenReturn(Optional.of(gate));
        when(checkInLogRepository.save(any())).thenAnswer(inv -> {
            CheckInLog log = inv.getArgument(0);
            log.setId(1L);
            log.setTicket(ticket);
            log.setGate(gate);
            return log;
        });

        // Staff with null assignedEventId — should not trigger CHECKIN_STAFF_EVENT_MISMATCH
        CheckInResponse response = service.checkIn(new CheckInRequest("qr-1", 1L), userDetails(null));

        assertNotNull(response);
    }

    private static Ticket ticket(String status, Long eventId) {
        Ticket ticket = new Ticket();
        ticket.setStatus(status);
        ticket.setQrCode("qr-1");
        TicketType ticketType = new TicketType();
        ticketType.setEvent(event(eventId));
        ticket.setTicketType(ticketType);
        return ticket;
    }

    private static Gate gate(Long eventId) {
        Gate gate = new Gate();
        gate.setId(1L);
        gate.setEvent(event(eventId));
        return gate;
    }

    private static Event event(Long id) {
        Event event = new Event();
        event.setId(id);
        return event;
    }

    private static CustomUserDetails userDetails(Long assignedEventId) {
        User user = new User();
        user.setAssignedEventId(assignedEventId);
        return new CustomUserDetails(user);
    }
}
