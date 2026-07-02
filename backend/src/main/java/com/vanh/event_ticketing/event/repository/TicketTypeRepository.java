// Package: com.vanh.event_ticketing.event.repository
// File: TicketTypeRepository.java
//
// Vai trò: Spring Data JPA repository cho entity TicketType.
// Extends JpaRepository<TicketType, Long>
// Annotate @Repository
//
// === QUERY METHODS ===
//
// List<TicketType> findByEventId(Long eventId)
//   - Lấy tất cả loại vé của một event
//   - Dùng trong: TicketTypeService.listByEvent()
//
// === QUERY QUAN TRỌNG NHẤT: findByIdForUpdate ===
// @Lock(LockModeType.PESSIMISTIC_WRITE)
// @Query("SELECT tt FROM TicketType tt WHERE tt.id = :id")
// Optional<TicketType> findByIdForUpdate(@Param("id") Long id)
//   - Dùng trong: TicketServiceImpl.reserve() để chống oversell
//   - PESSIMISTIC_WRITE = SELECT ... FOR UPDATE trong SQL
//   - Đảm bảo chỉ 1 transaction tại một thời điểm có thể đọc và ghi quantityRemaining
//   - Phải được gọi trong @Transactional — nếu không sẽ throw exception
//   - Flow: lock row -> check quantityRemaining > 0 -> giảm -> save -> unlock (commit)
//   - Index: CREATE INDEX idx_ticket_types_event_id ON ticket_types(event_id);
//
// === GHI CHÚ KỸ THUẬT ===
// - findByIdForUpdate là critical path của toàn bộ hệ thống bán vé
// - Timeout lock: gợi ý @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
//   -> Tránh deadlock chờ mãi
// - Cân nhắc PESSIMISTIC_READ vs PESSIMISTIC_WRITE:
//   PESSIMISTIC_WRITE: không ai đọc được khi đang lock (mạnh nhất, dùng cho reserve)
// - Nếu traffic rất cao: xem xét Redis distributed lock hoặc database sequence thay thế
