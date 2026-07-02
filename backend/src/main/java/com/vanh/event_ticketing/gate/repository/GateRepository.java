// Package: com.vanh.event_ticketing.gate.repository
// File: GateRepository.java
//
// Vai trò: Spring Data JPA repository cho entity Gate.
// Extends JpaRepository<Gate, Long>
// Annotate @Repository
//
// === QUERY METHODS ===
//
// List<Gate> findByEventId(Long eventId)
//   - Lấy tất cả cổng của một event
//   - Dùng trong: GateService.listByEvent()
//
// List<Gate> findByEventIdAndActiveTrue(Long eventId)
//   - Lấy cổng đang active của một event
//   - Dùng trong: CheckIn app để hiển thị danh sách cổng khả dụng
//
// === GHI CHÚ KỸ THUẬT ===
// - Index: CREATE INDEX idx_gates_event_id ON gates(event_id);
