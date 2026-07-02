// Package: com.vanh.event_ticketing.gate.service
// File: GateServiceImpl.java
//
// Vai trò: Implementation của GateService.
// Annotate @Service, @Transactional
//
// === DEPENDENCIES (inject qua constructor) ===
// - GateRepository gateRepository
// - EventRepository eventRepository
//
// === IMPLEMENTATION NOTES ===
//
// createGate(Long eventId, GateRequest request, Long requesterId):
//   - event = eventRepository.findById(eventId) -> throw EVENT_NOT_FOUND
//   - checkOwnership(event, requesterId) — kiểm tra organizer hoặc ADMIN
//   - Map request -> Gate entity
//   - gate.setEvent(event)
//   - gate.setActive(request.isActive() != null ? request.isActive() : true)
//   - save và return response
//
// updateGate(Long gateId, GateRequest request, Long requesterId):
//   - gate = gateRepository.findById(gateId) -> throw GATE_NOT_FOUND (thêm vào ErrorCode)
//   - checkOwnership(gate.getEvent(), requesterId)
//   - Update name, location, active
//   - save và return
//
// deleteGate(Long gateId, Long requesterId):
//   - gate = findById -> throw if not found
//   - checkOwnership(gate.getEvent(), requesterId)
//   - Gợi ý: kiểm tra checkinLogRepository.existsByGateId(gateId)
//     Nếu có log: set active=false và save (soft delete)
//     Nếu không có log: hard delete
//
// listByEvent(Long eventId):
//   - @Transactional(readOnly = true)
//   - gateRepository.findByEventId(eventId)
//   - Map -> list response
//
// Private checkOwnership(Event event, Long requesterId):
//   - Throw FORBIDDEN nếu event.organizer.id != requesterId và không phải ADMIN
//
// === GHI CHÚ KỸ THUẬT ===
// - Dependency vào CheckInLogRepository cho deleteGate có thể cross-module
//   Cân nhắc: thêm method vào CheckInService để check, hoặc chấp nhận dependency
