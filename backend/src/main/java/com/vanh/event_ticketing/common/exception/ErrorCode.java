// Package: com.vanh.event_ticketing.common.exception
// File: ErrorCode.java
//
// Vai trò: Enum tập trung tất cả mã lỗi nghiệp vụ của hệ thống.
// Mỗi ErrorCode mang: HTTP status code tương ứng và default message.
//
// === ENUM STRUCTURE ===
// Mỗi entry có: (int httpStatus, String defaultMessage)
//
// === TICKET ERRORS ===
// TICKET_SOLD_OUT          (409, "Vé đã hết, không thể đặt thêm")
// TICKET_NOT_FOUND         (404, "Không tìm thấy vé")
// TICKET_EXPIRED           (410, "Vé đã hết hạn đặt chỗ")
// TICKET_ALREADY_CHECKED_IN (409, "Vé đã được check-in trước đó")
// TICKET_INVALID_STATUS    (400, "Trạng thái vé không hợp lệ cho thao tác này")
// TICKET_NOT_OWNED         (403, "Bạn không có quyền thao tác với vé này")
//
// === TICKET TYPE ERRORS ===
// TICKET_TYPE_NOT_FOUND    (404, "Không tìm thấy loại vé")
// TICKET_TYPE_HAS_SOLD_TICKETS (409, "Không thể xóa loại vé đã có vé bán ra")
// TICKET_TYPE_SALES_NOT_STARTED (400, "Chưa đến thời gian mở bán")
// TICKET_TYPE_SALES_ENDED  (400, "Thời gian bán vé đã kết thúc")
//
// === EVENT ERRORS ===
// EVENT_NOT_FOUND          (404, "Không tìm thấy sự kiện")
// EVENT_NOT_PUBLISHED      (400, "Sự kiện chưa được phát hành")
// EVENT_CANCELLED          (410, "Sự kiện đã bị hủy")
//
// === GATE ERRORS ===
// GATE_NOT_FOUND           (404, "Không tìm thấy cổng check-in")
//
// === AUTH ERRORS ===
// USER_ALREADY_EXISTS      (409, "Email đã được đăng ký")
// INVALID_CREDENTIALS      (401, "Email hoặc mật khẩu không đúng")
// TOKEN_EXPIRED            (401, "Token đã hết hạn")
// TOKEN_INVALID            (401, "Token không hợp lệ")
// USER_NOT_FOUND           (404, "Không tìm thấy người dùng")
// USER_DISABLED            (403, "Tài khoản đã bị vô hiệu hóa")
//
// === AUTHORIZATION ERRORS ===
// UNAUTHORIZED             (401, "Bạn chưa đăng nhập")
// FORBIDDEN                (403, "Bạn không có quyền thực hiện thao tác này")
//
// === SYSTEM ERRORS ===
// INTERNAL_SERVER_ERROR    (500, "Đã có lỗi xảy ra, vui lòng thử lại sau")
// QR_GENERATION_FAILED     (500, "Không thể tạo mã QR")
//
// === METHODS ===
// int getHttpStatus()
//   - Trả về HTTP status code tương ứng
// String getDefaultMessage()
//   - Trả về thông điệp lỗi mặc định (tiếng Việt)
//
// === GHI CHÚ KỸ THUẬT ===
// - GlobalExceptionHandler dùng errorCode.getHttpStatus() để set response status
// - Có thể thêm error code String (ví dụ: "TICKET_001") cho client xử lý programmatically
// - Tiếng Việt trong message: đảm bảo file .java encode UTF-8
