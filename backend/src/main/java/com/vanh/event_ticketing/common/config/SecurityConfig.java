// Package: com.vanh.event_ticketing.common.config
// File: SecurityConfig.java
//
// Vai trò: Cấu hình Spring Security toàn bộ ứng dụng.
// Annotate @Configuration, @EnableWebSecurity, @EnableMethodSecurity (cho @PreAuthorize)
//
// === BEANS ===
//
// SecurityFilterChain filterChain(HttpSecurity http)
//   - Disable CSRF (REST API + JWT, không dùng session)
//   - Session management: STATELESS
//   - CORS: corsConfigurationSource() bean
//   - Thêm JwtAuthenticationFilter trước UsernamePasswordAuthenticationFilter
//
//   Public endpoints (permitAll):
//     POST /api/v1/auth/register
//     POST /api/v1/auth/login
//     POST /api/v1/auth/refresh
//     GET  /api/v1/events (danh sách sự kiện public)
//     GET  /api/v1/events/{id}
//     GET  /api/v1/events/{id}/ticket-types
//     /ws/**  (WebSocket endpoint)
//     /actuator/health
//
//   Protected endpoints:
//     /api/v1/tickets/**      -> AUTHENTICATED
//     /api/v1/checkin/**      -> CHECKIN_STAFF, ORGANIZER, ADMIN
//     /api/v1/dashboard/**    -> ORGANIZER, ADMIN
//     Còn lại                 -> AUTHENTICATED
//
// BCryptPasswordEncoder passwordEncoder()
//   - @Bean
//   - strength = 12
//
// AuthenticationManager authenticationManager(AuthenticationConfiguration config)
//   - @Bean — expose AuthenticationManager để dùng trong AuthServiceImpl
//
// OAuth2 Login:
//   - http.oauth2Login()
//   - successHandler: OAuth2LoginSuccessHandler (redirect về frontend với JWT)
//   - userService: CustomOAuth2UserService (upsert user khi OAuth2 login)
//
// === GHI CHÚ KỸ THUẬT ===
// - @EnableMethodSecurity(prePostEnabled = true) để @PreAuthorize hoạt động
// - Spring Security 6: không dùng WebSecurityConfigurerAdapter (deprecated)
// - Thứ tự filter: JwtAuthenticationFilter -> UsernamePasswordAuthenticationFilter
// - Exception handling: authenticationEntryPoint (401), accessDeniedHandler (403)
//   -> Trả về JSON format thay vì HTML
