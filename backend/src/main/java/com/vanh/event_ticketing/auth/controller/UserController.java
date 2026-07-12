package com.vanh.event_ticketing.auth.controller;

import com.vanh.event_ticketing.auth.dto.RegisterRequest;
import com.vanh.event_ticketing.auth.dto.UserResponse;
import com.vanh.event_ticketing.auth.dto.UserStatusRequest;
import com.vanh.event_ticketing.auth.service.AuthService;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.common.util.PageResponse;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;

    @PostMapping("/api/v1/events/{eventId}/staff")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ORGANIZER')")
    public UserResponse createStaff(@PathVariable Long eventId, @Valid @RequestBody RegisterRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return authService.createStaff(eventId, request, userDetails);
    }

    @GetMapping("/api/v1/events/{eventId}/staff")
    @PreAuthorize("hasRole('ORGANIZER')")
    public List<UserResponse> listStaff(@PathVariable Long eventId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return authService.listStaff(eventId, userDetails);
    }

    @DeleteMapping("/api/v1/events/{eventId}/staff/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ORGANIZER')")
    public void removeStaff(@PathVariable Long eventId, @PathVariable Long userId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.removeStaff(eventId, userId, userDetails);
    }

    @GetMapping("/api/v1/users")
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> listUsers(Pageable pageable) {
        return authService.listUsers(pageable);
    }

    @PutMapping("/api/v1/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse setUserStatus(@PathVariable Long id, @Valid @RequestBody UserStatusRequest request) {
        return authService.setUserStatus(id, request.active());
    }
}
