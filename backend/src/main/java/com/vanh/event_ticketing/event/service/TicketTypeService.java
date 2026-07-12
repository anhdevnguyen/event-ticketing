package com.vanh.event_ticketing.event.service;

import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.event.dto.TicketTypeRequest;
import com.vanh.event_ticketing.event.dto.TicketTypeResponse;
import java.util.List;

public interface TicketTypeService {
    TicketTypeResponse create(Long eventId, TicketTypeRequest request, CustomUserDetails userDetails);
    List<TicketTypeResponse> list(Long eventId);
    TicketTypeResponse get(Long id);
    TicketTypeResponse update(Long id, TicketTypeRequest request, CustomUserDetails userDetails);
}
