// Package: com.vanh.event_ticketing.dashboard.websocket
// File: DashboardEventPublisher.java
//
// Vai trò: Component publish cập nhật real-time tới WebSocket clients sau mỗi check-in.
// Dùng Spring STOMP messaging qua SimpMessagingTemplate.
// Annotate @Component
//
// === DEPENDENCIES (inject qua constructor) ===
// - SimpMessagingTemplate messagingTemplate
// - DashboardService dashboardService  (để lấy snapshot mới nhất)
//
// === METHODS ===
//
// void publishCheckInEvent(Long eventId, Long gateId, CheckInResponse checkInResult)
//   - Gọi sau khi check-in thành công (triggered từ CheckInServiceImpl)
//   - Lấy snapshot mới nhất: DashboardSnapshotResponse snapshot = dashboardService.getSnapshot(eventId)
//   - Publish tới topic event-level:
//       messagingTemplate.convertAndSend("/topic/dashboard/" + eventId, snapshot)
//   - Publish tới topic gate-level:
//       messagingTemplate.convertAndSend("/topic/dashboard/" + eventId + "/" + gateId, snapshot)
//
// === WEBSOCKET TOPICS ===
// /topic/dashboard/{eventId}
//   - Organizer subscribe để xem tổng quan toàn event
//   - Payload: DashboardSnapshotResponse
//
// /topic/dashboard/{eventId}/{gateId}
//   - Staff hoặc Organizer subscribe để xem số liệu theo cổng cụ thể
//   - Payload: DashboardSnapshotResponse (có thể filter chỉ data của gate đó)
//
// === GHI CHÚ KỸ THUẬT ===
// - publishCheckInEvent nên được annotate @TransactionalEventListener(phase = AFTER_COMMIT)
//   -> Chỉ publish sau khi DB transaction commit xong
//   -> Tránh client nhận update trước khi DB ghi xong (inconsistency)
//   -> Cần dùng Spring ApplicationEvent: CheckInCompletedEvent { eventId, gateId, result }
//   -> CheckInServiceImpl publish event: applicationEventPublisher.publishEvent(new CheckInCompletedEvent(...))
//   -> DashboardEventPublisher listen: @TransactionalEventListener
//
// - SimpMessagingTemplate là thread-safe — có thể dùng từ nhiều thread
// - Nếu số lượng subscriber lớn: cân nhắc async (@Async) để không block main thread
// - Fallback khi WebSocket disconnect: client poll REST /snapshot endpoint
