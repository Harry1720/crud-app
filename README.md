# Event Management & Booking System

**Bản tóm tắt để triển khai (v2.1, rút gọn)**
_Cập nhật 16/10/2026. Role do Keycloak quản lý, không lưu trong DB._

---

## 1. Hệ thống làm gì

Nền tảng bán vé sự kiện: Organizer tạo event, Checker duyệt (Maker-Checker bằng Camunda), Customer đặt vé, và có quy trình hoàn vé giữa Checker và Admin.

```
Organizer tạo Event → Submit → Checker duyệt → Publish → Customer đặt vé → Vé
```

## 2. Các quyết định đã chốt

| Vấn đề          | Quyết định                                                                                                                                    |
| --------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| Role            | Quản lý hoàn toàn trong Keycloak (realm role ADMIN, ORGANIZER, CHECKER, CUSTOMER), đọc từ JWT. DB không có bảng `roles` và không có cột role. |
| Tạo tài khoản   | CUSTOMER tự đăng ký. ORGANIZER, CHECKER do ADMIN tạo (backend gọi Keycloak Admin API). ADMIN là tài khoản seed.                               |
| Bảng users      | Chỉ lưu profile: id, keycloak_id, email, full_name, phone, status. Không có password.                                                         |
| Bán vé          | Theo hạng vé (không seat map). Giữ chỗ bằng `quantity_held`, hết hạn sau 15 phút.                                                             |
| Thanh toán      | Mock, không cổng thật. Tiền VND, kiểu `NUMERIC(15,2)`.                                                                                        |
| Event           | Approve thì sang PUBLISHED luôn (không có trạng thái APPROVED riêng).                                                                         |
| Maker ≠ Checker | Người duyệt không được là người tạo event.                                                                                                    |
| Refund          | Quy trình BPMN thứ hai, mô phỏng. Bước 1 do CHECKER, bước 2 do ADMIN.                                                                         |
| Persistence     | JPA/Hibernate cho CRUD, MyBatis cho tìm kiếm, dashboard, báo cáo.                                                                             |
| Camunda         | Bản 7.x, embedded trong Spring Boot. Process bắt đầu khi Submit.                                                                              |

## 3. Vai trò

| Role      | Việc chính                                                                                                                         |
| --------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| ADMIN     | Quản lý user và category, xem mọi event, hủy event, duyệt hoàn vé bước 2, xem dashboard, report, import/export. Không duyệt event. |
| ORGANIZER | Tạo/sửa event của mình, thiết lập hạng vé, upload ảnh và giấy phép, Submit, theo dõi trạng thái duyệt và vé bán.                   |
| CHECKER   | Xem hàng chờ, Approve hoặc Reject (bắt buộc ghi lý do khi Reject), duyệt hoàn vé bước 1.                                           |
| CUSTOMER  | Tìm và xem event, đặt vé, xem hoặc hủy booking, yêu cầu hoàn vé, nhận mã vé.                                                       |

## 4. Vòng đời trạng thái

```
EVENT   DRAFT ─Submit─► PENDING_APPROVAL ─Approve─► PUBLISHED ─hết giờ (job)─► COMPLETED
          ▲                    │ Reject                 └─Cancel─► CANCELLED
          └──── Edit ──── REJECTED ◄──────┘

BOOKING PENDING ─pay (mock)─► CONFIRMED ─sau event─► COMPLETED
           │ hủy / hết hạn        │ yêu cầu hoàn vé
           ▼                      ▼
       CANCELLED          REFUND_PENDING ─► REFUNDED  (bị từ chối: về CONFIRMED)

REFUND  PENDING_REVIEW (Checker) ─► PENDING_ACCOUNTING (Admin) ─► REFUNDED
               └──────────── từ chối ở bước nào cũng ─► REJECTED
```

- Event đang PENDING_APPROVAL thì khóa sửa (kể cả hạng vé). Chỉ DRAFT mới xóa được (xóa mềm).
- Tổng số vé các hạng không vượt `capacity` của event.
- Hủy event: booking PENDING bị hủy, booking CONFIRMED tự tạo yêu cầu hoàn vé.
- Hủy booking: PENDING hủy ngay; CONFIRMED phải đi qua refund.
- Sửa event đã PUBLISHED (giá, số lượng, thời gian): Core thì khóa; nếu còn thời gian thì cho sửa nhưng phải duyệt lại.

