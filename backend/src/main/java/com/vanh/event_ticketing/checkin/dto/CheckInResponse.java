// Package: com.vanh.event_ticketing.checkin.dto
// File: CheckInResponse.java
//
// Vai trò: DTO trả về kết quả check-in cho thiết bị quét.
//
// === FIELDS ===
//
// boolean success
//   - true: check-in thành công
//   - false: thất bại (đã check-in rồi, vé không hợp lệ, v.v.)
//
// Long ticketId
//   - ID của vé đã được quét
//
// String holderName
//   - Tên người giữ vé (customer.displayName)
//   - Hiển thị trên màn hình thiết bị để staff xác nhận danh tính
//
// String eventName
//   - Tên sự kiện — để staff biết đang check-in sự kiện nào
//
// String gateName
//   - Tên cổng check-in
//
// String message
//   - Thông điệp mô tả kết quả:
//     + Success: "Check-in thành công! Chào mừng bạn đến với [EventName]"
//     + Fail:    "Vé đã được check-in lúc HH:mm DD/MM/YYYY"
//                "Vé không hợp lệ hoặc đã hết hạn"
//                "Mã QR không tồn tại"
//
// Instant checkedInAt
//   - Thời điểm check-in (nếu success)
//
// === GHI CHÚ KỸ THUẬT ===
// - Response này được hiển thị trực tiếp trên màn hình thiết bị scan
// - Nên có màu sắc indicator: success=true -> xanh lá, false -> đỏ
//   (frontend/app xử lý dựa trên success boolean)
// - HTTP status luôn 200 — không dùng 4xx cho business failure
