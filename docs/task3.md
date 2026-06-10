# Task 3: Phân tích kiến trúc

## 3.1 Service Discovery là gì? Tại sao Gateway không nên gọi trực tiếp IP/port?

**Service Discovery** là cơ chế các service tự động đăng ký địa chỉ của mình vào Eureka Registry. Gateway và các service khác chỉ cần gọi theo tên (ví dụ: `student-service`), không cần hard-code `localhost:8081`.

**Lý do không gọi trực tiếp IP/port:**
- Địa chỉ IP/port có thể thay đổi khi deploy, restart, hoặc scale
- Một service có thể chạy nhiều instance, cần load balancing
- Khi một instance bị down, Eureka tự động loại khỏi registry
- Cấu hình Gateway ổn định, không cần thay đổi

**Gateway sử dụng:**
```yaml
uri: lb://student-service  # lb = load balancer
uri: lb://course-service
```

`lb://` cho phép Spring Cloud tự động phân tán request qua các instance.

## 3.2 Nếu request tăng đột biến, làm sao scale service mà không thay đổi Gateway?

Chỉ cần chạy thêm instance của service với port khác. Mỗi instance sẽ tự động đăng ký vào Eureka với cùng service name.

Eureka Registry sẽ có 3 instance `student-service`. Gateway tự động load balance request qua 3 instance này **mà không cần sửa cấu hình**.

## 3.3 So sánh OpenFeign (đồng bộ) vs Kafka (bất đồng bộ)

| Tiêu chí | OpenFeign | Kafka |
|---------|-----------|-------|
| **Response** | Ngay lập tức  | Eventual  |
| **Độ phức tạp** | Đơn giản | Phức tạp |
| **Phụ thuộc service** | Có (service phải online) | Không (message queue) |
| **Load cao** | Giới hạn | Vô hạn |
| **Khối request** | Có (blocking threads) | Không (non-blocking) |

**Dành cho đề bài này, OpenFeign tốt hơn vì:**
- Sinh viên cần phản hồi ngay: "Đã đăng ký thành công" hoặc "Khóa học hết chỗ"
- Lượng request vừa phải
- Logic đơn giản (chỉ check capacity → update count)
- Dễ debug

**Kafka phù hợp khi:**
- Lượng request khổng lồ 
- Có thể chấp nhận delay 
- Cần guaranteed delivery (quan trọng hơn tốc độ)
- Ví dụ: gửi email xác nhận, audit log, analytics
