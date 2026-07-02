// Package: com.vanh.event_ticketing.auth.repository
// File: RoleRepository.java
//
// Vai trò: Spring Data JPA repository cho entity Role.
// Extends JpaRepository<Role, Long>
// Annotate @Repository
//
// === QUERY METHODS ===
//
// Optional<Role> findByName(String name)
//   - Dùng trong: AuthServiceImpl.register() — lấy role CUSTOMER
//   - Dùng trong: DataInitializer (seed data) — lấy hoặc tạo role
//   - name là unique, ví dụ: "ADMIN", "ORGANIZER", "CHECKIN_STAFF", "CUSTOMER"
//
// === GHI CHÚ KỸ THUẬT ===
// - Bảng roles nên được seed sẵn khi khởi động app (dùng CommandLineRunner hoặc data.sql)
// - Có thể dùng Enum thay vì String để tránh typo:
//   Ví dụ: findByName(RoleName.CUSTOMER) nếu Role.name là kiểu Enum
// - Cache: có thể @Cacheable vì bảng roles hiếm khi thay đổi
