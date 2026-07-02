// Package: com.vanh.event_ticketing.event.mapper
// File: TicketTypeMapper.java
//
// Vai trò: Mapper chuyển đổi giữa TicketType entity và TicketTypeResponse DTO.
// Annotate @Component hoặc MapStruct @Mapper
//
// === METHODS ===
//
// TicketTypeResponse toResponse(TicketType ticketType)
//   - Map: id, name, description, price, quantityTotal, quantityRemaining
//   - Map: salesStartAt, salesEndAt
//   - Tính available:
//       now = Instant.now()
//       available = quantityRemaining > 0
//           AND (salesStartAt == null || !now.isBefore(salesStartAt))
//           AND (salesEndAt == null || now.isBefore(salesEndAt))
//
// List<TicketTypeResponse> toResponseList(List<TicketType> ticketTypes)
//
// === GHI CHÚ KỲ THUẬT ===
// - Nếu dùng MapStruct: @AfterMapping để tính available field
//   @AfterMapping
//   default void setAvailable(TicketType source, @MappingTarget TicketTypeResponse.Builder target) { ... }
// - Hoặc tính available trong service trước khi trả về mapper
