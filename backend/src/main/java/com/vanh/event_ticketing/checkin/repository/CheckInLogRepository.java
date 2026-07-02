// Package: com.vanh.event_ticketing.checkin.repository
// File: CheckInLogRepository.java
//
// Vai trò: Spring Data JPA repository cho entity CheckInLog.
// Extends JpaRepository<CheckInLog, Long>
// Annotate @Repository
//
// === QUERY METHODS ===
//
// List<CheckInLog> findByTicketId(Long ticketId)
//   - Lịch sử check-in của một vé cụ thể
//   - Useful để debug khi có khiếu nại double check-in
//
// List<CheckInLog> findByGateId(Long gateId)
//   - Tất cả log của một cổng check-in
//
// Page<CheckInLog> findByTicketTicketTypeEventId(Long eventId, Pageable pageable)
//   - Lấy tất cả log của một event (qua chain: ticket -> ticketType -> event)
//   - Dùng trong: CheckInService.getLogsByEvent()
//   - Tên method dài — có thể dùng @Query JPQL thay thế cho rõ ràng hơn
//
// === CUSTOM QUERIES ===
//
// @Query("""
//     SELECT g.name as gateName, COUNT(cl) as totalScanned,
//            SUM(CASE WHEN cl.success = true THEN 1 ELSE 0 END) as successCount
//     FROM CheckInLog cl JOIN cl.gate g
//     WHERE cl.ticket.ticketType.event.id = :eventId
//     GROUP BY g.id, g.name
//     """)
// List<GateStatProjection> findByEventIdGroupByGate(@Param("eventId") Long eventId)
//   - Dùng trong: DashboardService.getSnapshot() — thống kê theo cổng
//   - GateStatProjection: interface projection với gateName, totalScanned, successCount
//
// === GHI CHÚ KỸ THUẬT ===
// - Index gợi ý:
//   CREATE INDEX idx_checkin_logs_ticket_id ON checkin_logs(ticket_id);
//   CREATE INDEX idx_checkin_logs_gate_id ON checkin_logs(gate_id);
// - findByEventIdGroupByGate dùng cho dashboard real-time — nên cache ngắn hạn (5-10s)
