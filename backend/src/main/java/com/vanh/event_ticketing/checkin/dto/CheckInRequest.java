// Package: com.vanh.event_ticketing.checkin.dto
// File: CheckInRequest.java
//
// Vai trò: DTO nhận yêu cầu check-in từ thiết bị quét QR.
//
// === FIELDS ===
//
// String qrCode
//   - @NotBlank(message = "Mã QR không được để trống")
//   - UUID string được quét từ QR code trên vé
//
// Long gateId
//   - @NotNull(message = "Cổng check-in không được để trống")
//   - ID của cổng check-in mà staff đang đứng
//
// === GHI CHÚ KỸ THUẬT ===
// - staffId KHÔNG lấy từ request body — lấy từ SecurityContext (authenticated staff)
// - qrCode format: UUID "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
// - gateId nên được config sẵn trên thiết bị của từng cổng
//   (không để staff nhập tay để tránh sai)
// - Gợi ý: thêm @Pattern(regexp = "[0-9a-f-]{36}") để validate UUID format
