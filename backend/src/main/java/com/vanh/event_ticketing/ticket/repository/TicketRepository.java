// Package: com.vanh.event_ticketing.ticket.repository
// File: TicketRepository.java
//
// Vai trò: Spring Data JPA repository cho entity Ticket.
// Extends JpaRepository<Ticket, Long>
// Annotate @Repository
//
// === QUERY METHODS ===
//
// Optional<Ticket> findByQrCode(String qrCode)
//   - Dùng trong: CheckInService.checkIn() — tìm vé theo QR scan
//   - qrCode là unique, index: CREATE UNIQUE INDEX idx_tickets_qr_code ON tickets(qr_code)
//
// Page<Ticket> findByCustomerId(Long customerId, Pageable pageable)
//   - Dùng trong: TicketService.getMyTickets()
//   - Index: CREATE INDEX idx_tickets_customer_id ON tickets(customer_id)
//
// List<Ticket> findByStatusAndExpiresAtBefore(TicketStatus status, Instant expiresAt)
//   - Dùng trong: releaseExpiredReservations()
//   - Tìm tất cả RESERVED ticket đã quá hạn
//   - Ví dụ: findByStatusAndExpiresAtBefore(RESERVED, Instant.now())
//
// long countByTicketTypeIdAndStatusNot(Long ticketTypeId, TicketStatus status)
//   - Dùng trong: TicketTypeServiceImpl.deleteTicketType()
//   - Đếm vé đã bán (không phải EXPIRED) để quyết định có cho xóa loại vé không
//
// === QUERY QUAN TRỌNG: checkInIfConfirmed ===
// @Modifying
// @Query("""
//     UPDATE Ticket t SET t.status = 'CHECKED_IN', t.checkedInAt = :checkedInAt
//     WHERE t.id = :ticketId AND t.status = 'CONFIRMED'
//     """)
// int checkInIfConfirmed(@Param("ticketId") Long ticketId,
//                        @Param("checkedInAt") Instant checkedInAt)
//   - Trả về: số row bị ảnh hưởng (1 = thành công, 0 = đã check-in rồi hoặc không hợp lệ)
//   - Đây là conditional update ATOMIC — chống double check-in
//   - Nếu return 0: ticket không ở trạng thái CONFIRMED -> đã check-in trước đó
//   - Phải trong @Transactional
//   - @Modifying(clearAutomatically = true) để clear persistence context sau update
//
// === GHI CHÚ KỸ THUẬT ===
// - checkInIfConfirmed là cơ chế bảo vệ chống double check-in quan trọng nhất
// - Không dùng "load -> check -> update" vì có race condition
// - Conditional update (WHERE status = CONFIRMED) là atomic ở database level
