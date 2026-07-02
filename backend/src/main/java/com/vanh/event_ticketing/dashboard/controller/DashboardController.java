// Package: com.vanh.event_ticketing.dashboard.controller
// File: DashboardController.java
//
// Vai trò: REST Controller cung cấp dữ liệu dashboard real-time cho Organizer.
// Annotate @RestController, @RequestMapping("/api/v1/dashboard")
//
// === ENDPOINTS ===
//
// GET /api/v1/dashboard/{eventId}/snapshot
//   - Yêu cầu: ORGANIZER chủ event hoặc ADMIN
//   - Output: DashboardSnapshotResponse
//   - Gọi: dashboardService.getSnapshot(eventId)
//   - Trả về: totalSold, totalCheckedIn, totalRemaining, byGate (list stats theo cổng)
//   - HTTP cache gợi ý: Cache-Control: max-age=10 (10 giây) để giảm tải
//
// === WEBSOCKET ===
// WebSocket endpoint được config trong WebSocketConfig.java:
//   - Endpoint: /ws (với SockJS fallback)
//   - Client subscribe: /topic/dashboard/{eventId}
//   - Client subscribe: /topic/dashboard/{eventId}/{gateId}
//   - Server push: sau mỗi check-in thành công qua DashboardEventPublisher
//
// === GHI CHÚ KỸ THUẬT ===
// - DashboardController chỉ xử lý REST GET snapshot
// - WebSocket publish được thực hiện trong DashboardEventPublisher (triggered từ CheckInService)
// - Không cần @MessageMapping trong Controller này — broker handle subscription
// - @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')") trên GET snapshot
// - currentUserId từ SecurityContext để kiểm tra ownership của event
