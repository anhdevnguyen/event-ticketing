package com.vanh.event_ticketing.event.service;

import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.common.util.PageResponse;
import com.vanh.event_ticketing.event.dto.EventRequest;
import com.vanh.event_ticketing.event.dto.EventResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    EventResponse create(EventRequest request, CustomUserDetails userDetails);
    PageResponse<EventResponse> list(Pageable pageable);
    EventResponse get(Long id);
    EventResponse update(Long id, EventRequest request, CustomUserDetails userDetails);
    void delete(Long id, CustomUserDetails userDetails);
    EventResponse uploadBanner(Long id, MultipartFile file, CustomUserDetails userDetails);
}
