package com.vanh.event_ticketing.ticket.service;

import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.ticket.dto.ReserveRequest;
import com.vanh.event_ticketing.ticket.dto.TicketResponse;
import java.util.List;

public interface TicketService {
    TicketResponse reserve(ReserveRequest request, String idempotencyKey, CustomUserDetails userDetails);
    TicketResponse confirm(Long id, CustomUserDetails userDetails);
    void cancel(Long id, CustomUserDetails userDetails);
    List<TicketResponse> myTickets(CustomUserDetails userDetails);
    TicketResponse get(Long id, CustomUserDetails userDetails);
    byte[] qrPng(Long id, CustomUserDetails userDetails);
}
