// Package: com.vanh.event_ticketing.ticket.qr
// File: QrCodeGenerator.java
//
// Vai trò: Utility class sinh ảnh QR Code PNG từ một UUID string.
// Dùng thư viện ZXing (com.google.zxing).
// Annotate @Component
//
// === DEPENDENCIES (Maven/Gradle) ===
// implementation 'com.google.zxing:core:3.5.2'
// implementation 'com.google.zxing:javase:3.5.2'
//
// === METHODS ===
//
// byte[] generate(String content)
//   - Sinh QR Code PNG từ content (UUID string của qrCode)
//   - Trả về mảng byte[] của ảnh PNG
//   - Throw RuntimeException nếu có lỗi khi sinh QR
//
//   Pseudo code:
//     QRCodeWriter qrCodeWriter = new QRCodeWriter();
//     BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
//     ByteArrayOutputStream baos = new ByteArrayOutputStream();
//     MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
//     return baos.toByteArray();
//
// === CONFIG ===
// int width = 300   — chiều rộng ảnh QR (pixel)
// int height = 300  — chiều cao ảnh QR (pixel)
//   - Có thể config từ application.properties: app.qr.width, app.qr.height
//   - Inject qua @Value("${app.qr.width:300}")
//
// === GHI CHÚ KỸ THUẬT ===
// - QrCodeGenerator chỉ sinh ảnh từ UUID string đã có — không tự tạo UUID
// - UUID được sinh trong TicketServiceImpl.confirm()
// - Endpoint trả về QR image: GET /api/v1/tickets/{id}/qr-image
//   -> ResponseEntity<byte[]> với Content-Type: image/png
// - Cache gợi ý: @Cacheable(key = "#qrCode") vì QR không thay đổi sau khi tạo
// - ErrorHandling: bắt WriterException, wrap vào RuntimeException với message rõ ràng
