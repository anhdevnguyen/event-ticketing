// Package: com.vanh.event_ticketing.auth.dto
// File: RegisterRequest.java
//
// Vai trò: DTO nhận dữ liệu từ client cho endpoint POST /api/v1/auth/register.
//
// === FIELDS ===
//
// String email
//   - @NotBlank
//   - @Email
//
// String password
//   - @NotBlank
//   - @Size(min = 8, max = 100)
//   - Pattern gợi ý: @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$")
//     -> Yêu cầu ít nhất 1 chữ và 1 số
//
// String displayName
//   - @NotBlank
//   - @Size(min = 2, max = 100)
//
// === GHI CHÚ KỸ THUẬT ===
// - Validate bằng @Valid trong Controller
// - Không có confirmPassword ở đây — có thể thêm nếu cần frontend UX
// - Role được assign tự động là CUSTOMER (không nhận từ client)
// - Có thể dùng Java Record hoặc class với Lombok
