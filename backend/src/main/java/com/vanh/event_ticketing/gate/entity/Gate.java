// Package: com.vanh.event_ticketing.gate.entity
// File: Gate.java
//
// Vai trò: JPA Entity ánh xạ bảng "gates" — cổng check-in tại sự kiện.
// Extends BaseEntity (id, createdAt, updatedAt, version)
// Annotate @Entity, @Table(name = "gates")
//
// === FIELDS ===
//
// Event event
//   - @ManyToOne(fetch = FetchType.LAZY)
//   - @JoinColumn(name = "event_id", nullable = false)
//   - Mỗi cổng thuộc về một sự kiện
//
// String name
//   - @Column(nullable = false, length = 100)
//   - Ví dụ: "Cổng A", "Cổng VIP", "Cổng Chính"
//
// String location
//   - @Column(length = 255)
//   - Nullable — mô tả vị trí cổng trong khuôn viên
//   - Ví dụ: "Sảnh tầng trệt, bên trái"
//
// boolean active
//   - @Column(nullable = false)
//   - Default: true
//   - false: cổng đã đóng hoặc không sử dụng (soft delete)
//
// === GHI CHÚ KỸ THUẬT ===
// - @Table indexes: (event_id)
// - Không dùng @Data Lombok
// - Relationship với CheckInLog: Gate @OneToMany CheckInLog (có thể không cần map ngược)
