// Package: com.vanh.event_ticketing.event.service
// File: EventServiceImpl.java
//
// Vai trò: Implementation của EventService.
// Annotate @Service, @Transactional
//
// === DEPENDENCIES (inject qua constructor) ===
// - EventRepository eventRepository
// - UserRepository userRepository  (để load organizer)
// - EventMapper eventMapper
//
// === IMPLEMENTATION NOTES ===
//
// createEvent(EventRequest request, Long organizerId):
//   - Load User organizer = userRepository.findById(organizerId)
//   - Map request -> Event entity (qua EventMapper hoặc thủ công)
//   - event.setStatus(EventStatus.DRAFT)
//   - event.setOrganizer(organizer)
//   - eventRepository.save(event)
//   - return eventMapper.toResponse(savedEvent)
//
// updateEvent(Long eventId, EventRequest request, Long requesterId):
//   - event = eventRepository.findById(eventId) -> throw EVENT_NOT_FOUND
//   - checkOwnership(event, requesterId) — method nội bộ
//   - Map request fields vào event
//   - save và return response
//
// deleteEvent(Long eventId, Long requesterId):
//   - event = findById -> throw if not found
//   - checkOwnership(event, requesterId)
//   - Kiểm tra: nếu event có ticket đã sold -> set CANCELLED thay vì hard delete
//   - Ngược lại: eventRepository.delete(event)
//
// getEvent(Long eventId):
//   - @Transactional(readOnly = true)
//   - findById -> throw EVENT_NOT_FOUND nếu empty
//   - return eventMapper.toResponse(event)
//
// listEvents(filter, pageable):
//   - @Transactional(readOnly = true)
//   - Dùng Specification<Event> để filter động (status, organizerId, keyword)
//   - eventRepository.findAll(spec, pageable)
//   - Wrap trong PageResponse
//
// Private checkOwnership(Event event, Long requesterId):
//   - Nếu event.getOrganizer().getId() != requesterId -> load user để check role
//   - Nếu không phải ADMIN -> throw BusinessException(FORBIDDEN)
//
// === GHI CHÚ KỸ THUẬT ===
// - Dùng JPA Specification cho filter động thay vì nhiều findBy method
// - @Transactional propagation mặc định (REQUIRED)
// - Không inject trực tiếp Repository của module khác
