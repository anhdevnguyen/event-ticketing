# 02 - FOLDER STRUCTURE

## Mục lục

1. [Nguyên tắc chung](#1-nguyên-tắc-chung)
2. [Cấu trúc Repo (Monorepo)](#2-cấu-trúc-repo-monorepo)
3. [Cấu trúc Backend (Package-by-feature)](#3-cấu-trúc-backend-package-by-feature)
4. [Cấu trúc Frontend (Feature-based)](#4-cấu-trúc-frontend-feature-based)
5. [Infra / Deploy](#5-infra--deploy)
6. [Quy tắc đặt file mới](#6-quy-tắc-đặt-file-mới)

---

## 1. Nguyên tắc chung

- **Monorepo**: 1 repo Git duy nhất chứa cả `backend/` và `frontend/`.
- Backend tổ chức theo **package-by-feature** (không phải package-by-layer): mỗi module domain tự chứa toàn bộ Controller/Service/Repository/Entity/DTO của mình.
- Frontend tổ chức theo **feature-based folder**: mỗi tính năng tự chứa components/hooks/api riêng.
- Không tạo folder rỗng "phòng khi cần". Chỉ tạo `infra/` hoặc `deploy/` riêng khi số lượng file cấu hình tăng lên đủ nhiều (xem mục 5).

## 2. Cấu trúc Repo (Monorepo)

```
event-ticketing/
├── backend/                   # Spring Boot app
├── frontend/                  # React app
├── .github/
│   └── workflows/             # GitHub Actions CI/CD
│       ├── backend-ci.yml
│       ├── backend-deploy.yml
│       ├── frontend-ci.yml
│       └── frontend-deploy.yml
├── docs/                      # Bộ tài liệu này
├── docker-compose.yml         # Local dev: Postgres + backend + frontend
├── .gitignore
└── README.md
```

## 3. Cấu trúc Backend (Package-by-feature)

Package gốc: `com.vanh.eventticketing` (xem quy ước đặt tên package tại [`03-CODING-STANDARDS.md`](./03-CODING-STANDARDS.md)).

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/vanh/eventticketing/
│   │   │   ├── EventTicketingApplication.java
│   │   │   │
│   │   │   ├── auth/                          # Module: auth
│   │   │   │   ├── controller/
│   │   │   │   │   └── AuthController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── AuthService.java
│   │   │   │   │   └── AuthServiceImpl.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   └── RoleRepository.java
│   │   │   │   ├── entity/
│   │   │   │   │   ├── User.java
│   │   │   │   │   └── Role.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── LoginResponse.java
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   └── RefreshTokenRequest.java
│   │   │   │   └── mapper/
│   │   │   │       └── UserMapper.java
│   │   │   │
│   │   │   ├── event/                         # Module: event
│   │   │   │   ├── controller/
│   │   │   │   │   ├── EventController.java
│   │   │   │   │   └── TicketTypeController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── EventService.java
│   │   │   │   │   ├── EventServiceImpl.java
│   │   │   │   │   ├── TicketTypeService.java
│   │   │   │   │   └── TicketTypeServiceImpl.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── EventRepository.java
│   │   │   │   │   └── TicketTypeRepository.java
│   │   │   │   ├── entity/
│   │   │   │   │   ├── Event.java
│   │   │   │   │   └── TicketType.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── EventRequest.java
│   │   │   │   │   ├── EventResponse.java
│   │   │   │   │   ├── TicketTypeRequest.java
│   │   │   │   │   └── TicketTypeResponse.java
│   │   │   │   └── mapper/
│   │   │   │       ├── EventMapper.java
│   │   │   │       └── TicketTypeMapper.java
│   │   │   │
│   │   │   ├── ticket/                        # Module: ticket
│   │   │   │   ├── controller/
│   │   │   │   │   └── TicketController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── TicketService.java
│   │   │   │   │   └── TicketServiceImpl.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── TicketRepository.java
│   │   │   │   ├── entity/
│   │   │   │   │   └── Ticket.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── ReserveRequest.java
│   │   │   │   │   ├── ConfirmRequest.java
│   │   │   │   │   └── TicketResponse.java
│   │   │   │   ├── mapper/
│   │   │   │   │   └── TicketMapper.java
│   │   │   │   └── qr/
│   │   │   │       └── QrCodeGenerator.java   # dùng ZXing
│   │   │   │
│   │   │   ├── checkin/                       # Module: checkin
│   │   │   │   ├── controller/
│   │   │   │   │   └── CheckInController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── CheckInService.java
│   │   │   │   │   └── CheckInServiceImpl.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── CheckInLogRepository.java
│   │   │   │   ├── entity/
│   │   │   │   │   └── CheckInLog.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── CheckInRequest.java
│   │   │   │   │   └── CheckInResponse.java
│   │   │   │   └── mapper/
│   │   │   │       └── CheckInLogMapper.java
│   │   │   │
│   │   │   ├── gate/                          # Module: gate
│   │   │   │   ├── controller/
│   │   │   │   │   └── GateController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── GateService.java
│   │   │   │   │   └── GateServiceImpl.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── GateRepository.java
│   │   │   │   ├── entity/
│   │   │   │   │   └── Gate.java
│   │   │   │   └── dto/
│   │   │   │       ├── GateRequest.java
│   │   │   │       └── GateResponse.java
│   │   │   │
│   │   │   ├── dashboard/                     # Module: dashboard
│   │   │   │   ├── controller/
│   │   │   │   │   └── DashboardController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── DashboardService.java
│   │   │   │   │   └── DashboardServiceImpl.java
│   │   │   │   ├── dto/
│   │   │   │   │   └── DashboardSnapshotResponse.java
│   │   │   │   └── websocket/
│   │   │   │       └── DashboardEventPublisher.java
│   │   │   │
│   │   │   └── common/                        # Shared / cross-cutting
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── WebSocketConfig.java
│   │   │       │   └── CorsConfig.java
│   │   │       ├── exception/
│   │   │       │   ├── BusinessException.java
│   │   │       │   ├── ValidationException.java
│   │   │       │   ├── ErrorCode.java
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       ├── entity/
│   │   │       │   └── BaseEntity.java         # id, createdAt, updatedAt, version
│   │   │       ├── security/
│   │   │       │   ├── JwtTokenProvider.java
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   └── CustomUserDetails.java
│   │   │       └── util/
│   │   │           └── PageResponse.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/                  # Flyway
│   │           ├── V1__init_schema.sql
│   │           ├── V2__seed_roles.sql
│   │           └── V3__seed_demo_data.sql
│   │
│   └── test/
│       └── java/com/vanh/eventticketing/
│           ├── ticket/
│           │   ├── TicketServiceTest.java           # unit test
│           │   ├── TicketReservationConcurrencyIT.java  # concurrency test
│           │   └── TicketIntegrationTest.java        # Testcontainers
│           └── checkin/
│               ├── CheckInServiceTest.java
│               └── CheckInConcurrencyIT.java
│
├── Dockerfile
├── pom.xml
└── mvnw / mvnw.cmd
```

> Quy tắc bắt buộc: module **không** được `import` trực tiếp Repository/Entity của module khác. Muốn dùng dữ liệu module khác → gọi qua Service interface public. Xem [`01-ARCHITECTURE.md`](./01-ARCHITECTURE.md#4-ranh-giới-module-module-boundaries).

## 4. Cấu trúc Frontend (Feature-based)

```
frontend/
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   ├── routes/
│   │   └── router.tsx                 # định nghĩa route, phân quyền theo role
│   │
│   ├── features/
│   │   ├── auth/
│   │   │   ├── components/
│   │   │   │   ├── LoginForm.tsx
│   │   │   │   └── GoogleLoginButton.tsx
│   │   │   ├── hooks/
│   │   │   │   └── useAuth.ts
│   │   │   ├── api/
│   │   │   │   └── authApi.ts
│   │   │   └── types.ts
│   │   │
│   │   ├── events/
│   │   │   ├── components/
│   │   │   │   ├── EventForm.tsx
│   │   │   │   ├── EventList.tsx
│   │   │   │   └── TicketTypeForm.tsx
│   │   │   ├── hooks/
│   │   │   │   └── useEvents.ts
│   │   │   ├── api/
│   │   │   │   └── eventApi.ts
│   │   │   └── types.ts
│   │   │
│   │   ├── tickets/
│   │   │   ├── components/
│   │   │   │   ├── ReserveButton.tsx
│   │   │   │   ├── ConfirmDialog.tsx
│   │   │   │   └── TicketQrView.tsx
│   │   │   ├── hooks/
│   │   │   │   └── useTicketReservation.ts
│   │   │   ├── api/
│   │   │   │   └── ticketApi.ts
│   │   │   └── types.ts
│   │   │
│   │   ├── checkin/
│   │   │   ├── components/
│   │   │   │   ├── QrScanner.tsx        # dùng html5-qrcode
│   │   │   │   └── CheckInResultToast.tsx
│   │   │   ├── hooks/
│   │   │   │   └── useCheckIn.ts
│   │   │   ├── api/
│   │   │   │   └── checkinApi.ts
│   │   │   └── types.ts
│   │   │
│   │   └── dashboard/
│   │       ├── components/
│   │       │   ├── LiveStatsCard.tsx
│   │       │   └── GateBreakdownTable.tsx
│   │       ├── hooks/
│   │       │   └── useDashboardSocket.ts   # kết nối STOMP
│   │       ├── api/
│   │       │   └── dashboardApi.ts
│   │       └── types.ts
│   │
│   ├── components/                     # UI dùng chung (shadcn)
│   │   ├── ui/                         # button, dialog, input... (shadcn generate)
│   │   └── layout/
│   │       ├── AppShell.tsx
│   │       └── ProtectedRoute.tsx
│   │
│   ├── lib/
│   │   ├── apiClient.ts                # axios/fetch instance + interceptor JWT
│   │   ├── stompClient.ts
│   │   └── utils.ts
│   │
│   └── styles/
│       └── globals.css                 # Tailwind base
│
├── public/
├── index.html
├── vite.config.ts
├── tailwind.config.ts
├── tsconfig.json
├── Dockerfile
└── package.json
```

## 5. Infra / Deploy

Ở MVP: **không tạo** folder `infra/` hoặc `deploy/` riêng vì số lượng file cấu hình còn ít:

- `backend/Dockerfile` — nằm trong `backend/`
- `frontend/Dockerfile` — nằm trong `frontend/`
- `docker-compose.yml` — nằm ở root (dùng chung cho local dev)
- `.github/workflows/*.yml` — theo đúng chuẩn GitHub Actions

**Khi nào tạo `infra/` riêng:** nếu số lượng file cấu hình tăng lên (VD: nhiều `docker-compose.*.yml` cho từng môi trường, nhiều script deploy, thêm Kubernetes manifest...), lúc đó mới tách:

```
infra/
├── docker/
│   ├── docker-compose.dev.yml
│   ├── docker-compose.staging.yml
│   └── docker-compose.prod.yml
└── scripts/
    └── deploy.sh
```

> TODO: Need confirmation — chưa xác nhận thời điểm cụ thể sẽ tách `infra/`, tạm thời giữ nguyên cấu trúc tối giản như mục 2 cho đến khi có nhu cầu thực tế.

## 6. Quy tắc đặt file mới

| Bạn muốn thêm | Đặt ở đâu |
|---|---|
| API endpoint mới cho module đã có | `<module>/controller/` |
| Business logic mới | `<module>/service/` (khai báo ở interface, code ở `*Impl`) |
| Bảng DB mới thuộc về 1 module | `<module>/entity/` + file migration mới trong `resources/db/migration/` |
| Logic dùng chung nhiều module | `common/` (không đặt vào module cụ thể) |
| Component UI dùng lại nhiều nơi | `frontend/src/components/` |
| Component chỉ dùng trong 1 tính năng | `frontend/src/features/<feature>/components/` |
| Module domain hoàn toàn mới | Tạo folder cùng cấp `event/`, `ticket/`... theo đúng mẫu ở mục 3 |