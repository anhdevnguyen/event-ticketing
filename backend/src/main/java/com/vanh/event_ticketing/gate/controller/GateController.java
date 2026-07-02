// Package: com.vanh.event_ticketing.gate.controller
// File: GateController.java
//
// Vai trò: REST Controller quản lý CRUD cổng check-in của sự kiện.
// Annotate @RestController
//
// === ENDPOINTS ===
//
// GET /api/v1/events/{eventId}/gates
//   - Yêu cầu: ORGANIZER chủ event hoặc ADMIN (hoặc CHECKIN_STAFF nếu cần)
//   - Output: List<GateResponse>
//   - Gọi: gateService.listByEvent(eventId)
//
// POST /api/v1/events/{eventId}/gates
//   - Yêu cầu: ORGANIZER chủ event hoặc ADMIN
//   - Input: @Valid GateRequest
//   - Output: GateResponse, HTTP 201
//   - Gọi: gateService.createGate(eventId, request, currentUserId)
//
// PUT /api/v1/gates/{id}
//   - Yêu cầu: ORGANIZER chủ event hoặc ADMIN
//   - Input: @Valid GateRequest
//   - Output: GateResponse
//   - Gọi: gateService.updateGate(id, request, currentUserId)
//
// DELETE /api/v1/gates/{id}
//   - Yêu cầu: ORGANIZER chủ event hoặc ADMIN
//   - Output: 204 No Content
//   - Gọi: gateService.deleteGate(id, currentUserId)
//   - Gợi ý: không xóa nếu gate đã có checkin log — set active=false thay vì hard delete
//
// === GHI CHÚ KỸ THUẬT ===
// - Ownership check ở service layer
// - @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')") trên các write endpoints
// - currentUserId từ SecurityContext
