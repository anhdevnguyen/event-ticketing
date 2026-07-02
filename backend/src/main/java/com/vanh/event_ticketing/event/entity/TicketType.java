// Package: com.vanh.event_ticketing.event.entity
// File: TicketType.java
//
// Vai trò: JPA Entity ánh xạ bảng "ticket_types" — loại vé của một sự kiện.
// Extends BaseEntity (id, createdAt, updatedAt, version)
// Annotate @Entity, @Table(name = "ticket_types")
//
// === FIELDS ===
//
// Event event
//   - @ManyToOne(fetch = FetchType.LAZY)
//   - @JoinColumn(name = "event_id", nullable = false)
//
// String name
//   - @Column(nullable = false, length = 100)
//   - Ví dụ: "VIP", "Regular", "Early Bird"
//
// String description
//   - @Column(columnDefinition = "TEXT")
//
// BigDecimal price
//   - @Column(nullable = false, precision = 15, scale = 2)
//   - Dùng BigDecimal, không dùng double/float cho tiền tệ
//   - Ví dụ: 150000.00 (VND)
//
// int quantityTotal
//   - @Column(name = "quantity_total", nullable = false)
//   - Tổng số vé của loại này — không đổi sau khi đã bán (hoặc chỉ tăng)
//
// int quantityRemaining
//   - @Column(name = "quantity_remaining", nullable = false)
//   - Số vé còn lại — được giảm khi reserve, tăng khi expire reservation
//   - CRITICAL: chỉ được modify qua pessimistic lock (findByIdForUpdate)
//   - Constraint DB: CHECK (quantity_remaining >= 0)
//
// Instant salesStartAt
//   - @Column(name = "sales_start_at")
//   - Thời điểm bắt đầu bán vé
//
// Instant salesEndAt
//   - @Column(name = "sales_end_at")
//   - Thời điểm kết thúc bán vé
//
// === GHI CHÚ KỸ THUẬT ===
// - version field từ BaseEntity (@Version Long version) cung cấp optimistic lock
//   -> Kết hợp với pessimistic lock trong findByIdForUpdate khi cần
// - Không dùng @Data Lombok
// - @Table indexes: (event_id)
// - Business rule: salesStartAt <= salesEndAt <= event.endTime
