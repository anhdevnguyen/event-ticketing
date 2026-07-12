package com.vanh.event_ticketing.event.mapper;

import com.vanh.event_ticketing.event.dto.EventResponse;
import com.vanh.event_ticketing.event.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public EventResponse toResponse(Event event) {
        return new EventResponse(event.getId(), event.getName(), event.getDescription(), event.getLocation(), event.getOrganizer().getId(), event.getStatus(), event.getStartTime(), event.getEndTime(), event.getBannerUrl(), event.getCreatedAt());
    }
}
