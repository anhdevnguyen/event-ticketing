// Package: com.vanh.event_ticketing.gate.dto
// File: GateResponse.java
//
// Vai trò: DTO trả về thông tin cổng check-in cho client.
//
// === FIELDS ===
//
// Long id
//
// String name
//
// String location
//
// boolean active
//
// Long eventId
//   - ID của event mà cổng thuộc về
//   - Dùng để frontend associate gate với event
//
// String eventName
//   - Tên sự kiện — optional, tiện cho display
//
// === GHI CHÚ KỸ THUẬT ===
// - Không expose toàn bộ Event object trong response — chỉ eventId và eventName
// - Dùng record hoặc Lombok @Value
