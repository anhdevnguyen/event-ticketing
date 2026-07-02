// Package: com.vanh.event_ticketing.ticket.entity
// File: Ticket.java
//
// Vai trò: JPA Entity ánh xạ bảng "tickets" — mỗi record là một vé cụ thể.
// Extends BaseEntity (id, createdAt, updatedAt, version)
// Annotate @Entity, @Table(name = "tickets")
//
// === FIELDS ===
//
// TicketType ticketType
//   - @ManyToOne(fetch = FetchType.LAZY)
//   - @JoinColumn(name = "ticket_type_id", nullable = false)
//
// User customer
//   - @ManyToOne(fetch = FetchType.LAZY)
//   - @JoinColumn(name = "customer_id", nullable = false)
//
// TicketStatus status
//   - @Enumerated(EnumType.STRING)
//   - @Column(nullable = false, length = 20)
//   - Enum: RESERVED, CONFIRMED, EXPIRED, CHECKED_IN
//   - Default: RESERVED
//
// String qrCode
//   - @Column(name = "qr_code", unique = true, length = 36)
//   - UUID string: "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
//   - Null khi status = RESERVED (chưa confirm)
//   - Set khi confirm: UUID.randomUUID().toString()
//
// Instant expiresAt
//   - @Column(name = "expires_at", nullable = false)
//   - Thời điểm RESERVED hết hạn (now + 15 phút khi tạo)
//   - Sau khi confirm xong: không còn ý nghĩa nhưng vẫn lưu để tracking
//
// Instant checkedInAt
//   - @Column(name = "checked_in_at")
//   - Nullable — set khi status chuyển sang CHECKED_IN
//
// === ENUM TicketStatus ===
// Định nghĩa trong ticket/entity/TicketStatus.java hoặc inner enum:
//   RESERVED    — vừa reserve, chờ confirm và thanh toán
//   CONFIRMED   — đã confirm, có QR code, sẵn sàng check-in
//   EXPIRED     — quá hạn reserve mà chưa confirm
//   CHECKED_IN  — đã check-in tại cổng
//
// === GHI CHÚ KỸ THUẬT ===
// - @Table unique constraints: qr_code (đã có unique=true trên @Column)
// - @Table indexes: (customer_id), (ticket_type_id), (status, expires_at)
//   Index (status, expires_at) dùng cho query releaseExpiredReservations
// - @Version từ BaseEntity hỗ trợ optimistic lock (dự phòng thêm cho pessimistic)
// - Không dùng @Data Lombok