## 5. Luồng đặt vé và chống bán vượt

- Kiểm tra: event PUBLISHED, trong thời gian đăng ký, hạng vé đang mở bán, không vượt `max_per_booking`.
- Giữ chỗ nguyên tử: `UPDATE ticket_types SET quantity_held = quantity_held + :q WHERE id = :id AND quantity_total - quantity_sold - quantity_held >= :q`. Không cập nhật được dòng nào nghĩa là hết vé (HTTP 409).
- Tạo booking PENDING, `expires_at` = bây giờ + 15 phút. `booking_items` lưu `unit_price` tại thời điểm mua.
- Thanh toán mock trong hạn: held giảm, sold tăng, booking CONFIRMED, phát hành vé.
- Job chạy mỗi phút: booking PENDING quá hạn thì CANCELLED (EXPIRED) và trả lại `quantity_held`.

## 6. Dữ liệu (PostgreSQL)

_Khóa chính BIGINT tự tăng; thời gian TIMESTAMPTZ; mọi bảng chính có created_at, updated_at. Không có bảng roles._

| Bảng                | Cột chính                                                                                                                                                                               |
| ------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| users               | keycloak_id (unique), email (unique), full_name, phone, status (ACTIVE/INACTIVE)                                                                                                        |
| categories          | name (unique), description, icon_url, status                                                                                                                                            |
| events              | title, description, category_id, location, capacity, banner_image_url, start/end_datetime, registration_start/end, status, cancel_reason, organizer_id, process_instance_id, deleted_at |
| event_images        | event_id, image_url, sort_order                                                                                                                                                         |
| event_documents     | event_id, doc_type (PERMIT/OTHER), file_name, file_url, uploaded_by                                                                                                                     |
| event_approval_logs | event_id, action (SUBMIT/APPROVE/REJECT/CANCEL), action_by, from_status, to_status, comment (bắt buộc khi REJECT/CANCEL)                                                                |
| ticket_types        | event_id, name, price, quantity_total, quantity_sold, quantity_held, sale_start/end, max_per_booking, status, version                                                                   |
| bookings            | booking_code (unique), user_id, event_id, total_amount, status, cancel_reason, payment_method, paid_at, expires_at, version                                                             |
| booking_items       | booking_id, ticket_type_id, quantity, unit_price (giá lúc mua), subtotal                                                                                                                |
| tickets             | booking_item_id, ticket_code (unique), status (ISSUED/REFUNDED/CANCELLED)                                                                                                               |
| refund_requests     | booking_id, requested_by, reason, refund_amount, status, reviewed_by/at/comment, accounting_by/at/comment, rejection_reason, process_instance_id                                        |
| notifications       | user_id, type, title, content, ref_type, ref_id, is_read                                                                                                                                |

## 7. API chính (prefix `/api/v1`)

_Login, register, forgot password do Keycloak xử lý, không có endpoint riêng. Lỗi trả thống nhất `{status, code, message, errors[]}`; 409 cho hết vé hoặc sai trạng thái._

