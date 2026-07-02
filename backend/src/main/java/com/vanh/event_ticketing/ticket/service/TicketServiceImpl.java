// Package: com.vanh.event_ticketing.ticket.service
// File: TicketServiceImpl.java
//
// Vai trò: Implementation của TicketService.
// Annotate @Service, @Transactional
//
// === DEPENDENCIES (inject qua constructor) ===
// - TicketRepository ticketRepository
// - TicketTypeRepository ticketTypeRepository  (cross-module — chấp nhận vì critical)
// - UserRepository userRepository
// - TicketMapper ticketMapper
// - QrCodeGenerator qrCodeGenerator  (tùy: có thể sinh UUID không cần thư viện ZXing ở đây)
//
// === IMPLEMENTATION NOTES ===
//
// reserve(ReserveRequest request, Long customerId):
//   - @Transactional (bắt buộc cho pessimistic lock)
//   - ticketType = ticketTypeRepository.findByIdForUpdate(request.getTicketTypeId())
//     -> Nếu không tìm thấy: throw BusinessException(TICKET_TYPE_NOT_FOUND)
//     -> Lock row ngay khi load
//   - Kiểm tra ticketType.getQuantityRemaining() >= request.getQuantity()
//     -> Nếu không đủ: throw BusinessException(TICKET_SOLD_OUT)
//   - Kiểm tra thời gian bán hàng (salesStartAt, salesEndAt)
//   - Giảm: ticketType.setQuantityRemaining(remaining - quantity)
//   - Tạo quantity Ticket objects:
//       status = RESERVED, customerId = customerId
//       expiresAt = Instant.now().plusSeconds(900)  // 15 phút
//       qrCode = null (chưa confirm)
//   - ticketTypeRepository.save(ticketType)
//   - ticketRepository.saveAll(tickets)
//   - return ticketMapper.toResponseList(tickets)
//
// confirm(Long ticketId, Long customerId):
//   - ticket = ticketRepository.findById(ticketId) -> throw TICKET_NOT_FOUND
//   - Kiểm tra ticket.getCustomer().getId() == customerId -> throw FORBIDDEN
//   - Kiểm tra ticket.getStatus() == RESERVED -> throw nếu EXPIRED/CONFIRMED
//   - Kiểm tra ticket.getExpiresAt().isAfter(Instant.now()) -> throw TICKET_EXPIRED
//   - ticket.setQrCode(UUID.randomUUID().toString())
//   - ticket.setStatus(TicketStatus.CONFIRMED)
//   - save và return response
//
// getTicket(Long ticketId, Long requesterId):
//   - @Transactional(readOnly = true)
//   - Load ticket, check ownership/role
//
// getMyTickets(Long customerId, Pageable pageable):
//   - @Transactional(readOnly = true)
//   - ticketRepository.findByCustomerId(customerId, pageable)
//   - Wrap trong PageResponse
//
// releaseExpiredReservations():
//   - @Scheduled(fixedRate = 60_000)
//   - @Transactional
//   - expiredTickets = ticketRepository.findByStatusAndExpiresAtBefore(RESERVED, Instant.now())
//   - Group by ticketTypeId, đếm số lượng mỗi loại
//   - Với mỗi ticketTypeId: findByIdForUpdate -> tăng quantityRemaining -> save
//   - Update tất cả ticket: setStatus(EXPIRED)
//   - ticketRepository.saveAll(expiredTickets)
//   - Log: "Released X expired reservations"
//
// === GHI CHÚ KỸ THUẬT ===
// - reserve() PHẢI trong @Transactional — nếu không sẽ mất lock ngay sau findByIdForUpdate
// - Xử lý concurrent reserve: lock row -> check -> giảm -> save (atomic trong transaction)
// - releaseExpiredReservations: nên batch nhỏ để tránh long transaction
// - @EnableScheduling cần ở một @Configuration class
