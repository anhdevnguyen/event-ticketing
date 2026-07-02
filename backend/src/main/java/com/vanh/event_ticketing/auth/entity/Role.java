// Package: com.vanh.event_ticketing.auth.entity
// File: Role.java
//
// Vai trò: JPA Entity ánh xạ bảng "roles" — danh sách vai trò người dùng.
// Extends BaseEntity (id, createdAt, updatedAt, version)
// Annotate @Entity, @Table(name = "roles")
//
// === FIELDS ===
//
// String name
//   - @Column(nullable = false, unique = true, length = 50)
//   - Các giá trị hợp lệ: "ADMIN", "ORGANIZER", "CHECKIN_STAFF", "CUSTOMER"
//   - Gợi ý: dùng Enum RoleName { ADMIN, ORGANIZER, CHECKIN_STAFF, CUSTOMER }
//     và @Enumerated(EnumType.STRING) để type-safe
//
// === GHI CHÚ KỸ THUẬT ===
// - Bảng roles nên có ít nhất 4 bản ghi được seed khi startup:
//     INSERT INTO roles(name) VALUES ('ADMIN'), ('ORGANIZER'), ('CHECKIN_STAFF'), ('CUSTOMER')
//     ON CONFLICT DO NOTHING;
// - Spring Security: role name trong hasRole() tự thêm prefix "ROLE_"
//   Ví dụ: hasRole("ORGANIZER") -> check authority "ROLE_ORGANIZER"
//   Hoặc dùng hasAuthority("ORGANIZER") nếu không dùng prefix
// - Relationship: User @ManyToOne -> Role (một user có một role)
// - Không dùng @Data Lombok cho entity
