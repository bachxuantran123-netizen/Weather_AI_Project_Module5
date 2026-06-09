🚀 BẢN BÀN GIAO & KẾ HOẠCH DỰ ÁN: WEATHER AI (PHASE 4 - SPRINT CUỐI)

Chào toàn team,

Xin chúc mừng tất cả anh em! Chúng ta đã khép lại Phase 3 một cách vô cùng ngoạn mục. Lõi Backend giờ đây không chỉ bảo mật chặt chẽ với Refresh Token & Redis, mà còn cung cấp luồng dữ liệu Thời tiết - AI Realtime cực kỳ mạnh mẽ. Hệ thống API Docs (Swagger) cũng đã lên hình mượt mà để sẵn sàng cho team Frontend.

Bước sang Phase 4 (Sprint Cuối Cùng), mục tiêu tối thượng của chúng ta là "Thổi Hồn Vào Giao Diện (Data Binding) và Đưa Ứng Dụng Lên Cloud". 

🛠️ PHẦN 1: HƯỚNG DẪN CẤU HÌNH LOCAL & TEST API (BẮT BUỘC)
Hệ thống vẫn giữ nguyên kiến trúc Docker, nhưng bổ sung thêm các endpoint API mới. Anh em làm theo các bước sau để pull code và test:

1. Clone & Cập nhật: Chạy lệnh `git pull origin main` và nhấn Reload All Gradle Projects.
2. Kiểm tra file `.env`: Đảm bảo file `.env` ở thư mục gốc vẫn chứa đủ 3 key bảo mật (Tuyệt đối không commit file này).
3. Khởi động Hạ tầng: 
   - Mở Terminal chạy: `docker-compose up -d mysql redis`
4. Khởi động App & Trải nghiệm:
   - Chạy class `WeatherAiApplication`.
   - 📖 Test API qua Swagger: http://localhost:8080/swagger-ui/index.html
   - 📱 Test Mobile UI: http://localhost:8080/mobile-preview

🏆 PHẦN 2: THÀNH QUẢ ĐẠT ĐƯỢC Ở PHASE 3
- **Security & Auth:** Hoàn thiện luồng xác thực nâng cao. Đã tích hợp Refresh Token cấp phát lại phiên đăng nhập qua Redis mà không cần user phải gõ lại mật khẩu. API lấy Profile (`/api/v1/users/me`) hoạt động chuẩn xác.
- **Forecast & AI Upgrade:** Nâng cấp từ Current API lên Forecast API (Dự báo 3 ngày). Đã có thuật toán tự động nội suy thời gian thực để cắt chính xác 24 khung giờ tiếp theo trả về cho App. Gemini AI đã trả về cấu trúc JSON xịn xò.
- **DevOps & Document:** Tích hợp thành công Swagger (OpenAPI 3.0) tích hợp sẵn cơ chế gài Bearer Token. Thiết lập Github Actions CI/CD chạy Unit Test tự động mỗi khi có Pull Request.

🎯 PHẦN 3: MỤC TIÊU TIẾP THEO & PHÂN CÔNG (PHASE 4 - DATA BINDING & DEPLOYMENT)
Nhiệm vụ của Sprint này là khai tử toàn bộ dữ liệu giả (Mock Data) trên giao diện Mobile và kết nối trực tiếp với API thật.

👤 1. Minh: Chuyên trách Frontend & Trải nghiệm người dùng (UX)
- *Nhiệm vụ:* Thông luồng dữ liệu thời tiết thực tế lên màn hình Mobile.
- *Công việc cụ thể:*
  - Xóa hàm `fetchMockWeather()` trong `mobile.js`. Viết hàm `fetchRealWeather(city)` gọi API GET `/api/v1/weather/current?city=...` (Kèm Token nếu có đăng nhập).
  - Bóc tách JSON trả về để fill dữ liệu vào các thẻ nhiệt độ, độ ẩm, sức gió, UV.

👤 2. Lương: Chuyên trách Xử lý DOM Nâng cao & AI Rendering
- *Nhiệm vụ:* Render các mảng dữ liệu phức tạp (Array) thành HTML.
- *Công việc cụ thể:*
  - Viết vòng lặp Javascript để tự động sinh ra các block `<div class="hour-item">` cuộn ngang cho 24 khung giờ tiếp theo.
  - Xử lý bóc tách mảng `items_to_bring` và `warnings` từ cục JSON của AI để render ra các thẻ tag màu sắc (Đỏ cho cảnh báo, Xanh cho lời khuyên).

👤 3. Bách: Deployment & System Optimization (DevOps)
- *Nhiệm vụ:* Đưa ứng dụng ra Internet và kiểm tra sức chịu tải.
- *Công việc cụ thể:*
  - Cấu hình file Docker và deploy dự án lên một nền tảng Cloud (Render / Railway / AWS EC2).
  - Setup CSDL MySQL và Redis trên Cloud.
  - Kiểm tra xem Rate Limiter (Bucket4j) có hoạt động đúng khi bị gọi API liên tục trên môi trường production hay không.

🚨 PHẦN 4: QUY TẮC LÀM VIỆC (GIỮ VỮNG KỶ LUẬT)
- Giai đoạn cuối rất dễ xảy ra conflict file `mobile.js`. Anh em bắt buộc phải chia hàm ra làm riêng biệt và báo cho nhau trước khi gộp code.
- Tuyệt đối không commit file `.env` lên Github.
- Vẫn tuân thủ tạo Pull Request (PR) để Code Review chéo nhau trước khi Merge vào nhánh `main`.

Tiến lên nào anh em, vạch đích ở ngay trước mắt rồi! 🚀
