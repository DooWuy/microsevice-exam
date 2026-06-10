

# Task 3: Phân tích kiến trúc

TỔNG QUAN KIẾN TRÚC HỆ THỐNG

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (Postman/Browser)                  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY (Port 8080)                     │
│  - Route: /api/students/** → Student Service                     │
│  - Route: /api/courses/** → Course Service                       │
│  - Global Filter: Log (Time, Method, URI)                        │
└─────────────────────────────────────────────────────────────────┘
         ↓                              ↓
┌──────────────────────┐      ┌──────────────────────┐
│  STUDENT SERVICE     │      │  COURSE SERVICE      │
│  (Port 8081)         │      │  (Port 8082)         │
│                      │      │                      │
│ Database:            │      │ Database:            │
│ student_db           │      │ course_db            │
│                      │      │                      │
│ Endpoints:           │      │ Endpoints:           │
│ - POST /enroll       │      │ - GET /{courseId}    │
│ - GET /students/{id} │      │ - PUT /courses/{id}  │
└──────────────────────┘      └──────────────────────┘
         ↓                              ↓
         └──────────────────────────────┘
                      ↓
      ┌────────────────────────────────┐
      │  EUREKA SERVER (Port 8761)     │
      │  Service Registry & Discovery  │
      └────────────────────────────────┘
         ↑                ↑
      Đăng ký        Khám phá
```


## 3.1 Service Discovery là gì? Tại sao Gateway không nên gọi trực tiếp IP/port?

**Service Discovery** là khi mỗi service tự động đăng ký vào Eureka (như một danh bạ điện thoại). Gateway chỉ cần biết tên service (ví dụ: "gọi cho `student-service`"), không cần biết IP/port cụ thể.

**Ví dụ thực tế:**

Giả sử không có Service Discovery :
```
Gateway gọi: http://localhost:8081/api/students
=> Nếu Student Service chuyển sang port 8083, tất cả tương tác Gateway sẽ fail
=> Phải sửa cấu hình Gateway → Phải restart Gateway thì Service bị gián đoạn
=>Nếu Student Service chạy 3 instances, Gateway phải được cấu hình phức tạp để load balance
```

Với Service Discovery (cách mới):
```
Gateway gọi: lb://student-service
Eureka trả lời: "student-service đang chạy ở port tiêu chuẩn "
Gateway tự động gửi request đến một trong số đó =>  Không cần thay đổi cấu hình
```

**Lợi ích thực tế:**
- Nếu server Student Service bị down, Eureka tự động xóa nó, request được gửi server khác
- Khi restart Student Service, nó tự động đăng ký lại (không cần config Gateway)
- Chạy thêm instance mới? Nó tự động được phát hiện (không cần config Gateway)

**Ví dụ từ thế giới thực:**
- Netflix sử dụng Eureka. Mỗi ngày họ deploy hàng chục lần. Nếu dùng cách cũ là hard-code , toàn bộ Netflix sẽ sập từ lâu.

## 3.2 Nếu request tăng đột biến, làm sao scale service mà không thay đổi Gateway?

**Kịch bản thực tế:**
Lúc 9h sáng , hệ thống có 10,000 sinh viên truy cập cùng lúc để đăng ký khóa học. Server Student Service 1 instance không đủ, bị chậm. Sếp bắt mở thêm 2 server. Làm sao không làm gián đoạn hệ thống?

**Cách làm:**
Chạy thêm 2 instances mới cùng service name:
```bash

 student-service --server.port=8081


student-service --server.port=8083


student-service --server.port=8084
```

**Kết quả:**
- Eureka thấy 3 instances `student-service` => Tự động update danh sách
- Gateway tiếp tục gọi `student-service`
- LoadBalancer phân tán 10,000 request: ~3,333 cho mỗi instance
- KHÔNG CẦN sửa cấu hình Gateway, KHÔNG CẦN restart Gateway

**Thực tế công ty:**
- Amazon EC2: Khi server quá tải, tự động scale up thêm instance
- Kubernetes: Tự động chạy pod mới khi load cao
- Nếu dùng hard-code IP, phải sửa config thủ công trước khi scale → Muộn

## 3.3 So sánh OpenFeign (đồng bộ) vs Kafka (bất đồng bộ)

| Tiêu chí | OpenFeign                       | Kafka |
|---------|---------------------------------|-------|
| **Response** | Ngay lập tức                    | Ngắt quãng  |
| **Độ phức tạp** | Đơn giản                        | Phức tạp hơn |
| **Trusted** | Phụ thuộc Course Service online | Hàng đợi tin nhắn, luôn bảo đảm |
| **Lượng request nhiều** | Có thể delay                    | Xử lý tốt |
| **Khi nào xài** | Cần feedback ngay               | Không cần feedback ngay |

**Ví dụ thực tế - Đăng ký khóa học:**

**Cách OpenFeign (đồng bộ):**
```
Sinh viên click "Đăng ký khóa học Java"
  ↓
Student Service gọi OpenFeign → Hỏi Course Service: "Còn chỗ không?"
  ↓
Course Service trả lời: "Còn 5 chỗ"
  ↓
Student Service cập nhật enrolledCourses = [Java]
  ↓
Sinh viên nhận: "✓ Đăng ký thành công" 
```
→ **Ưu điểm:** Sinh viên biết ngay kết quả. Lỗi cũng biết ngay.
→ **Nhược điểm:** Nếu Course Service bị down hoặc chậm, sinh viên phải chờ.

**Cách Kafka (bất đồng bộ):**
```
Sinh viên click "Đăng ký khóa học Java"
  ↓
Student Service: "OK, tôi sẽ xử lý"
  ↓
Sinh viên nhận: "✓ Yêu cầu được gửi" (ngay, không cần chờ Course Service)
  ↓
(Ở phía sau) Student Service gửi event lên Kafka: "sinh-vien-1 dang-ky course-java"
  ↓
(Sau vài giây) Course Service lấy event từ Kafka, check capacity, update count
  ↓
(Tùy chọn) Gửi email cho sinh viên: "Đã xử lý xong, bạn đã được đăng ký"
```
→ **Ưu điểm:** Không phụ thuộc Course Service, chịu load lớn được.
→ **Nhược điểm:** Sinh viên không biết kết quả ngay, phải nhờ email hoặc refresh lại.

**Dành cho đề bài này, OpenFeign tốt hơn vì:**
- Sinh viên cần phản hồi ngay lập tức: "Đã đăng ký" hoặc "Khóa học hết chỗ"
- Nếu dùng Kafka, sinh viên không biết đã đăng ký thành công hay thất bại (sau 2-3 giây)
- Là bài thi, không có hàng triệu request (ngồi máy chủ không bị tắc)
- Đơn giản và dễ debug

**Kafka được dùng khi:**
- Ví dụ 1: Shopee có 1 triệu người mua hàng trong 60 phút 
  - Nếu dùng OpenFeign, server bị quá tải 
  - Chuyển sang Kafka: Event vào hàng đợi, xử lý từ từ


- Ví dụ 3: E-commerce checkout
  - Khách hàng thanh toán
  - Event vào Kafka, rồi nhiều service xử lý: trừ tiền, cập nhật inventory, gửi email, print hóa đơn
  - Khách hàng không phải chờ tất cả, thanh toán xong là được


