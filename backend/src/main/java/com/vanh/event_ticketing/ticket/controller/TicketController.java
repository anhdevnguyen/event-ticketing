// Package: com.vanh.event_ticketing.ticket.controller
// File: TicketController.java
//
// Vai trò: REST Controller xử lý các nghiệp vụ liên quan đến vé.
// Annotate @RestController, @RequestMapping("/api/v1/tickets")
//
// === ENDPOINTS ===
//
// POST /api/v1/tickets/reserve
//   - Yêu cầu: AUTHENTICATED (role CUSTOMER)
//   - Input: @Valid ReserveRequest (ticketTypeId, quantity)
//   - Output: List<TicketResponse>, HTTP 201
//   - Gọi: ticketService.reserve(request, currentUserId)
//   - Trả về danh sách vé vừa được reserve (có expiresAt)
//   - Lưu ý: Vé ở trạng thái RESERVED — cần confirm trong thời hạn
//
// POST /api/v1/tickets/{id}/confirm
//   - Yêu cầu: AUTHENTICATED, chỉ chủ vé mới confirm được
//   - Input: không có body (ticketId từ path)
//   - Output: TicketResponse (với qrCode đã sinh)
//   - Gọi: ticketService.confirm(ticketId, currentUserId)
//   - Sinh QR code, đổi status RESERVED -> CONFIRMED
//
// GET /api/v1/tickets/{id}
//   - Yêu cầu: AUTHENTICATED, chỉ chủ vé hoặc ORGANIZER/ADMIN xem được
//   - Output: TicketResponse
//   - Gọi: ticketService.getTicket(ticketId, currentUserId)
//
// GET /api/v1/tickets/my
//   - Yêu cầu: AUTHENTICATED (CUSTOMER)
//   - Query params: page, size, status
//   - Output: PageResponse<TicketResponse>
//   - Gọi: ticketService.getMyTickets(currentUserId, pageable)
//   - Trả về tất cả vé của user đang đăng nhập
//
// === GHI CHÚ KỸ THUẬT ===
// - Thứ tự route quan trọng: /my phải được define TRƯỚC /{id}
//   để tránh Spring match "my" như một Long id
// - Lấy currentUserId từ SecurityContext (CustomUserDetails)
// - Rate limiting gợi ý trên /reserve: tránh spam reserve
