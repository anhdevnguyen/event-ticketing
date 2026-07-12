package com.vanh.event_ticketing.ticket.controller;

import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.ticket.dto.ReserveRequest;
import com.vanh.event_ticketing.ticket.dto.TicketResponse;
import com.vanh.event_ticketing.ticket.service.TicketService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class TicketController {
    private final TicketService ticketService;

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse reserve(@Valid @RequestBody ReserveRequest request, @RequestHeader("Idempotency-Key") String idempotencyKey, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ticketService.reserve(request, idempotencyKey, userDetails);
    }

    @PostMapping("/{id}/confirm")
    public TicketResponse confirm(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ticketService.confirm(id, userDetails);
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ticketService.cancel(id, userDetails);
    }

    @GetMapping("/my")
    public List<TicketResponse> myTickets(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ticketService.myTickets(userDetails);
    }

    @GetMapping("/{id}")
    public TicketResponse get(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ticketService.get(id, userDetails);
    }

    @GetMapping(value = "/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] qr(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ticketService.qrPng(id, userDetails);
    }
}
