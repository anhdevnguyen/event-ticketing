// Package: com.vanh.event_ticketing.auth.service
// File: AuthService.java
//
// Vai trò: Interface định nghĩa contract cho tất cả nghiệp vụ xác thực.
// Được implement bởi AuthServiceImpl.
//
// === METHODS ===
//
// LoginResponse register(RegisterRequest request)
//   - Kiểm tra email chưa tồn tại (existsByEmail)
//   - Tạo User mới với role CUSTOMER mặc định
//   - Hash password bằng BCrypt
//   - Issue JWT (access + refresh token)
//   - Trả về LoginResponse
//   - Throw: BusinessException(USER_ALREADY_EXISTS) nếu email đã tồn tại
//
// LoginResponse login(LoginRequest request)
//   - Tìm User theo email, throw INVALID_CREDENTIALS nếu không thấy
//   - So khớp password với BCrypt
//   - Issue JWT mới, lưu refreshToken vào DB
//   - Trả về LoginResponse
//   - Throw: BusinessException(INVALID_CREDENTIALS) nếu sai password hoặc user disabled
//
// LoginResponse refreshToken(RefreshTokenRequest request)
//   - Tìm User theo refreshToken trong DB
//   - Validate refreshToken chưa hết hạn (kiểm tra qua JwtTokenProvider)
//   - Issue access token mới (có thể rotate refresh token)
//   - Trả về LoginResponse mới
//   - Throw: BusinessException(TOKEN_EXPIRED) hoặc UNAUTHORIZED nếu token không hợp lệ
//
// void logout(Long userId)
//   - Xóa / set null refreshToken của User trong DB
//   - Sau đó access token sẽ tự expire (stateless JWT)
//
// LoginResponse processOAuth2Login(OAuth2User oAuth2User, String provider)
//   - Extract email, displayName, avatarUrl, providerId từ OAuth2User attributes
//   - Upsert: nếu email chưa có thì tạo mới User (role CUSTOMER), enabled=true, passwordHash=null
//   - Nếu đã tồn tại: cập nhật oauth2Provider, oauth2ProviderId, avatarUrl
//   - Issue JWT và trả về LoginResponse
//   - Provider hiện tại: "google"
