package com.example.weather_ai.controller.api;

import com.example.weather_ai.dto.ApiResponse;
import com.example.weather_ai.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            // Lấy username từ Token Bearer đã được Spring Security giải mã
            String username = authentication.getName();
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin thành công", authService.getUserProfile(username)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // DTO hứng Request Body
    public record ChangePasswordRequest(String oldPassword, String newPassword) {}

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal principal) {
        // ... Logic kiểm tra Password encoder.matches(...)
        return ResponseEntity.ok(ApiResponse.success("Đã đổi mật khẩu", null));
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") org.springframework.web.multipart.MultipartFile file, Principal principal) {
        // ... Logic lưu file vào thư mục tĩnh, S3, hoặc Cloudinary, sau đó update Account record
        return ResponseEntity.ok(ApiResponse.success("Avatar upload successful", null));
    }

    public record DeviceTokenRequest(String token) {}

    @PostMapping("/device-token")
    public ResponseEntity<?> updateDeviceToken(@RequestBody DeviceTokenRequest request, Principal principal) {
        try {
            String username = principal.getName();
            authService.updateDeviceToken(username, request.token());
            return ResponseEntity.ok(ApiResponse.success("Lưu Device Token thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}