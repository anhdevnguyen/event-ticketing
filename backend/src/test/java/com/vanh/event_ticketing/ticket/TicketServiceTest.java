package com.vanh.event_ticketing.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vanh.event_ticketing.auth.entity.User;
import com.vanh.event_ticketing.common.exception.BusinessException;
import com.vanh.event_ticketing.common.exception.ErrorCode;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.event.entity.Event;
import com.vanh.event_ticketing.event.entity.TicketType;
import com.vanh.event_ticketing.event.repository.TicketTypeRepository;
import com.vanh.event_ticketing.ticket.dto.ReserveRequest;
import com.vanh.event_ticketing.ticket.dto.TicketResponse;
import com.vanh.event_ticketing.ticket.entity.Ticket;
import com.vanh.event_ticketing.ticket.mapper.TicketMapper;
import com.vanh.event_ticketing.ticket.qr.QrCodeGenerator;
import com.vanh.event_ticketing.ticket.repository.TicketRepository;
import com.vanh.event_ticketing.ticket.service.TicketServiceImpl;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {
    @Mock
    private TicketTypeRepository ticketTypeRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private QrCodeGenerator qrCodeGenerator;

    @Test
    void reserve_shouldThrowTicketSoldOut_whenNotEnoughQuantity() {
        TicketServiceImpl service = new TicketServiceImpl(ticketTypeRepository, ticketRepository, new TicketMapper(), qrCodeGenerator);
        TicketType ticketType = new TicketType();
        ticketType.setQuantityRemaining(1);
        when(ticketRepository.findByCustomerIdAndIdempotencyKey(10L, "key-1")).thenReturn(Optional.empty());
        when(ticketTypeRepository.findWithLockById(1L)).thenReturn(Optional.of(ticketType));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.reserve(new ReserveRequest(1L, 2), "key-1", userDetails(10L)));

        assertEquals(ErrorCode.TICKET_SOLD_OUT, ex.getErrorCode());
    }

    @Test
    void reserve_shouldRequireIdempotencyKey() {
        TicketServiceImpl service = new TicketServiceImpl(ticketTypeRepository, ticketRepository, new TicketMapper(), qrCodeGenerator);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.reserve(new ReserveRequest(1L, 1), " ", userDetails(10L)));

        assertEquals(ErrorCode.IDEMPOTENCY_KEY_REQUIRED, ex.getErrorCode());
    }

    @Test
    void reserve_shouldReturnExistingTicket_whenIdempotencyKeyAlreadyExists() {
        TicketServiceImpl service = new TicketServiceImpl(ticketTypeRepository, ticketRepository, new TicketMapper(), qrCodeGenerator);
        Ticket existingTicket = reservedTicket();
        when(ticketRepository.findByCustomerIdAndIdempotencyKey(10L, "key-1")).thenReturn(Optional.of(existingTicket));

        TicketResponse response = service.reserve(new ReserveRequest(1L, 1), "key-1", userDetails(10L));

        assertNotNull(response);
        assertEquals("RESERVED", response.status());
        verify(ticketTypeRepository, never()).findWithLockById(any());
    }

    @Test
    void reserve_shouldRejectMultipleTickets() {
        TicketServiceImpl service = new TicketServiceImpl(ticketTypeRepository, ticketRepository, new TicketMapper(), qrCodeGenerator);
        TicketType ticketType = new TicketType();
        ticketType.setQuantityRemaining(5);
        when(ticketRepository.findByCustomerIdAndIdempotencyKey(10L, "key-1")).thenReturn(Optional.empty());
        when(ticketTypeRepository.findWithLockById(1L)).thenReturn(Optional.of(ticketType));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.reserve(new ReserveRequest(1L, 2), "key-1", userDetails(10L)));

        assertEquals(ErrorCode.ONE_TICKET_PER_RESERVATION, ex.getErrorCode());
    }

    @Test
    void confirm_shouldRejectWhenReservationExpired() {
        TicketServiceImpl service = new TicketServiceImpl(ticketTypeRepository, ticketRepository, new TicketMapper(), qrCodeGenerator);
        Ticket expiredTicket = reservedTicket();
        expiredTicket.setExpiresAt(Instant.now().minusSeconds(60));
        when(ticketRepository.findWithLockById(1L)).thenReturn(Optional.of(expiredTicket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.confirm(1L, userDetails(10L)));

        assertEquals(ErrorCode.RESERVATION_EXPIRED, ex.getErrorCode());
        assertEquals("EXPIRED", expiredTicket.getStatus());
    }

    @Test
    void confirm_shouldRejectWhenNotReservedStatus() {
        TicketServiceImpl service = new TicketServiceImpl(ticketTypeRepository, ticketRepository, new TicketMapper(), qrCodeGenerator);
        Ticket confirmedTicket = reservedTicket();
        confirmedTicket.setStatus("CONFIRMED");
        confirmedTicket.setExpiresAt(Instant.now().plusSeconds(300));
        when(ticketRepository.findWithLockById(1L)).thenReturn(Optional.of(confirmedTicket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.confirm(1L, userDetails(10L)));

        assertEquals(ErrorCode.INVALID_TICKET_STATUS, ex.getErrorCode());
    }

    @Test
    void cancel_shouldRejectWhenNotReservedStatus() {
        TicketServiceImpl service = new TicketServiceImpl(ticketTypeRepository, ticketRepository, new TicketMapper(), qrCodeGenerator);
        Ticket confirmedTicket = reservedTicket();
        confirmedTicket.setStatus("CONFIRMED");
        when(ticketRepository.findWithLockById(1L)).thenReturn(Optional.of(confirmedTicket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.cancel(1L, userDetails(10L)));

        assertEquals(ErrorCode.INVALID_TICKET_STATUS, ex.getErrorCode());
    }

    @Test
    void get_shouldRejectWhenOwnershipViolation() {
        TicketServiceImpl service = new TicketServiceImpl(ticketTypeRepository, ticketRepository, new TicketMapper(), qrCodeGenerator);
        Ticket ticket = reservedTicket();
        ticket.getCustomer().setId(99L);
        when(ticketRepository.findWithLockById(1L)).thenReturn(Optional.of(ticket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.get(1L, userDetails(10L)));

        assertEquals(ErrorCode.TICKET_OWNERSHIP_VIOLATION, ex.getErrorCode());
    }

    @Test
    void qrPng_shouldRejectWhenTicketNotConfirmed() {
        TicketServiceImpl service = new TicketServiceImpl(ticketTypeRepository, ticketRepository, new TicketMapper(), qrCodeGenerator);
        Ticket reservedTicket = reservedTicket();
        reservedTicket.setQrCode(null);
        when(ticketRepository.findWithLockById(1L)).thenReturn(Optional.of(reservedTicket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.qrPng(1L, userDetails(10L)));

        assertEquals(ErrorCode.INVALID_TICKET_STATUS, ex.getErrorCode());
    }

    private static Ticket reservedTicket() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setStatus("RESERVED");
        ticket.setIdempotencyKey("key-1");
        ticket.setReservedAt(Instant.now());
        ticket.setExpiresAt(Instant.now().plusSeconds(600));
        
        User customer = new User();
        customer.setId(10L);
        ticket.setCustomer(customer);
        
        TicketType ticketType = new TicketType();
        ticketType.setId(1L);
        ticketType.setQuantityRemaining(5);
        Event event = new Event();
        event.setId(1L);
        ticketType.setEvent(event);
        ticket.setTicketType(ticketType);
        
        return ticket;
    }

    private static CustomUserDetails userDetails(Long id) {
        User user = new User();
        user.setId(id);
        return new CustomUserDetails(user);
    }
}
