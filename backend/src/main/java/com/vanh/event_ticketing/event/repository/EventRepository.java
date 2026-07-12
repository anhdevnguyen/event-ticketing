package com.vanh.event_ticketing.event.repository;

import com.vanh.event_ticketing.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByDeletedAtIsNull(Pageable pageable);
}
