package com.vanh.event_ticketing.event.controller;

import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.event.dto.TicketTypeRequest;
import com.vanh.event_ticketing.event.dto.TicketTypeResponse;
import com.vanh.event_ticketing.event.service.TicketTypeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TicketTypeController {
    private final TicketTypeService ticketTypeService;

    @PostMapping("/api/v1/events/{eventId}/ticket-types")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ORGANIZER')")
    public TicketTypeResponse create(@PathVariable Long eventId, @Valid @RequestBody TicketTypeRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ticketTypeService.create(eventId, request, userDetails);
    }

    @GetMapping("/api/v1/events/{eventId}/ticket-types")
    public List<TicketTypeResponse> list(@PathVariable Long eventId) {
        return ticketTypeService.list(eventId);
    }

    @GetMapping("/api/v1/ticket-types/{id}")
    public TicketTypeResponse get(@PathVariable Long id) {
        return ticketTypeService.get(id);
    }

    @PutMapping("/api/v1/ticket-types/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public TicketTypeResponse update(@PathVariable Long id, @Valid @RequestBody TicketTypeRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ticketTypeService.update(id, request, userDetails);
    }
}
