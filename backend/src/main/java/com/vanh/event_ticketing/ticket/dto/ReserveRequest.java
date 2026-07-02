// Package: com.vanh.event_ticketing.ticket.dto
// File: ReserveRequest.java
//
// Vai trò: DTO nhận yêu cầu reserve vé từ client.
//
// === FIELDS ===
//
// Long ticketTypeId
//   - @NotNull(message = "Loại vé không được để trống")
//
// int quantity
//   - @Positive(message = "Số lượng vé phải lớn hơn 0")
//   - @Max(value = 10, message = "Tối đa 10 vé mỗi lần đặt")
//   - Max 10 để tránh abuse (mặc dù spec gốc là 100 — nên điều chỉnh theo business)
//
// === GHI CHÚ KỸ THUẬT ===
// - Không nhận customerId từ client — lấy từ SecurityContext
// - @Max(10) là business rule — có thể config thành property thay vì hardcode
// - Validate @Valid trong Controller
// - Gợi ý thêm: idempotency key để tránh duplicate reserve khi network retry
