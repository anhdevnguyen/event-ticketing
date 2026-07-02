// Package: com.vanh.event_ticketing.event.service
// File: EventService.java
//
// Vai trò: Interface định nghĩa contract nghiệp vụ quản lý sự kiện.
//
// === METHODS ===
//
// EventResponse createEvent(EventRequest request, Long organizerId)
//   - Tạo event mới với status = DRAFT
//   - organizerId lấy từ SecurityContext (truyền vào từ Controller)
//
// EventResponse updateEvent(Long eventId, EventRequest request, Long requesterId)
//   - Cập nhật thông tin event
//   - Kiểm tra: requesterId == event.organizer.id HOẶC requester là ADMIN
//   - Throw BusinessException(FORBIDDEN) nếu không phải chủ event
//   - Không cho update event đã CANCELLED hoặc COMPLETED
//
// void deleteEvent(Long eventId, Long requesterId)
//   - Soft delete hoặc hard delete
//   - Kiểm tra ownership
//   - Gợi ý: không hard delete nếu đã có ticket, chỉ set status = CANCELLED
//
// EventResponse getEvent(Long eventId)
//   - Lấy thông tin event
//   - Throw BusinessException(EVENT_NOT_FOUND) nếu không tìm thấy
//
// PageResponse<EventResponse> listEvents(EventFilterRequest filter, Pageable pageable)
//   - Lọc theo: status, organizerId, keyword (search name)
//   - Trả về paginated response
//
// === GHI CHÚ KỸ THUẬT ===
// - Tất cả write operations nên @Transactional ở impl
// - Read operations: @Transactional(readOnly = true)
