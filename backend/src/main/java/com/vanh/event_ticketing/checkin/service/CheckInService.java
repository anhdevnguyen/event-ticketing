// Package: com.vanh.event_ticketing.checkin.service
// File: CheckInService.java
//
// Vai trò: Interface định nghĩa contract nghiệp vụ check-in.
//
// === METHODS ===
//
// CheckInResponse checkIn(String qrCode, Long gateId, Long staffId)
//   - Xử lý check-in tại cổng
//   - Tìm ticket theo qrCode
//   - Dùng atomic conditional update để chống double check-in
//   - Ghi log vào CheckInLog
//   - Publish WebSocket event để cập nhật dashboard real-time
//   - Trả về CheckInResponse kể cả khi thất bại (success=false + message mô tả lỗi)
//   - Throw BusinessException(TICKET_NOT_FOUND) nếu qrCode không tồn tại
//   - Không throw exception khi vé đã check-in — trả về success=false
//
// PageResponse<CheckInResponse> getLogsByEvent(Long eventId, Long gateId, Pageable pageable)
//   - Lấy lịch sử check-in của một event, có thể filter theo gate
//   - @Transactional(readOnly = true)
//
// === GHI CHÚ KỸ THUẬT ===
// - checkIn() phải @Transactional để đảm bảo:
//     1. checkInIfConfirmed (DB update) atomic
//     2. CheckInLog.save() cùng transaction
//   - Nếu save log fail -> cả check-in bị rollback (đúng behavior)
// - WebSocket publish nên sau transaction commit (dùng @TransactionalEventListener)
//   để tránh publish trước khi DB commit xong
