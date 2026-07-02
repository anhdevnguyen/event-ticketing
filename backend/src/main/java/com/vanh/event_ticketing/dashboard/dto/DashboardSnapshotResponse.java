// Package: com.vanh.event_ticketing.dashboard.dto
// File: DashboardSnapshotResponse.java
//
// Vai trò: DTO trả về snapshot số liệu real-time của một sự kiện.
//
// === FIELDS ===
//
// Long eventId
//
// String eventName
//
// long totalSold
//   - Tổng số vé đã bán (status = CONFIRMED + CHECKED_IN)
//
// long totalCheckedIn
//   - Tổng số vé đã check-in (status = CHECKED_IN)
//
// long totalRemaining
//   - Tổng số vé còn lại chưa bán (sum của tất cả TicketType.quantityRemaining)
//
// List<GateStats> byGate
//   - Thống kê check-in theo từng cổng
//
// Instant snapshotAt
//   - Thời điểm lấy snapshot — để client biết dữ liệu "mới" đến đâu
//
// === NESTED CLASS / RECORD: GateStats ===
// Long gateId
// String gateName
// long checkedInCount    — số lần check-in thành công tại cổng này
// long failedCount       — số lần quét thất bại (đã check-in rồi, không hợp lệ)
//
// === GHI CHÚ KỸ THUẬT ===
// - Dữ liệu này cũng được push qua WebSocket sau mỗi check-in
//   -> DashboardEventPublisher gọi dashboardService.getSnapshot() sau check-in và publish
// - Gợi ý: GateStats là inner record để gọn code
//   public record GateStats(Long gateId, String gateName, long checkedInCount, long failedCount) {}
