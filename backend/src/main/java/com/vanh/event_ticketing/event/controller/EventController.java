// Package: com.vanh.event_ticketing.event.controller
// File: EventController.java
//
// Vai trò: REST Controller quản lý CRUD sự kiện.
// Annotate @RestController, @RequestMapping("/api/v1/events")
//
// === ENDPOINTS ===
//
// GET /api/v1/events
//   - Public hoặc AUTHENTICATED
//   - Query params: page(default=0), size(default=20), status, organizerId
//   - Output: PageResponse<EventResponse>
//   - Gọi: eventService.listEvents(filter, pageable)
//
// POST /api/v1/events
//   - Yêu cầu role: ORGANIZER hoặc ADMIN
//   - Input: @Valid EventRequest
//   - Output: EventResponse, HTTP 201 Created
//   - Gọi: eventService.createEvent(request, currentUserId)
//   - @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
//
// GET /api/v1/events/{id}
//   - Public
//   - Output: EventResponse
//   - Gọi: eventService.getEvent(id)
//   - Throw 404 nếu không tìm thấy
//
// PUT /api/v1/events/{id}
//   - Yêu cầu: ORGANIZER chủ event HOẶC ADMIN
//   - Input: @Valid EventRequest
//   - Output: EventResponse
//   - Gọi: eventService.updateEvent(id, request, currentUserId)
//   - Service sẽ check ownership
//
// DELETE /api/v1/events/{id}
//   - Yêu cầu: ORGANIZER chủ event HOẶC ADMIN
//   - Output: 204 No Content
//   - Gọi: eventService.deleteEvent(id, currentUserId)
//   - Service sẽ check: không xóa nếu đã có ticket sold
//
// === GHI CHÚ KỸ THUẬT ===
// - Lấy currentUserId từ SecurityContext:
//   ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId()
// - Dùng @PreAuthorize hoặc check trong SecurityConfig
// - Pagination: Spring Pageable từ @PageableDefault(size=20)
