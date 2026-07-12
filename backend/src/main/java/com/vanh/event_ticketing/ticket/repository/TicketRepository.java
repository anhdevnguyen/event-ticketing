package com.vanh.event_ticketing.ticket.repository;

import com.vanh.event_ticketing.ticket.entity.Ticket;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByCustomerIdAndIdempotencyKey(Long customerId, String idempotencyKey);

    List<Ticket> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Ticket> findWithLockById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Ticket> findWithLockByQrCode(String qrCode);

    long countByTicketTypeEventIdAndStatusIn(Long eventId, Iterable<String> statuses);

    long countByTicketTypeEventIdAndStatus(Long eventId, String status);
}
