// Package: com.vanh.event_ticketing.checkin.mapper
// File: CheckInLogMapper.java
//
// Vai trò: Mapper chuyển đổi CheckInLog entity sang CheckInResponse DTO.
// Annotate @Component hoặc MapStruct @Mapper
//
// === METHODS ===
//
// CheckInResponse toResponse(CheckInLog log)
//   - Map: log.isSuccess() -> success
//   - Map: log.getTicket().getId() -> ticketId
//   - Map: log.getTicket().getCustomer().getDisplayName() -> holderName
//   - Map: log.getTicket().getTicketType().getEvent().getName() -> eventName
//   - Map: log.getGate().getName() -> gateName
//   - Map: log.getCheckedInAt() -> checkedInAt
//   - Message:
//       Nếu success: "Check-in thành công"
//       Nếu không: log.getFailReason()
//
// Page<CheckInResponse> toResponsePage(Page<CheckInLog> logs)
//   - Map Page<CheckInLog> -> PageResponse<CheckInResponse>
//   - Sử dụng logs.map(this::toResponse) rồi wrap PageResponse
//
// === GHI CHÚ KỸ THUẬT ===
// - Chain lazy load: log -> ticket -> ticketType -> event
//   Cần @EntityGraph hoặc JOIN FETCH để tránh N+1 query
// - Nếu dùng MapStruct:
//   @Mapping(target = "ticketId", source = "ticket.id")
//   @Mapping(target = "holderName", source = "ticket.customer.displayName")
//   @Mapping(target = "eventName", source = "ticket.ticketType.event.name")
//   @Mapping(target = "gateName", source = "gate.name")
//   @Mapping(target = "message", expression = "java(log.isSuccess() ? \"Check-in thành công\" : log.getFailReason())")
