<div align="center">
  <h1>🌤️ Weather AI - Smart Weather & AI Advisor</h1>
  <p>
    <b>Ứng dụng tra cứu thời tiết thông minh thế hệ mới, tích hợp sâu Trí tuệ Nhân tạo (AI).</b>
  </p>

<!-- Tech Stack Badges -->
![Java](https://img.shields.io/badge/java-17-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring--boot-3.0-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-8.0-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-Cache-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/docker-Ready-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-EC2-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)

</div>

---

Khác với các ứng dụng thời tiết truyền thống, **Weather AI** không chỉ cung cấp các chỉ số đo lường (nhiệt độ, độ ẩm, sức gió, UV...) mà còn sử dụng AI (Google Gemini) để đóng vai trò như một *"trợ lý cá nhân"*. Hệ thống sẽ tự động đưa ra những lời khuyên chuẩn bị trang phục, vật dụng và cảnh báo an toàn theo thời gian thực dựa vào thời tiết tại vị trí của bạn.

## 🚀 Tính năng nổi bật

### 📱 Dành cho Người dùng cuối (Mobile/Web Client)
* 📍 **Tra cứu đa điểm:** Xem thời tiết hiện tại và dự báo 24 giờ tới tại mọi thành phố trên thế giới.
* 🤖 **AI Cố vấn (Gemini AI):** Tự động phân tích dữ liệu thời tiết và trả về danh sách đồ dùng cần mang theo & các lưu ý an toàn.
* ❤️ **Quản lý địa điểm:** Lưu trữ danh sách các vị trí yêu thích để tra cứu nhanh.
* 🔒 **Bảo mật tối đa:** Xác thực người dùng bằng hệ thống token JWT kép (Access/Refresh Token) qua Redis.

### 💻 Dành cho Quản trị viên (Admin Dashboard)
* 📊 **Thống kê trực quan:** Theo dõi biểu đồ lượng truy cập và tra cứu thời tiết thời gian thực bằng `Chart.js`.
* 👥 **Quản lý tài khoản:** Xem danh sách, cấp quyền quản trị hoặc khóa tài khoản người dùng vi phạm.
* 🛡️ **Rate Limiting:** Chống Spam/DDoS tự động với thuật toán Token Bucket (`Bucket4j`).

---

## 🛠️ Sơ đồ Kiến trúc & Công nghệ (Tech Stack)

### 1. Công nghệ lõi
* **Backend:** `Java 17`, `Spring Boot 3`, `Spring Data JPA`, `Spring Security`.
* **Caching & Session:** `Redis` (Quản lý Blacklist Token & Rate Limit).
* **Database:** `MySQL 8.0`.
* **Frontend:** `HTML5`, `CSS3`, `Vanilla JS`, `Thymeleaf`, `Chart.js`.

### 2. Dịch vụ bên thứ ba (Third-party APIs)
* **WeatherAPI:** Lấy dữ liệu khí tượng chuẩn xác.
* **Google Gemini AI:** Xử lý ngôn ngữ tự nhiên (NLP) & Trả lời tư vấn.

### 3. Cấu trúc thư mục tiêu biểu
```text
Weather_AI_Project_Module5/
├── .github/workflows/   # Kịch bản tự động hóa CI/CD (GitHub Actions)
├── src/main/java/       # Mã nguồn Java (Controllers, Services, Configs, Entities...)
├── src/main/resources/  # File tĩnh (CSS/JS), giao diện Thymeleaf, application.properties
├── docker-compose.yml   # Cấu hình cụm Docker (App, MySQL, Redis)
├── Dockerfile           # Kịch bản đóng gói ứng dụng Spring Boot
└── build.gradle         # Quản lý thư viện và dependencies
```

---

## ⚙️ Hướng dẫn Cài đặt & Vận hành (Local & Production)

Hệ thống được đóng gói hoàn toàn bằng **Docker**, đảm bảo chạy mượt mà trên mọi môi trường (Windows, macOS, Linux, AWS EC2) chỉ với vài dòng lệnh.

### 1. Yêu cầu hệ thống
* Cài đặt sẵn [Docker](https://docs.docker.com/get-docker/) và [Docker Compose](https://docs.docker.com/compose/install/).
* Git.

### 2. Các bước triển khai

**Bước 1: Clone mã nguồn về máy**
```bash
git clone https://github.com/bachxuantran123-netizen/Weather_AI_Project_Module5.git
cd Weather_AI_Project_Module5
```

**Bước 2: Cấu hình biến môi trường API** 
Tạo một file `.env` ở thư mục gốc của dự án và điền các API Key của bạn:
```env
GEMINI_API_KEY=your_gemini_api_key_here
JWT_SECRET_KEY=your_super_secret_jwt_key_here
WEATHER_API_KEY=your_weatherapi_key_here
TZ=Asia/Ho_Chi_Minh
```

**Bước 3: Biên dịch mã nguồn (Build JAR)**
```bash
chmod +x gradlew
./gradlew clean build -x test
```

**Bước 4: Khởi động cụm Server (App + MySQL + Redis) bằng Docker**
```bash
docker compose up -d --build
```
*(💡 Lưu ý: Nếu chạy trên máy chủ Linux/AWS, bạn cần thêm `sudo` trước lệnh `docker compose`)*

### 3. Truy cập hệ thống
Sau khi Docker khởi động xong (khoảng 15-30 giây), bạn có thể truy cập:
* **Giao diện Client:** `http://localhost:8080/mobile-preview`
* **Giao diện Admin:** `http://localhost:8080/admin/dashboard`

---

## 🔌 Các luồng API chính (Endpoints)

| HTTP Method | Endpoint | Mô tả chức năng | Quyền hạn yêu cầu |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Đăng ký tài khoản mới | `Public` |
| `POST` | `/api/v1/auth/login` | Đăng nhập & Lấy JWT | `Public` |
| `GET` | `/api/v1/weather/current?city={name}` | Lấy thời tiết & Lời khuyên AI | `Bearer Token` |
| `POST` | `/api/v1/locations` | Lưu địa điểm yêu thích | `Bearer Token` |
| `PUT` | `/api/v1/admin/users/{id}/status` | Khóa/Mở khóa tài khoản | `ROLE_ADMIN` |

---

## 👥 Đội ngũ Phát triển

Dự án được xây dựng và phát triển với sự đóng góp của:

* 👨‍💻 **Trần Xuân Bách** - DevOps & Backend Developer *(Cloud Architecture, Security, Rate Limiting)*
* 👨‍💻 **Lương** - Backend & Admin UI Developer *(Spring Boot, AI Integration, Dashboard)*
* 👨‍💻 **Minh** - Frontend & Mobile UI Developer *(UI/UX, Data Binding, DOM Manipulation)*

<div align="center">
  <p><i>Phát triển trong khuôn khổ Module 5 - 2026</i></p>
</div>
