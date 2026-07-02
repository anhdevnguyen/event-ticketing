// Package: com.vanh.event_ticketing.dashboard.service
// File: DashboardService.java
//
// Vai trò: Interface định nghĩa contract lấy dữ liệu tổng hợp dashboard.
//
// === METHODS ===
//
// DashboardSnapshotResponse getSnapshot(Long eventId)
//   - Tổng hợp số liệu real-time của một sự kiện:
//       + totalSold: tổng vé đã bán (status = CONFIRMED + CHECKED_IN)
//       + totalCheckedIn: tổng vé đã check-in (status = CHECKED_IN)
//       + totalRemaining: tổng vé còn lại (sum quantityRemaining của tất cả TicketType)
//       + byGate: List<GateStats> — số lượng check-in thành công theo từng cổng
//   - @Transactional(readOnly = true)
//   - Throw BusinessException(EVENT_NOT_FOUND) nếu eventId không tồn tại
//
// === GHI CHÚ KỸ THUẬT ===
// - KHÔNG truy cập trực tiếp Repository của module khác từ DashboardServiceImpl
//   Thay vào đó: gọi qua TicketService và CheckInService (inter-service call)
// - Nếu muốn tối ưu: dùng DB aggregate queries (COUNT, SUM) thay vì load entity
// - Cache gợi ý: @Cacheable(key = "#eventId", cacheNames = "dashboard", condition = "...")
//   TTL 5-10 giây để balance giữa freshness và DB load
