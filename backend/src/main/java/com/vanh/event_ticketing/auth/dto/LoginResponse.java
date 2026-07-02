// Package: com.vanh.event_ticketing.auth.dto
// File: LoginResponse.java
//
// Vai trò: DTO trả về cho client sau khi login/register/refresh thành công.
//
// === FIELDS ===
//
// String accessToken
//   - JWT access token, thời hạn 15 phút
//   - Client dùng để gọi API (Authorization: Bearer <accessToken>)
//
// String refreshToken
//   - JWT hoặc opaque token, thời hạn 7 ngày
//   - Dùng để lấy access token mới khi hết hạn
//   - Nên lưu trong httpOnly cookie thay vì localStorage
//
// String tokenType
//   - Giá trị cố định: "Bearer"
//
// long expiresIn
//   - Thời gian hết hạn của access token tính bằng giây (900 = 15 phút)
//
// UserSummary user
//   - Thông tin cơ bản của user: id, email, displayName, avatarUrl, roleName
//   - Là nested DTO, KHÔNG chứa passwordHash
//
// === GHI CHÚ KỸ THUẬT ===
// - UserSummary có thể là inner record/class hoặc DTO riêng
// - Gợi ý UserSummary fields: Long id, String email, String displayName,
//   String avatarUrl, String role
// - Nên dùng @JsonProperty("access_token") nếu muốn snake_case trong JSON
