// Package: com.vanh.event_ticketing.auth.dto
// File: LoginRequest.java
//
// Vai trò: DTO nhận dữ liệu từ client cho endpoint POST /api/v1/auth/login.
// Dùng record hoặc class với Lombok @Data
//
// === FIELDS ===
//
// String email
//   - @NotBlank(message = "Email không được để trống")
//   - @Email(message = "Email không đúng định dạng")
//
// String password
//   - @NotBlank(message = "Mật khẩu không được để trống")
//   - @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
//
// === GHI CHÚ KỸ THUẬT ===
// - Dùng @Valid trong Controller để kích hoạt validation
// - Không log password ở bất kỳ đâu
// - Có thể dùng Java Record:
//   public record LoginRequest(
//       @NotBlank @Email String email,
//       @NotBlank @Size(min=8) String password
//   ) {}
