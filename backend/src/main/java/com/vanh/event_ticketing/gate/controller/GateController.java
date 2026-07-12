package com.vanh.event_ticketing.gate.controller;

import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.gate.dto.GateRequest;
import com.vanh.event_ticketing.gate.dto.GateResponse;
import com.vanh.event_ticketing.gate.service.GateService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GateController {
    private final GateService gateService;

    @PostMapping("/api/v1/events/{eventId}/gates")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ORGANIZER')")
    public GateResponse create(@PathVariable Long eventId, @Valid @RequestBody GateRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return gateService.create(eventId, request, userDetails);
    }

    @GetMapping("/api/v1/events/{eventId}/gates")
    @PreAuthorize("hasAnyRole('ORGANIZER','CHECKIN_STAFF')")
    public List<GateResponse> list(@PathVariable Long eventId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return gateService.list(eventId, userDetails);
    }

    @PutMapping("/api/v1/gates/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public GateResponse update(@PathVariable Long id, @Valid @RequestBody GateRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return gateService.update(id, request, userDetails);
    }

    @DeleteMapping("/api/v1/gates/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ORGANIZER')")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        gateService.delete(id, userDetails);
    }
}
