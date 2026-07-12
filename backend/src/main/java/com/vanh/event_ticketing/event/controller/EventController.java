package com.vanh.event_ticketing.event.controller;

import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.common.util.PageResponse;
import com.vanh.event_ticketing.event.dto.EventRequest;
import com.vanh.event_ticketing.event.dto.EventResponse;
import com.vanh.event_ticketing.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ORGANIZER')")
    public EventResponse create(@Valid @RequestBody EventRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return eventService.create(request, userDetails);
    }

    @GetMapping
    public PageResponse<EventResponse> list(Pageable pageable) {
        return eventService.list(pageable);
    }

    @GetMapping("/{id}")
    public EventResponse get(@PathVariable Long id) {
        return eventService.get(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public EventResponse update(@PathVariable Long id, @Valid @RequestBody EventRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return eventService.update(id, request, userDetails);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ORGANIZER')")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        eventService.delete(id, userDetails);
    }

    @PostMapping("/{id}/banner")
    @PreAuthorize("hasRole('ORGANIZER')")
    public EventResponse uploadBanner(@PathVariable Long id, @RequestPart("file") MultipartFile file, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return eventService.uploadBanner(id, file, userDetails);
    }
}