| Nhóm     | Endpoint                                                                                                                                                                                            | Role                        |
| -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------- |
| User     | `GET /users/me`; `GET, POST /users`; `PUT /users/{id}`; `PATCH /users/{id}/status`<br>(role lấy từ Keycloak Admin API theo trang)                                                                   | ADMIN (me: mọi role)        |
| Category | `GET /categories`, `/categories/{id}`; `POST, PUT, DELETE` (xóa mềm); `POST /categories/import`                                                                                                     | Đọc: public; ghi: ADMIN     |
| Event    | `GET /events` (lọc, phân trang), `/events/{id}`, `/events/my-events`; `POST, PUT, DELETE /events`; `POST /events/{id}/submit`, `/cancel`, `/images`, `/documents`; `GET /events/{id}/approval-logs` | Public / ORGANIZER / ADMIN  |
| Duyệt    | `GET /events/pending-approval`; `POST /events/{id}/approve`; `POST /events/{id}/reject` `{reason}`                                                                                                  | CHECKER                     |
| Hạng vé  | `GET, POST /events/{eventId}/ticket-types`; `PUT, DELETE /ticket-types/{id}`                                                                                                                        | Đọc: public; ghi: ORGANIZER |
| Booking  | `POST /bookings`; `GET /bookings/my-bookings`, `/bookings/{id}`, `/bookings/{id}/tickets`; `POST /bookings/{id}/pay`, `/cancel`                                                                     | CUSTOMER                    |
| Refund   | `POST /bookings/{id}/refund-request`; `GET /refunds/my`, `/refunds/pending`; `POST /refunds/{id}/approve`, `/reject`                                                                                | CUSTOMER / CHECKER / ADMIN  |
| Khác     | `GET /notifications`, `PATCH /notifications/{id}/read`; `GET /dashboard/summary`, `/dashboard/revenue`; `GET /reports/events`, `/reports/bookings` (`format=pdf\|xlsx`)                             | Theo role                   |

## 8. Workflow Camunda

```
1) event_approval_process
   Start (Submit) → Service: set PENDING_APPROVAL + log + báo Checker
   → User Task: Review (nhóm CHECKER) → Gateway
       Approve → Service: PUBLISHED + log → Gửi mail Organizer → End
       Reject  → Service: REJECTED + log (lý do) → Gửi mail Organizer → End

2) refund_process (Should)
   Start → booking REFUND_PENDING → User Task: CS Review (CHECKER) → Gateway
       Từ chối → booking về CONFIRMED, báo khách → End
       Duyệt → User Task: Accounting (ADMIN) → Gateway (từ chối như trên)
       Duyệt → Service: Refund (mock): booking, tickets = REFUNDED → Báo khách → End
```

- Angular chỉ gọi REST của Spring Boot, không gọi Camunda trực tiếp. Spring start process khi Submit và complete task khi Approve/Reject (tìm task theo `businessKey` = eventId).
- Mọi thay đổi status và ghi log nằm trong Java Delegate để nhất quán với Camunda.

## 9. Phạm vi ưu tiên

| Mức    | Nội dung                                                                                                                                                                                                                |
| ------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Must   | Keycloak auth, User, Category (+ import Excel), Event + hạng vé + upload, Maker-Checker Camunda (có gửi mail), Booking (giữ chỗ, hết hạn, mock pay, phát hành vé), Jasper report/export, Notification, Git main/develop |
| Should | Refund BPMN, Dashboard, Redis cache, Kafka gửi thông báo, hiển thị tiến trình duyệt bằng bpmn-js                                                                                                                        |
| Could  | Seat map, QR check-in, Custom User Provider cho Keycloak                                                                                                                                                                |
| Won't  | Payment thật, hoàn tiền thực tế, AI recommendation, chat, real-time bidding                                                                                                                                             |

## 10. Công nghệ và checklist theo Excel

| Yêu cầu Excel                              | Đáp ứng bằng                                                                                                                  |
| ------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------- |
| Angular, SCSS, Tailwind/Bootstrap, icon    | Angular + TypeScript, Tailwind 3.4 + SCSS, bootstrap-icons                                                                    |
| Spring Boot, JPA, Hibernate, MyBatis       | JPA cho CRUD, MyBatis cho query báo cáo/tìm kiếm; PostgreSQL + Flyway                                                         |
| Login/Logout/Register/Forgot Password, JWT | Keycloak (mật khẩu, register, reset do Keycloak; cần cấu hình SMTP)                                                           |
| Import/Export bằng Jasper                  | Import `categories.xlsx` bằng Apache POI (validate cả file rồi mới lưu); export PDF/Excel bằng Jasper với template dùng chung |
| Camunda maker-checker + send mail          | Mục 8, Service Task gửi mail bằng Spring Mail                                                                                 |
| Message Queue/Cache, Design Pattern        | Kafka (thông báo), Redis (cache category/event); State, Strategy, Observer, Factory, Builder                                  |
| Git main và develop                        | `main` (ổn định), `develop` (tích hợp), `feature/*`; merge qua Pull Request                                                   |
