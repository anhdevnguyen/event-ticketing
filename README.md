# event-ticketing
Nền tảng bán vé sự kiện quy mô nhỏ. Người tổ chức (Organizer) tạo sự kiện và các loại vé có giới hạn số lượng; khách hàng (Customer) đặt vé và nhận vé QR; nhân viên tại cổng (Checkin Staff) quét QR để check-in; dashboard hiển thị số liệu real-time cho Organizer.

## Chạy local

```powershell
Copy-Item .env.example .env
Copy-Item frontend\.env.example frontend\.env
docker compose up -d postgres
```

Chạy backend bằng script có sẵn:

```powershell
cd backend
.\run-dev.ps1
```

Chạy frontend ở terminal khác:

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

URL:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`

Tài khoản demo:

- `admin@event.local` / `Admin@123`
- `organizer@event.local` / `Organizer@123`
- `staff@event.local` / `Staff@123`
- `customer@event.local` / `Customer@123`
