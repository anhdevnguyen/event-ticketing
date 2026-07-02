// Package: com.vanh.event_ticketing.event.dto
// File: EventResponse.java
//
// Vai trò: DTO trả về thông tin sự kiện cho client.
//
// === FIELDS ===
//
// Long id
//
// String name
//
// String description
//
// String bannerUrl
//
// Instant startTime
//
// Instant endTime
//
// String location
//
// String status
//   - Giá trị: "DRAFT", "PUBLISHED", "CANCELLED", "COMPLETED"
//   - Trả về String thay vì Enum để JSON dễ đọc
//
// String organizerName
//   - Tên hiển thị của organizer (displayName)
//   - Không trả về toàn bộ User object — tránh expose thông tin nhạy cảm
//
// Long organizerId
//   - ID của organizer — dùng để frontend check ownership
//
// === GHI CHÚ KỸ THUẬT ===
// - Có thể thêm List<TicketTypeResponse> ticketTypes nếu endpoint GET /events/{id} cần
//   (detail endpoint) — tùy thuộc API design
// - Dùng record hoặc class với Lombok @Value (immutable)
// - Serialize Instant sang ISO 8601 string (Spring Boot tự làm nếu config đúng)
