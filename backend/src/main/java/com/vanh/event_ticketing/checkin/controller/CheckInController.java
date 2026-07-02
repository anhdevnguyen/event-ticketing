// Package: com.vanh.event_ticketing.checkin.controller
// File: CheckInController.java
//
// Vai trò: REST Controller xử lý nghiệp vụ check-in tại cổng.
// Annotate @RestController, @RequestMapping("/api/v1/checkin")
//
// === ENDPOINTS ===
//
// POST /api/v1/checkin
//   - Yêu cầu role: CHECKIN_STAFF (hoặc ORGANIZER, ADMIN)
//   - Input: @Valid CheckInRequest (qrCode, gateId)
//   - Output: CheckInResponse (success, ticketId, holderName, eventName, gateName, message)
//   - Gọi: checkInService.checkIn(request.getQrCode(), request.getGateId(), currentStaffId)
//   - HTTP 200 kể cả khi check-in thất bại (vé đã scan rồi) — dùng success field để phân biệt
//   - Lý do: thiết bị scan luôn cần response, không nên 4xx khi business logic fail
//
// GET /api/v1/checkin/logs/{eventId}
//   - Yêu cầu role: ORGANIZER (chủ event) hoặc ADMIN
//   - Path variable: eventId
//   - Query params: page, size, gateId (optional filter)
//   - Output: PageResponse<CheckInResponse>
//   - Gọi: checkInService.getLogsByEvent(eventId, filter, pageable)
//
// === GHI CHÚ KỸ THUẬT ===
// - currentStaffId lấy từ SecurityContext
// - @PreAuthorize("hasAnyRole('CHECKIN_STAFF','ORGANIZER','ADMIN')") cho POST /checkin
// - @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')") cho GET /logs/{eventId}
// - Endpoint POST nên được rate-limited để tránh scan spam
// - Response luôn trả về message mô tả: "Check-in thành công", "Vé đã được check-in trước đó", v.v.
