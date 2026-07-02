// Package: com.vanh.event_ticketing.auth.entity
// File: User.java
//
// Vai trò: JPA Entity ánh xạ bảng "users" trong database.
// Extends BaseEntity (id, createdAt, updatedAt, version)
// Annotate @Entity, @Table(name = "users")
//
// === FIELDS ===
//
// String email
//   - @Column(nullable = false, unique = true, length = 255)
//   - Dùng làm username/identifier chính
//
// String passwordHash
//   - @Column(name = "password_hash")
//   - Nullable — null nếu user đăng nhập qua OAuth2
//   - KHÔNG bao giờ expose ra ngoài (không map vào DTO)
//
// String displayName
//   - @Column(name = "display_name", length = 100)
//
// String avatarUrl
//   - @Column(name = "avatar_url", length = 500)
//   - Lấy từ Google profile khi OAuth2
//
// Role role
//   - @ManyToOne(fetch = FetchType.EAGER) — EAGER vì luôn cần role khi load User
//   - @JoinColumn(name = "role_id", nullable = false)
//
// String refreshToken
//   - @Column(name = "refresh_token", length = 512)
//   - Nullable — null sau khi logout
//   - Gợi ý: lưu hashed value để bảo mật
//
// String oauth2Provider
//   - @Column(name = "oauth2_provider", length = 50)
//   - Ví dụ: "google", "facebook"
//   - Null nếu đăng ký thông thường
//
// String oauth2ProviderId
//   - @Column(name = "oauth2_provider_id", length = 255)
//   - Sub/ID trả về từ OAuth2 provider
//
// boolean enabled
//   - @Column(nullable = false)
//   - Default: true
//   - Dùng để soft-ban user mà không xóa dữ liệu
//
// === GHI CHÚ KỸ THUẬT ===
// - Implements org.springframework.security.core.userdetails.UserDetails
//   HOẶC wrap trong CustomUserDetails (khuyến nghị — tách biệt concern)
// - @Table unique constraint: (oauth2Provider, oauth2ProviderId) nếu muốn link account
// - Không dùng @Data của Lombok cho entity — dùng @Getter @Setter @NoArgsConstructor @AllArgsConstructor
// - equals/hashCode nên dựa trên id, không phải tất cả fields
