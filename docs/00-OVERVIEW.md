# 00 - OVERVIEW

> Tài liệu tổng quan dự án **event-ticketing**. Đây là điểm khởi đầu bắt buộc phải đọc trước khi đọc bất kỳ tài liệu nào khác trong bộ `docs/`.

## Mục lục

1. [Giới thiệu dự án](#1-giới-thiệu-dự-án)
2. [Mục tiêu](#2-mục-tiêu)
3. [Bài toán kỹ thuật cốt lõi](#3-bài-toán-kỹ-thuật-cốt-lõi)
4. [Đối tượng sử dụng](#4-đối-tượng-sử-dụng)
5. [Vai trò người dùng (Roles)](#5-vai-trò-người-dùng-roles)
6. [Quy mô hệ thống](#6-quy-mô-hệ-thống)
7. [Phạm vi chức năng MVP](#7-phạm-vi-chức-năng-mvp)
8. [Ngoài phạm vi MVP (Post-MVP)](#8-ngoài-phạm-vi-mvp-post-mvp)
9. [Entities chính](#9-entities-chính)
10. [Luồng nghiệp vụ chính (happy path)](#10-luồng-nghiệp-vụ-chính-happy-path)
11. [Tech Stack tóm tắt](#11-tech-stack-tóm-tắt)
12. [Bản đồ tài liệu](#12-bản-đồ-tài-liệu)

---

## 1. Giới thiệu dự án

**Tên dự án:** `event-ticketing`

Nền tảng bán vé sự kiện quy mô nhỏ. Người tổ chức (Organizer) tạo sự kiện và các loại vé có giới hạn số lượng; khách hàng (Customer) đặt vé và nhận vé QR; nhân viên tại cổng (Checkin Staff) quét QR để check-in; dashboard hiển thị số liệu real-time cho Organizer.

Đây là **dự án cá nhân phục vụ mục đích học tập**, không phải sản phẩm thương mại. Dự án được dùng để tự demo và mời bạn bè dùng thử thật (không phải chỉ chạy trên máy cá nhân).

## 2. Mục tiêu

Trọng tâm học tập của dự án là **concurrency control** (kiểm soát truy cập đồng thời) trong hệ thống backend thực tế. Toàn bộ quyết định kiến trúc, lựa chọn công nghệ và thiết kế database trong bộ tài liệu này đều phục vụ mục tiêu chứng minh được khả năng xử lý đúng khi có nhiều request cạnh tranh trên cùng một tài nguyên.

## 3. Bài toán kỹ thuật cốt lõi

Hệ thống phải đảm bảo đúng trong mọi trường hợp, kể cả khi nhiều request đến **đồng thời**:

| Bài toán | Yêu cầu |
|---|---|
| **Chống oversell** | Không được bán vượt quá số lượng vé giới hạn của một loại vé, kể cả khi nhiều khách đặt cùng lúc chiếc vé cuối cùng |
| **Chống double check-in** | Một vé chỉ được check-in thành công đúng 1 lần, kể cả khi nhiều nhân viên/máy quét cùng lúc quét 1 mã QR |

Xem chi tiết cơ chế kỹ thuật tại [`01-ARCHITECTURE.md`](./01-ARCHITECTURE.md) và [`07-BUSINESS-RULES.md`](./07-BUSINESS-RULES.md).

## 4. Đối tượng sử dụng

- **Chính chủ (tác giả dự án):** dùng để tự demo, học tập, làm minh chứng kỹ thuật (portfolio).
- **Bạn bè:** được mời dùng thử thật (đặt vé, check-in) trên môi trường production thật, không phải chỉ là dữ liệu giả lập.

Đây **không phải** sản phẩm thương mại, không có mục tiêu kinh doanh, không cần các cơ chế thanh toán thật ở giai đoạn MVP.

## 5. Vai trò người dùng (Roles)

Hệ thống có đúng **4 role cố định**, không phân quyền động (dynamic permission):

| Role | Mô tả |
|---|---|
| `ADMIN` | Quản lý toàn hệ thống (toàn quyền, vượt trên tất cả Organizer) |
| `ORGANIZER` | Quản lý event của chính mình: tạo/sửa event, tạo loại vé, tạo tài khoản Checkin Staff, xem dashboard |
| `CHECKIN_STAFF` | Quét mã QR tại cổng để check-in vé, chỉ trong phạm vi event được gán |
| `CUSTOMER` | Đặt vé, xem vé QR của mình |

> Lưu ý quan trọng: RBAC theo role thôi **chưa đủ** — có 2 quy tắc **ownership** bắt buộc kiểm tra thêm ở tầng Service. Xem chi tiết tại [`06-AUTHENTICATION.md`](./06-AUTHENTICATION.md#ownership-check).

## 6. Quy mô hệ thống

| Thông số | Giá trị |
|---|---|
| Số event chạy song song tối đa | 10 |
| Số vé tối đa / event | 1.000 |
| Kịch bản chịu tải chuẩn (concurrency benchmark) | 1 vé cuối cùng, **50 request đặt vé đồng thời** — chỉ đúng 1 request thành công |

Quy mô này là nhỏ về mặt hạ tầng nhưng đủ để chứng minh đúng đắn của cơ chế khoá (locking) — đây là mục tiêu chính, không phải khả năng chịu tải lớn (load-test quy mô lớn để ở giai đoạn mở rộng).

## 7. Phạm vi chức năng MVP

MVP gồm đúng 6 nhóm chức năng:

1. Organizer tạo sự kiện + loại vé kèm số lượng giới hạn
2. Customer đặt vé theo luồng **giữ chỗ (reserve) → xác nhận (confirm)**, sinh mã QR unique cho từng vé sau khi xác nhận thành công
3. Checkin Staff check-in bằng camera điện thoại quét QR tại cổng (web app, dùng `html5-qrcode`)
4. Dashboard real-time cho Organizer: số vé đã bán / đã check-in / còn lại — cập nhật theo từng cổng, không cần refresh trang (WebSocket/STOMP)
5. Cảnh báo khi 1 vé bị quét lại lần 2 (dấu hiệu vé giả / chia sẻ QR), có ghi log riêng
6. Lịch sử check-in theo cổng, theo khung giờ

## 8. Ngoài phạm vi MVP (Post-MVP)

Các mục sau **không làm ở MVP**, chỉ ghi nhận để thiết kế không bị chặn đường mở rộng sau này:

- Thanh toán thật (VD: VNPay/Momo sandbox) — MVP đặt vé miễn phí, không tích hợp cổng thanh toán
- Refund / hủy vé sau khi đã `CONFIRMED`
- Multi-gate với phân quyền staff theo từng cổng cụ thể (bảng `staff_gate_assignments`)
- Load-test quy mô lớn bằng k6/JMeter
- Redis (cache dashboard hoặc distributed lock)
- Dark mode giao diện
- Sentry / Grafana hoặc công cụ giám sát chuyên sâu

## 9. Entities chính

`User`, `Role`, `Event`, `TicketType`, `Ticket`, `Gate`, `CheckInLog`.

Chi tiết đầy đủ về cột, quan hệ, index tại [`05-DATABASE.md`](./05-DATABASE.md).

## 10. Luồng nghiệp vụ chính (happy path)

```
┌─────────────┐        ┌──────────────┐        ┌──────────────┐        ┌──────────────┐
│  ORGANIZER  │──(1)──▶│  Tạo Event + │        │              │        │              │
│             │        │  TicketType  │        │              │        │              │
└─────────────┘        └──────────────┘        │              │        │              │
                                                  │              │        │              │
┌─────────────┐        ┌──────────────┐  (2)   ┌──────────────┐        │              │
│  CUSTOMER   │──────▶│ Reserve      │──────▶│ Confirm      │──────▶│  Ticket +    │
│             │        │ (giữ chỗ)    │        │ (xác nhận)   │        │  QR sinh ra  │
└─────────────┘        └──────────────┘        └──────────────┘        └──────────────┘
                                                                                │
┌─────────────┐        ┌──────────────┐        ┌──────────────┐              │
│CHECKIN_STAFF│──(3)──▶│  Quét QR     │──────▶│ Check-in OK  │◀─────────────┘
│             │        │  tại cổng    │        │ hoặc từ chối │
└─────────────┘        └──────────────┘        └──────┬───────┘
                                                          │ (4) WebSocket/STOMP
                                                          ▼
                                                 ┌──────────────────┐
                                                 │ ORGANIZER         │
                                                 │ Dashboard real-time│
                                                 └──────────────────┘
```

1. Organizer tạo `Event` + `TicketType` (VD: "Vé thường" x100, "Vé VIP" x20)
2. Customer chọn loại vé → **Reserve** (giữ chỗ, trừ tạm số lượng, hạn 5–10 phút) → **Confirm** (xác nhận, sinh `Ticket` + QR) — nếu không confirm kịp thì tự động `EXPIRED`, trả lại chỗ
3. Đến ngày sự kiện, Checkin Staff quét QR bằng web app trên điện thoại
4. Backend xác thực vé hợp lệ + chưa check-in → cập nhật trạng thái → bắn sự kiện qua WebSocket → Dashboard của Organizer cập nhật ngay lập tức

## 11. Tech Stack tóm tắt

| Layer | Công nghệ |
|---|---|
| Backend | Java 21, Spring Boot 3.5, Spring Security, Spring Data JPA, Spring WebSocket (STOMP) |
| Frontend | React + Vite + TypeScript, Tailwind CSS + shadcn/ui, `@stomp/stompjs` |
| Database | PostgreSQL (Neon) |
| Auth | JWT (access + refresh token) + Google OAuth2 (Customer) |
| QR | ZXing (sinh QR backend), `html5-qrcode` (quét QR frontend) |
| Migration | Flyway |
| Build tool | Maven (backend), npm (frontend) |
| CI/CD | GitHub Actions |
| Deployment | Render (backend), Vercel (frontend), Neon (PostgreSQL) |

Chi tiết đầy đủ tại [`01-ARCHITECTURE.md`](./01-ARCHITECTURE.md).

## 12. Bản đồ tài liệu

| File | Nội dung |
|---|---|
| `00-OVERVIEW.md` | Tài liệu này — tổng quan dự án |
| `01-ARCHITECTURE.md` | Kiến trúc hệ thống, luồng dữ liệu, cơ chế concurrency |
| `02-FOLDER-STRUCTURE.md` | Cấu trúc thư mục backend/frontend |
| `03-CODING-STANDARDS.md` | Coding convention, naming convention |
| `04-API.md` | Danh sách API, request/response, versioning |
| `05-DATABASE.md` | Schema database, quan hệ, index, migration |
| `06-AUTHENTICATION.md` | JWT, OAuth2, RBAC, ownership check |
| `07-BUSINESS-RULES.md` | Toàn bộ quy tắc nghiệp vụ, workflow, edge case |
| `08-UI-UX.md` | Design system, responsive, accessibility |
| `09-ERROR-CODES.md` | Mã lỗi, HTTP status, exception handling |
| `10-TESTING.md` | Chiến lược test, đặc biệt là concurrency test |
| `11-DEPLOYMENT.md` | Môi trường, Docker, CI/CD |
| `12-CONTRIBUTING.md` | Git flow, commit convention, PR rules |

> Với AI Coding Agent: khi cần sinh code cho một phần cụ thể (VD: API đặt vé), hãy đọc tối thiểu `00-OVERVIEW.md` + `01-ARCHITECTURE.md` + `05-DATABASE.md` + file chuyên đề tương ứng (VD: `07-BUSINESS-RULES.md`) trước khi sinh code, để tránh vi phạm cơ chế chống oversell/double check-in.