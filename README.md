# 🚀 BẢN BÀN GIAO & HƯỚNG DẪN DỰ ÁN: WEATHER AI (PHASE 2)

**Chào toàn team,**
Đầu tiên, xin chúc mừng tất cả anh em vì chúng ta đã hoàn thành xuất sắc **Phase 1**! Hệ thống móng của dự án hiện tại đang cực kỳ vững chắc, chạy mượt mà và đã được gộp toàn bộ lên nhánh `main`. 

Bây giờ là lúc chúng ta bước sang **Phase 2 (Sprint 2)**: Đắp thêm thịt cho dự án, xử lý các luồng nghiệp vụ phức tạp hơn và chuẩn bị cho việc triển khai thực tế. Mọi người vui lòng đọc kỹ tài liệu này trước khi bắt đầu code.

---

## PHẦN 1: HƯỚNG DẪN CẤU HÌNH LOCAL (CẬP NHẬT MỚI)

Do Phase 1 chúng ta đã tích hợp thêm công nghệ mới để tối ưu hiệu năng, anh em khi `git pull origin main` về máy bắt buộc phải làm thêm các bước sau:

1. **Cập nhật thư viện:** Nhấn nút **Reload All Gradle Projects** để tải thêm thư viện Spring Data Redis.
2. **Cài đặt & Bật Redis Server (BẮT BUỘC):**
   - Hệ thống giờ đã có Cache, nếu không bật Redis thì App sẽ báo lỗi không chạy được.
   - *Windows:* Tải bản Portable của Memurai hoặc Redis-x64-5.0.14.1, chạy file `redis-server.exe`.
   - *Docker (Khuyên dùng):* Chạy lệnh `docker run --name weather-redis -p 6379:6379 -d redis`
3. **Cập nhật Database:** Do cấu hình `spring.jpa.hibernate.ddl-auto=update`, Spring Boot sẽ tự động map các bảng mới, anh em không cần chạy lại script tạo bảng nữa.
4. **Kiểm tra API Keys:** Đảm bảo file `application.properties` của anh em ở local vẫn chứa `weatherapi.key` và `ai.api-key` hợp lệ.

---

## PHẦN 2: TIẾN TRÌNH HIỆN TẠI (CHÚNG TA ĐÃ HOÀN THÀNH GÌ?)

- **Bảo mật (Security):** Đã tích hợp thành công Spring Security 6 + JWT. Luồng đăng nhập, mã hóa BCrypt, cấp phát và chặn/mở Token đã hoạt động 100%.
- **Tích hợp API:** Tích hợp thành công Weather API (Lấy thời tiết) và Gemini AI (Đóng vai chuyên gia khuyên mặc đồ gì).
- **Core Database & CRUD:** Xây dựng thành công tính năng "Lưu Địa điểm Yêu thích" (`Location` & `AccountLocation`) tự động nhận diện User qua JWT Token.
- **Tối ưu Hiệu năng:** Đã tích hợp **Redis Cache**, tốc độ truy vấn thời tiết từ lần thứ 2 trở đi giảm xuống dưới 10ms (Không tốn API Quota). Đã chặn Cache lỗi (Cache Poisoning).
- **Giao diện Admin:** Xây dựng thành công Admin Dashboard (Thymeleaf + Bootstrap), đổ dữ liệu thật và vẽ biểu đồ cực đẹp bằng Chart.js.

---

## PHẦN 3: MỤC TIÊU TIẾP THEO & PHÂN CÔNG (SPRINT 2)

Ở Phase này, chúng ta sẽ làm cho hệ thống "thực chiến" hơn.

### 👤 1. Minh: Chuyên trách Security & Admin Logic
* **Nhiệm vụ:** Biến Admin Dashboard thành công cụ quản trị thực sự, không chỉ để ngắm.
* **Công việc cụ thể:** 1. Viết API Khóa / Mở khóa / Xóa mềm (Soft Delete) tài khoản người dùng.
    2. Gắn API này vào nút thao tác trên giao diện Tab "Quản lý User" (Thymeleaf).
    3. Viết luồng **Đăng xuất (Logout)** (Gợi ý: Đưa Token hiện tại vào Blacklist trong Redis).

### 👤 2. Lương: Chuyên trách Tính năng Mở rộng (Feature)
* **Nhiệm vụ:** Thu thập dữ liệu sử dụng để thay thế các con số giả lập (Mock data) trên Dashboard.
* **Công việc cụ thể:** 1. Tạo thêm Entity `SearchHistory` (Lưu lại việc User nào vừa tra cứu thành phố nào, lúc mấy giờ).
    2. Cắm logic lưu lịch sử này vào trong `WeatherFacadeService` (chạy bất đồng bộ để không làm chậm luồng chính).
    3. Trích xuất số liệu thật (Tổng số Request, Lời khuyên AI) đẩy lên giao diện Dashboard.

### 👤 3. Bách: Core, DevOps & Testing
* **Nhiệm vụ:** Chuẩn hóa quy trình phát triển và bảo vệ Server.
* **Công việc cụ thể:** 1. **Dockerize:** Viết file `docker-compose.yml` để đóng gói toàn bộ App + MySQL + Redis. Anh em sau này chỉ cần 1 lệnh `docker-compose up` là chạy được hết, không cần cài đặt lẻ tẻ.
    2. **Rate Limiting:** Cấu hình Bucket4j giới hạn số lần gọi API (Ví dụ: 1 User chỉ được gọi API thời tiết 10 lần/phút) để chống DDoS và chống cháy túi tài khoản Google Gemini.
    3. **Unit Test:** Viết JUnit 5 cho các Service cốt lõi (WeatherService, AuthService).

---

## 🚨 PHẦN 4: QUY TẮC LÀM VIỆC (VẪN NHƯ CŨ - KHÔNG NGOẠI LỆ)

1.  **Tuyệt đối không code và đẩy (push) trực tiếp lên nhánh `main`.**
2.  Bắt buộc phải rẽ nhánh khi bắt đầu Task (VD: `feature/admin-user-management`, `feature/search-history`, `feature/dockerize`).
3.  Khi hoàn thành, đẩy nhánh lên Github và tạo **Pull Request (PR)**.
4.  Tech Lead (Bách) sẽ review. Code chỉ được Merge khi chạy test không lỗi và không bị Conflict.

**Phase 1 chúng ta đã làm quá tốt rồi, Phase 2 anh em tiếp tục giữ vững phong độ nhé! Chúc team code ít bug!**
