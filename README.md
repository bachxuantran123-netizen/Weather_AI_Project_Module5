# 🚀 BẢN BÀN GIAO & HƯỚNG DẪN DỰ ÁN: WEATHER AI (PHASE 1)

**Chào toàn team,**
Hiện tại, bộ khung mã nguồn cốt lõi (Base Code) của hệ thống đã được Bách thiết lập hoàn chỉnh 100% và đẩy lên nhánh `main` (Private Repo). Để chúng ta có thể bắt tay vào code Phase 1 một cách trơn tru và không giẫm chân lên nhau, mọi người vui lòng đọc kỹ tài liệu này.

## PHẦN 1: HƯỚNG DẪN CẤU HÌNH LOCAL (BẮT BUỘC MỌI NGƯỜI PHẢI LÀM)

Sau khi `git clone` dự án về máy, các bạn hãy thực hiện đúng 4 bước sau trước khi nhấn nút Run:

1. **Cập nhật thư viện:** Nhấn nút **Reload All Gradle Projects** (biểu tượng con voi trong IntelliJ) để tải thư viện Spring WebFlux, Spring Data JPA, MySQL Driver về máy.
2. **Khởi tạo Database:** Mở công cụ quản lý MySQL (Workbench/DBeaver/XAMPP) và chạy lệnh:
```sql
CREATE DATABASE weather_ai_db;

```


3. **Cấu hình file `application.properties`:** Mở file `src/main/resources/application.properties`. Bạn hãy sửa lại cấu hình MySQL cho khớp với máy của bạn:

```properties
    spring.datasource.username=root 
    spring.datasource.password=mat_khau_cua_ban
```
4.  **Chuẩn bị API Keys cá nhân:** 
    *   Đăng ký tài khoản tại `weatherapi.com` để lấy Key thời tiết.
    *   Vào `aistudio.google.com` để tạo Key Gemini.
    *   Điền Key vào file `application.properties` (Mục `weatherapi.key` và `ai.api-key`). **Tuyệt đối không push file này lên Git** để tránh rò rỉ Key.

---

## PHẦN 2: TIẾN TRÌNH HIỆN TẠI (CHÚNG TA ĐÃ CÓ NHỮNG GÌ?)

Kiến trúc hệ thống đang tuân thủ nghiêm ngặt nguyên lý **Trách nhiệm Độc nhất (SRP)**. Mọi người chú ý không gộp logic lung tung:

*   **Tầng Data (Entities & Repositories):** Đã có sẵn `Account`, `Location` và `AccountLocation` được cấu hình quan hệ Many-to-Many chuẩn xác.
*   **Tầng Giao tiếp ngoại vi (Services):**
    *   `WeatherService`: Chỉ dùng để gọi WeatherAPI.
    *   `AiAdvisorService`: Chỉ dùng để gọi Gemini API.
*   **Tầng Điều phối (Facade):** `WeatherFacadeService` đóng vai trò "nhạc trưởng", gọi luồng thời tiết xong thì đẩy qua luồng AI để trộn kết quả.
*   **Tầng Hiển thị (Controllers):** `AdminWebController` (trả về trang HTML quản trị) và `WeatherApiController` (trả về JSON cho Mobile App).
*   **Bắt lỗi (Global Exception):** `GlobalExceptionHandler` đã được thiết lập để chống sập app khi API bên thứ 3 bị lỗi. 

---

## PHẦN 3: MỤC TIÊU TIẾP THEO & PHÂN CÔNG (SPRINT 1)

Để hệ thống sớm có hình hài, chúng ta sẽ chia nhau đánh chiếm 3 mặt trận độc lập:

### 👤 1. Thành viên A: Chuyên trách Bảo mật (Security)
*   **Nhiệm vụ:** Khóa các API lại để người dùng phải đăng nhập mới xem được thời tiết.
*   **Công việc cụ thể:** 
    1. Nghiên cứu thư viện Spring Security 3.0+.
    2. Viết logic mã hóa mật khẩu người dùng (BCrypt) khi lưu vào bảng `Account`.
    3. Cấu hình cấp phát và xác thực Token JWT.

### 👤 2. Thành viên B: Chuyên trách Tích hợp API (Integration)
*   **Nhiệm vụ:** Thay thế dữ liệu giả (Mock Data) bằng dữ liệu thật từ vệ tinh và AI.
*   **Công việc cụ thể:** 
    1. Vào `WeatherService`, dùng `WebClient` thực hiện lệnh GET để kéo dữ liệu JSON từ WeatherAPI và map vào file `WeatherApiResponse.java`.
    2. Vào `AiAdvisorService`, thiết kế Prompt Template và dùng `WebClient` thực hiện lệnh POST gửi cho Gemini lấy lời khuyên.

### 👤 3. Bách: Core & Tối ưu hóa
*   **Nhiệm vụ:** Hoàn thiện luồng Location, tối ưu hiệu năng và Review Code.
*   **Công việc cụ thể:** 
    1. Viết các API CRUD (Thêm/Sửa/Xóa) cho bảng `Location`.
    2. Thiết lập Redis Cache để lưu tạm dữ liệu thời tiết (tránh gọi API quá nhiều lần làm cháy túi).
    3. Trực tiếp Review các Pull Request của A và B.

---

## 🚨 PHẦN 4: QUY TẮC LÀM VIỆC NHÓM TRÊN GIT (BẮT BUỘC)

Để tránh "thảm họa" Merge Conflict làm hỏng công sức của nhau:
1.  **Tuyệt đối không code và đẩy (push) trực tiếp lên nhánh `main`.**
2.  Khi bắt đầu làm việc, hãy rẽ nhánh (Branch):
    *   Thành viên A tạo nhánh: `feature/security-jwt`
    *   Thành viên B tạo nhánh: `feature/api-integration`
3.  Làm xong tính năng, đẩy nhánh cá nhân lên Github/Gitlab và tạo **Pull Request (PR)**.
4.  Tech Lead sẽ vào kiểm tra code, nếu đạt chuẩn mới được phép gộp (Merge) vào `main`.

**Chúc anh em code mượt mà, không bug!**
