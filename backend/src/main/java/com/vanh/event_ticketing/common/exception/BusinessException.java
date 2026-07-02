// Package: com.vanh.event_ticketing.common.exception
// File: BusinessException.java
//
// Vai trò: Base RuntimeException cho tất cả lỗi nghiệp vụ có thể xảy ra.
// Extends RuntimeException
//
// === FIELDS ===
//
// ErrorCode errorCode
//   - Enum xác định loại lỗi cụ thể
//   - Dùng để map sang HTTP status code trong GlobalExceptionHandler
//
// String message
//   - Override từ RuntimeException
//   - Lấy từ errorCode.getDefaultMessage() nếu không truyền message riêng
//
// === CONSTRUCTORS ===
//
// BusinessException(ErrorCode errorCode)
//   - super(errorCode.getDefaultMessage())
//   - this.errorCode = errorCode
//
// BusinessException(ErrorCode errorCode, String customMessage)
//   - super(customMessage)
//   - this.errorCode = errorCode
//
// === USAGE EXAMPLES ===
// throw new BusinessException(ErrorCode.TICKET_SOLD_OUT)
// throw new BusinessException(ErrorCode.EVENT_NOT_FOUND, "Sự kiện #" + id + " không tồn tại")
//
// === GHI CHÚ KỸ THUẬT ===
// - Không log stack trace cho BusinessException (đây là expected error, không phải system error)
//   -> GlobalExceptionHandler: log ở INFO level, không phải ERROR
// - Không wrap IOException, DB exception vào BusinessException
//   -> Các lỗi hệ thống vẫn cần stack trace để debug
