// Package: com.vanh.event_ticketing.common.config
// File: CorsConfig.java
//
// Vai trò: Cấu hình CORS (Cross-Origin Resource Sharing) toàn ứng dụng.
// Annotate @Configuration
//
// === BEANS ===
//
// CorsConfigurationSource corsConfigurationSource()
//   - @Bean
//   - Đọc allowed origin từ env/property: FRONTEND_URL (hoặc app.cors.allowed-origins)
//   - UrlBasedCorsConfigurationSource
//
//   CorsConfiguration config:
//     - config.setAllowedOrigins(List.of(frontendUrl))
//       -> Không dùng * trong production (không work với allowCredentials = true)
//     - config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"))
//     - config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"))
//     - config.setExposedHeaders(List.of("Authorization"))
//     - config.setAllowCredentials(true)
//       -> Cần true nếu dùng cookie cho refresh token
//     - config.setMaxAge(3600L)  -- cache preflight response 1 giờ
//
//   source.registerCorsMapping("/api/**", config)
//   source.registerCorsMapping("/ws/**", config)
//
// === PROPERTY CONFIG ===
// @Value("${app.cors.allowed-origins:http://localhost:3000}")
// private String frontendUrl;
//   -> Default: localhost:3000 (development)
//   -> Production: set env FRONTEND_URL=https://your-frontend.com
//
// === GHI CHÚ KỸ THUẬT ===
// - CorsConfigurationSource bean này được inject vào SecurityConfig.filterChain()
// - Không dùng @CrossOrigin trên từng Controller — manage tập trung ở đây
// - Multiple origins: List.of("https://app.example.com", "https://admin.example.com")
//   đọc từ comma-separated property: app.cors.allowed-origins=url1,url2
