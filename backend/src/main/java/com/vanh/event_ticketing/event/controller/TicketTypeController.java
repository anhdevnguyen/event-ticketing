// Package: com.vanh.event_ticketing.event.controller
// File: TicketTypeController.java
//
// Vai trò: REST Controller quản lý CRUD loại vé (ticket type) của một sự kiện.
// Annotate @RestController
//
// === ENDPOINTS ===
//
// GET /api/v1/events/{eventId}/ticket-types
//   - Public (xem danh sách loại vé)
//   - Output: List<TicketTypeResponse>
//   - Gọi: ticketTypeService.listByEvent(eventId)
//
// POST /api/v1/events/{eventId}/ticket-types
//   - Yêu cầu: ORGANIZER chủ event HOẶC ADMIN
//   - Input: @Valid TicketTypeRequest
//   - Output: TicketTypeResponse, HTTP 201
//   - Gọi: ticketTypeService.createTicketType(eventId, request, currentUserId)
//
// PUT /api/v1/ticket-types/{id}
//   - Yêu cầu: ORGANIZER chủ event HOẶC ADMIN
//   - Input: @Valid TicketTypeRequest
//   - Output: TicketTypeResponse
//   - Gọi: ticketTypeService.updateTicketType(id, request, currentUserId)
//
// DELETE /api/v1/ticket-types/{id}
//   - Yêu cầu: ORGANIZER chủ event HOẶC ADMIN
//   - Output: 204 No Content
//   - Gọi: ticketTypeService.deleteTicketType(id, currentUserId)
//   - Service throw BusinessException nếu đã có ticket sold
//
// === GHI CHÚ KỸ THUẬT ===
// - @RequestMapping chia cho 2 prefix: /api/v1/events và /api/v1/ticket-types
//   Có thể dùng 2 @RequestMapping riêng hoặc define từng @PostMapping/@DeleteMapping với path đầy đủ
// - Ownership check được thực hiện ở service layer, không phải controller
// - Lấy currentUserId từ SecurityContext giống EventController
