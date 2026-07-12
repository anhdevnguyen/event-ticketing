package com.vanh.event_ticketing.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.vanh.event_ticketing.auth.entity.Role;
import com.vanh.event_ticketing.auth.entity.User;
import com.vanh.event_ticketing.auth.repository.RoleRepository;
import com.vanh.event_ticketing.auth.repository.UserRepository;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.event.entity.Event;
import com.vanh.event_ticketing.event.entity.TicketType;
import com.vanh.event_ticketing.event.repository.EventRepository;
import com.vanh.event_ticketing.event.repository.TicketTypeRepository;
import com.vanh.event_ticketing.support.AbstractIntegrationTest;
import com.vanh.event_ticketing.support.TestDataBuilder;
import com.vanh.event_ticketing.ticket.dto.ReserveRequest;
import com.vanh.event_ticketing.ticket.dto.TicketResponse;
import com.vanh.event_ticketing.ticket.repository.TicketRepository;
import com.vanh.event_ticketing.ticket.service.TicketService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TicketIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TicketTypeRepository ticketTypeRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void reserve_shouldPersistCorrectly_withRealPostgresLock() {
        // Arrange: seed 1 ticket type with quantityRemaining = 5
        TicketType ticketType = seedTicketTypeWithQuantity(5);
        CustomUserDetails customer = new CustomUserDetails(seedUser("CUSTOMER"));

        // Act: reserve 1 ticket through the real service with real DB lock
        TicketResponse response = ticketService.reserve(
                new ReserveRequest(ticketType.getId(), 1),
                UUID.randomUUID().toString(),
                customer
        );

        // Assert: ticket was created correctly
        assertNotNull(response);
        assertEquals("RESERVED", response.status());
        assertNotNull(response.expiresAt());

        // Assert: quantityRemaining decreased correctly in DB
        TicketType updated = ticketTypeRepository.findById(ticketType.getId()).orElseThrow();
        assertEquals(4, updated.getQuantityRemaining());
        assertEquals(5, updated.getQuantityTotal());

        // Assert: ticket persisted in DB
        long ticketCount = ticketRepository.countByTicketTypeEventIdAndStatusIn(
                ticketType.getEvent().getId(),
                java.util.List.of("RESERVED")
        );
        assertEquals(1, ticketCount);
    }

    @Test
    void reserve_shouldRespectForUpdateLock_sequentialReserves() {
        // Arrange: seed with exactly 3 tickets
        TicketType ticketType = seedTicketTypeWithQuantity(3);
        CustomUserDetails customer1 = new CustomUserDetails(seedUser("CUSTOMER"));
        CustomUserDetails customer2 = new CustomUserDetails(seedUser("CUSTOMER"));
        CustomUserDetails customer3 = new CustomUserDetails(seedUser("CUSTOMER"));

        // Act: make 3 sequential reservations
        ticketService.reserve(new ReserveRequest(ticketType.getId(), 1), UUID.randomUUID().toString(), customer1);
        ticketService.reserve(new ReserveRequest(ticketType.getId(), 1), UUID.randomUUID().toString(), customer2);
        ticketService.reserve(new ReserveRequest(ticketType.getId(), 1), UUID.randomUUID().toString(), customer3);

        // Assert: exactly 0 remaining
        TicketType finalState = ticketTypeRepository.findById(ticketType.getId()).orElseThrow();
        assertEquals(0, finalState.getQuantityRemaining());

        // Assert: exactly 3 tickets created
        long ticketCount = ticketRepository.countByTicketTypeEventIdAndStatusIn(
                ticketType.getEvent().getId(),
                java.util.List.of("RESERVED")
        );
        assertEquals(3, ticketCount);
    }

    @Test
    void confirm_shouldUpdateTicketAndGenerateQrCode() {
        // Arrange: create a reserved ticket
        TicketType ticketType = seedTicketTypeWithQuantity(1);
        CustomUserDetails customer = new CustomUserDetails(seedUser("CUSTOMER"));
        TicketResponse reserved = ticketService.reserve(
                new ReserveRequest(ticketType.getId(), 1),
                UUID.randomUUID().toString(),
                customer
        );

        // Act: confirm the ticket
        TicketResponse confirmed = ticketService.confirm(reserved.id(), customer);

        // Assert: status changed and QR generated
        assertEquals("CONFIRMED", confirmed.status());
        assertNotNull(confirmed.qrCode());
        assertNotNull(confirmed.confirmedAt());

        // Assert: quantityRemaining still at 0 (not restored)
        TicketType finalState = ticketTypeRepository.findById(ticketType.getId()).orElseThrow();
        assertEquals(0, finalState.getQuantityRemaining());
    }

    @Test
    void cancel_shouldRestoreQuantityRemaining() {
        // Arrange: create a reserved ticket
        TicketType ticketType = seedTicketTypeWithQuantity(5);
        CustomUserDetails customer = new CustomUserDetails(seedUser("CUSTOMER"));
        TicketResponse reserved = ticketService.reserve(
                new ReserveRequest(ticketType.getId(), 1),
                UUID.randomUUID().toString(),
                customer
        );
        assertEquals(4, ticketTypeRepository.findById(ticketType.getId()).orElseThrow().getQuantityRemaining());

        // Act: cancel the ticket
        ticketService.cancel(reserved.id(), customer);

        // Assert: quantity restored
        TicketType finalState = ticketTypeRepository.findById(ticketType.getId()).orElseThrow();
        assertEquals(5, finalState.getQuantityRemaining());
    }

    private TicketType seedTicketTypeWithQuantity(int quantity) {
        User organizer = seedUser("ORGANIZER");
        Event event = eventRepository.save(TestDataBuilder.event(organizer));
        return ticketTypeRepository.save(TestDataBuilder.ticketType(event, quantity));
    }

    private User seedUser(String roleName) {
        Role role = roleRepository.findByName(roleName).orElseThrow();
        return userRepository.save(TestDataBuilder.user(role));
    }
}
