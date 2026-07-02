// Package: com.vanh.event_ticketing.event.entity
// File: Event.java
//
// Vai trò: JPA Entity ánh xạ bảng "events".
// Extends BaseEntity (id, createdAt, updatedAt, version)
// Annotate @Entity, @Table(name = "events")
//
// === FIELDS ===
//
// String name
//   - @Column(nullable = false, length = 255)
//
// String description
//   - @Column(columnDefinition = "TEXT")
//   - Nullable
//
// String bannerUrl
//   - @Column(name = "banner_url", length = 500)
//   - URL ảnh banner sự kiện
//
// Instant startTime
//   - @Column(name = "start_time", nullable = false)
//
// Instant endTime
//   - @Column(name = "end_time")
//   - Nullable — sự kiện có thể chưa xác định thời gian kết thúc
//
// String location
//   - @Column(length = 500)
//   - Địa chỉ hoặc link online
//
// EventStatus status
//   - @Enumerated(EnumType.STRING)
//   - @Column(nullable = false, length = 20)
//   - Enum: DRAFT, PUBLISHED, CANCELLED, COMPLETED
//   - Default: DRAFT
//
// User organizer
//   - @ManyToOne(fetch = FetchType.LAZY)
//   - @JoinColumn(name = "organizer_id", nullable = false)
//   - FetchType.LAZY vì không phải lúc nào cũng cần load organizer
//
// List<TicketType> ticketTypes
//   - @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
//   - Cascade ALL: xóa event sẽ xóa tất cả ticketType liên quan
//
// === ENUM EventStatus ===
// Định nghĩa trong event/entity/EventStatus.java hoặc inner enum:
//   DRAFT        — vừa tạo, chưa public
//   PUBLISHED    — đang bán vé
//   CANCELLED    — đã hủy
//   COMPLETED    — đã kết thúc
//
// === GHI CHÚ KỸ THUẬT ===
// - Không dùng @Data Lombok cho entity
// - @Table indexes: (organizer_id), (status), (start_time)
// - Validation startTime < endTime nên ở DTO hoặc @PrePersist/@PreUpdate
