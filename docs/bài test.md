## ĐỀ KIỂM TRA THỰC HÀNH:KIẾNTRÚC MICROSERVICE
Môn học : microservice và spring boot 

Thời gian làm bài : 180 phút 

Công nghệ : java 17 , spring boot 3.x  trở lên, Spring cloud, maven,docker/kafka,postgreSQL

### Mục tiêu bài kiểm tra 
đánh giá khả năng xây dựng h thống microservice cơ bản, bao gồm quản lý đăng kí dịch vụ điều phối yêu cầu qua api gateway và giao tiếp giữa các dịch vụ 
### Yêu cầu hệ thống 
Sinh viên phải xây dựng hệ thống gồm 4 thành phần độc lập:
1. Service Registry(eureka Server): cổng quản lý danh bạ dịch vụ.
2. Api Gateway : điều phối và định tuyến request
3. student Service: quản lý thông tin sinh viên :
	1. Bảng **student** thuộc database **student_db**: 
		1. **Id**(Pk, Long)
		2. name (String)
		3. Email(String)
		4. enrolled_courses((Array of Long‒ID khóa học)
4. Course service: Quản lý khóa học 
	1. Bảng courses thuộc database **course_db** 
		1. Id (pk, Long )
		2. Course_name ( String )
		3. capacity(int) - số lượng học viên tối đa 
		4. Credits(int)

###  các nhiệm vụ cụ thể 
#### Task 1 : Hạ tầng (30 điểm )
- Khởi động eureka server : Cấu hình Student Service và Course Service tự động đăng ký vào Eureka 
- Cấu hình API Gateway : 
	- Route /api/students/** → Student Service
	- Route /api/courses/** → CourseService**
	- Viết một **Global Filter** để in log (Time, Method, URI) của mọi request vào Console.

#### Task 2 :  nghiệp vụ và giao tiếp (50 điểm )
- Student Service: API POST **/api/students/enroll** nhận payload {studentId, courseId} để đăng ký khóa học
- CourseService: API GET {studentId, /api/courses/{courseId} trả về thông tin khóa học.
- **Luồng giao tiếp**:Khi gọi /api/students/enroll ,Student Service phải kiểm tra:
	- Khóa học còn chỗ không( capacity > 0 )qua Course Service.
	- Cách giao tiếp:
		- Cách A:Dùng OpenFeign (đồng bộ)
- Cập nhật số lượng học viên và danh sách **enrolled_courses** tương ứng

#### Task 3: Phân tích kiến trúc(20điểm - Trình bày trong file README.md
1. Giải thích cơ chế “service discovery” tại sao gateway không nên gọi trực tiếp địa chỉ IP/port của service ? 
2. Nếu số lượng request tăng đột biến, bạn sẽ làm gì để mở rộng *scale* order service mà  không làm thay đổi cấu hình của gateway?
3. So sánh ưu/nhược điểm của phương thức giao tiếp qua Open Feign(Đồng bộ ) so với Kafka ( bất đồng bộ) trong bài toán này 


### Thang điểm chi tiết 

| Mục tiêu            | Tiêu chí đánh giá                                    | điểm |
| ------------------- | ---------------------------------------------------- | ---- |
| Hạ tầng             | Eureka Server nhận diện đủ 3 service                 | 15   |
| Gateway             | Định tuyến đúng + Filter hoạt động chính xác         | 20   |
| Kết nối             | Giao tiếp giữa Order và Inventory thành công         | 15   |
| Nghiệp vụ           | Xử lý logic đúng theo yêu cầu ( tồn kho / đặt hàng ) | 20   |
| Thiết ké các Api    | Hoạt động tốt với các case kiểm thử                  | 15   |
| Trả lời các câu hỏi | Nắm được các kiến thức và hiểu kiến trúc             | 15   |
| Tổng                |                                                      | 100  |


---
