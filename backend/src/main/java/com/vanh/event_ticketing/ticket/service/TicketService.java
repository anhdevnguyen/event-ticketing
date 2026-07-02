// Package: com.vanh.event_ticketing.ticket.service
// File: TicketService.java
//
// Vai trò: Interface định nghĩa contract nghiệp vụ quản lý vé.
//
// === METHODS ===
//
// List<TicketResponse> reserve(ReserveRequest request, Long customerId)
//   - Reserve một hoặc nhiều vé (theo quantity)
//   - Dùng pessimistic lock trên TicketType.quantityRemaining
//   - Vé sau khi reserve: status=RESERVED, expiresAt = now + 15 phút
//   - Throw BusinessException(TICKET_SOLD_OUT) nếu không đủ vé
//
// TicketResponse confirm(Long ticketId, Long customerId)
//   - Chuyển trạng thái RESERVED -> CONFIRMED
//   - Sinh UUID làm qrCode (unique)
//   - Kiểm tra: vé thuộc về customerId, chưa expire
//   - Throw nếu vé đã EXPIRED, CANCELLED, hoặc không phải của customer này
//
// TicketResponse getTicket(Long ticketId, Long requesterId)
//   - Lấy thông tin vé
//   - Kiểm tra quyền xem (chủ vé, hoặc ORGANIZER của event đó, hoặc ADMIN)
//
// PageResponse<TicketResponse> getMyTickets(Long customerId, Pageable pageable)
//   - Lấy tất cả vé của một customer
//   - @Transactional(readOnly = true)
//
// === INTERNAL / SCHEDULED ===
//
// void releaseExpiredReservations()
//   - @Scheduled(fixedRate = 60000) — chạy mỗi 60 giây
//   - Tìm tất cả ticket có status=RESERVED và expiresAt < now()
//   - Set status = EXPIRED
//   - Hoàn trả quantityRemaining về TicketType (dùng pessimistic lock)
//   - Ghi log số vé được release
//
// === GHI CHÚ KỸ THUẬT ===
// - releaseExpiredReservations cần @EnableScheduling trong config class
// - @Transactional riêng cho từng reservation khi release (tránh long transaction)
