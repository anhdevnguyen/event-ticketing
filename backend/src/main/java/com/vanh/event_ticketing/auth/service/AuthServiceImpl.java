// Package: com.vanh.event_ticketing.auth.service
// File: AuthServiceImpl.java
//
// Vai trò: Implementation của AuthService.
// Annotate @Service, @Transactional
//
// === DEPENDENCIES (inject qua constructor) ===
// - UserRepository userRepository
// - RoleRepository roleRepository
// - JwtTokenProvider jwtTokenProvider
// - BCryptPasswordEncoder passwordEncoder  (bean trong SecurityConfig)
// - UserMapper userMapper
//
// === IMPLEMENTATION NOTES ===
//
// register(RegisterRequest):
//   - userRepository.existsByEmail() -> throw BusinessException(USER_ALREADY_EXISTS)
//   - roleRepository.findByName("CUSTOMER") -> assign role
//   - passwordEncoder.encode(request.getPassword())
//   - Save User, gọi issueTokensAndSave(user) -> LoginResponse
//
// login(LoginRequest):
//   - userRepository.findByEmail() -> Optional, nếu empty throw INVALID_CREDENTIALS
//   - Kiểm tra user.isEnabled()
//   - passwordEncoder.matches(rawPassword, user.getPasswordHash())
//   - Nếu sai: throw BusinessException(INVALID_CREDENTIALS) — KHÔNG tiết lộ "email không tồn tại"
//   - issueTokensAndSave(user)
//
// refreshToken(RefreshTokenRequest):
//   - userRepository.findByRefreshToken(token) -> Optional
//   - jwtTokenProvider.validateToken(token) -> nếu invalid throw TOKEN_EXPIRED
//   - Rotate: sinh refreshToken mới, lưu lại DB
//   - Trả về LoginResponse mới
//
// logout(Long userId):
//   - @Transactional
//   - userRepository.findById(userId) -> set refreshToken = null -> save
//
// processOAuth2Login(OAuth2User, String provider):
//   - Extract attributes: sub/id -> providerId, email, name, picture
//   - userRepository.findByEmail(email):
//       + Nếu empty: tạo User mới (role CUSTOMER, enabled=true, passwordHash=null)
//       + Nếu có: update oauth2Provider, oauth2ProviderId, avatarUrl
//   - issueTokensAndSave(user)
//
// Private helper: issueTokensAndSave(User user) -> LoginResponse
//   - accessToken = jwtTokenProvider.generateAccessToken(user)
//   - refreshToken = jwtTokenProvider.generateRefreshToken(user)
//   - user.setRefreshToken(refreshToken) -> save
//   - return userMapper.toLoginResponse(accessToken, refreshToken, user)
//
// === GHI CHÚ KỸ THUẬT ===
// - Tất cả DB write nên trong @Transactional
// - Không log thông tin nhạy cảm (password, token)
// - BCryptPasswordEncoder strength = 12 (cân bằng security vs performance)
