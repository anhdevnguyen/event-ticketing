// Package: com.vanh.event_ticketing.gate.service
// File: GateService.java
//
// Vai trò: Interface định nghĩa contract nghiệp vụ quản lý cổng check-in.
//
// === METHODS ===
//
// GateResponse createGate(Long eventId, GateRequest request, Long requesterId)
//   - Tạo cổng mới cho event
//   - Kiểm tra event tồn tại và ownership
//   - active = true mặc định khi tạo
//
// GateResponse updateGate(Long gateId, GateRequest request, Long requesterId)
//   - Cập nhật thông tin cổng (name, location, active)
//   - Kiểm tra ownership qua event
//
// void deleteGate(Long gateId, Long requesterId)
//   - Xóa cổng (hoặc set active=false nếu đã có log)
//   - Kiểm tra ownership
//
// List<GateResponse> listByEvent(Long eventId)
//   - Lấy tất cả cổng của một event
//   - @Transactional(readOnly = true)
