// Package: com.vanh.event_ticketing.auth.mapper
// File: UserMapper.java
//
// Vai trò: Mapper chuyển đổi giữa User entity và các DTO liên quan.
// Annotate @Component hoặc dùng MapStruct @Mapper
//
// === METHODS ===
//
// LoginResponse toLoginResponse(String accessToken, String refreshToken, User user)
//   - Tạo LoginResponse từ tokens + User entity
//   - tokenType = "Bearer"
//   - expiresIn = 900 (15 phút tính bằng giây) — hoặc lấy từ config
//   - user field = toUserSummary(user)
//   - KHÔNG đưa passwordHash vào response
//
// UserSummary toUserSummary(User user)
//   - Map: user.getId() -> id
//   - Map: user.getEmail() -> email
//   - Map: user.getDisplayName() -> displayName
//   - Map: user.getAvatarUrl() -> avatarUrl
//   - Map: user.getRole().getName() -> role (String)
//   - TUYỆT ĐỐI KHÔNG map passwordHash, refreshToken
//
// === GHI CHÚ KỸ THUẬT ===
// - Nếu dùng MapStruct: @Mapper(componentModel = "spring")
//   @Mapping(target = "role", source = "user.role.name")
//   @Mapping(target = "passwordHash", ignore = true) — không cần nếu DTO không có field này
// - Nếu dùng manual mapping: implement đơn giản trong @Component
// - UserSummary là nested DTO trong LoginResponse hoặc class riêng
//   Fields: Long id, String email, String displayName, String avatarUrl, String role
