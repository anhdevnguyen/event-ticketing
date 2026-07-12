package com.vanh.event_ticketing.gate.service;

import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.gate.dto.GateRequest;
import com.vanh.event_ticketing.gate.dto.GateResponse;
import java.util.List;

public interface GateService {
    GateResponse create(Long eventId, GateRequest request, CustomUserDetails userDetails);
    List<GateResponse> list(Long eventId, CustomUserDetails userDetails);
    GateResponse update(Long id, GateRequest request, CustomUserDetails userDetails);
    void delete(Long id, CustomUserDetails userDetails);
}
