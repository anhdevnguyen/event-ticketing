// Package: com.vanh.event_ticketing.ticket.dto
// File: ConfirmRequest.java
//
// Vai trò: DTO nhận yêu cầu confirm vé (chuyển RESERVED -> CONFIRMED) từ client.
//
// === FIELDS ===
//
// Long ticketId
//   - @NotNull(message = "Mã vé không được để trống")
//
// === GHI CHÚ KỸ THUẬT ===
// - ticketId cũng có thể lấy từ path variable thay vì request body
//   Endpoint: POST /api/v1/tickets/{id}/confirm (không cần body)
//   -> Nếu dùng path variable: DTO này không cần thiết
// - Tùy API design: nếu confirm kết hợp với payment token, có thể thêm field paymentToken
// - Hiện tại không có payment module -> confirm = free confirmation
