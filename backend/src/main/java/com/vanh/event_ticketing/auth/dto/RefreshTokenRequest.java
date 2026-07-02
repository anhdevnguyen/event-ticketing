// Package: com.vanh.event_ticketing.auth.dto
// File: RefreshTokenRequest.java
//
// Vai trò: DTO nhận refresh token từ client cho endpoint POST /api/v1/auth/refresh.
//
// === FIELDS ===
//
// String refreshToken
//   - @NotBlank(message = "Refresh token không được để trống")
//
// === GHI CHÚ KỸ THUẬT ===
// - Thay thế tốt hơn: gửi refresh token qua httpOnly cookie thay vì request body
//   -> Tránh XSS attack đọc token từ JavaScript
//   -> Nếu dùng cookie: đọc từ HttpServletRequest.getCookies() trong Controller
// - Có thể dùng Java Record:
//   public record RefreshTokenRequest(@NotBlank String refreshToken) {}
