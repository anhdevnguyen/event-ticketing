// Package: com.vanh.event_ticketing.event.dto
// File: TicketTypeRequest.java
//
// Vai trò: DTO nhận dữ liệu tạo/cập nhật loại vé từ client.
//
// === FIELDS ===
//
// String name
//   - @NotBlank
//   - @Size(max = 100)
//
// String description
//   - Nullable
//
// BigDecimal price
//   - @NotNull
//   - @DecimalMin("0.00") — vé miễn phí cho phép (giá = 0)
//   - @Digits(integer = 13, fraction = 2)
//
// int quantityTotal
//   - @Positive(message = "Số lượng vé phải lớn hơn 0")
//   - @Max(value = 100000) — giới hạn hợp lý
//
// Instant salesStartAt
//   - Nullable
//
// Instant salesEndAt
//   - Nullable
//   - Nếu có: phải sau salesStartAt
//
// === GHI CHÚ KỸ THUẬT ===
// - quantityRemaining KHÔNG nhận từ client — tự tính trong service
// - Dùng @Valid + @RequestBody trong Controller
// - Giá dùng BigDecimal để tránh lỗi floating point
