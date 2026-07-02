// Package: com.vanh.event_ticketing.gate.dto
// File: GateRequest.java
//
// Vai trò: DTO nhận dữ liệu tạo/cập nhật cổng check-in từ client.
//
// === FIELDS ===
//
// String name
//   - @NotBlank(message = "Tên cổng không được để trống")
//   - @Size(max = 100)
//
// String location
//   - Nullable
//   - @Size(max = 255)
//
// Boolean active
//   - Nullable — khi null thì mặc định true (khi tạo mới)
//   - Dùng Boolean (object) thay vì boolean (primitive) để phân biệt null và false
//
// === GHI CHÚ KỸ THUẬT ===
// - eventId không nhận từ body — lấy từ path variable trong Controller
// - Validate @Valid trong Controller
