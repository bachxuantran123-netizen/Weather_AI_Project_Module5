🚀 BẢN BÀN GIAO & KẾ HOẠCH DỰ ÁN: WEATHER AI (PHASE 3 - SPRINT 3)

Chào toàn team,

Xin chúc mừng tất cả anh em! Chúng ta đã khép lại Phase 2 một cách cực kỳ bùng nổ. Hạ tầng Backend giờ đây không chỉ mạnh mẽ, bảo mật mà giao diện Admin Dashboard cũng đã được "độ" lại theo phong cách Dark Theme (Hi-Tech) cực kỳ chuyên nghiệp.

Bước sang Phase 3, mục tiêu tối thượng của chúng ta là "Đóng gói, Mở rộng API cho Mobile và Triển khai (Deployment)". Hãy đọc kỹ hướng dẫn cấu hình mới trước khi pull code về nhé!

🛠️ PHẦN 1: HƯỚNG DẪN CẤU HÌNH LOCAL CHO MÔI TRƯỜNG MỚI (BẮT BUỘC)
Hệ thống hiện tại đã được Dockerize hoàn toàn (cả MySQL và Redis) và sử dụng biến môi trường cực kỳ nghiêm ngặt. Anh em làm theo 4 bước sau để chạy app:

Clone & Cập nhật: Chạy lệnh git pull origin main và nhấn Reload All Gradle Projects.

Tạo file .env: Trong thư mục gốc của project (cùng cấp với build.gradle), anh em TỰ TẠO một file tên là .env và dán các key bảo mật vào (Tuyệt đối không commit file này lên Git):
   WEATHER_API_KEY=your_weather_api_key_here
   GEMINI_API_KEY=your_gemini_api_key_here
   JWT_SECRET_KEY=MinhLuongBachDuAnModule5WeatherAIDangCapVippro
Khởi động Hạ tầng (Docker):

Đảm bảo Docker Desktop đang bật.

Mở Terminal tại thư mục gốc và chạy: docker-compose up -d mysql redis

(Lưu ý: MySQL chạy ở port 3307, Redis chạy ở 6379).

Khởi động App:

Chạy class WeatherAiApplication trên IntelliJ.

Mở trình duyệt kiểm tra Admin UI: http://localhost:8080/admin/dashboard

🏆 PHẦN 2: THÀNH QUẢ ĐẠT ĐƯỢC Ở PHASE 2
Bảo mật & Rate Limiting: JWT Token Filter hoạt động hoàn hảo, kết hợp cùng Bucket4j (chặn spam quá 10 requests/phút). Tính năng Đăng xuất (Blacklist Token vào Redis) đã hoàn thành.

Tự động hóa Hạ tầng: Triển khai thành công docker-compose.yml. Giải quyết triệt để lỗi xung đột Port và Race Condition (chờ DB boot xong mới chạy App).

Kiểm thử tự động: Đã có hệ thống Unit Test (JUnit 5 + Mockito) cho các luồng xử lý cốt lõi (AuthService, WeatherService).

Giao diện Anti-Slop: Bảng Admin Dashboard đã được kết nối với API thật qua script.js (Fetch API), render biểu đồ Chart.js với phong cách UI Cyberpunk/Valorant sắc nét.

🎯 PHẦN 3: MỤC TIÊU TIẾP THEO & PHÂN CÔNG (SPRINT 3 - CHUẨN BỊ CHO MOBILE APP)
Dự án ban đầu định hướng cung cấp API cho Mobile App (MVVM Pattern). Do đó, Sprint này tập trung vào việc chuẩn hóa API và Deploy.

👤 1. Minh: Chuyên trách Security Nâng cao & User Profile

Nhiệm vụ: Hoàn thiện luồng xác thực dành cho thiết bị di động.

Công việc cụ thể:

Viết cơ chế Refresh Token (Vì Access Token hiện tại chỉ sống 24h, cần Refresh Token để user không phải login lại liên tục trên Mobile).

Viết API lấy thông tin cá nhân (Profile) /api/v1/users/me.

Cập nhật phân quyền Role rõ ràng (ROLE_ADMIN mới được vào trang /admin/, ROLE_USER chỉ gọi API thời tiết).

👤 2. Lương: Chuyên trách Mở rộng Feature API & AI

Nhiệm vụ: Biến AI thành một trợ lý đắc lực hơn.

Công việc cụ thể:

Nâng cấp WeatherService: Tích hợp thêm API lấy dự báo thời tiết 3-5 ngày tới (Forecast API).

Nâng cấp Prompt cho Gemini trong AiAdvisorService: Trả về JSON cấu trúc rõ ràng thay vì String đơn thuần (Ví dụ: tách riêng mảng items_to_bring và warnings) để Mobile dễ parse UI.

👤 3. Bách: Core, OpenAPI & Deployment (DevOps)

Nhiệm vụ: Viết tài liệu API cho team Mobile và đưa dự án lên Internet.

Công việc cụ thể:

Tích hợp Swagger / OpenAPI 3.0: Gắn các annotation để tự động sinh ra trang Document API (/swagger-ui.html), giúp team Mobile biết cách gọi API.

CI/CD Cơ bản: Viết một file .github/workflows/build.yml để Github tự động chạy Unit Test mỗi khi có người push code.

Deploy (Tùy chọn nâng cao): Tìm hiểu cách deploy Docker container này lên một VPS hoặc các nền tảng như Render/Railway/AWS.

🚨 PHẦN 4: QUY TẮC LÀM VIỆC (GIỮ VỮNG KỶ LUẬT)
Tuyệt đối không commit file .env lên Github.

Bắt buộc rẽ nhánh theo Feature (VD: feature/refresh-token, feature/swagger-docs).

Tạo Pull Request (PR), phải pass toàn bộ Unit Test hiện có mới được Merge.

Tuân thủ triệt để nguyên tắc SOLID. Không viết logic nghiệp vụ vào Controller!
