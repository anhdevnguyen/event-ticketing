package com.vanh.event_ticketing.checkin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vanh.event_ticketing.auth.entity.Role;
import com.vanh.event_ticketing.auth.entity.User;
import com.vanh.event_ticketing.auth.repository.RoleRepository;
import com.vanh.event_ticketing.auth.repository.UserRepository;
import com.vanh.event_ticketing.checkin.dto.CheckInRequest;
import com.vanh.event_ticketing.checkin.repository.CheckInLogRepository;
import com.vanh.event_ticketing.checkin.service.CheckInService;
import com.vanh.event_ticketing.common.exception.BusinessException;
import com.vanh.event_ticketing.common.exception.ErrorCode;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.event.entity.Event;
import com.vanh.event_ticketing.event.entity.TicketType;
import com.vanh.event_ticketing.event.repository.EventRepository;
import com.vanh.event_ticketing.event.repository.TicketTypeRepository;
import com.vanh.event_ticketing.gate.entity.Gate;
import com.vanh.event_ticketing.gate.repository.GateRepository;
import com.vanh.event_ticketing.support.AbstractIntegrationTest;
import com.vanh.event_ticketing.support.TestDataBuilder;
import com.vanh.event_ticketing.ticket.entity.Ticket;
import com.vanh.event_ticketing.ticket.repository.TicketRepository;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CheckInConcurrencyIT extends AbstractIntegrationTest {
    @Autowired
    private CheckInService checkInService;
    @Autowired
    private CheckInLogRepository checkInLogRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TicketTypeRepository ticketTypeRepository;
    @Autowired
    private GateRepository gateRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void checkIn_20ConcurrentScans_onlyOneShouldSucceed() throws InterruptedException {
        User organizer = seedUser("ORGANIZER");
        Event event = eventRepository.save(TestDataBuilder.event(organizer));
        User staff = seedUser("CHECKIN_STAFF");
        staff.setAssignedEventId(event.getId());
        staff = userRepository.save(staff);
        TicketType ticketType = ticketTypeRepository.save(TestDataBuilder.ticketType(event, 1));
        Gate gate = gateRepository.save(TestDataBuilder.gate(event));
        Ticket ticket = ticketRepository.save(TestDataBuilder.confirmedTicket(ticketType, seedUser("CUSTOMER")));
        CustomUserDetails staffDetails = new CustomUserDetails(staff);
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger duplicateCount = new AtomicInteger();
        Queue<Throwable> unexpected = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    checkInService.checkIn(new CheckInRequest(ticket.getQrCode(), gate.getId()), staffDetails);
                    successCount.incrementAndGet();
                } catch (BusinessException ex) {
                    if (ex.getErrorCode() == ErrorCode.TICKET_ALREADY_CHECKED_IN) {
                        duplicateCount.incrementAndGet();
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
        assertEquals(19, duplicateCount.get());
        assertEquals("CHECKED_IN", ticketRepository.findById(ticket.getId()).orElseThrow().getStatus());
        assertEquals(1, checkInLogRepository.countByGateIdAndResult(gate.getId(), "SUCCESS"));
        assertEquals(19, checkInLogRepository.countByGateIdAndResult(gate.getId(), "DUPLICATE"));
    }

    private User seedUser(String roleName) {
        Role role = roleRepository.findByName(roleName).orElseThrow();
        return userRepository.save(TestDataBuilder.user(role));
    }
}
