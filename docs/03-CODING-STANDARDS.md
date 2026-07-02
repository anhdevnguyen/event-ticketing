# 03 - CODING STANDARDS

## Mục lục

1. [Package Naming](#1-package-naming)
2. [Naming Convention tổng quát](#2-naming-convention-tổng-quát)
3. [Class Naming theo Layer](#3-class-naming-theo-layer)
4. [Response Format](#4-response-format)
5. [Validation](#5-validation)
6. [Mapper](#6-mapper)
7. [Logging](#7-logging)
8. [Exception](#8-exception)
9. [Comment](#9-comment)
10. [Frontend Conventions](#10-frontend-conventions)
11. [Best Practices tổng hợp](#11-best-practices-tổng-hợp)

---

## 1. Package Naming

Package gốc: **`com.vanh.eventticketing`**

Mỗi module domain là 1 sub-package:

```
com.vanh.eventticketing.auth
com.vanh.eventticketing.event
com.vanh.eventticketing.ticket
com.vanh.eventticketing.checkin
com.vanh.eventticketing.gate
com.vanh.eventticketing.dashboard
com.vanh.eventticketing.common
```

Trong mỗi module, sub-package theo layer: `.controller`, `.service`, `.repository`, `.entity`, `.dto`, `.mapper`.

VD đầy đủ: `com.vanh.eventticketing.ticket.service.TicketServiceImpl`

## 2. Naming Convention tổng quát

| Đối tượng | Quy ước | Ví dụ |
|---|---|---|
| Class / Interface | `PascalCase` | `TicketService`, `EventController` |
| Method / field | `camelCase` | `reserveTicket()`, `quantityRemaining` |
| Hằng số (constant) | `UPPER_SNAKE_CASE` | `DEFAULT_RESERVATION_MINUTES` |
| Bảng DB | `snake_case`, số nhiều | `ticket_types`, `checkin_logs` |
| Cột DB | `snake_case` | `quantity_remaining`, `created_at` |
| REST endpoint (URL) | `kebab-case`, số nhiều | `/api/v1/ticket-types` |
| JSON field (response) | `camelCase` | `"quantityRemaining"` |
| Branch Git | xem [`12-CONTRIBUTING.md`](./12-CONTRIBUTING.md) | `feature/ticket-reservation` |

## 3. Class Naming theo Layer

| Layer | Hậu tố (suffix) | Ví dụ |
|---|---|---|
| Entity | *(không hậu tố, danh từ số ít)* | `Ticket`, `Event` (không phải `Tickets`) |
| DTO — request | `Request` | `ReserveRequest`, `EventRequest` |
| DTO — response | `Response` | `TicketResponse`, `EventResponse` |
| Repository | `Repository` | `TicketRepository` |
| Service — interface | `Service` | `TicketService` |
| Service — implementation | `ServiceImpl` | `TicketServiceImpl` |
| Controller | `Controller` | `TicketController` |
| Mapper | `Mapper` | `TicketMapper` |
| Business Exception | `Exception` | `BusinessException`, `ValidationException` |

**Service luôn tách interface + implementation** (đã chốt): mọi module đều có `XxxService` (interface) và `XxxServiceImpl` (implementation), Controller chỉ phụ thuộc vào interface.

```java
public interface TicketService {
    TicketResponse reserve(ReserveRequest request, Long customerId);
    TicketResponse confirm(Long ticketId, Long customerId);
}

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;

    @Override
    @Transactional
    public TicketResponse reserve(ReserveRequest request, Long customerId) {
        // ...
    }
}
```

## 4. Response Format

Theo **chuẩn REST thuần** — **không bọc envelope** cho response thành công:

```http
GET /api/v1/events/42

200 OK
{
  "id": 42,
  "name": "Tech Conference 2026",
  "startTime": "2026-09-01T09:00:00Z",
  "status": "PUBLISHED"
}
```

```http
GET /api/v1/events?page=0&size=20

200 OK
{
  "content": [ { "id": 42, "name": "..." }, ... ],
  "page": 0,
  "size": 20,
  "totalElements": 57,
  "totalPages": 3
}
```

Response lỗi theo **RFC 7807 Problem Details** — chi tiết đầy đủ tại [`09-ERROR-CODES.md`](./09-ERROR-CODES.md):

```json
{
  "type": "https://event-ticketing.dev/errors/TICKET_SOLD_OUT",
  "title": "Ticket Sold Out",
  "status": 409,
  "detail": "Loại vé 'VIP' đã hết chỗ.",
  "instance": "/api/v1/tickets/reserve",
  "errorCode": "TICKET_SOLD_OUT"
}
```

## 5. Validation

**2 tầng validate riêng biệt:**

| Tầng | Dùng cho | Cơ chế |
|---|---|---|
| Controller / DTO | Validate cấu trúc dữ liệu cơ bản (bắt buộc nhập, đúng kiểu, đúng định dạng, giới hạn độ dài...) | Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`, `@Positive`, `@Email`...) |
| Service | Business rule phức tạp (VD: "vé chỉ được check-in trong khung giờ sự kiện", "loại vé đã hết hạn bán") | Ném `BusinessException` riêng, có `errorCode` |

```java
public record ReserveRequest(
    @NotNull Long ticketTypeId,
    @Positive @Max(100) Integer quantity
) {}
```

```java
@PostMapping("/reserve")
public ResponseEntity<TicketResponse> reserve(@Valid @RequestBody ReserveRequest request,
                                               @AuthenticationPrincipal CustomUserDetails user) {
    return ResponseEntity.ok(ticketService.reserve(request, user.getId()));
}
```

```java
// Business rule phức tạp → xử lý ở Service, KHÔNG ở Controller/DTO
if (ticketType.getSalesEndAt().isBefore(Instant.now())) {
    throw new BusinessException(ErrorCode.TICKET_TYPE_SALES_ENDED);
}
```

## 6. Mapper

Mỗi module có `Mapper` riêng để convert Entity ↔ DTO, tránh trộn business logic vào Controller.

```java
@Component
public class TicketMapper {
    public TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
            ticket.getId(),
            ticket.getStatus().name(),
            ticket.getQrCode(),
            ticket.getExpiresAt()
        );
    }
}
```

> Khuyến nghị: viết Mapper thủ công (plain Java) thay vì dùng MapStruct — dự án quy mô nhỏ, ưu tiên đơn giản, dễ đọc, không cần thêm annotation processor.

## 7. Logging

Log dạng **text thông thường** (không cần structured JSON logging), dùng **SLF4J + Logback** mặc định của Spring Boot.

```java
private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

log.info("Reserve thành công: ticketId={}, ticketTypeId={}, customerId={}",
          ticket.getId(), ticketType.getId(), customerId);

log.warn("Từ chối check-in do đã check-in trước đó: ticketId={}, gateId={}", ticketId, gateId);

log.error("Lỗi không xác định khi xử lý reserve", exception);
```

Quy tắc mức log:

| Level | Dùng khi |
|---|---|
| `INFO` | Sự kiện nghiệp vụ quan trọng thành công (reserve, confirm, check-in thành công) |
| `WARN` | Business exception có thể đoán trước (sold out, đã check-in, hết hạn) |
| `ERROR` | Lỗi hệ thống ngoài dự đoán (exception không xử lý được, lỗi kết nối DB...) |
| `DEBUG` | Chi tiết kỹ thuật khi cần trace, tắt ở production |

## 8. Exception

Xem chi tiết đầy đủ tại [`09-ERROR-CODES.md`](./09-ERROR-CODES.md). Tóm tắt quy ước code:

```java
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }
}
```

- `ValidationException`: lỗi input, thường tự động sinh ra từ `@Valid` (không tự ném thủ công).
- `BusinessException`: lỗi nghiệp vụ, luôn mang theo `errorCode` (enum `ErrorCode`).
- Bắt toàn bộ tại 1 nơi duy nhất: `GlobalExceptionHandler` (`@RestControllerAdvice`), không `try-catch` để format lỗi rải rác trong Controller.

## 9. Comment

- Comment bằng **tiếng Việt hoặc tiếng Anh đều được**, ưu tiên nhất quán trong cùng 1 file.
- Bắt buộc comment giải thích **lý do** (why), không comment lặp lại điều code đã tự nói (what).
- Bắt buộc comment tại mọi đoạn code liên quan đến **concurrency** (lock, conditional update, scheduled job) để giải thích rõ cơ chế — đây là phần dễ gây hiểu nhầm nhất của dự án.

```java
// Dùng FOR UPDATE để khoá dòng ticket_type ngay từ đầu transaction,
// đảm bảo các request Reserve đồng thời khác phải chờ, tránh oversell.
TicketType ticketType = ticketTypeRepository.findByIdForUpdate(id)...
```

## 10. Frontend Conventions

| Đối tượng | Quy ước | Ví dụ |
|---|---|---|
| Component | `PascalCase`, file trùng tên component | `ReserveButton.tsx` |
| Hook | `camelCase`, tiền tố `use` | `useTicketReservation.ts` |
| API function | `camelCase`, hậu tố mô tả hành động | `reserveTicket()`, `confirmTicket()` |
| Type/Interface | `PascalCase` | `TicketResponse`, `EventFormValues` |
| File API theo feature | `<feature>Api.ts` | `ticketApi.ts` |

- Component chỉ chứa UI + gọi hook, **không gọi trực tiếp API** trong component — luôn qua hook (`useXxx`) để tách logic khỏi UI.
- State server (dữ liệu từ API) và state UI cục bộ tách biệt rõ ràng.

## 11. Best Practices tổng hợp

- Không để business logic trong Controller — Controller mỏng, Service dày.
- Không truy cập trực tiếp Repository của module khác (xem [`01-ARCHITECTURE.md`](./01-ARCHITECTURE.md#4-ranh-giới-module-module-boundaries)).
- Mọi API thay đổi dữ liệu quan trọng (reserve, confirm, check-in) phải áp dụng idempotency — xem [`07-BUSINESS-RULES.md`](./07-BUSINESS-RULES.md#idempotency).
- Entity không được lộ trực tiếp ra Controller/response — luôn qua Mapper → DTO.
- Không hardcode magic number — dùng constant có tên rõ nghĩa (VD: `DEFAULT_RESERVATION_MINUTES = 7`).