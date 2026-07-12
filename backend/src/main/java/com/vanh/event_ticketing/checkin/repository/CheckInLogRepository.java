package com.vanh.event_ticketing.checkin.repository;

import com.vanh.event_ticketing.checkin.entity.CheckInLog;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInLogRepository extends JpaRepository<CheckInLog, Long> {
    Page<CheckInLog> findByGateIdAndCheckedInAtBetween(Long gateId, Instant from, Instant to, Pageable pageable);

    long countByGateIdAndResult(Long gateId, String result);
}
