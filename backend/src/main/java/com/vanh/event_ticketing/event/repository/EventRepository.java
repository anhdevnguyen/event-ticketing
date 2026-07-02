// Package: com.vanh.event_ticketing.event.repository
// File: EventRepository.java
//
// Vai trò: Spring Data JPA repository cho entity Event.
// Extends JpaRepository<Event, Long> và JpaSpecificationExecutor<Event>
// Annotate @Repository
//
// === QUERY METHODS ===
//
// List<Event> findByOrganizerId(Long organizerId)
//   - Lấy tất cả event của một organizer
//   - Dùng trong: EventController (organizer xem event của mình)
//
// List<Event> findByStatus(EventStatus status)
//   - Lấy event theo trạng thái (PUBLISHED, DRAFT, ...)
//   - Dùng trong: public event listing
//
// === JPA SPECIFICATION EXECUTOR ===
// - Extends JpaSpecificationExecutor<Event> để hỗ trợ dynamic filter
// - findAll(Specification<Event> spec, Pageable pageable) — tự động có từ JpaSpecificationExecutor
// - Ví dụ Specification: filter theo status AND organizerId AND name LIKE keyword
//
// === CUSTOM QUERIES (nếu cần) ===
// @Query("SELECT e FROM Event e WHERE e.organizer.id = :organizerId AND e.status = :status")
// List<Event> findByOrganizerIdAndStatus(Long organizerId, EventStatus status)
//
// === GHI CHÚ KỸ THUẬT ===
// - Index gợi ý:
//   CREATE INDEX idx_events_organizer_id ON events(organizer_id);
//   CREATE INDEX idx_events_status ON events(status);
// - Nên dùng JpaSpecificationExecutor cho filter phức tạp thay vì nhiều method
