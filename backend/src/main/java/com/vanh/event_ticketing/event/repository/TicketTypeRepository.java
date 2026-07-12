package com.vanh.event_ticketing.event.repository;

import com.vanh.event_ticketing.event.entity.TicketType;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
    List<TicketType> findByEventIdAndDeletedAtIsNull(Long eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TicketType> findWithLockById(Long id);
}
