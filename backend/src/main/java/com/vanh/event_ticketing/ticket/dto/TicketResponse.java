// Package: com.vanh.event_ticketing.ticket.dto
// File: TicketResponse.java
//
// Vai trò: DTO trả về thông tin vé cho client.
//
// === FIELDS ===
//
// Long id
//
// String status
//   - Giá trị: "RESERVED", "CONFIRMED", "EXPIRED", "CHECKED_IN"
//
// String qrCode
//   - UUID string — chỉ có giá trị khi status = CONFIRMED hoặc CHECKED_IN
//   - Null khi RESERVED
//   - Client dùng để hiển thị QR code (hoặc gọi endpoint lấy ảnh QR)
//
// Instant expiresAt
//   - Thời điểm hết hạn reservation
//   - Quan trọng với frontend: hiển thị countdown timer
//
// String ticketTypeName
//   - Tên loại vé: "VIP", "Regular", ...
//
// String eventName
//   - Tên sự kiện
//
// Instant checkedInAt
//   - Null nếu chưa check-in
//   - Có giá trị khi status = CHECKED_IN
//
// String holderName
//   - Tên người giữ vé (customer.displayName)
//
// BigDecimal price
//   - Giá vé đã trả
//
// === GHI CHÚ KỸ THUẬT ===
// - qrCode chỉ expose khi status = CONFIRMED/CHECKED_IN
//   Tùy security policy: có thể cần auth để lấy qrCode
// - Endpoint GET /api/v1/tickets/{id}/qr-image có thể trả về byte[] (ảnh QR PNG)
//   sử dụng QrCodeGenerator.generate(qrCode)
