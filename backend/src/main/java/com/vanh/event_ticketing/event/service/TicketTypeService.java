// Package: com.vanh.event_ticketing.event.service
// File: TicketTypeService.java
//
// Vai trò: Interface định nghĩa contract nghiệp vụ quản lý loại vé.
//
// === METHODS ===
//
// TicketTypeResponse createTicketType(Long eventId, TicketTypeRequest request, Long requesterId)
//   - Tạo loại vé mới cho event
//   - Kiểm tra event tồn tại và requester là chủ event
//   - quantityRemaining = quantityTotal khi tạo mới
//
// TicketTypeResponse updateTicketType(Long ticketTypeId, TicketTypeRequest request, Long requesterId)
//   - Cập nhật thông tin loại vé
//   - Kiểm tra ownership qua event
//   - Lưu ý: không được giảm quantityTotal xuống dưới số đã bán
//   - Khi update quantityTotal: quantityRemaining += (newTotal - oldTotal)
//
// void deleteTicketType(Long ticketTypeId, Long requesterId)
//   - Xóa loại vé
//   - KHÔNG cho xóa nếu đã có ít nhất 1 ticket có status != EXPIRED trong DB
//   - Throw BusinessException(TICKET_TYPE_HAS_SOLD_TICKETS) nếu vi phạm
//
// List<TicketTypeResponse> listByEvent(Long eventId)
//   - Lấy tất cả loại vé của một event
//   - @Transactional(readOnly = true)
//
// === GHI CHÚ KỸ THUẬT ===
// - quantityRemaining là field cực kỳ quan trọng — chỉ được giảm trong ticket module
//   thông qua pessimistic lock (findByIdForUpdate)
// - Không expose method giảm quantityRemaining ở đây
//   -> Xem TicketTypeRepository.findByIdForUpdate()
