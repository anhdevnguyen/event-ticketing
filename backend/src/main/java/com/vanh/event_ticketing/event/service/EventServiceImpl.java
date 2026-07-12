package com.vanh.event_ticketing.event.service;

import com.vanh.event_ticketing.common.exception.BusinessException;
import com.vanh.event_ticketing.common.exception.ErrorCode;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.common.util.PageResponse;
import com.vanh.event_ticketing.event.dto.EventRequest;
import com.vanh.event_ticketing.event.dto.EventResponse;
import com.vanh.event_ticketing.event.entity.Event;
import com.vanh.event_ticketing.event.mapper.EventMapper;
import com.vanh.event_ticketing.event.repository.EventRepository;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final long MAX_BANNER_BYTES = 5 * 1024 * 1024;

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventResponse create(EventRequest request, CustomUserDetails userDetails) {
        Event event = new Event();
        event.setOrganizer(userDetails.getUser());
        apply(event, request);
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Override
    public PageResponse<EventResponse> list(Pageable pageable) {
        return PageResponse.from(eventRepository.findByDeletedAtIsNull(pageable).map(eventMapper::toResponse));
    }

    @Override
    public EventResponse get(Long id) {
        return eventMapper.toResponse(findActive(id));
    }

    @Override
    @Transactional
    public EventResponse update(Long id, EventRequest request, CustomUserDetails userDetails) {
        Event event = findActive(id);
        requireOwner(event, userDetails);
        apply(event, request);
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional
    public void delete(Long id, CustomUserDetails userDetails) {
        Event event = findActive(id);
        requireOwner(event, userDetails);
        event.setDeletedAt(Instant.now());
    }

    @Override
    @Transactional
    public EventResponse uploadBanner(Long id, MultipartFile file, CustomUserDetails userDetails) {
        Event event = findActive(id);
        requireOwner(event, userDetails);
        String contentType = file.getContentType();
        if (file.isEmpty() || file.getSize() > MAX_BANNER_BYTES || (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType))) {
            throw new BusinessException(ErrorCode.INVALID_BANNER_FILE);
        }
        try {
            // ponytail: store small MVP banners inline; replace with Cloudinary upload when production storage is wired.
            event.setBannerUrl("data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(file.getBytes()));
            return eventMapper.toResponse(event);
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.INVALID_BANNER_FILE);
        }
    }

    private Event findActive(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.EVENT_NOT_FOUND));
        if (event.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.EVENT_NOT_FOUND);
        }
        return event;
    }

    private void requireOwner(Event event, CustomUserDetails userDetails) {
        if (!event.getOrganizer().getId().equals(userDetails.getId())) {
            throw new BusinessException(ErrorCode.EVENT_OWNERSHIP_VIOLATION);
        }
    }

    private void apply(Event event, EventRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new BusinessException(ErrorCode.INVALID_EVENT_TIME);
        }
        event.setName(request.name().trim());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
    }
}
