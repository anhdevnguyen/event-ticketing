package com.vanh.event_ticketing.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vanh.event_ticketing.auth.entity.Role;
import com.vanh.event_ticketing.auth.entity.User;
import com.vanh.event_ticketing.auth.repository.RoleRepository;
import com.vanh.event_ticketing.auth.repository.UserRepository;
import com.vanh.event_ticketing.common.exception.BusinessException;
import com.vanh.event_ticketing.common.exception.ErrorCode;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.event.entity.Event;
import com.vanh.event_ticketing.event.entity.TicketType;
import com.vanh.event_ticketing.event.repository.EventRepository;
import com.vanh.event_ticketing.event.repository.TicketTypeRepository;
import com.vanh.event_ticketing.support.AbstractIntegrationTest;
import com.vanh.event_ticketing.support.TestDataBuilder;
import com.vanh.event_ticketing.ticket.dto.ReserveRequest;
import com.vanh.event_ticketing.ticket.repository.TicketRepository;
import com.vanh.event_ticketing.ticket.service.TicketService;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TicketReservationConcurrencyIT extends AbstractIntegrationTest {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private TicketTypeRepository ticketTypeRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void reserve_50ConcurrentRequests_onlyOneShouldSucceed_whenOnlyOneTicketLeft() throws InterruptedException {
        TicketType ticketType = seedTicketTypeWithQuantity(1);
        CustomUserDetails customer = new CustomUserDetails(seedUser("CUSTOMER"));
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger soldOutCount = new AtomicInteger();
        Queue<Throwable> unexpected = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    ticketService.reserve(new ReserveRequest(ticketType.getId(), 1), UUID.randomUUID().toString(), customer);
                    successCount.incrementAndGet();
                } catch (BusinessException ex) {
                    if (ex.getErrorCode() == ErrorCode.TICKET_SOLD_OUT) {
                        soldOutCount.incrementAndGet();
                    } else {
                        unexpected.add(ex);
                    }
                } catch (Throwable ex) {
                    unexpected.add(ex);
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await(10, TimeUnit.SECONDS);
        start.countDown();
        assertTrue(done.await(20, TimeUnit.SECONDS));
        executor.shutdownNow();

        assertTrue(unexpected.isEmpty(), unexpected.toString());
        assertEquals(1, successCount.get());
        assertEquals(49, soldOutCount.get());
        assertEquals(1, ticketRepository.countByTicketTypeEventIdAndStatusIn(ticketType.getEvent().getId(), java.util.List.of("RESERVED")));
        assertEquals(0, ticketTypeRepository.findById(ticketType.getId()).orElseThrow().getQuantityRemaining());
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
