// Package: com.vanh.event_ticketing.auth.repository
// File: UserRepository.java
//
// Vai trò: Spring Data JPA repository cho entity User.
// Extends JpaRepository<User, Long>
// Annotate @Repository
//
// === QUERY METHODS ===
//
// Optional<User> findByEmail(String email)
//   - Dùng trong: login, processOAuth2Login, JWT filter lookup
//   - Index gợi ý: CREATE UNIQUE INDEX idx_users_email ON users(email)
//
// boolean existsByEmail(String email)
//   - Dùng trong: register — kiểm tra trùng email trước khi tạo
//   - Nhanh hơn findByEmail vì không load toàn bộ entity
//
// Optional<User> findByRefreshToken(String refreshToken)
//   - Dùng trong: refreshToken endpoint
//   - Lưu ý: refreshToken lưu dạng plain (hoặc hashed) trong DB
//   - Gợi ý: nên hash refreshToken khi lưu DB để tránh lộ nếu DB bị dump
//   - Index gợi ý: CREATE INDEX idx_users_refresh_token ON users(refresh_token)
//     (không unique vì có thể null khi user đã logout)
//
// === GHI CHÚ KỸ THUẬT ===
// - Spring Data tự generate query từ method name — không cần @Query
// - Nếu cần update refreshToken: dùng @Modifying @Query để tránh load entity
//   Ví dụ: @Modifying @Query("UPDATE User u SET u.refreshToken = :token WHERE u.id = :id")
//   void updateRefreshToken(@Param("id") Long id, @Param("token") String token)
