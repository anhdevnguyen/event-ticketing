// Package: com.vanh.event_ticketing.event.service
// File: TicketTypeServiceImpl.java
//
// Vai trò: Implementation của TicketTypeService.
// Annotate @Service, @Transactional
//
// === DEPENDENCIES (inject qua constructor) ===
// - TicketTypeRepository ticketTypeRepository
// - EventRepository eventRepository
// - TicketRepository ticketRepository  (để check sold tickets khi delete)
// - TicketTypeMapper ticketTypeMapper
//
// === IMPLEMENTATION NOTES ===
//
// createTicketType(Long eventId, TicketTypeRequest request, Long requesterId):
//   - event = eventRepository.findById(eventId) -> throw EVENT_NOT_FOUND
//   - checkEventOwnership(event, requesterId)
//   - Map request -> TicketType
//   - ticketType.setQuantityRemaining(request.getQuantityTotal())  // Khởi tạo = total
//   - save và return
//
// updateTicketType(Long id, TicketTypeRequest request, Long requesterId):
//   - ticketType = findById -> throw TICKET_TYPE_NOT_FOUND
//   - checkEventOwnership(ticketType.getEvent(), requesterId)
//   - Nếu thay đổi quantityTotal:
//       soldCount = quantityTotal - quantityRemaining (hiện tại)
//       Nếu request.getQuantityTotal() < soldCount -> throw BusinessException
//       quantityRemaining = request.getQuantityTotal() - soldCount
//   - Update các field khác
//   - save và return
//
// deleteTicketType(Long id, Long requesterId):
//   - ticketType = findById -> throw TICKET_TYPE_NOT_FOUND
//   - checkEventOwnership(ticketType.getEvent(), requesterId)
//   - soldCount = ticketRepository.countByTicketTypeIdAndStatusNot(id, TicketStatus.EXPIRED)
//   - Nếu soldCount > 0 -> throw BusinessException(TICKET_TYPE_HAS_SOLD_TICKETS)
//   - ticketTypeRepository.delete(ticketType)
//
// listByEvent(Long eventId):
//   - @Transactional(readOnly = true)
//   - eventRepository.existsById(eventId) -> throw EVENT_NOT_FOUND nếu false
//   - ticketTypeRepository.findByEventId(eventId) -> map -> return list
//
// Private checkEventOwnership(Event event, Long requesterId):
//   - Throw BusinessException(FORBIDDEN) nếu event.organizer.id != requesterId
//   - Exception: ADMIN role được bỏ qua check (load user để kiểm tra role)
//
// === GHI CHÚ KỸ THUẬT ===
// - Dependency vào TicketRepository là cross-module — cân nhắc dùng service method thay vì repository trực tiếp
// - @Transactional đảm bảo atomic khi update quantityRemaining
