package com.example.weather_ai.config;

import com.example.weather_ai.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class    RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Principal principal = request.getUserPrincipal();

        // Chỉ áp dụng giới hạn cho những request đã đăng nhập (có Principal)
        if (principal != null) {
            String username = principal.getName();
            Bucket bucket = rateLimitingService.resolveBucket(username);

            // tryConsume(1) sẽ lấy ra 1 token. Trả về true nếu thành công, false nếu xô rỗng.
            if (bucket.tryConsume(1)) {
                return true; // Cho phép đi vào Controller
            } else {
                // Hết quota -> Phạt thẻ đỏ (Lỗi 429 Too Many Requests)
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"Bạn đã vượt quá giới hạn truy cập (10 lần/phút). Xin đừng spam, hãy thử lại sau!\"}");
                return false;
            }
        }

        return true;
    }
}