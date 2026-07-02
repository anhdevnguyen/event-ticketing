// Package: com.vanh.event_ticketing.dashboard.service
// File: DashboardServiceImpl.java
//
// Vai trò: Implementation của DashboardService.
// Annotate @Service, @Transactional(readOnly = true)
//
// === DEPENDENCIES (inject qua constructor) ===
// - TicketService ticketService          (KHÔNG inject TicketRepository trực tiếp)
// - CheckInService checkInService        (KHÔNG inject CheckInLogRepository trực tiếp)
// - EventService eventService            (để verify event tồn tại)
//
// === IMPLEMENTATION NOTES ===
//
// getSnapshot(Long eventId):
//   - Bước 1: Verify event tồn tại
//     eventService.getEvent(eventId)  -- throw EVENT_NOT_FOUND nếu không có
//
//   - Bước 2: Tổng số vé đã bán (gọi qua TicketService)
//     Gợi ý: thêm method vào TicketService:
//       long countSoldByEvent(Long eventId) — đếm CONFIRMED + CHECKED_IN
//       long countCheckedInByEvent(Long eventId) — đếm CHECKED_IN
//       long countRemainingByEvent(Long eventId) — sum quantityRemaining
//     Hoặc: một method aggregate duy nhất getSummaryByEvent(eventId)
//
//   - Bước 3: Thống kê theo cổng (gọi qua CheckInService)
//     Gợi ý: thêm method vào CheckInService:
//       List<GateStats> getGateStatsByEvent(Long eventId)
//       GateStats: gateId, gateName, checkedInCount
//
//   - Bước 4: Build DashboardSnapshotResponse
//     return new DashboardSnapshotResponse(
//         eventId, totalSold, totalCheckedIn, totalRemaining, gateStatsList
//     )
//
// === GHI CHÚ KỸ THUẬT ===
// - Inter-service dependency: DashboardService -> TicketService, CheckInService
//   Tránh circular dependency: DashboardService không được inject bởi TicketService/CheckInService
// - @Transactional(readOnly = true) truyền xuống các service được gọi
// - Nếu performance quan trọng: bypass service layer, dùng native SQL aggregate queries
//   Nhưng phải viết SQL thủ công và mất đi abstraction
