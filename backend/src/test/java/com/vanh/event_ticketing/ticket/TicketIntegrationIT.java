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

class TicketIntegrationIT extends AbstractIntegrationTest {
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
        TicketType ticketType = seedTicketTypeWithQuantity(5);
        CustomUserDetails customer = new CustomUserDetails(seedUser("CUSTOMER"));

        TicketResponse response = ticketService.reserve(
                new ReserveRequest(ticketType.getId(), 1),
                UUID.randomUUID().toString(),
                customer
        );

        assertNotNull(response);
        assertEquals("RESERVED", response.status());
        assertNotNull(response.expiresAt());

        TicketType updated = ticketTypeRepository.findById(ticketType.getId()).orElseThrow();
        assertEquals(4, updated.getQuantityRemaining());
        assertEquals(5, updated.getQuantityTotal());

        long ticketCount = ticketRepository.countByTicketTypeEventIdAndStatusIn(
                ticketType.getEvent().getId(),
                java.util.List.of("RESERVED")
        );
        assertEquals(1, ticketCount);
    }

    @Test
    void reserve_shouldRespectForUpdateLock_sequentialReserves() {
        TicketType ticketType = seedTicketTypeWithQuantity(3);
        CustomUserDetails customer1 = new CustomUserDetails(seedUser("CUSTOMER"));
        CustomUserDetails customer2 = new CustomUserDetails(seedUser("CUSTOMER"));
        CustomUserDetails customer3 = new CustomUserDetails(seedUser("CUSTOMER"));

        ticketService.reserve(new ReserveRequest(ticketType.getId(), 1), UUID.randomUUID().toString(), customer1);
        ticketService.reserve(new ReserveRequest(ticketType.getId(), 1), UUID.randomUUID().toString(), customer2);
        ticketService.reserve(new ReserveRequest(ticketType.getId(), 1), UUID.randomUUID().toString(), customer3);

        TicketType finalState = ticketTypeRepository.findById(ticketType.getId()).orElseThrow();
        assertEquals(0, finalState.getQuantityRemaining());

        long ticketCount = ticketRepository.countByTicketTypeEventIdAndStatusIn(
                ticketType.getEvent().getId(),
                java.util.List.of("RESERVED")
        );
        assertEquals(3, ticketCount);
    }

    @Test
    void confirm_shouldUpdateTicketAndGenerateQrCode() {
        TicketType ticketType = seedTicketTypeWithQuantity(1);
        CustomUserDetails customer = new CustomUserDetails(seedUser("CUSTOMER"));
        TicketResponse reserved = ticketService.reserve(
                new ReserveRequest(ticketType.getId(), 1),
                UUID.randomUUID().toString(),
                customer
        );

        TicketResponse confirmed = ticketService.confirm(reserved.id(), customer);

        assertEquals("CONFIRMED", confirmed.status());
        assertNotNull(confirmed.qrCode());
        assertNotNull(confirmed.confirmedAt());

        TicketType finalState = ticketTypeRepository.findById(ticketType.getId()).orElseThrow();
        assertEquals(0, finalState.getQuantityRemaining());
    }

    @Test
    void cancel_shouldRestoreQuantityRemaining() {
        TicketType ticketType = seedTicketTypeWithQuantity(5);
        CustomUserDetails customer = new CustomUserDetails(seedUser("CUSTOMER"));
        TicketResponse reserved = ticketService.reserve(
                new ReserveRequest(ticketType.getId(), 1),
                UUID.randomUUID().toString(),
                customer
        );
        assertEquals(4, ticketTypeRepository.findById(ticketType.getId()).orElseThrow().getQuantityRemaining());

        ticketService.cancel(reserved.id(), customer);

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
