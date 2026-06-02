package com.example.weather_ai.auth;

import com.example.weather_ai.service.CustomUserDetailsService;
import com.example.weather_ai.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final StringRedisTemplate redisTemplate;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService,  StringRedisTemplate redisTemplate) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {
                Boolean isBlacklisted = redisTemplate.hasKey("BL_" + jwt.trim());

                if (Boolean.TRUE.equals(isBlacklisted)) {
                    logger.warn("🚨 Có kẻ đang cố dùng Token đã bị đăng xuất!");

                    // 1. ÉP TRẢ VỀ LỖI 401 UNAUTHORIZED TRỰC TIẾP
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\": \"Token đã bị vô hiệu hóa (Đăng xuất). Vui lòng đăng nhập lại!\"}");

                    // 2. RETURN NGAY LẬP TỨC ĐỂ BÓP CHẾT REQUEST (KHÔNG GỌI doFilter)
                    return;
                }
            }
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // KIỂM TRA TRẠNG THÁI KHÓA TÀI KHOẢN
                if (!userDetails.isEnabled()) {
                    logger.warn("🚨 User " + username + " đang cố gọi API bằng Token cũ, nhưng tài khoản ĐÃ BỊ KHÓA!");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\": \"Tài khoản của bạn đã bị Quản trị viên khóa!\"}");
                    return; // Ngắt luồng, không cho vào Controller
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Không thể xác thực user: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        // 1. Tìm JWT trong Header (Dành cho Mobile App / Postman)
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7).trim();
        }

        // 2. Tìm JWT trong Cookie (Dành cho trình duyệt truy cập Web Admin)
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue().trim();
                }
            }
        }

        return null;
    }
}
