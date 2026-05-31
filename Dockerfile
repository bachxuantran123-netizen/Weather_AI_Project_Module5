# Dùng môi trường Java 17 siêu nhẹ
FROM eclipse-temurin:17-jdk-alpine

# Thư mục làm việc bên trong container
WORKDIR /app

# Copy file jar sau khi build vào container
# (Lưu ý: Tên file jar phụ thuộc vào version trong build.gradle)
COPY build/libs/*-SNAPSHOT.jar app.jar

# Lệnh khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]