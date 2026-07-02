// Package: com.vanh.event_ticketing.common.exception
// File: ValidationException.java
//
// Vai trò: RuntimeException cho lỗi validation input phức tạp (cross-field, business rule).
// Extends RuntimeException (hoặc extends BusinessException nếu muốn dùng ErrorCode)
//
// === FIELDS ===
//
// Map<String, String> fieldErrors
//   - Map<fieldName, errorMessage>
//   - Dùng cho lỗi nhiều field cùng lúc
//   - Ví dụ: {"endTime": "endTime phải sau startTime", "price": "Giá không âm"}
//
// String globalMessage
//   - Thông điệp lỗi tổng quan (không gắn với field cụ thể)
//
// === CONSTRUCTORS ===
//
// ValidationException(String globalMessage)
//   - Lỗi validation tổng quan
//
// ValidationException(Map<String, String> fieldErrors)
//   - Lỗi nhiều field
//
// ValidationException(String field, String message)
//   - Lỗi một field cụ thể
//   - Tự tạo Map: {field: message}
//
// === GHI CHÚ KỸ THUẬT ===
// - MethodArgumentNotValidException (từ @Valid) được xử lý riêng trong GlobalExceptionHandler
//   -> ValidationException dùng cho validation thủ công trong service layer
// - Ví dụ: kiểm tra startTime < endTime sau khi đã parse DTO
// - GlobalExceptionHandler bắt và format theo RFC 7807 Problem Details
