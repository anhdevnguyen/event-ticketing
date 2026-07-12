package com.vanh.event_ticketing.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vanh.event_ticketing.auth.entity.User;
import com.vanh.event_ticketing.common.exception.BusinessException;
import com.vanh.event_ticketing.common.exception.ErrorCode;
import com.vanh.event_ticketing.common.security.CustomUserDetails;
import com.vanh.event_ticketing.event.dto.EventRequest;
import com.vanh.event_ticketing.event.dto.EventResponse;
import com.vanh.event_ticketing.event.entity.Event;
import com.vanh.event_ticketing.event.mapper.EventMapper;
import com.vanh.event_ticketing.event.repository.EventRepository;
import com.vanh.event_ticketing.event.service.EventServiceImpl;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;

    @Test
    void create_shouldRejectWhenEndTimeBeforeStartTime() {
        EventServiceImpl service = new EventServiceImpl(eventRepository, eventMapper);
        Instant start = Instant.now().plusSeconds(3600);
        Instant end = start.minusSeconds(1800);
        EventRequest request = new EventRequest("Event", "Description", "Location", start, end);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(request, userDetails(10L)));

        assertEquals(ErrorCode.INVALID_EVENT_TIME, ex.getErrorCode());
    }

    @Test
    void create_shouldRejectWhenEndTimeEqualsStartTime() {
        EventServiceImpl service = new EventServiceImpl(eventRepository, eventMapper);
        Instant time = Instant.now().plusSeconds(3600);
        EventRequest request = new EventRequest("Event", "Description", "Location", time, time);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(request, userDetails(10L)));

        assertEquals(ErrorCode.INVALID_EVENT_TIME, ex.getErrorCode());
    }

    @Test
    void create_shouldSetOrganizer() {
        EventServiceImpl service = new EventServiceImpl(eventRepository, eventMapper);
        Instant start = Instant.now().plusSeconds(3600);
        EventRequest request = new EventRequest("Event", "Description", "Location", start, start.plusSeconds(3600));
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(eventMapper.toResponse(any())).thenReturn(new EventResponse(1L, "Event", "Description", "Location", 10L, "DRAFT", start, start.plusSeconds(3600), null, null));

        EventResponse response = service.create(request, userDetails(10L));

        assertNotNull(response);
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        assertEquals(10L, eventCaptor.getValue().getOrganizer().getId());
    }

    @Test
    void update_shouldRejectWhenNotOwner() {
        EventServiceImpl service = new EventServiceImpl(eventRepository, eventMapper);
        Event event = event(99L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        Instant start = Instant.now().plusSeconds(3600);
        EventRequest request = new EventRequest("Updated", "Description", "Location", start, start.plusSeconds(3600));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(1L, request, userDetails(10L)));

        assertEquals(ErrorCode.EVENT_OWNERSHIP_VIOLATION, ex.getErrorCode());
    }

    @Test
    void delete_shouldRejectWhenNotOwner() {
        EventServiceImpl service = new EventServiceImpl(eventRepository, eventMapper);
        Event event = event(99L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.delete(1L, userDetails(10L)));

        assertEquals(ErrorCode.EVENT_OWNERSHIP_VIOLATION, ex.getErrorCode());
    }

    @Test
    void get_shouldRejectWhenDeleted() {
        EventServiceImpl service = new EventServiceImpl(eventRepository, eventMapper);
        Event event = event(10L);
        event.setDeletedAt(Instant.now());
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.get(1L));

        assertEquals(ErrorCode.EVENT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void uploadBanner_shouldRejectWhenNotOwner() {
        EventServiceImpl service = new EventServiceImpl(eventRepository, eventMapper);
        Event event = event(99L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        MockMultipartFile file = new MockMultipartFile("banner", "banner.png", "image/png", new byte[100]);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.uploadBanner(1L, file, userDetails(10L)));

        assertEquals(ErrorCode.EVENT_OWNERSHIP_VIOLATION, ex.getErrorCode());
    }

    @Test
    void uploadBanner_shouldRejectWhenFileTooLarge() {
        EventServiceImpl service = new EventServiceImpl(eventRepository, eventMapper);
        Event event = event(10L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile file = new MockMultipartFile("banner", "banner.png", "image/png", largeContent);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.uploadBanner(1L, file, userDetails(10L)));

        assertEquals(ErrorCode.INVALID_BANNER_FILE, ex.getErrorCode());
    }

    @Test
    void uploadBanner_shouldRejectWhenInvalidContentType() {
        EventServiceImpl service = new EventServiceImpl(eventRepository, eventMapper);
        Event event = event(10L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        MockMultipartFile file = new MockMultipartFile("banner", "banner.txt", "text/plain", new byte[100]);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.uploadBanner(1L, file, userDetails(10L)));

        assertEquals(ErrorCode.INVALID_BANNER_FILE, ex.getErrorCode());
    }

    @Test
    void uploadBanner_shouldRejectWhenEmpty() {
        EventServiceImpl service = new EventServiceImpl(eventRepository, eventMapper);
        Event event = event(10L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        MockMultipartFile file = new MockMultipartFile("banner", "banner.png", "image/png", new byte[0]);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.uploadBanner(1L, file, userDetails(10L)));

        assertEquals(ErrorCode.INVALID_BANNER_FILE, ex.getErrorCode());
    }

    private static Event event(Long organizerId) {
        Event event = new Event();
        event.setId(1L);
        User organizer = new User();
        organizer.setId(organizerId);
        event.setOrganizer(organizer);
        event.setName("Test Event");
        event.setLocation("Test Location");
        event.setStartTime(Instant.now().plusSeconds(3600));
        event.setEndTime(Instant.now().plusSeconds(7200));
        event.setStatus("PUBLISHED");
        return event;
    }

    private static CustomUserDetails userDetails(Long id) {
        User user = new User();
        user.setId(id);
        return new CustomUserDetails(user);
    }
}
