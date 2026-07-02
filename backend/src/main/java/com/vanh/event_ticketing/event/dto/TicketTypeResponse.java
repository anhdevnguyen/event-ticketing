// Package: com.vanh.event_ticketing.event.dto
// File: TicketTypeResponse.java
//
// Vai trò: DTO trả về thông tin loại vé cho client.
//
// === FIELDS ===
//
// Long id
//
// String name
//
// String description
//
// BigDecimal price
//
// int quantityTotal
//   - Tổng số vé ban đầu
//
// int quantityRemaining
//   - Số vé còn lại — thay đổi real-time khi có người mua
//
// Instant salesStartAt
//
// Instant salesEndAt
//
// boolean available
//   - Computed field: quantityRemaining > 0 AND now() BETWEEN salesStartAt AND salesEndAt
//   - Giúp frontend disable nút mua khi hết vé hoặc ngoài thời gian bán
//   - Tính trong mapper hoặc service
//
// === GHI CHÚ KỸ THUẬT ===
// - Không expose event chi tiết trong response này (chỉ eventId nếu cần)
// - Dùng record hoặc Lombok @Value
