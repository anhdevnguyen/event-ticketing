// Package: com.vanh.event_ticketing.auth.controller
// File: AuthController.java
//
// Vai trò: REST Controller xử lý tất cả các endpoint xác thực người dùng.
// Annotate @RestController, @RequestMapping("/api/v1/auth")
//
// === ENDPOINTS ===
//
// POST /api/v1/auth/register
//   - Input: RegisterRequest (email, password, displayName)
//   - Output: LoginResponse (accessToken, refreshToken, user info)
//   - Gọi: authService.register(request)
//   - Public endpoint (không cần authentication)
//
// POST /api/v1/auth/login
//   - Input: LoginRequest (email @NotBlank @Email, password @NotBlank @Size(min=8))
//   - Output: LoginResponse
//   - Gọi: authService.login(request)
//   - Public endpoint
//   - Validate bằng @Valid
//
// POST /api/v1/auth/refresh
//   - Input: RefreshTokenRequest (refreshToken @NotBlank)
//   - Output: LoginResponse (access token mới)
//   - Gọi: authService.refreshToken(request)
//   - Public endpoint (vì access token đã hết hạn)
//
// POST /api/v1/auth/logout
//   - Input: RefreshTokenRequest hoặc lấy từ SecurityContext
//   - Output: 204 No Content
//   - Gọi: authService.logout(userId)
//   - Yêu cầu: AUTHENTICATED (Bearer token còn hạn)
//   - Xóa refreshToken trong DB
//
// GET /api/v1/auth/oauth2/callback/google  (hoặc xử lý qua OAuth2LoginSuccessHandler)
//   - Xử lý callback từ Google OAuth2
//   - Gọi: authService.processOAuth2Login(oAuth2User)
//   - Redirect về frontend với token sau khi thành công
//   - Nên implement OAuth2AuthenticationSuccessHandler riêng thay vì endpoint trực tiếp
//
// === GHI CHÚ KỸ THUẬT ===
// - Inject AuthService qua constructor injection
// - Dùng @Valid trên tất cả @RequestBody
// - ResponseEntity<LoginResponse> cho các endpoint trả token
// - OAuth2 callback nên được handle bởi Spring Security filter, không phải Controller thuần
// - CORS header được handle ở SecurityConfig / CorsConfig
