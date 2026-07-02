// Package: com.vanh.event_ticketing.checkin.entity
// File: CheckInLog.java
//
// Vai trò: JPA Entity ánh xạ bảng "checkin_logs" — lưu lịch sử mọi lần quét QR.
// Extends BaseEntity (id, createdAt, updatedAt, version)
// Annotate @Entity, @Table(name = "checkin_logs")
//
// === FIELDS ===
//
// Ticket ticket
//   - @ManyToOne(fetch = FetchType.LAZY)
//   - @JoinColumn(name = "ticket_id", nullable = false)
//   - Có thể có nhiều log cho một vé (lần quét thành công + các lần quét bị từ chối sau đó)
//
// Gate gate
//   - @ManyToOne(fetch = FetchType.LAZY)
//   - @JoinColumn(name = "gate_id", nullable = false)
//
// User staff
//   - @ManyToOne(fetch = FetchType.LAZY)
//   - @JoinColumn(name = "staff_id", nullable = false)
//   - Staff thực hiện check-in
//
// Instant checkedInAt
//   - @Column(name = "checked_in_at", nullable = false)
//   - Thời điểm quét QR
//
// boolean success
//   - @Column(nullable = false)
//   - true: check-in thành công (CONFIRMED -> CHECKED_IN)
//   - false: thất bại (đã check-in rồi, vé không hợp lệ, v.v.)
//
// String failReason
//   - @Column(name = "fail_reason", length = 255)
//   - Nullable — chỉ có giá trị khi success = false
//   - Ví dụ: "Vé đã được check-in lúc 14:30", "Vé đã hết hạn", "Mã QR không hợp lệ"
//
// === GHI CHÚ KỸ THUẬT ===
// - Log này là immutable sau khi tạo — không nên update
// - Gợi ý: bỏ @Version field (từ BaseEntity) vì log không cần optimistic lock
//   Hoặc để nguyên BaseEntity và chấp nhận version = 0
// - @Table indexes: (ticket_id), (gate_id), (checked_in_at)
// - Không dùng @Data Lombok
