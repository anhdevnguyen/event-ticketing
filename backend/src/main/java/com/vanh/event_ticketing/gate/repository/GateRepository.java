package com.vanh.event_ticketing.gate.repository;

import com.vanh.event_ticketing.gate.entity.Gate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GateRepository extends JpaRepository<Gate, Long> {
    List<Gate> findByEventId(Long eventId);
}
