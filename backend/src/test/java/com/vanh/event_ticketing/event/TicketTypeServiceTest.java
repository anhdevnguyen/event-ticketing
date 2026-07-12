package com.vanh.event_ticketing.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vanh.event_ticketing.auth.entity.User;
import com.vanh.event_ticketing.common.exception.BusinessException;
import com.vanh.event_ticketing.common.exception.ErrorCode;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.event.dto.TicketTypeRequest;
import com.vanh.event_ticketing.event.dto.TicketTypeResponse;
import com.vanh.event_ticketing.event.entity.Event;
import com.vanh.event_ticketing.event.entity.TicketType;
import com.vanh.event_ticketing.event.mapper.TicketTypeMapper;
import com.vanh.event_ticketing.event.repository.EventRepository;
import com.vanh.event_ticketing.event.repository.TicketTypeRepository;
import com.vanh.event_ticketing.event.service.TicketTypeServiceImpl;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketTypeServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private TicketTypeRepository ticketTypeRepository;
    @Mock
    private TicketTypeMapper ticketTypeMapper;

    @Test
    void create_shouldSetQuantityRemainingEqualToTotal() {
        TicketTypeServiceImpl service = new TicketTypeServiceImpl(eventRepository, ticketTypeRepository, ticketTypeMapper);
        Event event = event(10L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(ticketTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(ticketTypeMapper.toResponse(any())).thenReturn(new TicketTypeResponse(1L, 1L, "Standard", new BigDecimal("100000"), 50, 50, null, null));
        TicketTypeRequest request = new TicketTypeRequest("Standard", new BigDecimal("100000"), 50, null, null);

        TicketTypeResponse response = service.create(1L, request, userDetails(10L));

        assertNotNull(response);
        ArgumentCaptor<TicketType> captor = ArgumentCaptor.forClass(TicketType.class);
        verify(ticketTypeRepository).save(captor.capture());
        assertEquals(50, captor.getValue().getQuantityTotal());
        assertEquals(50, captor.getValue().getQuantityRemaining());
    }

    @Test
    void create_shouldRejectWhenNotOwner() {
        TicketTypeServiceImpl service = new TicketTypeServiceImpl(eventRepository, ticketTypeRepository, ticketTypeMapper);
        Event event = event(99L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        TicketTypeRequest request = new TicketTypeRequest("Standard", new BigDecimal("100000"), 50, null, null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(1L, request, userDetails(10L)));

        assertEquals(ErrorCode.EVENT_OWNERSHIP_VIOLATION, ex.getErrorCode());
    }

    @Test
    void create_shouldRejectWhenEventNotFound() {
        TicketTypeServiceImpl service = new TicketTypeServiceImpl(eventRepository, ticketTypeRepository, ticketTypeMapper);
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        TicketTypeRequest request = new TicketTypeRequest("Standard", new BigDecimal("100000"), 50, null, null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(1L, request, userDetails(10L)));

        assertEquals(ErrorCode.EVENT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void update_shouldRejectWhenNotOwner() {
        TicketTypeServiceImpl service = new TicketTypeServiceImpl(eventRepository, ticketTypeRepository, ticketTypeMapper);
        TicketType ticketType = ticketType(99L);
        when(ticketTypeRepository.findById(1L)).thenReturn(Optional.of(ticketType));
        TicketTypeRequest request = new TicketTypeRequest("Updated", new BigDecimal("100000"), 50, null, null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(1L, request, userDetails(10L)));

        assertEquals(ErrorCode.EVENT_OWNERSHIP_VIOLATION, ex.getErrorCode());
    }

    @Test
    void get_shouldRejectWhenDeleted() {
        TicketTypeServiceImpl service = new TicketTypeServiceImpl(eventRepository, ticketTypeRepository, ticketTypeMapper);
        TicketType ticketType = ticketType(10L);
        ticketType.setDeletedAt(Instant.now());
        when(ticketTypeRepository.findById(1L)).thenReturn(Optional.of(ticketType));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.get(1L));

        assertEquals(ErrorCode.TICKET_TYPE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void update_shouldNotChangeQuantityTotal() {
        // Business rule: quantity_total only set at creation, not at update
        TicketTypeServiceImpl service = new TicketTypeServiceImpl(eventRepository, ticketTypeRepository, ticketTypeMapper);
        TicketType ticketType = ticketType(10L);
        ticketType.setQuantityTotal(50);
        ticketType.setQuantityRemaining(30);
        when(ticketTypeRepository.findById(1L)).thenReturn(Optional.of(ticketType));
        when(ticketTypeMapper.toResponse(any())).thenReturn(new TicketTypeResponse(1L, 1L, "Updated", new BigDecimal("100000"), 50, 30, null, null));
        TicketTypeRequest request = new TicketTypeRequest("Updated", new BigDecimal("100000"), 999, null, null);

        TicketTypeResponse response = service.update(1L, request, userDetails(10L));

        // quantityTotal should remain 50, not changed to 999
        assertEquals(50, ticketType.getQuantityTotal());
        assertEquals(30, ticketType.getQuantityRemaining());
    }

    private static Event event(Long organizerId) {
        Event event = new Event();
        event.setId(1L);
        User organizer = new User();
        organizer.setId(organizerId);
        event.setOrganizer(organizer);
        event.setName("Test Event");
        event.setStartTime(Instant.now().plusSeconds(3600));
        event.setEndTime(Instant.now().plusSeconds(7200));
        return event;
    }

    private static TicketType ticketType(Long organizerId) {
        TicketType ticketType = new TicketType();
        ticketType.setId(1L);
        ticketType.setEvent(event(organizerId));
        ticketType.setName("Standard");
        ticketType.setPrice(new BigDecimal("100000"));
        ticketType.setQuantityTotal(100);
        ticketType.setQuantityRemaining(80);
        return ticketType;
    }

    private static CustomUserDetails userDetails(Long id) {
        User user = new User();
        user.setId(id);
        return new CustomUserDetails(user);
    }
}
