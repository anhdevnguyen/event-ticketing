// Package: com.vanh.event_ticketing.event.dto
// File: EventRequest.java
//
// Vai trò: DTO nhận dữ liệu tạo/cập nhật sự kiện từ client.
//
// === FIELDS ===
//
// String name
//   - @NotBlank(message = "Tên sự kiện không được để trống")
//   - @Size(max = 255)
//
// String description
//   - Nullable
//
// String bannerUrl
//   - Nullable
//   - @URL nếu muốn validate định dạng URL
//
// Instant startTime
//   - @NotNull(message = "Thời gian bắt đầu không được để trống")
//   - @FutureOrPresent — gợi ý: sự kiện không nên ở quá khứ
//
// Instant endTime
//   - Nullable
//   - Nếu có: phải sau startTime — validate bằng custom @AssertTrue hoặc cross-field validator
//
// String location
//   - Nullable
//   - @Size(max = 500)
//
// === GHI CHÚ KỸ THUẬT ===
// - Dùng @Valid trong Controller
// - Không nhận status từ client — status được quản lý bởi business logic
// - Không nhận organizerId từ client — lấy từ SecurityContext
// - Cross-field validation: có thể dùng custom @Constraint hoặc @AssertTrue isEndTimeValid()
