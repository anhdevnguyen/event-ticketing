// Package: com.vanh.event_ticketing.checkin.service
// File: CheckInServiceImpl.java
//
// Vai trò: Implementation của CheckInService.
// Annotate @Service, @Transactional
//
// === DEPENDENCIES (inject qua constructor) ===
// - TicketRepository ticketRepository        (cross-module — critical path)
// - CheckInLogRepository checkInLogRepository
// - GateRepository gateRepository
// - UserRepository userRepository
// - DashboardEventPublisher dashboardEventPublisher
// - CheckInLogMapper checkInLogMapper
//
// === IMPLEMENTATION NOTES ===
//
// checkIn(String qrCode, Long gateId, Long staffId):
//   - @Transactional
//
//   Bước 1: Tìm ticket theo qrCode
//     ticket = ticketRepository.findByQrCode(qrCode)
//       -> Nếu không thấy: throw BusinessException(TICKET_NOT_FOUND)
//         (hoặc return CheckInResponse(success=false, "Mã QR không hợp lệ"))
//
//   Bước 2: Load gate và staff
//     gate = gateRepository.findById(gateId) -> throw nếu không thấy
//     staff = userRepository.findById(staffId) -> throw nếu không thấy
//
//   Bước 3: Atomic conditional update (CRITICAL — chống double check-in)
//     updatedRows = ticketRepository.checkInIfConfirmed(ticket.getId(), Instant.now())
//     success = (updatedRows == 1)
//     failReason = success ? null : "Vé đã được check-in trước đó hoặc không hợp lệ"
//
//   Bước 4: Ghi CheckInLog
//     log = new CheckInLog()
//     log.setTicket(ticket)
//     log.setGate(gate)
//     log.setStaff(staff)
//     log.setCheckedInAt(Instant.now())
//     log.setSuccess(success)
//     log.setFailReason(failReason)
//     checkInLogRepository.save(log)
//
//   Bước 5: Publish WebSocket (sau commit — dùng ApplicationEvent)
//     Nếu success: publishCheckInEvent(ticket, gate)
//
//   Bước 6: Build và return CheckInResponse
//     response.success = success
//     response.ticketId = ticket.getId()
//     response.holderName = ticket.getCustomer().getDisplayName()
//     response.eventName = ticket.getTicketType().getEvent().getName()
//     response.gateName = gate.getName()
//     response.message = success ? "Check-in thành công" : failReason
//
// getLogsByEvent(Long eventId, Long gateId, Pageable pageable):
//   - @Transactional(readOnly = true)
//   - checkInLogRepository.findByEventId (hoặc filter theo gateId nếu có)
//   - Map và wrap PageResponse
//
// Private publishCheckInEvent(Ticket ticket, Gate gate):
//   - Dùng ApplicationEventPublisher.publishEvent(new CheckInEvent(...))
//   - @TransactionalEventListener(phase = AFTER_COMMIT) trong DashboardEventPublisher
//
// === GHI CHÚ KỸ THUẬT ===
// - KHÔNG dùng: load ticket -> check status -> update (race condition!)
// - PHẢI dùng: checkInIfConfirmed (conditional UPDATE WHERE status='CONFIRMED') (atomic)
// - WebSocket publish phải AFTER_COMMIT để tránh client nhận event trước DB ghi xong
