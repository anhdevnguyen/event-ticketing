// Package: com.vanh.event_ticketing.ticket.mapper
// File: TicketMapper.java
//
// Vai trò: Mapper chuyển đổi giữa Ticket entity và TicketResponse DTO.
// Annotate @Component hoặc MapStruct @Mapper
//
// === METHODS ===
//
// TicketResponse toResponse(Ticket ticket)
//   - Map: ticket.getId() -> id
//   - Map: ticket.getStatus().name() -> status
//   - Map: ticket.getQrCode() -> qrCode
//   - Map: ticket.getExpiresAt() -> expiresAt
//   - Map: ticket.getCheckedInAt() -> checkedInAt
//   - Map: ticket.getTicketType().getName() -> ticketTypeName
//   - Map: ticket.getTicketType().getEvent().getName() -> eventName
//   - Map: ticket.getCustomer().getDisplayName() -> holderName
//   - Map: ticket.getTicketType().getPrice() -> price
//   - Chú ý: lazy load chains — đảm bảo gọi trong @Transactional
//
// List<TicketResponse> toResponseList(List<Ticket> tickets)
//   - stream().map(this::toResponse).collect(toList())
//
// === GHI CHÚ KỸ THUẬT ===
// - Chain lazy load: ticket -> ticketType -> event
//   Cần @EntityGraph hoặc JOIN FETCH trong query để tránh N+1
// - Nếu dùng MapStruct:
//   @Mapping(target = "ticketTypeName", source = "ticketType.name")
//   @Mapping(target = "eventName", source = "ticketType.event.name")
//   @Mapping(target = "holderName", source = "customer.displayName")
//   @Mapping(target = "status", expression = "java(ticket.getStatus().name())")
